package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.Vo.CinemaInfoVo;
import com.stylefeng.guns.rest.Vo.FilmFieldVo;
import com.stylefeng.guns.rest.Vo.HallFilmInfoVo;
import com.stylefeng.guns.rest.Vo.HallInfoVo;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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
    public List<HalltypeVO> getHallTypesById(Integer hallType) {
        List<HalltypeVO> list = new ArrayList<>();
        EntityWrapper<MtimeHallDictT> entityWrapper = new EntityWrapper<>();
        List<MtimeHallDictT> mtimeHallDictTS = mtimeHallDictTMapper.selectList(entityWrapper);
        if(CollectionUtils.isEmpty(mtimeHallDictTS)){
            return list;
        }
        for(MtimeHallDictT mtimeHallDictT :mtimeHallDictTS){
            HalltypeVO HalltypeVO = new HalltypeVO();
            HalltypeVO.setHalltypeId(mtimeHallDictT.getUuid());
            HalltypeVO.setHalltypeName(mtimeHallDictT.getShowName());
            HalltypeVO.setActive(false);
            if(hallType==mtimeHallDictT.getUuid()){
                HalltypeVO.setActive(true);
            }
            list.add(HalltypeVO);
        }
        return list;
    }

    @Override
    public CinemaListVO getCinemas(Integer brandId, Integer halltypeId,
                                   Integer hallType, Integer areaId,
                                   Integer pageSize, Integer nowPage) {
        List<CinemaVO> list = new ArrayList<>();
        CinemaListVO cinemaListVO = new CinemaListVO();
        EntityWrapper<MtimeCinemaT> entityWrapper = new EntityWrapper<>();
        if(brandId!=99){
            entityWrapper.eq("brand_id",brandId);
        }
        if(areaId!=99){
            entityWrapper.eq("area_id",areaId);
        }
        if(hallType!=null && hallType!=99){
            entityWrapper.like("hall_ids", "#" + hallType + "#");
        }
        if(halltypeId!=null && halltypeId!=99){
            entityWrapper.like("hall_ids", "#" + halltypeId + "#");
        }
        Page<MtimeCinemaT> page = new Page<>(nowPage,pageSize);
        List<MtimeCinemaT> mtimeCinemaTS = mtimeCinemaTMapper.selectPage(page,entityWrapper);
        int totalPage=0;
        Integer count = mtimeCinemaTMapper.selectCount(entityWrapper);
        totalPage=count/pageSize;
        if(count%pageSize !=0){
            totalPage++;
        }
        cinemaListVO.setTotalPage(totalPage+"");
        for(MtimeCinemaT mtimeCinemaT :mtimeCinemaTS){
            CinemaVO cinemaVO = new CinemaVO();
            cinemaVO.setUuid(mtimeCinemaT.getUuid());
            cinemaVO.setCinemaAddress(mtimeCinemaT.getCinemaAddress());
            cinemaVO.setCinemaName(mtimeCinemaT.getCinemaName());
            cinemaVO.setMinimumPrice(mtimeCinemaT.getMinimumPrice());
            list.add(cinemaVO);
        }
        cinemaListVO.setCinemaVO(list);
        return cinemaListVO;
    }

    //获取播放场次
    @Override
    public BaseReqVo getFields(Integer cinemaId) {
        BaseReqVo<Map> baseReqVo = new BaseReqVo<>();
        HashMap<String, Object> dataMap = new HashMap<>();
        //查询该电影院信息
        MtimeCinemaT cinema = mtimeCinemaTMapper.selectById(cinemaId);
        CinemaInfoVo cinemaInfo = conver2CinemaInfoVo(cinema);
        dataMap.put("cinemaInfo", cinemaInfo);
        EntityWrapper<MtimeFieldT> wrapper = new EntityWrapper<>();
        wrapper.eq("cinema_id", cinemaId);
        //查询该电影院的所有场次
        List<MtimeFieldT> mtimeFieldTList = mtimeFieldTMapper.selectList(wrapper);
        //所有的场次中有哪些电影
        HashSet<Integer> filmIds = new HashSet<>();
        for (MtimeFieldT mtimeFieldT : mtimeFieldTList) {
            filmIds.add(mtimeFieldT.getFilmId());
        }
        ArrayList<Object> filmList = new ArrayList<>();
        //获取场次电影详情
        ArrayList<MtimeHallFilmInfoT> mtimeHallFilmInfoTS = new ArrayList<>();
        for (Integer filmId : filmIds) {
            MtimeHallFilmInfoT mtimeHallFilmInfoTForSelect = new MtimeHallFilmInfoT();
            mtimeHallFilmInfoTForSelect.setFilmId(filmId);
            MtimeHallFilmInfoT mtimeHallFilmInfoT = mtimeHallFilmInfoTMapper.selectOne(mtimeHallFilmInfoTForSelect);
            HallFilmInfoVo hallFilmInfoVo = conver2FilmInfoVo(mtimeHallFilmInfoT);
            ArrayList<FilmFieldVo> filmFieldVos = new ArrayList<>();
            for (MtimeFieldT mtimeFieldT : mtimeFieldTList) {
                if (filmId == mtimeFieldT.getFilmId()){
                    FilmFieldVo filmFieldVo = conver2FilmFieldVo(mtimeFieldT);
                    filmFieldVo.setLanguage(mtimeHallFilmInfoT.getFilmLanguage());
                    filmFieldVos.add(filmFieldVo);
                }
            }
            hallFilmInfoVo.setFilmFields(filmFieldVos);
            filmList.add(hallFilmInfoVo);
        }
        dataMap.put("filmList", filmList);
        baseReqVo.setData(dataMap);
        baseReqVo.setImgPre("http://img.meetingshop.cn/");
        baseReqVo.setStatus(0);
        return baseReqVo;
    }

    //获取当前影院信息
    @Override
    public BaseReqVo getFieldInfo(Integer cinemaId, Integer fieldId, String soldSeats) {
        BaseReqVo<Map> baseReqVo = new BaseReqVo<>();
        //获取影院信息
        HashMap<String, Object> dataMap = new HashMap<>();
        MtimeCinemaT cinema = mtimeCinemaTMapper.selectById(cinemaId);
        CinemaInfoVo cinemaInfo = conver2CinemaInfoVo(cinema);
        dataMap.put("cinemaInfo", cinemaInfo);
        //获取影片信息
        MtimeFieldT mtimeFieldT = mtimeFieldTMapper.selectById(fieldId);
        MtimeHallFilmInfoT mtimeHallFilmInfoTForSelect = new MtimeHallFilmInfoT();
        mtimeHallFilmInfoTForSelect.setFilmId(mtimeFieldT.getFilmId());
        MtimeHallFilmInfoT mtimeHallFilmInfoT = mtimeHallFilmInfoTMapper.selectOne(mtimeHallFilmInfoTForSelect);
        HallFilmInfoVo filmInfo = conver2FilmInfoVo(mtimeHallFilmInfoT);
        dataMap.put("filmInfo", filmInfo);
        //获取影院信息
        HallInfoVo hallInfo = getHallInfoVo(fieldId);
        hallInfo.setSoldSeats(soldSeats);
        dataMap.put("hallInfo", hallInfo);
        baseReqVo.setData(dataMap);
        baseReqVo.setStatus(0);
        baseReqVo.setImgPre("http://img.meetingshop.cn/");
        return baseReqVo;
    }

    @Override
    public String getCinemaNameById(int cinemaId) {
        String cinemaName = mtimeCinemaTMapper.getCinemaNameById(cinemaId);
        return cinemaName;
    }

    private HallInfoVo getHallInfoVo(Integer fieldId) {
        MtimeFieldT mtimeFieldT = new MtimeFieldT();
        mtimeFieldT.setUuid(fieldId);
        MtimeFieldT fieldInfo = mtimeFieldTMapper.selectOne(mtimeFieldT);
        MtimeHallDictT mtimeHallDictT = new MtimeHallDictT();
        mtimeHallDictT.setShowName(fieldInfo.getHallName());
        MtimeHallDictT hallDictT = mtimeHallDictTMapper.selectOne(mtimeHallDictT);
        HallInfoVo hallInfoVo = new HallInfoVo();
        hallInfoVo.setDiscountPrice("");
        hallInfoVo.setHallFieldId(fieldInfo.getUuid());
        hallInfoVo.setHallName(fieldInfo.getHallName());
        hallInfoVo.setPrice(fieldInfo.getPrice());
        hallInfoVo.setSeatFile(hallDictT.getSeatAddress());
        return hallInfoVo;
    }

    private FilmFieldVo conver2FilmFieldVo(MtimeFieldT mtimeFieldT) {
        FilmFieldVo filmFieldVo = new FilmFieldVo();
        if (mtimeFieldT == null){
            return filmFieldVo;
        }
        BeanUtils.copyProperties(mtimeFieldT, filmFieldVo);
        filmFieldVo.setPrice(Integer.toString(mtimeFieldT.getPrice()));
        filmFieldVo.setFieldId(mtimeFieldT.getUuid());
        return filmFieldVo;
    }

    private HallFilmInfoVo conver2FilmInfoVo(MtimeHallFilmInfoT mtimeHallFilmInfoT) {
        HallFilmInfoVo hallFilmInfoVo = new HallFilmInfoVo();
        if (mtimeHallFilmInfoT == null){
            return hallFilmInfoVo;
        }
        BeanUtils.copyProperties(mtimeHallFilmInfoT, hallFilmInfoVo);
        hallFilmInfoVo.setFilmType(mtimeHallFilmInfoT.getFilmLanguage());
        return hallFilmInfoVo;
    }

    private CinemaInfoVo conver2CinemaInfoVo(MtimeCinemaT cinema) {
        CinemaInfoVo cinemaInfo = new CinemaInfoVo();
        if (cinema == null){
            return cinemaInfo;
        }
        BeanUtils.copyProperties(cinema, cinemaInfo);
        cinemaInfo.setCinemaAdress(cinema.getCinemaAddress());
        cinemaInfo.setCinemaId(cinema.getUuid());
        cinemaInfo.setImgUrl(cinema.getImgAddress());
        return cinemaInfo;
    }
}
