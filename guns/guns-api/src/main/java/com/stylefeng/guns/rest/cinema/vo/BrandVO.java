package com.stylefeng.guns.rest.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BrandVO implements Serializable {

    private static final long serialVersionUID = -1492423837019177858L;

    private Integer brandId;

    private String brandName;

    private Boolean active;
}
