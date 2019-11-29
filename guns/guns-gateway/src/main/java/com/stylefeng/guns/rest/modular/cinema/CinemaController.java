package com.stylefeng.guns.rest.modular.cinema;

<<<<<<< HEAD
=======
import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.vo.BaseReqVo;
>>>>>>> 0e8ae5bbcdbf6a6e7e2898fd694c1ce25536346f
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/28 22:16
 */
@RestController
@RequestMapping("cinema")
public class CinemaController {

<<<<<<< HEAD
=======
    @Reference(interfaceClass = CinemaService.class, check = false)
    CinemaService cinemaService;

    @RequestMapping("getFields")
    public BaseReqVo getFields(Integer cinemaId){
        return null;
    }
>>>>>>> 0e8ae5bbcdbf6a6e7e2898fd694c1ce25536346f
}
