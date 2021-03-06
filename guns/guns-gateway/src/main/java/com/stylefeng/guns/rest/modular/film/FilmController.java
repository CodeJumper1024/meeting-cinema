package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.film.FilmService;
import com.stylefeng.guns.rest.film.vo.*;
import com.stylefeng.guns.rest.modular.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

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

    public static final String FILM_INDEX_VO = "film_index_vo";

    public static final String CACHE_INDEX_VO = "cache_index_vo";

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    CacheService cacheService;


    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = FilmService.class, check = false)
    private FilmService filmService;
    @RequestMapping(value = "/films/{filmId}",method = RequestMethod.GET)
    public BaseReqVo showFilm(@PathVariable Integer filmId,@RequestParam Integer searchType ) {
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        ShowFilmVo showFilmVo = new ShowFilmVo();
        showFilmVo = filmService.getShowFilmVo(filmId);
        if (showFilmVo == null) {
            return BaseReqVo.queryFail();
        } else {
            try {
                baseReqVo.setData(showFilmVo);
                baseReqVo.setMsg("成功");
                baseReqVo.setStatus(0);
                baseReqVo.setImgPre("http://img.meetingshop.cn/");
                return baseReqVo;
            } catch (Exception e) {
                return BaseReqVo.fail();
            }
        }
    }

    @RequestMapping(value = "/getFilms")
    public BaseReqVo getFilms(@RequestParam Integer showType, @RequestParam Integer sortId,
                              @RequestParam Integer catId,@RequestParam Integer sourceId,
                              @RequestParam Integer yearId,@RequestParam Integer offset,
                              @RequestParam Integer pageSize){
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        Integer nowPage=offset;
        GetFilmsVoAndPages film = filmService.getFilm(showType, sortId, catId, sourceId, yearId, nowPage, pageSize);
        if (film == null) {
            return BaseReqVo.queryFail();
        } else {
            try {
                baseReqVo.setData(film.getGetFilmsVOS());
                baseReqVo.setMsg("成功");
                baseReqVo.setStatus(0);
                baseReqVo.setNowPage(nowPage+"");
                baseReqVo.setTotalPage(film.getTotalPage());
                baseReqVo.setImgPre("http://img.meetingshop.cn/");
                return baseReqVo;
            } catch (Exception e) {
                return BaseReqVo.fail();
            }
        }
    }
    @RequestMapping(value = "/getIndex")
    public BaseReqVo getIndex(){
        BaseReqVo<Object> reqVo = new BaseReqVo<>();
        //从本地缓存获取indexVo
        BaseReqVo cacheReqVo = (BaseReqVo)cacheService.get(CACHE_INDEX_VO);
        if (cacheReqVo != null){
            System.out.println("cache");
            reqVo = cacheReqVo;
            return reqVo;
        }
        //从redis缓存中获取indexVo
        BaseReqVo redisReqVo = (BaseReqVo)redisTemplate.opsForValue().get(FILM_INDEX_VO);
        if (redisReqVo != null){
            System.out.println("redis");
            cacheService.put(CACHE_INDEX_VO, redisReqVo);
            reqVo = redisReqVo;
            return reqVo;
        }
        try {
            FilmIndexVo filmIndexVo = new FilmIndexVo();
            List<BannerVo> banners = filmService.getBanner();
            List<FilmRankVo> expectRanking = filmService.getExpectRanking(10);
            FilmsVo hotFilms = filmService.getHotFilm(8, true);
            List<FilmRankVo> boxRanking = filmService.getRanking(10);
            FilmsVo soonFilms = filmService.getSoonFilm(8, true);
            List<FilmRankVo> top100 = filmService.getTop100(10);
            filmIndexVo.setBanners(banners);
            filmIndexVo.setBoxRanking(boxRanking);
            filmIndexVo.setExpectRanking(expectRanking);
            filmIndexVo.setHotFilms(hotFilms);
            filmIndexVo.setSoonFilms(soonFilms);
            filmIndexVo.setTop100(top100);
            reqVo.setData(filmIndexVo);
            reqVo.setStatus(0);
            reqVo.setImgPre(IMG_PRE);
        }catch (Exception e){
            return BaseReqVo.queryFail();
        }
        redisTemplate.opsForValue().set(FILM_INDEX_VO, reqVo);
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
