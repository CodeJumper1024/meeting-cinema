package com.stylefeng.guns.rest.alipay;

import com.stylefeng.guns.rest.BaseReqVo;

public interface AlipayService {
    BaseReqVo getPayInfo(String orderId);
}
