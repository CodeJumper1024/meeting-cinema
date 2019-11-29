package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RealActorsVo implements Serializable {
    private static final long serialVersionUID = 942111536922185838L;
    private String imgAddress;
    private String directorName;
    private String roleName;
}
