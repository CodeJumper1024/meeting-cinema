package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.film.FilmService;
import com.stylefeng.guns.rest.film.vo.FilmVo;
import com.stylefeng.guns.rest.film.vo.ShowFilmVo;
import com.stylefeng.guns.rest.vo.BaseReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/27 19:35
 */
@RestController
@RequestMapping(value = "/film")
@Slf4j
public class FilmController {

    @Reference(interfaceClass = FilmService.class, check = false)
    private FilmService filmService;
    @RequestMapping("get")
    public FilmVo get(Integer id){
        FilmVo filmVo = filmService.get(id);
        return filmVo;
    }
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
    }
}
