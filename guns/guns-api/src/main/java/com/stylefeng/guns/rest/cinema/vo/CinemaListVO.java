package com.stylefeng.guns.rest.cinema.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CinemaListVO implements Serializable {

    private static final long serialVersionUID = 2283580393057355183L;

    private List<CinemaVO> cinemaVO;

    private String totalPage;
}
