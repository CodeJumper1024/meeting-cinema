package com.stylefeng.guns.rest.cinema.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataVO implements Serializable {

    private static final long serialVersionUID = 8206447634510520926L;

    List<AreaVO> areaList;

    List<BrandVO> brandList;

    List<HalltypeVO> halltypeList;
}
