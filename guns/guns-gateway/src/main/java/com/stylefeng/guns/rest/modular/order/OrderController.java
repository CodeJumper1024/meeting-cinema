package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.alipay.AlipayService;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import com.stylefeng.guns.rest.order.OrderService;
import com.stylefeng.guns.rest.order.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("order")
public class OrderController {

    @Reference(interfaceClass = OrderService.class, check = false, timeout = 5000)
    OrderService orderService;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    RedisTemplate redisTemplate;

    @Reference(interfaceClass = AlipayService.class, check = false)
    AlipayService alipayService;

    @PostMapping("buyTickets")
    public BaseReqVo ticketsBuying(String fieldId, String soldSeats, String seatsName, HttpServletRequest request) {

        String[] soldSeat = soldSeats.split(",");
        BaseReqVo baseReqVo = new BaseReqVo();

        String header = request.getHeader(jwtProperties.getHeader());
        String token = header.substring(7);
        Integer userId = (Integer) redisTemplate.opsForValue().get(token);

        Boolean isSoldSeats = null;
        for (String seatId : soldSeat) {
            Boolean isTrueSeats = orderService.isTrueSeats(fieldId, seatId);
            if (isTrueSeats) {
                isSoldSeats = orderService.isSoldSeats(fieldId, seatId);
            }
        }
        if (isSoldSeats) {
            OrderVo orderVo = orderService.saveOrderInfo(fieldId, soldSeats, seatsName, userId);
            baseReqVo.setStatus(0);
            baseReqVo.setData(orderVo);
            baseReqVo.setMsg("");
        }
        return baseReqVo;
    }

    @RequestMapping("getPayInfo")
    public BaseReqVo getPayInfo(String orderId){
        BaseReqVo baseReqVo = alipayService.getPayInfo(orderId);
        return baseReqVo;
    }
}
