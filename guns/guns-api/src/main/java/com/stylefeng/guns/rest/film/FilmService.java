package com.stylefeng.guns.rest.film;

import com.stylefeng.guns.rest.film.vo.*;

import java.util.List;

public interface FilmService {
    // getIndex接口中的方法
    public List<BannerVo> getBanner();
    public FilmsVo getHotFilm(Integer count,Boolean isLimit);
    public FilmsVo getSoonFilm(Integer count,Boolean isLimit);
    public List<FilmRankVo> getRanking(Integer count);
    public List<FilmRankVo> getExpectRanking(Integer count);
    public List<FilmRankVo> getTop100(Integer count);
    // getConditionList接口中的方法
    public List<CatInfoVo> getCat(Integer catId);
    public List<SourceInfoVo> getSource(Integer sourceId);
    public List<YearInfoVo> getYear(Integer yearId);
}
