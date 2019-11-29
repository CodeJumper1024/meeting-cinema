package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FilmInfoVo implements Serializable {

    private static final long serialVersionUID = -6956990115363031838L;

    private Integer filmId;
    private Integer filmType;
    private String imgAddress;
    private String filmName;
    private String filmScore;
    private Date showTime;
    private Integer expectNum;
}
