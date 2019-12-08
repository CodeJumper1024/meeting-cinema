package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.core.constant.StockLogStatus;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.core.exception.GunsExceptionEnum;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.consistant.RedisPrefixConsistant;
import com.stylefeng.guns.rest.mq.Consumer;
import com.stylefeng.guns.rest.mq.Producer;
import com.stylefeng.guns.rest.promo.PromoService;
import com.stylefeng.guns.rest.promo.vo.PromoOrderVo;
import com.stylefeng.guns.rest.promo.vo.PromoStockVo;
import com.stylefeng.guns.rest.promo.vo.PromoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service(interfaceClass = PromoService.class)
@Component
public class PromoServiceImpl implements PromoService {
    @Autowired
    MtimePromoMapper mtimePromoMapper;
    @Autowired
    MtimePromoStockMapper mtimePromoStockMapper;
    @Autowired
    MtimeCinemaTMapper mtimeCinemaTMapper;
    @Autowired
    MtimePromoOrderMapper mtimePromoOrderMapper;
    @Autowired
    MtimeStockLogMapper stockLogMapper;
    @Autowired
    Producer producer;
    @Autowired
    Consumer consumer;
    @Autowired
    private StringRedisTemplate redisTemplate;
    // 设置一个线程池
    private ExecutorService executorService;

    @PostConstruct
    public void init(){
        //创建一个线程池，里面有十个线程
        executorService  = Executors.newFixedThreadPool(100);
    }

