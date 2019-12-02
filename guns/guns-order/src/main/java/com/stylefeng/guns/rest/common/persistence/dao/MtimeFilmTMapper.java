package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimeFilmT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 影片主表 Mapper 接口
 * </p>
 *
 * @author malei
 * @since 2019-11-27
 */
public interface MtimeFilmTMapper extends BaseMapper<MtimeFilmT> {

    String selectFilmNameById(@Param("filmId") int filmId);
}
