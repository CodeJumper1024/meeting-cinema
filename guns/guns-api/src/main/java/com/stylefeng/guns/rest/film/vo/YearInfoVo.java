package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class YearInfoVo implements Serializable {
    private static final long serialVersionUID = 4316185335790992944L;
    private Integer yearId;
    private String yearName;
    private Boolean isActive;
}
