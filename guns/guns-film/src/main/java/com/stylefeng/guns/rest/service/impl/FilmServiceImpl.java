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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
    @Override
    public FilmVo get(Integer id) {
        MtimeFilmT mtimeFilmT = mtimeFilmTMapper.selectById(id);
        FilmVo filmVo = conver2FilmVo(mtimeFilmT);
        return filmVo;
    }
    private FilmVo conver2FilmVo(MtimeFilmT mtimeFilmT) {
        FilmVo filmVo = new FilmVo();
        if (mtimeFilmT == null){
            return filmVo;
        }
        BeanUtils.copyProperties(mtimeFilmT, filmVo);
        return filmVo;
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
}
