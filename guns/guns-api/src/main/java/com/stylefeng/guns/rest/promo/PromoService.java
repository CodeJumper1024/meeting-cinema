package com.stylefeng.guns.rest.promo;

import com.stylefeng.guns.rest.promo.vo.PromoVo;

import java.util.List;

public interface PromoService {
    public List<PromoVo> getPromoVo(Integer cinemaId);

    int insert(Integer promoId, Integer amount, String promoToken, Integer userId);
}
