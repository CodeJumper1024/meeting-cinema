package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DirectorVo implements Serializable {
    private static final long serialVersionUID = 883228649763457615L;
    private String imgAddress;
    private String directorName;
}
