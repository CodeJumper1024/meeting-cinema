package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.film.FilmService;
import com.stylefeng.guns.rest.film.vo.FilmVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/27 19:35
 */
@RestController
@RequestMapping(value = "/film")
public class FilmController {

    @Reference(interfaceClass = FilmService.class, check = false)
    private FilmService filmService;

    @RequestMapping("get")
    public FilmVo get(Integer id){
        FilmVo filmVo = filmService.get(id);
        return filmVo;
    }
}
