package com.stylefeng.guns.rest.cinema;

import com.stylefeng.guns.rest.vo.BaseReqVo;

public interface CinemaService {
    BaseReqVo getFields(Integer cinemaId);

    BaseReqVo getFieldInfo(Integer cinemaId, Integer fieldId);
}
