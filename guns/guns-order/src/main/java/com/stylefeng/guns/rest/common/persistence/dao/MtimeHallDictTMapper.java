package com.stylefeng.guns.rest.common.persistence.dao;

import org.apache.ibatis.annotations.Param;

public interface MtimeHallDictTMapper {

    String selectJsonById(@Param("hallId") int hallId);
}
