package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xdd
 * @since 2019-12-03
 */
public interface MtimePromoStockMapper extends BaseMapper<MtimePromoStock> {
    MtimePromoStock queryByPromoId(@Param("promoId") Integer promoId);

    Integer decrease(@Param("amount") Integer amount, @Param("promoId") Integer promoId);
}
