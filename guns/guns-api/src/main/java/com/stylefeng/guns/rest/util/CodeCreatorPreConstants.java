package com.stylefeng.guns.rest.util;

/**
 * @author: jia.xue
 * @create: 2019-08-09 17:12
 * @Description
 **/
public enum  CodeCreatorPreConstants {

    PROMO_ORDER_NO("PO","秒杀订单编号"),
    WALLET_LOG_NO("WN","钱包流水编号"),
    EXCHANGE_CODE_NO("EX","兑换码编号"),
    STOCK_LOG_NO("SL","库存流水编号"),
    ;

    //编号前缀
    private String PRE;
    //编号描述
    private String desc;


    CodeCreatorPreConstants(String PRE, String desc) {
        this.PRE = PRE;
        this.desc = desc;
    }
    CodeCreatorPreConstants() {
    }

    public String getPRE() {
        return PRE;
    }

    public void setPRE(String PRE) {
        this.PRE = PRE;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}