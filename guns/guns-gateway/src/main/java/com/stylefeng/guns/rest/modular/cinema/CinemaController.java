package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.cinema.vo.*;
import com.stylefeng.guns.rest.order.OrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/28 22:16
 */
@RestController
@RequestMapping("cinema/")
public class CinemaController {
    @Reference(interfaceClass = CinemaService.class, check = false)
    CinemaService cinemaService;

    @Reference(interfaceClass = OrderService.class, check = false)
    OrderService orderService;

    @RequestMapping("getFields")
    public BaseReqVo getFields(Integer cinemaId){
        BaseReqVo baseReqVo = cinemaService.getFields(cinemaId);
        return baseReqVo;
    }

    @RequestMapping("getFieldInfo")
    public BaseReqVo getFieldInfo(Integer cinemaId, Integer fieldId){
        String soldSeats = orderService.getSoldSeats(fieldId);
        BaseReqVo baseReqVo = cinemaService.getFieldInfo(cinemaId, fieldId, soldSeats);
        return baseReqVo;
    }

    @RequestMapping("getCondition")
    public BaseReqVo getCondition(Integer brandId, Integer hallType, Integer areaId){
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        DataVO dataVO = new DataVO();
        if(brandId ==null || hallType == null || areaId == null){
            return baseReqVo.queryFail();
        }
        List<AreaVO> areaVOList = cinemaService.getAreasById(areaId);
        List<BrandVO> brandVOList = cinemaService.getBrandsById(brandId);
        List<HalltypeVO> HalltypeVOList = cinemaService.getHallTypesById(hallType);
        dataVO.setAreaList(areaVOList);
        dataVO.setBrandList(brandVOList);
        dataVO.setHalltypeList(HalltypeVOList);

        baseReqVo.setData(dataVO);
        baseReqVo.setStatus(0);
        baseReqVo.setImgPre("");
        baseReqVo.setMsg("");
        return baseReqVo;
    }

    @RequestMapping("getCinemas")
    public BaseReqVo getCinemas(Integer brandId,Integer halltypeId,Integer hallType,Integer areaId,
                                Integer pageSize,
                                Integer nowPage){
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        CinemaListVO cinemaListVO = cinemaService.getCinemas(brandId,halltypeId,hallType,areaId,
                pageSize,
                nowPage);
        if(CollectionUtils.isEmpty(cinemaListVO.getCinemaVO())){
            return baseReqVo.queryFail();
        }
        baseReqVo.setData(cinemaListVO.getCinemaVO());
        baseReqVo.setStatus(0);
        baseReqVo.setImgPre("http://img.meetingshop.cn/");
        baseReqVo.setNowPage(nowPage.toString());
        baseReqVo.setTotalPage(cinemaListVO.getTotalPage());
        return baseReqVo;
    }
}
