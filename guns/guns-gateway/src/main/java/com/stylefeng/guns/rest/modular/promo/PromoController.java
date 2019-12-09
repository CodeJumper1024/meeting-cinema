package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.util.concurrent.RateLimiter;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import com.stylefeng.guns.rest.promo.PromoService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@RestController
@RequestMapping("promo/")
public class PromoController {

    @Reference(interfaceClass = PromoService.class, check = false, timeout = 10000000)
    PromoService promoService;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    RedisTemplate redisTemplate;

    private ExecutorService executorService;

    //声明一个令牌桶
    private RateLimiter rateLimiter;

    //初始化一个线程池
    @PostConstruct
    public void init(){
        executorService = Executors.newFixedThreadPool(800);
        rateLimiter = RateLimiter.create(1000);
    }

    //秒杀库存为0的标志
    private final static String PROMO_STOCK_NULL_PREFIX = "promo_stock_null";

    //参数分别为秒杀活动id,用户id
    private final static String PROMO_TOKEN_PREFIX = "promo_token_%s_%s";

    //指定秒杀id的令牌数秒杀的令牌数
    private final static String PROMO_TOKEN_AMOUNT_PREFIX = "promo_token_amount_prefix_%s";

    @RequestMapping("getPromo")
    public BaseReqVo getPromo(@RequestParam(required = false) Integer brandId,
                              @RequestParam(required = false)  Integer hallType,
                              @RequestParam(required = false) Integer areaId,
                              @RequestParam(required = false) Integer pageSize,
                              @RequestParam(required = false) Integer nowPage){
        BaseReqVo baseReqVo = null;
        if (pageSize == null && nowPage == null){
            baseReqVo = promoService.getPromoNoParam();
        }else {
            baseReqVo = promoService.getPromo(pageSize, nowPage);
        }
        System.out.println(1);
        return baseReqVo;
    }

    @RequestMapping("publishPromoStock")
    public BaseReqVo publishPromoStock(){
        //获取所有秒杀库存
        BaseReqVo baseReqVo = promoService.publishPromoStock();
        return baseReqVo;
    }

    /**
     * 根据秒杀活动id获取令牌
     * @param promoId
     */
    @RequestMapping("generateToken")
    public BaseReqVo generateToken(Integer promoId, HttpServletRequest request){
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        //获取已经登录用户的信息
        String header = request.getHeader(jwtProperties.getHeader());
        String token = header.substring(7);
        Integer userId = (Integer) redisTemplate.opsForValue().get(token);
        //
        String sellOutKey = String.format(PROMO_STOCK_NULL_PREFIX, Integer.toString(promoId));
        //查看redis是该秒杀库存视口为0
        Object o = redisTemplate.opsForValue().get(sellOutKey);
        if (o != null){
            baseReqVo.setMsg("库存为0");
            baseReqVo.setStatus(700);
            return baseReqVo;
        }
        //判断秒杀活动是否存在,活动是在有效期内
        Boolean isExist = promoService.promoIsExist(promoId);
        if (!isExist){
            baseReqVo.setMsg("参数校验失败");
            baseReqVo.setStatus(400);
            return baseReqVo;
        }
        String tokenAmountKey = String.format(PROMO_TOKEN_AMOUNT_PREFIX, promoId);
        Long reaminAmount = redisTemplate.opsForValue().increment(tokenAmountKey, -1);
        if (reaminAmount < 0){
            baseReqVo.setMsg("获取令牌失败");
            baseReqVo.setStatus(700);
            return baseReqVo;
        }
        baseReqVo = promoService.generateToken(promoId, userId);
        return baseReqVo;
    }

    /**
     * 秒杀下单
     * @param promoId
     * @param amount
     * @param promoToken
     * @param request
     * @return
     */
    @RequestMapping("createOrder")
    public BaseReqVo createOrder(Integer promoId, Integer amount, String promoToken, HttpServletRequest request){
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        //通过RataLimiter去限制每秒访问的峰值
        double acquire = rateLimiter.acquire();
        if (acquire < 0){
            baseReqVo.setMsg("秒杀失败");
            baseReqVo.setStatus(400);
            return baseReqVo;
        }
        //获取已经登录用户的信息
        String header = request.getHeader(jwtProperties.getHeader());
        String token = header.substring(7);
        Integer userId = (Integer) redisTemplate.opsForValue().get(token);
        //校验参数
        if (userId == null){
            baseReqVo.setMsg("请登录");
            baseReqVo.setStatus(403);
            return baseReqVo;
        }
        if (amount <= 0 || amount > 10){
            baseReqVo.setMsg("参数校验失败");
            baseReqVo.setStatus(400);
            return baseReqVo;
        }
        String promoTokenKey = String.format(PROMO_TOKEN_PREFIX, promoId, userId);
        String promoTokenInRedis = (String)redisTemplate.opsForValue().get(promoTokenKey);
        if (promoTokenInRedis == null){
            baseReqVo.setMsg("没有令牌");
            baseReqVo.setStatus(400);
            return baseReqVo;
        }
        if (!promoToken.equals(promoTokenInRedis)){
            baseReqVo.setMsg("令牌不合法");
            baseReqVo.setStatus(400);
            return baseReqVo;
        }
        //查看redis是该秒杀库存是否为0
        String sellOutKey = String.format(PROMO_STOCK_NULL_PREFIX, Integer.toString(promoId));
        Object o = redisTemplate.opsForValue().get(sellOutKey);
        if (o != null){
            baseReqVo.setMsg("库存为0");
            baseReqVo.setStatus(700);
            return baseReqVo;
        }
        //用户已经登录
        //开始执行分布式事务
        //首先新增stock_log条目,跟踪秒杀订单的状态,返回主键id并校验
        //队列泄洪控制同时访问的峰值
        Future<?> future = executorService.submit(() -> {
            String stockLogId = promoService.insertStockLog(promoId, amount);
            Boolean result = false;
            result = promoService.createPromoOrderInMqTransaction(userId, promoId, amount, stockLogId);
            return result;
        });
        Boolean result = false;
        try {
            result = (Boolean)future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //1调用producer,获取消息事务的执行结果
        if (result){
            baseReqVo.setMsg("下单成功");
            baseReqVo.setStatus(0);
        }else {
            baseReqVo.setMsg("下单失败");
            baseReqVo.setStatus(700);
        }
        return baseReqVo;
    }

}
