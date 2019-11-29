package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class CatInfoVo implements Serializable {
    private static final long serialVersionUID = -2390274849588429528L;
    private Integer catId;
    private String catName;
    private Boolean active;
    private Boolean isActive;
}
