package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.util.concurrent.RateLimiter;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.core.exception.GunsExceptionEnum;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import com.stylefeng.guns.rest.consistant.RedisPrefixConsistant;
import com.stylefeng.guns.rest.promo.PromoService;
import com.stylefeng.guns.rest.promo.vo.PromoStockVo;
import com.stylefeng.guns.rest.promo.vo.PromoVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.*;

@RestController
@RequestMapping("promo")
public class PromoController {
    @Reference(interfaceClass = PromoService.class, check = false)
    PromoService promoService;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    RedisTemplate redisTemplate;
    // 声明一个令牌桶
    private RateLimiter rateLimiter;
    // 设置一个线程池
    private ExecutorService executorService;
    @PostConstruct
    public void init(){
        // 初始化一个固定大小的线程池
        // 数值大小就是拥塞窗口
        executorService = Executors.newFixedThreadPool(100);
        // 每秒产生10个令牌
        rateLimiter = RateLimiter.create(10);
    }
    //秒杀令牌对于库存的倍数
    public static final Integer PROMO_TOKEN_TIMES = 5;
    @RequestMapping("getPromo")
    public BaseReqVo getPromo(Integer cinemaId){
        List<PromoVo> promoVos = promoService.getPromoVo(cinemaId);
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        if(promoVos == null){
            return BaseReqVo.queryFail();
        }else{
            baseReqVo.setData(promoVos);
            baseReqVo.setStatus(0);
        }
        return baseReqVo;
    }
    @PostMapping("createOrder")
    public BaseReqVo createOrder(Integer promoId, Integer amount, String promoToken, HttpServletRequest request){
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        // 通过RateLimiter去限流
        // 返回的是等待时间 其实就是获取一个令牌
        double acquire = rateLimiter.acquire();
        if(acquire < 0){
            baseReqVo.setMsg("秒杀失败");
            baseReqVo.setStatus(1);
            return  baseReqVo;
        }

        String header = request.getHeader(jwtProperties.getHeader());
        String token = header.substring(7);
        String userId = (String) redisTemplate.opsForValue().get(token);
        if(token == null || userId == null){
            baseReqVo.setStatus(700);
            baseReqVo.setMsg("获取用户失败！请用户重新登录!");
            return baseReqVo;
        }
        if(amount < 0 || amount > 5){
            baseReqVo.setMsg("订单数量不合法");
            baseReqVo.setStatus(1);
            return  baseReqVo;
        }
        // 判断秒杀令牌是否合法
        String killTokenKey = String.format(RedisPrefixConsistant.USER_PROMO_TOKEN_PREFIX,promoId,userId);
        Boolean rest = redisTemplate.hasKey(killTokenKey);
        if(!rest){
            baseReqVo.setMsg("秒杀令牌不合法");
            baseReqVo.setStatus(1);
            return  baseReqVo;
        }
        String tokenInRedis = (String) redisTemplate.opsForValue().get(killTokenKey);
        if(!tokenInRedis.equals(promoToken)){
            baseReqVo.setMsg("秒杀令牌不合法");
            baseReqVo.setStatus(1);
            return  baseReqVo;
        }

        Future<Boolean> future = executorService.submit(()->{
            Boolean result =false;
            try {
                // 下单前 初始化一条库存流水 并把状态设置为初始值 并返回这条记录的主键id
                String stockLogId = promoService.initPromoStockLog(promoId,amount);
                if(StringUtils.isBlank(stockLogId)){
                    throw new GunsException(GunsExceptionEnum.DATABASE_ERROR);
                }
                // 下单接口
                // 创建订单 扣减库存
                result = promoService.savePromoOrderInTransaction(promoId, Integer.valueOf(userId),amount,stockLogId);
            } catch (Exception e) {
                throw new GunsException(GunsExceptionEnum.DATABASE_ERROR);
            }
            if(!result){
                throw new GunsException(GunsExceptionEnum.DATABASE_ERROR);
            }
            return result;
        });
        Boolean result = false;
        try {
            result = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new GunsException(GunsExceptionEnum.DATABASE_ERROR);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new GunsException(GunsExceptionEnum.DATABASE_ERROR);
        } catch (GunsException e){
            throw new GunsException(GunsExceptionEnum.DATABASE_ERROR);
        }
        baseReqVo.setStatus(0);
        baseReqVo.setMsg("下单成功");
        return baseReqVo;
    }
    @RequestMapping("publishPromoStock")
    public BaseReqVo publishPromoStock(Integer cinemaId) {
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        List<PromoStockVo> promoStockVos = promoService.getPromoStockVo();
        if (promoStockVos == null) {
            return BaseReqVo.fail();
        }
        for (PromoStockVo promoStockVo : promoStockVos) {
            String promoId = String.valueOf(promoStockVo.getPromoId());
            if(redisTemplate.opsForValue().get(promoId) == null) {
                redisTemplate.opsForValue().set(promoId, promoStockVo.getStock()+"");
                // 存入秒杀令牌的数量在redis里面
                String key = RedisPrefixConsistant.PROMO_STOCK_AMOUNT_LIMIT + promoId;
                Integer value = promoStockVo.getStock() * PROMO_TOKEN_TIMES;
                redisTemplate.opsForValue().set(key,value+"");
            }
        }
        baseReqVo.setStatus(0);
        baseReqVo.setMsg("发布成功");
        return baseReqVo;
    }
    @RequestMapping("generateToken")
    public BaseReqVo generateToken(Integer promoId,HttpServletRequest request) {
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        String header = request.getHeader(jwtProperties.getHeader());
        String headerToken = header.substring(7);
        String userId = (String) redisTemplate.opsForValue().get(headerToken);
        String soldedKey = RedisPrefixConsistant.PROMO_STOCK_NULL_PROMOID + promoId;
        // 先判断库存是否已经售罄，如果库存已经售罄，则直接返回
        Boolean res = redisTemplate.hasKey(soldedKey);
        if(res){
            baseReqVo.setMsg("库存售罄");
            baseReqVo.setStatus(1);
            return  baseReqVo;
        }
        if(headerToken == null || userId == null){
            baseReqVo.setStatus(700);
            baseReqVo.setMsg("获取用户失败！请用户重新登录!");
            return baseReqVo;
        }
        // 先根据promoId判断活动是否存在
        // 判断活动是否有效
        // 判断活动是否正在进行
        String token = promoService.generateToken(promoId, Integer.valueOf(userId));
        if(StringUtils.isBlank(token)){
            baseReqVo.setMsg("获取秒杀令牌失败");
            baseReqVo.setStatus(1);
            return  baseReqVo;
        }
        baseReqVo.setStatus(0);
        baseReqVo.setMsg(token);
        return baseReqVo;
    }
}
