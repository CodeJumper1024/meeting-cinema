package com.stylefeng.guns.rest.Vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/28 22:57
 */
@Data
public class HallFilmInfoVo implements Serializable {

    private String actors;
    private String filmCats;
    private Integer filmId;
    private String filmLength;
    private String filmName;
    private String filmType;
    private String imgAddress;
    private List<FilmFieldVo> filmFields;
}
