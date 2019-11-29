package com.stylefeng.guns.rest.film;

import com.stylefeng.guns.rest.film.vo.FilmVo;
import com.stylefeng.guns.rest.film.vo.ShowFilmVo;

public interface FilmService {
    FilmVo get(Integer id);
    public ShowFilmVo getShowFilmVo(Integer filmId);
}
