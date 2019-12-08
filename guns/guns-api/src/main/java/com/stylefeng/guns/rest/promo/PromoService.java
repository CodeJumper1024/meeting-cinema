package com.stylefeng.guns.rest.promo;

import com.stylefeng.guns.rest.promo.vo.PromoOrderVo;
import com.stylefeng.guns.rest.promo.vo.PromoStockVo;
import com.stylefeng.guns.rest.promo.vo.PromoVo;

import java.util.List;

public interface PromoService {
    public List<PromoVo> getPromoVo(Integer cinemaId);

    Boolean insert(Integer promoId, Integer amount, String promoToken, Integer userId);

    List<PromoStockVo> getPromoStockVo();

    String initPromoStockLog(Integer promoId, Integer amount);

    Boolean savePromoOrderInTransaction(Integer promoId, Integer userId, Integer amount, String stockLogId);

    PromoOrderVo savePromoOrderVo(Integer promoId, Integer userId, Integer amount, String stockLogId);

    String generateToken(Integer promoId, Integer userId);
}
