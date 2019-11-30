package com.stylefeng.guns.rest.modular.auth.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.rest.common.exception.BizExceptionEnum;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthRequest;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthResponse;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.modular.auth.validator.IReqValidator;
import com.stylefeng.guns.rest.user.UserServiceAPI;
import com.stylefeng.guns.rest.user.vo.UserInfoVo;
import com.stylefeng.guns.rest.vo.BaseReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 请求验证的
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:22
 */
@RestController
public class AuthController {

    @Reference(interfaceClass = UserServiceAPI.class, check = false)
    UserServiceAPI userServiceAPI;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Resource(name = "simpleValidator")
    private IReqValidator reqValidator;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "${jwt.auth-path}")
    public BaseReqVo createAuthenticationToken(AuthRequest authRequest, HttpServletResponse response) {

        BaseReqVo baseReqVo = new BaseReqVo();

        //对账号密码进行验证
        boolean validate = userServiceAPI.login(authRequest.getUserName(), authRequest.getPassword());

        if (validate) {
            //登录成功，生成randomkey和token并返回
            final String randomKey = jwtTokenUtil.getRandomKey();
            final String token = jwtTokenUtil.generateToken(authRequest.getUserName(), randomKey);

            //取出用户信息
            UserInfoVo userInfo = userServiceAPI.getUserInfoByName(authRequest.getUserName());
            Integer uuid = userInfo.getUuid();
            //将token和用户信息保存到redis 设置token在redis的保存时间为5分钟
            redisTemplate.opsForValue().set(token,uuid);
            redisTemplate.expire(token,5*60, TimeUnit.SECONDS);

            //将token和randomkey返回给前端
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("randomKey",randomKey);
            dataMap.put("token",token);
            baseReqVo.setStatus(0);
            baseReqVo.setMsg("登录成功");
            baseReqVo.setData(dataMap);

        } else {
            //登录失败
            baseReqVo.setStatus(1);
            baseReqVo.setMsg("用户名或密码错误");
        }

        return baseReqVo;
    }
}
