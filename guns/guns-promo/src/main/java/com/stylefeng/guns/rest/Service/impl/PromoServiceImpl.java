package com.stylefeng.guns.rest.Service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.core.exception.GunsExceptionEnum;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.Vo.PromoInfoVo;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.mq.PromoProducer;
import com.stylefeng.guns.rest.promo.PromoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/12/3 20:21
 */
@Slf4j
@Component
@Service(interfaceClass = PromoService.class)
public class PromoServiceImpl implements PromoService {

    @Autowired
    MtimePromoMapper mtimePromoMapper;
    @Autowired
    MtimeCinemaTMapper mtimeCinemaTMapper;
    @Autowired
    MtimePromoStockMapper mtimePromoStockMapper;
    @Autowired
    MtimePromoOrderMapper mtimePromoOrderMapper;
    @Autowired
    MtimeStockLogMapper mtimeStockLogMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    PromoProducer promoProducer;

    private ExecutorService executorService;
    //初始化一个线程池
    @PostConstruct
    public void initThreadPoll(){
        executorService = Executors.newFixedThreadPool(100);
    }

    //秒杀库存为0的标志
    private final static String PROMO_STOCK_NULL_PREFIX = "promo_stock_null_%s";

    //参数分别为秒杀活动id,用户id
    private final static String PROMO_TOKEN_PREFIX = "promo_token_%s_%s";

    //秒杀令牌数对于秒杀库存的倍数
    private final static Integer PROMO_TOKEN_TIMES_TO_STOCK = 5;

    //指定秒杀id的令牌数秒杀的令牌数
    private final static String PROMO_TOKEN_AMOUNT_PREFIX = "promo_token_amount_prefix_%s";

    //获取秒杀信息没有参数
    @Override
    public BaseReqVo getPromoNoParam() {
        List<MtimePromo> mtimePromos = mtimePromoMapper.selectList(null);
        ArrayList<Object> promos = new ArrayList<>();
        for (MtimePromo mtimePromo : mtimePromos) {
            MtimeCinemaT mtimeCinemaT = mtimeCinemaTMapper.selectById(mtimePromo.getCinemaId());
            MtimePromoStock mtimePromoStockE = new MtimePromoStock();
            mtimePromoStockE.setPromoId(mtimePromo.getUuid());
            MtimePromoStock mtimePromoStock = mtimePromoStockMapper.selectOne(mtimePromoStockE);
            PromoInfoVo promoInfoVo = conver2PromoInfoVo(mtimePromo, mtimeCinemaT, mtimePromoStock);
            promos.add(promoInfoVo);
        }
        BaseReqVo<List> baseReqVo = new BaseReqVo<>();
        baseReqVo.setData(promos);
        baseReqVo.setStatus(0);
        return baseReqVo;
    }

    //获取秒杀信息
    @Override
    public BaseReqVo getPromo(Integer pageSize, Integer nowPage) {
        List<MtimePromo> mtimePromos = mtimePromoMapper.selectPage(new Page<>(nowPage, pageSize), null);
        ArrayList<Object> promos = new ArrayList<>();
        for (MtimePromo mtimePromo : mtimePromos) {
            MtimeCinemaT mtimeCinemaT = mtimeCinemaTMapper.selectById(mtimePromo.getCinemaId());
            MtimePromoStock mtimePromoStockE = new MtimePromoStock();
            mtimePromoStockE.setPromoId(mtimePromo.getUuid());
            MtimePromoStock mtimePromoStock = mtimePromoStockMapper.selectOne(mtimePromoStockE);
            PromoInfoVo promoInfoVo = conver2PromoInfoVo(mtimePromo, mtimeCinemaT, mtimePromoStock);
            promos.add(promoInfoVo);
        }
        BaseReqVo<List> baseReqVo = new BaseReqVo<>();
        baseReqVo.setData(promos);
        baseReqVo.setStatus(0);
        return baseReqVo;
    }

    //获取秒杀信息:封装秒杀对象的信息
    private PromoInfoVo conver2PromoInfoVo(MtimePromo mtimePromo, MtimeCinemaT mtimeCinemaT, MtimePromoStock mtimePromoStock) {
        PromoInfoVo promoInfoVo = new PromoInfoVo();
        promoInfoVo.setCinemaAddress(mtimeCinemaT.getCinemaAddress());
        promoInfoVo.setCinemaId(mtimeCinemaT.getUuid());
        promoInfoVo.setCinemaName(mtimeCinemaT.getCinemaName());
        promoInfoVo.setImgAddress(mtimeCinemaT.getImgAddress());
        promoInfoVo.setDescription(mtimePromo.getDescription());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String endTime = simpleDateFormat.format(mtimePromo.getEndTime());
        promoInfoVo.setEndTime(endTime);
        String startTime = simpleDateFormat.format(mtimePromo.getStartTime());
        promoInfoVo.setStartTime(startTime);
        promoInfoVo.setPrice(mtimePromo.getPrice().intValue());
        promoInfoVo.setStatus(mtimePromo.getStatus());
        promoInfoVo.setUuid(mtimePromo.getUuid());
        promoInfoVo.setStock(mtimePromoStock.getStock());
        return promoInfoVo;
    }

