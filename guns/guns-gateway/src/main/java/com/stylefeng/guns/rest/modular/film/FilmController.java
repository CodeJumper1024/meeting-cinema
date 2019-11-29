package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.film.FilmService;
import com.stylefeng.guns.rest.film.vo.*;
import com.stylefeng.guns.rest.vo.BaseReqVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/27 19:35
 */
@RestController
@RequestMapping(value = "/film")
public class FilmController {
    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = FilmService.class, check = false)
    private FilmService filmService;

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
    }
}
