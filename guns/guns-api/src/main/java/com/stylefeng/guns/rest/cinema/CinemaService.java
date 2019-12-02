package com.stylefeng.guns.rest.cinema;

import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.cinema.vo.AreaVO;
import com.stylefeng.guns.rest.cinema.vo.BrandVO;
import com.stylefeng.guns.rest.cinema.vo.CinemaListVO;
import com.stylefeng.guns.rest.cinema.vo.HalltypeVO;

import java.util.List;


public interface CinemaService {
    List<AreaVO> getAreasById(Integer areaId);

    List<BrandVO> getBrandsById(Integer brandId);

    List<HalltypeVO> getHallTypesById(Integer hallType);

    CinemaListVO getCinemas(Integer brandId, Integer halltypeId,Integer hallType, Integer areaId,
                            Integer pageSize,
                            Integer nowPage);

    BaseReqVo getFields(Integer cinemaId);

    BaseReqVo getFieldInfo(Integer cinemaId, Integer fieldId, String soldSeats);
}
