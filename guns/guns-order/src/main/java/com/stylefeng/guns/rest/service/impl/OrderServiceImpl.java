package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.order.OrderService;
import org.springframework.stereotype.Component;

@Component
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {
}
