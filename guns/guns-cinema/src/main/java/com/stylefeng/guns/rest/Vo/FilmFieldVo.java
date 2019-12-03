package com.stylefeng.guns.rest.Vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/28 23:01
 */
@Data
public class FilmFieldVo implements Serializable {
    private static final long serialVersionUID = -7355871600199280945L;
    private String beginTime;
    private String endTime;
    private Integer fieldId;
    private String hallName;
    private String language;
    private String price;
}
