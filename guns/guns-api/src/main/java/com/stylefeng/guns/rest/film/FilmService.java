package com.stylefeng.guns.rest.film;
import com.stylefeng.guns.rest.film.vo.BannerVo;
import com.stylefeng.guns.rest.film.vo.FilmRankVo;
import com.stylefeng.guns.rest.film.vo.FilmsVo;
import com.stylefeng.guns.rest.film.vo.ShowFilmVo;
import com.stylefeng.guns.rest.film.vo.*;

import java.util.List;

public interface FilmService {
    // getIndex接口中的方法
    public List<BannerVo> getBanner();
    public FilmsVo getHotFilm(Integer count, Boolean isLimit);
    public FilmsVo getSoonFilm(Integer count,Boolean isLimit);
    public List<FilmRankVo> getRanking(Integer count);
    public List<FilmRankVo> getExpectRanking(Integer count);
    public List<FilmRankVo> getTop100(Integer count);
    public ShowFilmVo getShowFilmVo(Integer filmId);
    // getConditionList接口中的方法
    public List<CatInfoVo> getCat(Integer catId);
    public List<SourceInfoVo> getSource(Integer sourceId);
    public List<YearInfoVo> getYear(Integer yearId);
    public GetFilmsVoAndPages getFilm(Integer showType, Integer sortId,
                                    Integer catId,Integer sourceId,
                                    Integer yearId,Integer nowPage,
                                    Integer pageSize);
}
