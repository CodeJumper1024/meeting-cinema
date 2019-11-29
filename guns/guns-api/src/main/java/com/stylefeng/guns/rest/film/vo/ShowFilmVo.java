package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShowFilmVo implements Serializable {
    private static final long serialVersionUID = 5445048152160620695L;
    private String filmName;
    private String filmEnName;
    private String imgAddress;
    private String score;
    private Integer scoreNum;
    private Integer totalBox;
    private String info01;
    private String info02;
    private String info03;
    private Info04VO info04;
    private imgVO imgVO;
    private Integer filmId;
}