    //获取当前秒杀的库存到redis
    @Override
    public BaseReqVo publishPromoStock() {
        List<MtimePromoStock> stocks = mtimePromoStockMapper.selectList(null);
        HashMap<String, Integer> stockMap = new HashMap<>();
        for (MtimePromoStock stock : stocks) {
            stockMap.put(Integer.toString(stock.getPromoId()), stock.getStock());
            Integer amount = stock.getStock() * PROMO_TOKEN_TIMES_TO_STOCK;
            String promoTokenAmountKey = String.format(PROMO_TOKEN_AMOUNT_PREFIX, stock.getPromoId());
            redisTemplate.opsForValue().set(promoTokenAmountKey, amount);
            redisTemplate.expire(promoTokenAmountKey,1, TimeUnit.HOURS);
        }
        redisTemplate.opsForHash().putAll("promoStock", stockMap);
        redisTemplate.expire("promoStock",1, TimeUnit.HOURS);
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        baseReqVo.setMsg("发布成功");
        baseReqVo.setStatus(0);
        return baseReqVo;
    }

    //秒杀下单:非事务控制
    @Override
    public BaseReqVo createOrder(Integer userId, Integer amount, Integer promoId, String promoToken) {
        //获取秒杀信息
        MtimePromo mtimePromo = mtimePromoMapper.selectById(promoId);
        //获取影院信息
        MtimeCinemaT mtimeCinemaT = mtimeCinemaTMapper.selectById(mtimePromo.getCinemaId());
        //下单
        Integer insertOrderResult = insertOrderInfo(userId, amount, promoId, mtimePromo, mtimeCinemaT);
        //修改库存
        String promoIdStr = Integer.toString(promoId);
        Integer stock = (Integer) redisTemplate.opsForHash().get("promoStock", promoIdStr);
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        if (stock != null && stock >= amount){
            stock = stock - amount;
            redisTemplate.opsForHash().put("promoStock", promoIdStr, stock);
            Boolean result = promoProducer.reduceStock(promoId, amount);
            if (result){
                baseReqVo.setMsg("下单成功");
                baseReqVo.setStatus(0);
            }else {
                baseReqVo.setMsg("下单失败");
                baseReqVo.setStatus(700);
            }
        }else {
            baseReqVo.setMsg("库存不足");
            baseReqVo.setStatus(700);
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return baseReqVo;
        //没有接入消息队列前
        /*MtimePromoStock mtimePromoStock = new MtimePromoStock();
        mtimePromoStock.setPromoId(mtimePromo.getUuid());
        MtimePromoStock stockInfo = mtimePromoStockMapper.selectOne(mtimePromoStock);
        stockInfo.setStock(stockInfo.getStock() - amount);
        EntityWrapper<MtimePromoStock> stockEntityWrapper = new EntityWrapper<>();
        stockEntityWrapper.eq("promo_id", mtimePromo.getUuid());
        mtimePromoStockMapper.update(stockInfo, stockEntityWrapper);*/
    }

    //秒杀下单:新增订单
    private Integer insertOrderInfo(Integer userId, Integer amount, Integer promoId, MtimePromo mtimePromo, MtimeCinemaT mtimeCinemaT) {
        MtimePromoOrder mtimePromoOrder = new MtimePromoOrder();
        String uuid = UUID.randomUUID().toString();
        mtimePromoOrder.setUuid(uuid);
        mtimePromoOrder.setUserId(userId);
        mtimePromoOrder.setCinemaId(mtimeCinemaT.getUuid());
        String exchangeCode = UUID.randomUUID().toString();
        mtimePromoOrder.setExchangeCode(exchangeCode);
        mtimePromoOrder.setAmount(amount);
        mtimePromoOrder.setPrice(BigDecimal.valueOf(mtimePromo.getPrice().doubleValue() * amount));
        mtimePromoOrder.setStartTime(mtimePromo.getStartTime());
        Date nowDate = new Date();
        mtimePromoOrder.setCreateTime(nowDate);
        mtimePromoOrder.setEndTime(mtimePromo.getEndTime());
        Integer insertOrderResult = mtimePromoOrderMapper.insert(mtimePromoOrder);
        return insertOrderResult;
    }

    //初始化流水
    @Override
    public String insertStockLog(Integer promoId, Integer amount) {
        MtimeStockLog mtimeStockLog = new MtimeStockLog();
        String uuid = UUID.randomUUID().toString();
        mtimeStockLog.setUuid(uuid);
        mtimeStockLog.setPromoId(promoId);
        mtimeStockLog.setAmount(amount);
        Integer num = mtimeStockLogMapper.insert(mtimeStockLog);
        if (num > 0){
            return uuid;
        }else {
            return null;
        }
    }

    //分布式事务,秒杀下单
    @Override
    public Boolean createPromoOrderInMqTransaction(Integer userId, Integer promoId, Integer amount, String stockLogId) {
        Boolean result = promoProducer.createPromoOrderInMqTransaction(userId, promoId, amount, stockLogId);
        return result;
    }

    //分布式事务秒杀下单,执行本地本地事务
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Boolean executeLocalTransaction(Object args) {
        HashMap<String, Object> argsMap = (HashMap)args;
        Integer userId = (Integer) argsMap.get("userId");
        Integer promoId = (Integer) argsMap.get("promoId");
        Integer amount = (Integer) argsMap.get("amount");
        String stockLogId = (String) argsMap.get("stockLogId");
        //准备修改订单流水
        MtimeStockLog mtimeStockLog = new MtimeStockLog();
        EntityWrapper<MtimeStockLog> stockLogEntityWrapper = new EntityWrapper<>();
        stockLogEntityWrapper.eq("uuid", stockLogId);
        //获取秒杀信息
        MtimePromo mtimePromo = mtimePromoMapper.selectById(promoId);
        //获取影院信息
        MtimeCinemaT mtimeCinemaT = mtimeCinemaTMapper.selectById(mtimePromo.getCinemaId());
        //下单
        Integer insertOrderResult = insertOrderInfo(userId, amount, promoId, mtimePromo, mtimeCinemaT);
        if (insertOrderResult < 1){
            log.info("订单入库失败");
            //异步改变流水状态
            executorService.submit(() ->{
                mtimeStockLog.setStatus(3);
                mtimeStockLogMapper.update(mtimeStockLog, stockLogEntityWrapper);
            });
            throw new GunsException(GunsExceptionEnum.valueOf("订单入库失败"));
        }
        //扣减redis库存
        String promoIdStr = Integer.toString(promoId);
        Integer reduceNum = amount * -1;
        Long afterStock = redisTemplate.opsForHash().increment("promoStock", promoIdStr, reduceNum);
        if (afterStock < 0){
            log.info("库存不足");
            redisTemplate.opsForHash().increment("promoStock", promoIdStr, amount);
            //异步改变流水状态
            executorService.submit(() ->{
                mtimeStockLog.setStatus(3);
                mtimeStockLogMapper.update(mtimeStockLog, stockLogEntityWrapper);
            });
            throw new GunsException(GunsExceptionEnum.valueOf("库存不足"));
        }
        if (afterStock == 0){
            String sellOutKey = String.format(PROMO_STOCK_NULL_PREFIX, Integer.toString(promoId));
            redisTemplate.opsForValue().set(sellOutKey, "null");
        }
        //没有问题,更改流水状态
        mtimeStockLog.setStatus(2);
        mtimeStockLogMapper.update(mtimeStockLog, stockLogEntityWrapper);
        return true;
    }

    //判断秒杀活动是有效
    @Override
    public Boolean promoIsExist(Integer promoId) {
        //对秒杀活动是否存在,活动是在有效期内
        MtimePromo mtimePromo = mtimePromoMapper.selectById(promoId);
        if (mtimePromo != null){
            Date date = new Date();
            if (date.after(mtimePromo.getStartTime()) && date.before(mtimePromo.getEndTime())){
                return true;
            }
        }
        return false;
    }

    //获取秒杀令牌
    @Override
    public BaseReqVo<Object> generateToken(Integer promoId, Integer userId) {
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        String promoToken = UUID.randomUUID().toString();
        String promoTokenKey = String.format(PROMO_TOKEN_PREFIX, promoId, userId);
        redisTemplate.opsForValue().set(promoTokenKey, promoToken);
        baseReqVo.setStatus(0);
        baseReqVo.setMsg(promoToken);
        return baseReqVo;
    }
}
