package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.alipay.AlipayService;
import com.stylefeng.guns.rest.alipay.vo.GetPayResultVo;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import com.stylefeng.guns.rest.order.OrderService;
import com.stylefeng.guns.rest.order.vo.OrderListVo;
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
        if (!isSoldSeats) {
            OrderVo orderVo = orderService.saveOrderInfo(fieldId, soldSeats, seatsName, userId);
            baseReqVo.setStatus(0);
            baseReqVo.setData(orderVo);
        } else {
            baseReqVo.setStatus(1);
            baseReqVo.setMsg("该座位已被购买");
        }
        return baseReqVo;
    }

    @PostMapping("getOrderInfo")
    public BaseReqVo getOrderInfo(Integer nowPage,Integer pageSize,HttpServletRequest request) {
        BaseReqVo baseReqVo = new BaseReqVo();

        String header = request.getHeader(jwtProperties.getHeader());
        String token = header.substring(7);
        Integer userId = (Integer) redisTemplate.opsForValue().get(token);

        OrderListVo orderListVo = orderService.getOrderByUserId(userId, nowPage, pageSize);
        if (orderListVo.getOrderVoList().size() == 0) {
            return baseReqVo.queryFail();
        }
        long total = orderListVo.getTotal();
        long pages = total / pageSize;
        if (total % pageSize != 0) {
            pages++;
        }
        baseReqVo.setData(orderListVo.getOrderVoList());
        baseReqVo.setTotalPage(pages + "");
        baseReqVo.setNowPage(nowPage + "");
        baseReqVo.setImgPre("");
        baseReqVo.setStatus(0);
        baseReqVo.setMsg("");
        return baseReqVo;
    }
    @RequestMapping("getPayInfo")
    public BaseReqVo getPayInfo(String orderId,HttpServletRequest request){
        BaseReqVo baseReqVo = alipayService.getPayInfo(orderId);
        //int serverPort = request.getServerPort();
        int localPort = request.getLocalPort();
        String imgPre = "http://192.168.4.65:"  +  localPort + "/";
        baseReqVo.setImgPre(imgPre);
        return baseReqVo;
    }
    @RequestMapping("getPayResult")
    public BaseReqVo getPayResult(String orderId, Integer tryNums){
        BaseReqVo baseReqVo=new BaseReqVo();
        GetPayResultVo getPayResultVo = new GetPayResultVo();
        if(tryNums==4) {
            getPayResultVo = alipayService.updateFail(orderId);
            if (getPayResultVo == null) {
                return BaseReqVo.queryFail();
            }
                baseReqVo.setData(getPayResultVo);
                baseReqVo.setImgPre("http://img.meetingshop.cn/");
                baseReqVo.setMsg("失败");
        }else{
                getPayResultVo = alipayService.getPayResult(orderId);
            if (getPayResultVo == null) {
                return BaseReqVo.queryFail();
            }
                baseReqVo.setData(getPayResultVo);
                baseReqVo.setImgPre("http://img.meetingshop.cn/");
                baseReqVo.setMsg("成功");

        }
        return baseReqVo;
    }
}

