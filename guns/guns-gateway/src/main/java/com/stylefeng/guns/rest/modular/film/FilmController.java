package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.film.FilmService;
<<<<<<< HEAD
import com.stylefeng.guns.rest.film.vo.FilmVo;
import com.stylefeng.guns.rest.film.vo.ShowFilmVo;
import com.stylefeng.guns.rest.vo.BaseReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
=======
import com.stylefeng.guns.rest.film.vo.*;
import com.stylefeng.guns.rest.vo.BaseReqVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
>>>>>>> e2ec2602bd3a24269e265c8a3372f61b978c0c5d

import java.util.List;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/27 19:35
 */
@RestController
@RequestMapping(value = "/film")
@Slf4j
public class FilmController {
    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = FilmService.class, check = false)
    private FilmService filmService;
<<<<<<< HEAD
    @RequestMapping(value = "/films/{filmId}",method = RequestMethod.GET)
    public BaseReqVo showFilm(@PathVariable Integer filmId,@RequestParam Integer searchType ){
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        ShowFilmVo showFilmVo = new ShowFilmVo();
        showFilmVo=filmService.getShowFilmVo(filmId);
        if(showFilmVo==null){
            baseReqVo.setStatus(1);
            baseReqVo.setMsg("查询失败，无影片可加载");
            return baseReqVo;
        }else {
            try{
            baseReqVo.setData(showFilmVo);
            baseReqVo.setMsg("成功");
            baseReqVo.setStatus(0);
            return baseReqVo;
            }catch (Exception e){
                baseReqVo.setMsg("系统出现异常，请联系管理员");
                baseReqVo.setStatus(999);
                return baseReqVo;
            }
        }
=======

    @RequestMapping(value = "/getIndex")
    public BaseReqVo getIndex(){
        BaseReqVo<Object> reqVo = new BaseReqVo<>();
        try {
            FilmIndexVo filmIndexVo = new FilmIndexVo();
            List<BannerVo> banner = filmService.getBanner();
            List<FilmRankVo> expectRanking = filmService.getExpectRanking(10);
            FilmsVo hotFilm = filmService.getHotFilm(8, true);
            List<FilmRankVo> ranking = filmService.getRanking(10);
            FilmsVo soonFilm = filmService.getSoonFilm(8, true);
            List<FilmRankVo> top100 = filmService.getTop100(10);
            filmIndexVo.setBanners(banner);
            filmIndexVo.setBoxRanking(ranking);
            filmIndexVo.setExpectRanking(expectRanking);
            filmIndexVo.setHotFilms(hotFilm);
            filmIndexVo.setSoonFilm(soonFilm);
            filmIndexVo.setTop100(top100);
            reqVo.setData(filmIndexVo);
            reqVo.setStatus(0);
            reqVo.setImgPre(IMG_PRE);
        }catch (Exception e){
            return BaseReqVo.queryFail();
        }
        return reqVo;
    }
    @RequestMapping(value = "/getConditionList")
    public BaseReqVo getConditionList(Integer catId,Integer sourceId,Integer yearId){
        BaseReqVo<Object> reqVo = new BaseReqVo<>();
        try {
            ConditionVo conditionVo = new ConditionVo();
            List<CatInfoVo> catInfo = filmService.getCat(catId);
            List<SourceInfoVo> sourceInfo = filmService.getSource(sourceId);
            List<YearInfoVo> yearInfo = filmService.getYear(yearId);
            conditionVo.setCatInfo(catInfo);
            conditionVo.setSourceInfo(sourceInfo);
            conditionVo.setYearInfo(yearInfo);
            reqVo.setData(conditionVo);
            reqVo.setStatus(0);
        }catch (Exception e){
            return BaseReqVo.queryFail();
        }
        return reqVo;
>>>>>>> e2ec2602bd3a24269e265c8a3372f61b978c0c5d
    }
}
