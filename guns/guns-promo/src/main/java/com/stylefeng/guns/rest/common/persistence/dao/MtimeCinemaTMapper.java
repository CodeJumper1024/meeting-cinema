package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimeCinemaT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 影院信息表 Mapper 接口
 * </p>
 *
 * @author xdd
 * @since 2019-12-03
 */
public interface MtimeCinemaTMapper extends BaseMapper<MtimeCinemaT> {
    MtimeCinemaT queryByCinemaId(@Param("cinemaId") Integer cinemaId);
}
