package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeCinemaTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoOrderMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeCinemaT;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromo;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoOrder;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import com.stylefeng.guns.rest.promo.PromoService;
import com.stylefeng.guns.rest.promo.vo.PromoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            MtimePromoStock mtimePromoStock = mtimePromoStockMapper.queryByPromoId(mtimePromo.getUuid());
            MtimeCinemaT mtimeCinemaT = mtimeCinemaTMapper.queryByCinemaId(mtimePromo.getCinemaId());
            promoVo.setCinemaAddress(mtimeCinemaT.getCinemaAddress());
            promoVo.setCinemaId(mtimePromo.getCinemaId());
            promoVo.setCinemaName(mtimeCinemaT.getCinemaName());
            promoVo.setDescription(mtimePromo.getDescription());
            promoVo.setEndTime(mtimePromo.getEndTime());
            promoVo.setImgAddress(mtimeCinemaT.getImgAddress());
            promoVo.setPrice(mtimePromo.getPrice());
            promoVo.setStartTime(mtimePromo.getStartTime());
            promoVo.setStatus(mtimePromo.getStatus());
            promoVo.setStock(mtimePromoStock.getStock());
            promoVo.setUuid(mtimePromo.getUuid());
            list.add(promoVo);
        }
        return list;
    }
    @Transactional
    @Override
    public int insert(Integer promoId, Integer amount, String promoToken, Integer userId) {
        MtimePromo mtimePromo = mtimePromoMapper.selectById(promoId);
        MtimePromoStock mtimePromoStock = mtimePromoStockMapper.queryByPromoId(promoId);
        MtimePromoOrder mtimePromoOrder = new MtimePromoOrder();
        mtimePromoOrder.setUserId(userId);
        mtimePromoOrder.setAmount(amount);
        mtimePromoOrder.setCinemaId(mtimePromo.getCinemaId());
        mtimePromoOrder.setExchangeCode("123");
        mtimePromoOrder.setPrice(mtimePromo.getPrice());
        mtimePromoOrder.setStartTime(mtimePromo.getStartTime());
        mtimePromoOrder.setEndTime(mtimePromo.getEndTime());
        mtimePromoOrder.setCreateTime(new Date());
        Integer insert = mtimePromoOrderMapper.insert(mtimePromoOrder);
        int stock = mtimePromoStock.getStock() - amount;
        Integer integer = mtimePromoStockMapper.decrease(stock,promoId);
        return insert;
    }
}
