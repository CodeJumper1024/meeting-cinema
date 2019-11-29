package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.cinema.vo.AreaVO;
import com.stylefeng.guns.rest.cinema.vo.BrandVO;
import com.stylefeng.guns.rest.cinema.vo.CinemaVO;
import com.stylefeng.guns.rest.cinema.vo.HallTypeVO;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.MtimeAreaDictT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeBrandDictT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeCinemaT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeHallDictT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/28 22:16
 */
@Component
@Service(interfaceClass = CinemaService.class)
public class CinemaServiceImpl implements CinemaService {
    
    @Autowired
    MtimeAreaDictTMapper mtimeAreaDictTMapper;
    @Autowired
    MtimeBrandDictTMapper mtimeBrandDictTMapper;
    @Autowired
    MtimeCinemaTMapper mtimeCinemaTMapper;
    @Autowired
    MtimeFieldTMapper mtimeFieldTMapper;
    @Autowired
    MtimeHallDictTMapper mtimeHallDictTMapper;
    @Autowired
    MtimeHallFilmInfoTMapper mtimeHallFilmInfoTMapper;
    
    @Override
    public List<AreaVO> getAreasById(Integer areaId) {
        List<AreaVO> list = new ArrayList<>();
        EntityWrapper<MtimeAreaDictT> entityWrapper = new EntityWrapper<>();
        List<MtimeAreaDictT> mtimeAreaDictTS = mtimeAreaDictTMapper.selectList(entityWrapper);
        if(CollectionUtils.isEmpty(mtimeAreaDictTS)){
            return list;
        }
        for(MtimeAreaDictT mtimeAreaDictT :mtimeAreaDictTS){
            AreaVO areaVO = new AreaVO();
            areaVO.setAreaId(mtimeAreaDictT.getUuid());
            areaVO.setAreaName(mtimeAreaDictT.getShowName());
            areaVO.setActive(false);
            if(mtimeAreaDictT.getUuid()==areaId){
                areaVO.setActive(true);
            }
            list.add(areaVO);
        }
        return list;
    }

    @Override
    public List<BrandVO> getBrandsById(Integer brandId) {
        List<BrandVO> list = new ArrayList<>();
        EntityWrapper<MtimeBrandDictT> entityWrapper = new EntityWrapper<>();
        List<MtimeBrandDictT> mtimeBrandDictTS = mtimeBrandDictTMapper.selectList(entityWrapper);
        if(CollectionUtils.isEmpty(mtimeBrandDictTS)){
            return list;
        }
        for(MtimeBrandDictT mtimeBrandDictT : mtimeBrandDictTS){
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandId(mtimeBrandDictT.getUuid());
            brandVO.setBrandName(mtimeBrandDictT.getShowName());
            brandVO.setActive(false);
            if(mtimeBrandDictT.getUuid()==brandId){
                brandVO.setActive(true);
            }
            list.add(brandVO);
        }
        return list;
    }

    @Override
    public List<HallTypeVO> getHallTypesById(Integer hallType) {
        List<HallTypeVO> list = new ArrayList<>();
        EntityWrapper<MtimeHallDictT> entityWrapper = new EntityWrapper<>();
        List<MtimeHallDictT> mtimeHallDictTS = mtimeHallDictTMapper.selectList(entityWrapper);
        if(CollectionUtils.isEmpty(mtimeHallDictTS)){
            return list;
        }
        for(MtimeHallDictT mtimeHallDictT :mtimeHallDictTS){
            HallTypeVO hallTypeVO = new HallTypeVO();
            hallTypeVO.setHalltypeId(mtimeHallDictT.getUuid());
            hallTypeVO.setHalltypeName(mtimeHallDictT.getShowName());
            hallTypeVO.setActive(false);
            if(hallType==mtimeHallDictT.getUuid()){
                hallTypeVO.setActive(true);
            }
            list.add(hallTypeVO);
        }
        return list;
    }

    @Override
    public List<CinemaVO> getCinemas(Integer brandId, Integer hallType, Integer areaId) {
        List<CinemaVO> list = new ArrayList<>();
        EntityWrapper<MtimeCinemaT> entityWrapper = new EntityWrapper<>();
        if(brandId!=99){
            entityWrapper.eq("brand_id",brandId);
        }
        if(hallType!=99){
            entityWrapper.eq("area_id",areaId);
        }
        if(areaId!=99){
            entityWrapper.like("hall_ids","#"+hallType+"#");
        }
        List<MtimeCinemaT> mtimeCinemaTS = mtimeCinemaTMapper.selectList(entityWrapper);
        for(MtimeCinemaT mtimeCinemaT :mtimeCinemaTS){
            CinemaVO cinemaVO = new CinemaVO();
            cinemaVO.setUuid(mtimeCinemaT.getUuid());
            cinemaVO.setCinemaAddress(mtimeCinemaT.getCinemaAddress());
            cinemaVO.setCinemaName(mtimeCinemaT.getCinemaName());
            cinemaVO.setMinimumPrice(mtimeCinemaT.getMinimumPrice());
            list.add(cinemaVO);
        }
        return list;
    }
}
