package com.stylefeng.guns.rest.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class HallTypeVO implements Serializable {

    private static final long serialVersionUID = -8100763237975416531L;

    private Integer halltypeId;

    private String halltypeName;

    private Boolean active;
}
