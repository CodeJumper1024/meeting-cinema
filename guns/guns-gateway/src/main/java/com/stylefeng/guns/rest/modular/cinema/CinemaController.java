package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.cinema.vo.*;
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
    @RequestMapping("getFields")
    public BaseReqVo getFields(Integer cinemaId){
        BaseReqVo baseReqVo = cinemaService.getFields(cinemaId);
        return baseReqVo;
    }

    @RequestMapping("getFieldInfo")
    public BaseReqVo getFieldInfo(Integer cinemaId, Integer fieldId){
        BaseReqVo baseReqVo = cinemaService.getFieldInfo(cinemaId, fieldId);
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
        List<HallTypeVO> hallTypeVOList = cinemaService.getHallTypesById(hallType);
        dataVO.setAreaList(areaVOList);
        dataVO.setBrandList(brandVOList);
        dataVO.setHallTypeList(hallTypeVOList);

        baseReqVo.setData(dataVO);
        baseReqVo.setStatus(0);
        baseReqVo.setImgPre("");
        baseReqVo.setMsg("");
        return baseReqVo;
    }

    @RequestMapping("getCinemas")
    public BaseReqVo getCinemas(Integer brandId,Integer hallType,Integer areaId,Integer pageSize,
                                Integer nowPage){
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        if(brandId==null||hallType==null||areaId==null||pageSize==null||nowPage==null){
            return baseReqVo.queryFail();
        }
        CinemaListVO cinemaListVO = cinemaService.getCinemas(brandId,hallType,areaId,pageSize,nowPage);
        baseReqVo.setData(cinemaListVO.getCinemaVO());
        baseReqVo.setStatus(0);
        baseReqVo.setImgPre("http://img.meetingshop.cn/");
        baseReqVo.setNowPage(nowPage.toString());
        baseReqVo.setTotalPage(cinemaListVO.getTotalPage());
        return baseReqVo;
    }
}
