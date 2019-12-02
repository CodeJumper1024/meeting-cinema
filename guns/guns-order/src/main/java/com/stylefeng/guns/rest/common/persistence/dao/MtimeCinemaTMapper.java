package com.stylefeng.guns.rest.common.persistence.dao;

import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 影院信息表 Mapper 接口
 * </p>
 *
 * @author malei
 * @since 2019-11-28
 */
public interface MtimeCinemaTMapper {

    String selectCinemaNameById(@Param("cinemaId") int cinemaId);
}
