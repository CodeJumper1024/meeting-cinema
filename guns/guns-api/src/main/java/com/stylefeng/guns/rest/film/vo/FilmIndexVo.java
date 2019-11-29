package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilmIndexVo implements Serializable {

    private static final long serialVersionUID = 3470314991790532736L;
    private List<BannerVo> banners;
    private List<FilmRankVo> expectRanking;
    private FilmsVo hotFilms;
    private List<FilmRankVo> boxRanking;
    private FilmsVo soonFilm;
    private List<FilmRankVo> top100;

}
