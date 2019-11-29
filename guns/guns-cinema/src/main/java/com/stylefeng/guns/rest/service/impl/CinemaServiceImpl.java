package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.film.FilmService;
import org.springframework.stereotype.Component;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/28 22:16
 */
@Component
@Service(interfaceClass = FilmService.class)
public class CinemaServiceImpl implements CinemaService {
}
