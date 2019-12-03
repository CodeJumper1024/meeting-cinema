package com.stylefeng.guns.rest.alipay;

import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.alipay.vo.GetPayResultVo;

public interface AlipayService {
    BaseReqVo getPayInfo(String orderId);
    GetPayResultVo getPayResult(String orderId);
    GetPayResultVo updateFail(String orderId);
}
