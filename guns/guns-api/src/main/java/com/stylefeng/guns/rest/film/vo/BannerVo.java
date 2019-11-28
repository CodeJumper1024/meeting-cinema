package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BannerVo implements Serializable {

    private static final long serialVersionUID = -6035274298995856160L;
    private Integer bannerId;
    private String bannerAddress;
    private String bannerUrl;

}
