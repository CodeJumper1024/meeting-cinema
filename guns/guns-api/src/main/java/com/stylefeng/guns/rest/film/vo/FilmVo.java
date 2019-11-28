package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/27 17:32
 */
@Data
public class FilmVo implements Serializable {

    private static final long serialVersionUID = 2316343178374613037L;

    private Integer uuid;

    private String filmName;
}
