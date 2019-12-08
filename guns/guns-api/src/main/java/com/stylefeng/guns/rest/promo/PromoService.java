package com.stylefeng.guns.rest.promo;

import com.stylefeng.guns.rest.BaseReqVo;

public interface PromoService {

    BaseReqVo getPromoNoParam();

    BaseReqVo getPromo(Integer pageSize, Integer nowPage);

    BaseReqVo publishPromoStock();

    BaseReqVo createOrder(Integer userId, Integer amount, Integer promoId, String promoToken);

    String insertStockLog(Integer promoId, Integer amount);

    Boolean createPromoOrderInMqTransaction(Integer userId, Integer promoId, Integer amount, String stockLogId);

    Boolean executeLocalTransaction(Object args);

    Boolean promoIsExist(Integer promoId);

    BaseReqVo<Object> generateToken(Integer promoId, Integer userId);


}