    //库存缓存前缀
    private static final String  PROMO_STOCK_CACHE_PREFIX = "promo_stock_cache_prefix_";
    private static final String  PROMO_STOCK_NULL_PROMOID = "promo_stock_null_prefix_";
    @Override
    public List<PromoVo> getPromoVo(Integer cinemaId) {
        ArrayList<PromoVo> list = new ArrayList<>();
        EntityWrapper<MtimePromo> entityWrapper = new EntityWrapper<>();
        if(cinemaId != null){
            entityWrapper.eq("cinema_id",cinemaId);
        }
        List<MtimePromo> mtimePromos = mtimePromoMapper.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(mtimePromos)) {
            return list;
        }
        for (MtimePromo mtimePromo : mtimePromos) {
            PromoVo promoVo = new PromoVo();
            MtimeCinemaT mtimeCinemaT = mtimeCinemaTMapper.queryByCinemaId(mtimePromo.getCinemaId());
            Integer uuid = mtimePromo.getUuid();
            Integer stock = Integer.valueOf(redisTemplate.opsForValue().get(uuid+""));
            promoVo.setCinemaAddress(mtimeCinemaT.getCinemaAddress());
            promoVo.setCinemaId(mtimePromo.getCinemaId());
            promoVo.setCinemaName(mtimeCinemaT.getCinemaName());
            promoVo.setDescription(mtimePromo.getDescription());
            promoVo.setEndTime(mtimePromo.getEndTime());
            promoVo.setImgAddress(mtimeCinemaT.getImgAddress());
            promoVo.setPrice(mtimePromo.getPrice());
            promoVo.setStartTime(mtimePromo.getStartTime());
            promoVo.setStock(stock);
            promoVo.setStatus(mtimePromo.getStatus());
            promoVo.setUuid(mtimePromo.getUuid());
            list.add(promoVo);
        }
        return list;
    }
    @Override
    public Boolean insert(Integer promoId, Integer amount, String promoToken, Integer userId) {
//        MtimePromo mtimePromo = mtimePromoMapper.selectById(promoId);
//        MtimePromoStock mtimePromoStock = mtimePromoStockMapper.queryByPromoId(promoId);
//        MtimePromoOrder mtimePromoOrder = new MtimePromoOrder();
//        mtimePromoOrder.setUserId(userId);
//        mtimePromoOrder.setAmount(amount);
//        mtimePromoOrder.setCinemaId(mtimePromo.getCinemaId());
//        // 兑换码
//        mtimePromoOrder.setExchangeCode("123");
//        mtimePromoOrder.setPrice(mtimePromo.getPrice());
//        mtimePromoOrder.setStartTime(mtimePromo.getStartTime());
//        mtimePromoOrder.setEndTime(mtimePromo.getEndTime());
//        mtimePromoOrder.setCreateTime(new Date());
//        Integer insert = mtimePromoOrderMapper.insert(mtimePromoOrder);
//        Integer stock = (Integer) redisTemplate.opsForValue().get(String.valueOf(promoId));
//        stock -= amount;
//        redisTemplate.opsForValue().set(promoId, stock);
//        Boolean res = producer.decreaseStock(promoId,stock);
//        if(res){
//            return  true;
//        }
//        Integer integer = mtimePromoStockMapper.decrease(stock,promoId);
        return false;
    }

    @Override
    public List<PromoStockVo> getPromoStockVo() {
        EntityWrapper<MtimePromoStock> entityWrapper = new EntityWrapper<>();
        List<MtimePromoStock> promoStocks = mtimePromoStockMapper.selectList(entityWrapper);
        List<PromoStockVo> promoStockVos = new ArrayList<>();
        for (MtimePromoStock promoStock : promoStocks) {
            PromoStockVo promoStockVo = new PromoStockVo();
            promoStockVo.setUuid(promoStock.getUuid());
            promoStockVo.setPromoId(promoStock.getPromoId());
            promoStockVo.setStock(promoStock.getStock());
            promoStockVos.add(promoStockVo);
        }
        return promoStockVos;
    }

    @Override
    public String initPromoStockLog(Integer promoId, Integer amount) {
        MtimeStockLog mtimeStockLog = new MtimeStockLog();
        mtimeStockLog.setPromoId(promoId);
        mtimeStockLog.setAmount(amount);
        String uuid = UUIDUtil.getUUID();
        mtimeStockLog.setUuid(uuid);
        mtimeStockLog.setStatus(StockLogStatus.INIT.getIndex());
        Integer insert = stockLogMapper.insert(mtimeStockLog);
        if(insert > 0){
            return uuid;
        }else {
            return null;
        }
    }

    @Override
    public Boolean savePromoOrderInTransaction(Integer promoId, Integer userId, Integer amount, String stockLogId) {
        Boolean res = producer.sendStockMessageIntransaction(promoId,amount,userId,stockLogId);
        return res;
    }

    @Override
    public PromoOrderVo savePromoOrderVo(Integer promoId, Integer userId, Integer amount, String stockLogId) {
        // 参数校验
        processParam(promoId,userId,amount);
        MtimePromo promo = mtimePromoMapper.selectById(promoId);
        // 订单入库
        MtimePromoOrder promoOrder = savePromoOrder(promo, userId, amount);
        if(promoOrder == null){
            // 通过消息去异步化
            executorService.submit(() ->{
                stockLogMapper.updateStatusById(stockLogId,StockLogStatus.FAIL.getIndex());
            });
            throw new GunsException(GunsExceptionEnum.DATABASE_ERROR);
        }
        // 扣减库存
        Boolean ret = decreaseStock(promoId, amount);
        if(!ret) {
            //更新库存流水的状态   ----失败
            executorService.submit(() ->{
                stockLogMapper.updateStatusById(stockLogId,StockLogStatus.FAIL.getIndex());
            });
        }
        //组装参数返回前端
        PromoOrderVo promoOrderVo =  buildPromoOrderVo(promoOrder);

        //假如本地事务执行成功  更新库存流水记录的状态 -----成功
        stockLogMapper.updateStatusById(stockLogId,StockLogStatus.SUCCESS.getIndex());

        //返回前端
        return promoOrderVo;
    }
    // 产生秒杀令牌接口
    // 数量上的策略由两种
    // 1 固定令牌 固定令牌只有1000
    // 2 固定时间窗口内的令牌 固定一秒钟之内发放100个 第二秒还是会有100个
    @Override
    public String generateToken(Integer promoId, Integer userId) {
        // 先判断秒杀令牌的数量是否足够
        String key = RedisPrefixConsistant.PROMO_STOCK_AMOUNT_LIMIT + promoId;
        Long remainAmount = redisTemplate.opsForValue().increment(key, -1);
        if(remainAmount < 0){
            return null;
        }
        // 生成token
        String uuid = UUIDUtil.getUUID();
        // 把token放到redis里面
        String killTokenKey = String.format(RedisPrefixConsistant.USER_PROMO_TOKEN_PREFIX,promoId,userId);
        redisTemplate.opsForValue().set(killTokenKey,uuid);
        return uuid;
    }

    private PromoOrderVo buildPromoOrderVo(MtimePromoOrder promoOrder) {
        PromoOrderVo orderVo = new PromoOrderVo();
        orderVo.setUuid(promoOrder.getUuid());
        orderVo.setUserId(promoOrder.getUserId());
        orderVo.setEndTime(promoOrder.getEndTime());
        orderVo.setStartTime(promoOrder.getStartTime());
        orderVo.setAmount(promoOrder.getAmount());
        orderVo.setCinemaId(promoOrder.getCinemaId());
        orderVo.setCreateTime(promoOrder.getCreateTime());
        orderVo.setExchangeCode(promoOrder.getExchangeCode());
        orderVo.setPrice(promoOrder.getPrice());
        return orderVo;
    }

    private Boolean decreaseStock(Integer promoId, Integer amount) {
        String key = PROMO_STOCK_CACHE_PREFIX + promoId;

        Long increment = redisTemplate.opsForValue().increment(key, amount * -1);

        //如果库存不足
        if (increment < 0){
            redisTemplate.opsForValue().increment(key,amount);
            return  false;
        }
        if(increment == 0){
            String soldedKey = PROMO_STOCK_NULL_PROMOID + promoId;
            redisTemplate.opsForValue().set(soldedKey,"success");
            redisTemplate.expire(soldedKey,30, TimeUnit.MINUTES);
        }
        return true;
    }

    private MtimePromoOrder savePromoOrder(MtimePromo promo, Integer userId, Integer amount) {
        //组装promoOrder
        MtimePromoOrder promoOrder = buidPromoOrder(promo,userId,amount);
        //存入数据库
        Integer insertRet = mtimePromoOrderMapper.insert(promoOrder);
        if (insertRet < 1) {
            return null;
        }
        return promoOrder;
    }

    private MtimePromoOrder buidPromoOrder(MtimePromo promo, Integer userId, Integer amount) {
        MtimePromoOrder mtimePromoOrder = new MtimePromoOrder();
        MtimePromo mtimePromo = mtimePromoMapper.selectById(promo.getUuid());
        String uuid = UUIDUtil.getUUID();
        mtimePromoOrder.setUuid(uuid);
        mtimePromoOrder.setUserId(userId);
        mtimePromoOrder.setAmount(amount);
        mtimePromoOrder.setCinemaId(mtimePromo.getCinemaId());
        // 兑换码
        mtimePromoOrder.setExchangeCode("123");
        mtimePromoOrder.setPrice(mtimePromo.getPrice());
        mtimePromoOrder.setStartTime(mtimePromo.getStartTime());
        mtimePromoOrder.setEndTime(mtimePromo.getEndTime());
        mtimePromoOrder.setCreateTime(new Date());
        return mtimePromoOrder;
    }

    private void processParam(Integer promoId, Integer userId, Integer amount) {
        if (promoId == null) {
            throw new GunsException(GunsExceptionEnum.REQUEST_NULL);
        }
        if (userId == null) {
            throw new GunsException(GunsExceptionEnum.REQUEST_NULL);
        }
        if (amount == null) {
            throw new GunsException(GunsExceptionEnum.REQUEST_NULL);
        }
    }
}
