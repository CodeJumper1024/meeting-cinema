package com.stylefeng.guns.rest.film;

import com.stylefeng.guns.rest.film.vo.BannerVo;
import com.stylefeng.guns.rest.film.vo.FilmRankVo;
import com.stylefeng.guns.rest.film.vo.FilmVo;
import com.stylefeng.guns.rest.film.vo.FilmsVo;

import java.util.List;

public interface FilmService {
    public List<BannerVo> getBanner();
    public FilmsVo getHotFilm(Integer count,Boolean isLimit);
    public FilmsVo getSoonFilm(Integer count,Boolean isLimit);
    public List<FilmRankVo> getRanking(Integer count);
    public List<FilmRankVo> getExpectRanking(Integer count);
    public List<FilmRankVo> getTop100(Integer count);
}
