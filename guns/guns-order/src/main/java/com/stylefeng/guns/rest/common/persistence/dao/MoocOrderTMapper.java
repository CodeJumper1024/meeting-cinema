package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author xdd
 * @since 2019-11-30
 */
public interface MoocOrderTMapper extends BaseMapper<MoocOrderT> {

    List<String> selectOrderSeatsIdsByFieldId(@Param("fieldId") String fieldId);

    int insertOrder(@Param("uuid") String uuid, @Param("cinemaId") int cinemaId, @Param("fieldId") String fieldId, @Param("filmId") int filmId, @Param("soldSeats") String soldSeats, @Param("seatsName") String seatsName, @Param("price") Double price, @Param("orderPrice") String orderPrice, @Param("userId") Integer userId);
}
