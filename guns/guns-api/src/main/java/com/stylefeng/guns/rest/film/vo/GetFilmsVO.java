package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetFilmsVO implements Serializable {
    private static final long serialVersionUID = 1756836664748881172L;
    private Integer boxNum;
    private Integer expectNum;
    private Integer filmId;
    private String filmName;
    private String filmScore;
    private Integer filmType;
    private String imgAddress;
    private String showTime;
}
