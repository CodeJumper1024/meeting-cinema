package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetFilmsVoAndPages implements Serializable {
    private static final long serialVersionUID = -1548591416166573586L;
    private List<GetFilmsVO> getFilmsVOS;
    private String totalPage;
}
