package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class Info04VO implements Serializable {
    private static final long serialVersionUID = 7520408900330312753L;
    private String biography;
    private ActorsVO actors;
}
