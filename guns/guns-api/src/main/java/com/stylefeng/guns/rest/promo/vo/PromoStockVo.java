package com.stylefeng.guns.rest.promo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PromoStockVo implements Serializable {
    private static final long serialVersionUID = -2431429739092404294L;
    private Integer uuid;
    private Integer promoId;
    private Integer stock;
}
