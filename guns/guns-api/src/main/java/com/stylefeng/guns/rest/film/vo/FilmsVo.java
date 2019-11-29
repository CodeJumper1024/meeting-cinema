package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilmsVo implements Serializable {
    private static final long serialVersionUID = -3287765403616364038L;
    private Integer filmNum;
    private List<FilmInfoVo> filmInfo;
}
