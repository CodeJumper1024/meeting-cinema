package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.rest.Vo.CinemaInfoVo;
import com.stylefeng.guns.rest.Vo.FilmFieldVo;
import com.stylefeng.guns.rest.Vo.HallFilmInfoVo;
import com.stylefeng.guns.rest.Vo.HallInfoVo;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeCinemaTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeFieldTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeHallDictTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeHallFilmInfoTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeCinemaT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeFieldT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeHallDictT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeHallFilmInfoT;
import com.stylefeng.guns.rest.vo.BaseReqVo;
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
    MtimeCinemaTMapper mtimeCinemaTMapper;

    @Autowired
    MtimeFieldTMapper mtimeFieldTMapper;

    @Autowired
    MtimeHallFilmInfoTMapper mtimeHallFilmInfoTMapper;

    @Autowired
    MtimeHallDictTMapper mtimeHallDictTMapper;

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
    public BaseReqVo getFieldInfo(Integer cinemaId, Integer fieldId) {
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
        dataMap.put("hallInfo", hallInfo);
        baseReqVo.setData(dataMap);
        baseReqVo.setStatus(0);
        baseReqVo.setImgPre("http://img.meetingshop.cn/");
        return baseReqVo;
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
        hallInfoVo.setSoldSeats("50");//后面需要联合订单查询
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
