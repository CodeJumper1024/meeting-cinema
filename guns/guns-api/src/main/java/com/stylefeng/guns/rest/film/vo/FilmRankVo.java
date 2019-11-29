package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class FilmRankVo implements Serializable {

    private static final long serialVersionUID = 1479221654492961290L;
     private Integer filmId;
     private String imgAddress;
     private String filmName;
     private Integer boxNum;
     private Integer expectNum;
     private String score;
}
