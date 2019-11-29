package com.stylefeng.guns.rest.cinema;


import com.stylefeng.guns.rest.cinema.vo.AreaVO;
import com.stylefeng.guns.rest.cinema.vo.BrandVO;
import com.stylefeng.guns.rest.cinema.vo.CinemaVO;
import com.stylefeng.guns.rest.cinema.vo.HallTypeVO;

import java.util.List;

public interface CinemaService {
    List<AreaVO> getAreasById(Integer areaId);

    List<BrandVO> getBrandsById(Integer brandId);

    List<HallTypeVO> getHallTypesById(Integer hallType);

    List<CinemaVO> getCinemas(Integer brandId, Integer hallType, Integer areaId);
}
