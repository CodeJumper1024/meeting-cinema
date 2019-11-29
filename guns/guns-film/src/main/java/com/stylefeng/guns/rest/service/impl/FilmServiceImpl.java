package com.stylefeng.guns.rest.service.impl;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.film.vo.DirectorVo;
import com.stylefeng.guns.rest.film.vo.RealActorsVo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.stylefeng.guns.rest.film.vo.ActorsVO;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.rest.film.vo.Info04VO;
import com.stylefeng.guns.rest.film.vo.imgVO;
import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.film.FilmService;
import com.stylefeng.guns.rest.film.vo.FilmVo;
import com.stylefeng.guns.rest.film.vo.ShowFilmVo;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.film.FilmService;
import com.stylefeng.guns.rest.film.vo.*;
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
    private MtimeFilmInfoTMapper mtimeFilmInfoTMapper;
    @Autowired
    private MtimeHallFilmInfoTMapper mtimeHallFilmInfoTMapper;
    @Autowired
    private MtimeSourceDictTMapper mtimeSourceDictTMapper;
    @Autowired
    private MtimeFilmActorTMapper mtimeFilmActorTMapper;
    @Autowired
    private MtimeActorTMapper mtimeActorTMapper;
    @Autowired
    private MtimeBannerTMapper mtimeBannerTMapper;
    @Autowired
    private MtimeCatDictTMapper mtimeCatDictTMapper;
    @Autowired
    private MtimeYearDictTMapper mtimeYearDictTMapper;

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
    public ShowFilmVo getShowFilmVo(Integer filmId) {
        ShowFilmVo showFilmVo = new ShowFilmVo();
        MtimeFilmT mtimeFilmT = mtimeFilmTMapper.selectById(filmId);
        MtimeFilmInfoT mtimeFilmInfoT = new MtimeFilmInfoT();
        mtimeFilmInfoT.setFilmId(filmId);
        mtimeFilmInfoT = mtimeFilmInfoTMapper.selectOne(mtimeFilmInfoT);
        showFilmVo.setFilmName(mtimeFilmT.getFilmName());
        showFilmVo.setFilmEnName(mtimeFilmInfoT.getFilmEnName());
        showFilmVo.setImgAddress(mtimeFilmT.getImgAddress());
        showFilmVo.setScore(mtimeFilmInfoT.getFilmScore());
        showFilmVo.setScoreNum(mtimeFilmInfoT.getFilmScoreNum());
        showFilmVo.setTotalBox(mtimeFilmT.getFilmBoxOffice());
        MtimeHallFilmInfoT mtimeHallFilmInfoT = new MtimeHallFilmInfoT();
        mtimeHallFilmInfoT.setFilmId(filmId);
        mtimeHallFilmInfoT = mtimeHallFilmInfoTMapper.selectOne(mtimeHallFilmInfoT);
        showFilmVo.setInfo01(mtimeHallFilmInfoT.getFilmCats());
        MtimeSourceDictT mtimeSourceDictT = mtimeSourceDictTMapper.selectById(mtimeFilmT.getFilmSource());
        String source=mtimeSourceDictT.getShowName();
        showFilmVo.setInfo02(source+"/"+mtimeHallFilmInfoT.getFilmLength()+"分钟");
        Date filmTime = mtimeFilmT.getFilmTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(filmTime);
        showFilmVo.setInfo03(dateString+" "+source+"上映");
        Info04VO info04VO = new Info04VO();
        info04VO.setBiography(mtimeFilmInfoT.getBiography());
        ActorsVO actorsVO = new ActorsVO();
        DirectorVo directorVo = new DirectorVo();
        MtimeActorT mtimeActorT1 = mtimeActorTMapper.selectById(mtimeFilmInfoT.getDirectorId());
        directorVo.setImgAddress(mtimeActorT1.getActorImg());
        directorVo.setDirectorName(mtimeActorT1.getActorName());
        actorsVO.setDirector(directorVo);
        List <RealActorsVo> actors = new ArrayList<>();
        EntityWrapper<MtimeFilmActorT> entityWrapper=new EntityWrapper<>();
        entityWrapper.eq("film_id",filmId);
        List<MtimeFilmActorT> mtimeFilmActorTS=mtimeFilmActorTMapper.selectList(entityWrapper);
        if(CollectionUtils.isEmpty(mtimeFilmActorTS)){
            mtimeFilmActorTS=null;
        }
        for (MtimeFilmActorT mtimeFilmActorT : mtimeFilmActorTS) {
            MtimeActorT mtimeActorT = mtimeActorTMapper.selectById(mtimeFilmActorT.getActorId());
            RealActorsVo actorsVo=new RealActorsVo();
            actorsVo.setImgAddress(mtimeActorT.getActorImg());
            actorsVo.setDirectorName(mtimeActorT.getActorName());
            actorsVo.setRoleName(mtimeFilmActorT.getRoleName());
            actors.add(actorsVo);
        }
        actorsVO.setActors(actors);
        info04VO.setActors(actorsVO);
        showFilmVo.setInfo04(info04VO);
        imgVO imgVO = new imgVO();
        imgVO.setMainImg(mtimeFilmT.getImgAddress());
        imgVO.setImg01("");
        imgVO.setImg02("");
        imgVO.setImg03("");
        imgVO.setImg04("");
        showFilmVo.setImgVO(imgVO);
        showFilmVo.setFilmId(filmId);
        return showFilmVo;
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

    @Override
    public List<CatInfoVo> getCat(Integer catId) {
        ArrayList<CatInfoVo> catInfoVos = new ArrayList<>();
        EntityWrapper<MtimeCatDictT> entityWrapper = new EntityWrapper<>();
        List<MtimeCatDictT> mtimeCatDictTS = mtimeCatDictTMapper.selectList(entityWrapper);
        if(CollectionUtils.isEmpty(mtimeCatDictTS)){
            return catInfoVos;
        }
        catInfoVos = convert2CatInfoVo(mtimeCatDictTS,catId);
        return catInfoVos;
    }

    private ArrayList<CatInfoVo> convert2CatInfoVo(List<MtimeCatDictT> mtimeCatDictTS, Integer catId) {
        ArrayList<CatInfoVo> catInfoVos = new ArrayList<>();
        for (MtimeCatDictT mtimeCatDictT : mtimeCatDictTS) {
            CatInfoVo catInfoVo = new CatInfoVo();
            catInfoVo.setCatId(mtimeCatDictT.getUuid());
            catInfoVo.setCatName(mtimeCatDictT.getShowName());
            if(mtimeCatDictT.getUuid() == catId){
                catInfoVo.setIsActive(true);
            }else {
                catInfoVo.setIsActive(false);
            }
            catInfoVos.add(catInfoVo);
        }
        return catInfoVos;
    }

    @Override
    public List<SourceInfoVo> getSource(Integer sourceId) {
        ArrayList<SourceInfoVo> sourceInfoVos = new ArrayList<>();
        EntityWrapper<MtimeSourceDictT> entityWrapper = new EntityWrapper<>();
        List<MtimeSourceDictT> mtimeSourceDictTS = mtimeSourceDictTMapper.selectList(entityWrapper);
        if(CollectionUtils.isEmpty(mtimeSourceDictTS)){
            return sourceInfoVos;
        }
        sourceInfoVos = convert2SourceInfoVo(mtimeSourceDictTS,sourceId);
        return sourceInfoVos;
    }

    private ArrayList<SourceInfoVo> convert2SourceInfoVo(List<MtimeSourceDictT> mtimeSourceDictTS, Integer sourceId) {
        ArrayList<SourceInfoVo> sourceInfoVos = new ArrayList<>();
        for (MtimeSourceDictT mtimeSourceDictT  : mtimeSourceDictTS) {
            SourceInfoVo sourceInfoVo = new SourceInfoVo();
            sourceInfoVo.setSourceId(mtimeSourceDictT.getUuid());
            sourceInfoVo.setSourceName(mtimeSourceDictT.getShowName());
            if(mtimeSourceDictT.getUuid() == sourceId){
                sourceInfoVo.setIsActive(true);
            }else {
                sourceInfoVo.setIsActive(false);
            }
            sourceInfoVos.add(sourceInfoVo);
        }
        return sourceInfoVos;
    }

    @Override
    public List<YearInfoVo> getYear(Integer yearId) {
        ArrayList<YearInfoVo> yearInfoVos = new ArrayList<>();
        EntityWrapper<MtimeYearDictT> entityWrapper = new EntityWrapper<>();
        List<MtimeYearDictT> mtimeYearDictTS = mtimeYearDictTMapper.selectList(entityWrapper);
        if(CollectionUtils.isEmpty(mtimeYearDictTS)){
            return yearInfoVos;
        }
        yearInfoVos = convert2YearInfoVo(mtimeYearDictTS,yearId);
        return yearInfoVos;
    }

    @Override
    public GetFilmsVoAndPages getFilm(Integer showType, Integer sortId, Integer catId, Integer sourceId, Integer yearId, Integer nowPage, Integer pageSize) {
        GetFilmsVoAndPages getFilmsVoAndPages = new GetFilmsVoAndPages();
        EntityWrapper<MtimeFilmT> entityWrapper=new EntityWrapper<>();
        entityWrapper.eq("film_status",showType);
        if(catId!=99) {
            entityWrapper.like("film_cats", "#" + catId);
        }
        if(yearId!=99) {
            entityWrapper.eq("film_date", yearId);
        }
        if(sourceId!=99) {
            entityWrapper.eq("film_source", sourceId);
        }
        Page<MtimeFilmT> page=null;
        if(sortId==1){
            page = new Page<>(nowPage,pageSize,"film_status");
        }else if(sortId==2){
            page = new Page<>(nowPage,pageSize,"film_time");
        }else{
            page = new Page<>(nowPage,pageSize,"film_score");
        }
        List<MtimeFilmT> mtimeFilmTS=new ArrayList<>();
        mtimeFilmTS = mtimeFilmTMapper.selectPage(page, entityWrapper);
        int totalPage=0;
        List<MtimeFilmT> mtimeFilmTS1=new ArrayList<>();
        Integer count = mtimeFilmTMapper.selectCount(entityWrapper);
        totalPage=count/pageSize;
        if (count % pageSize != 0) {
            totalPage++;
        }
        getFilmsVoAndPages.setTotalPage(totalPage+"");
        List<GetFilmsVO> getFilmsVOS=new ArrayList<>();
        if(CollectionUtils.isEmpty(mtimeFilmTS)){
            return getFilmsVoAndPages;
        }
        for (MtimeFilmT mtimeFilmT : mtimeFilmTS) {
            GetFilmsVO filmsVO = new GetFilmsVO();
            filmsVO.setBoxNum(mtimeFilmT.getFilmBoxOffice());
            filmsVO.setExpectNum(mtimeFilmT.getFilmPresalenum());
            filmsVO.setFilmId(mtimeFilmT.getUuid());
            filmsVO.setFilmName(mtimeFilmT.getFilmName());
            filmsVO.setFilmScore(mtimeFilmT.getFilmScore());
            filmsVO.setFilmType(mtimeFilmT.getFilmType());
            filmsVO.setImgAddress(mtimeFilmT.getImgAddress());
            Date filmTime = mtimeFilmT.getFilmTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(filmTime);
            filmsVO.setShowTime("dateString");
            getFilmsVOS.add(filmsVO);
        }
        getFilmsVoAndPages.setGetFilmsVOS(getFilmsVOS);
        return getFilmsVoAndPages;
    }

    private ArrayList<YearInfoVo> convert2YearInfoVo(List<MtimeYearDictT> mtimeYearDictTS, Integer yearId) {
        ArrayList<YearInfoVo> yearInfoVos = new ArrayList<>();
        for (MtimeYearDictT mtimeYearDictT  : mtimeYearDictTS) {
            YearInfoVo yearInfoVo = new YearInfoVo();
            yearInfoVo.setYearId(mtimeYearDictT.getUuid());
            yearInfoVo.setYearName(mtimeYearDictT.getShowName());
            if(mtimeYearDictT.getUuid() == yearId){
                yearInfoVo.setIsActive(true);
            }else {
                yearInfoVo.setIsActive(false);
            }
            yearInfoVos.add(yearInfoVo);
        }
        return yearInfoVos;
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
