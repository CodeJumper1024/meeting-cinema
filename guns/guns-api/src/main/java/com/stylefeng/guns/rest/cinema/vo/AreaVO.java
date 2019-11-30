package com.stylefeng.guns.rest.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AreaVO implements Serializable {

    private static final long serialVersionUID = 5491728682951528025L;

    private Integer areaId;

    private String areaName;

    private Boolean active;
}
