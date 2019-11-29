package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeBannerTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeFilmTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeBannerT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeFilmT;
import com.stylefeng.guns.rest.film.FilmService;
import com.stylefeng.guns.rest.film.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/27 17:25
 */
@Component
@Service(interfaceClass = FilmService.class)
public class FilmServiceImpl implements FilmService {

    @Autowired
    private MtimeFilmTMapper mtimeFilmTMapper;
    @Autowired
    private MtimeBannerTMapper mtimeBannerTMapper;

    @Override
    public List<BannerVo> getBanner() {
        ArrayList<BannerVo> list = new ArrayList<>();
        EntityWrapper<MtimeBannerT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("is_valid",1);
        List<MtimeBannerT> bannerTS = mtimeBannerTMapper.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(bannerTS)) {
            return list;
        }
        for (MtimeBannerT bannerT : bannerTS) {
            BannerVo bannerVo = new BannerVo();
            bannerVo.setBannerId(bannerT.getUuid());
            bannerVo.setBannerAddress(bannerT.getBannerAddress());
            bannerVo.setBannerUrl(bannerT.getBannerUrl());
            list.add(bannerVo);
        }
        return list;
    }

    @Override
    public FilmsVo getHotFilm(Integer count, Boolean isLimit) {
        FilmsVo filmsVo = new FilmsVo();
        EntityWrapper<MtimeFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status",1);
        Integer needCount = mtimeFilmTMapper.selectCount(entityWrapper);
        List<MtimeFilmT> mtimeFilmTS;
        if (isLimit) {
            Page page = new Page(1, count);
            mtimeFilmTS = mtimeFilmTMapper.selectPage(page,entityWrapper);
        } else {
            mtimeFilmTS = mtimeFilmTMapper.selectList(entityWrapper);
        }
        List<FilmInfoVo> list = convert2FilmInfoVo(mtimeFilmTS);
        filmsVo.setFilmNum(needCount);
        filmsVo.setFilmInfo(list);
        return filmsVo;
    }

    private List<FilmInfoVo> convert2FilmInfoVo(List<MtimeFilmT> mtimeFilmTS) {
        List<FilmInfoVo> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(mtimeFilmTS)) {
            return list;
        }
        for (MtimeFilmT mtimeFilmT : mtimeFilmTS) {
            FilmInfoVo filmInfoVo = new FilmInfoVo();
            filmInfoVo.setFilmId(mtimeFilmT.getUuid());
            filmInfoVo.setFilmName(mtimeFilmT.getFilmName());
            filmInfoVo.setImgAddress(mtimeFilmT.getImgAddress());
            filmInfoVo.setFilmType(mtimeFilmT.getFilmType());
            filmInfoVo.setFilmScore(mtimeFilmT.getFilmScore());
            list.add(filmInfoVo);
        }
        return list;
    }

    @Override
    public FilmsVo getSoonFilm(Integer count, Boolean isLimit) {
        FilmsVo filmsVo = new FilmsVo();
        EntityWrapper<MtimeFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status",2);
        Integer needCount = mtimeFilmTMapper.selectCount(entityWrapper);
        List<MtimeFilmT> mtimeFilmTS;
        if (isLimit) {
            Page page = new Page(1, count);
            mtimeFilmTS = mtimeFilmTMapper.selectPage(page,entityWrapper);
        } else {
            mtimeFilmTS = mtimeFilmTMapper.selectList(entityWrapper);
        }
        List<FilmInfoVo> list = convert2FilmInfoVo2(mtimeFilmTS);
        filmsVo.setFilmNum(needCount);
        filmsVo.setFilmInfo(list);
        return filmsVo;
    }

    private List<FilmInfoVo> convert2FilmInfoVo2(List<MtimeFilmT> mtimeFilmTS) {
        List<FilmInfoVo> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(mtimeFilmTS)) {
            return list;
        }
        for (MtimeFilmT mtimeFilmT : mtimeFilmTS) {
            FilmInfoVo filmInfoVo = new FilmInfoVo();
            filmInfoVo.setFilmId(mtimeFilmT.getUuid());
            filmInfoVo.setFilmName(mtimeFilmT.getFilmName());
            filmInfoVo.setImgAddress(mtimeFilmT.getImgAddress());
            filmInfoVo.setFilmType(mtimeFilmT.getFilmType());
            filmInfoVo.setExpectNum(mtimeFilmT.getFilmPresalenum());
            filmInfoVo.setShowTime(mtimeFilmT.getFilmTime());
            list.add(filmInfoVo);
        }
        return list;
    }

    @Override
    public List<FilmRankVo> getRanking(Integer count) {
        List<FilmRankVo> vos = new ArrayList<>();
        Page<MtimeFilmT> page = new Page<>(1,count,"film_box_office",false);
        EntityWrapper<MtimeFilmT> entityWrapper = new EntityWrapper<>();
        List<MtimeFilmT> mtimeFilmTS = mtimeFilmTMapper.selectPage(page, entityWrapper);
        if(CollectionUtils.isEmpty(mtimeFilmTS)){
            return vos;
        }
        vos = convert2RankVo1(mtimeFilmTS);
        return vos;
    }

    private List<FilmRankVo> convert2RankVo1(List<MtimeFilmT> mtimeFilmTS) {
        ArrayList<FilmRankVo> rankVos = new ArrayList<>();
        for (MtimeFilmT mtimeFilmT : mtimeFilmTS) {
            FilmRankVo filmRankVo = new FilmRankVo();
            filmRankVo.setFilmId(mtimeFilmT.getUuid());
            filmRankVo.setFilmName(mtimeFilmT.getFilmName());
            filmRankVo.setImgAddress(mtimeFilmT.getImgAddress());
            filmRankVo.setBoxNum(mtimeFilmT.getFilmBoxOffice());
            rankVos.add(filmRankVo);
        }
        return rankVos;
    }

    @Override
    public List<FilmRankVo> getExpectRanking(Integer count) {
        List<FilmRankVo> vos = new ArrayList<>();
        Page<MtimeFilmT> page = new Page<>(1,count,"film_preSaleNum",false);
        EntityWrapper<MtimeFilmT> entityWrapper = new EntityWrapper<>();
        List<MtimeFilmT> mtimeFilmTS = mtimeFilmTMapper.selectPage(page, entityWrapper);
        if(CollectionUtils.isEmpty(mtimeFilmTS)){
            return vos;
        }
        vos = convert2RankVo2(mtimeFilmTS);
        return vos;
    }

    private List<FilmRankVo> convert2RankVo2(List<MtimeFilmT> mtimeFilmTS) {
        ArrayList<FilmRankVo> rankVos = new ArrayList<>();
        for (MtimeFilmT mtimeFilmT : mtimeFilmTS) {
            FilmRankVo filmRankVo = new FilmRankVo();
            filmRankVo.setFilmId(mtimeFilmT.getUuid());
            filmRankVo.setFilmName(mtimeFilmT.getFilmName());
            filmRankVo.setImgAddress(mtimeFilmT.getImgAddress());
            filmRankVo.setExpectNum(mtimeFilmT.getFilmPresalenum());
            rankVos.add(filmRankVo);
        }
        return rankVos;
    }

    @Override
    public List<FilmRankVo> getTop100(Integer count) {
        List<FilmRankVo> vos = new ArrayList<>();
        Page<MtimeFilmT> page = new Page<>(1,count,"film_score",false);
        EntityWrapper<MtimeFilmT> entityWrapper = new EntityWrapper<>();
        List<MtimeFilmT> mtimeFilmTS = mtimeFilmTMapper.selectPage(page, entityWrapper);
        if(CollectionUtils.isEmpty(mtimeFilmTS)){
            return vos;
        }
        vos = convert2RankVo3(mtimeFilmTS);
        return vos;
    }

    private List<FilmRankVo> convert2RankVo3(List<MtimeFilmT> mtimeFilmTS) {
        ArrayList<FilmRankVo> rankVos = new ArrayList<>();
        for (MtimeFilmT mtimeFilmT : mtimeFilmTS) {
            FilmRankVo filmRankVo = new FilmRankVo();
            filmRankVo.setFilmId(mtimeFilmT.getUuid());
            filmRankVo.setFilmName(mtimeFilmT.getFilmName());
            filmRankVo.setImgAddress(mtimeFilmT.getImgAddress());
            filmRankVo.setScore(mtimeFilmT.getFilmScore());
            rankVos.add(filmRankVo);
        }
        return rankVos;
    }

}
