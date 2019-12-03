package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import com.stylefeng.guns.rest.promo.PromoService;
import com.stylefeng.guns.rest.promo.vo.PromoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("promo")
public class PromoController {
    @Reference(interfaceClass = PromoService.class, check = false)
    PromoService promoService;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    RedisTemplate redisTemplate;
    @RequestMapping("getPromo")
    public BaseReqVo getPromo(Integer cinemaId){
        List<PromoVo> promoVo = promoService.getPromoVo(cinemaId);
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        if(promoVo == null){
            return BaseReqVo.queryFail();
        }else{
            baseReqVo.setData(promoVo);
            baseReqVo.setStatus(0);
        }
        return baseReqVo;
    }
    @PostMapping("createOrder")
    public BaseReqVo createOrder(Integer promoId, Integer amount, String promoToken, HttpServletRequest request){
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        String header = request.getHeader(jwtProperties.getHeader());
        String token = header.substring(7);
        Integer userId = (Integer) redisTemplate.opsForValue().get(token);
        int result = promoService.insert(promoId,amount,promoToken,userId);
        if(result != 1){
            return BaseReqVo.fail();
        }
        baseReqVo.setStatus(0);
        baseReqVo.setMsg("下单成功");
        return baseReqVo;
    }
}
