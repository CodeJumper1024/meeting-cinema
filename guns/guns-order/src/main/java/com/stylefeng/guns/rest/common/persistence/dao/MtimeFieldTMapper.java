package com.stylefeng.guns.rest.common.persistence.dao;

import org.apache.ibatis.annotations.Param;

public interface MtimeFieldTMapper {

    int selectHallIdById(@Param("fieldId") String fieldId);

    int selectCinemaIdById(@Param("fieldId") String fieldId);

    int selectFilmIdById(@Param("fieldId") String fieldId);

    Double selectPriceById(@Param("fieldId") String fieldId);
}
