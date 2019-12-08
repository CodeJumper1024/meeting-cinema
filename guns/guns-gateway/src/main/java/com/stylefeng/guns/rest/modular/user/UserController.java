package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import com.stylefeng.guns.rest.user.UserServiceAPI;
import com.stylefeng.guns.rest.user.vo.UserInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("user")
public class UserController {

    @Reference(interfaceClass = UserServiceAPI.class, check = false)
    UserServiceAPI userServiceAPI;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("check")
    public BaseReqVo usernameCheck(String username) {
        BaseReqVo baseReqVo = new BaseReqVo();
        int status = userServiceAPI.checkUsername(username);
        if (status == 1) {
            baseReqVo.setStatus(1);
            baseReqVo.setMsg("用户已存在");
        } else if (status == 0) {
            baseReqVo.setStatus(0);
            baseReqVo.setMsg("注册成功");
        } else {
            baseReqVo.setStatus(999);
            baseReqVo.setMsg("系统出现异常，请联系管理员");
        }
        return baseReqVo;
    }

    @PostMapping("updateUserInfo")
    public BaseReqVo userInfoUpdate(UserInfoModel userInfoModel) {
        BaseReqVo baseReqVo = new BaseReqVo();
        int status = userServiceAPI.updateUserInfo(userInfoModel);
        UserInfoModel userInfo = userServiceAPI.queryUserInfo(userInfoModel.getUuid());
        if (status == 1) {
            baseReqVo.setStatus(0);
            baseReqVo.setData(userInfo);
        } else if (status == 0) {
            baseReqVo.setStatus(1);
            baseReqVo.setMsg("用户信息修改失败");
        } else {
            baseReqVo.setStatus(999);
            baseReqVo.setMsg("系统出现异常，请联系管理员");
        }
        return baseReqVo;
    }

    @RequestMapping("getUserInfo")
    public BaseReqVo userInfoGet(HttpServletRequest request) {
        BaseReqVo baseReqVo = new BaseReqVo();
        String header = request.getHeader(jwtProperties.getHeader());
        String token = header.substring(7);
        Integer uuid = (Integer) redisTemplate.opsForValue().get(token);
        if (uuid != null) {
            UserInfoModel userInfo = userServiceAPI.queryUserInfo(uuid);
            baseReqVo.setStatus(0);
            baseReqVo.setData(userInfo);
        } else if (uuid == null) {
            baseReqVo.setStatus(1);
            baseReqVo.setMsg("查询失败，用户尚未登陆");
        } else {
            baseReqVo.setStatus(999);
            baseReqVo.setMsg("系统出现异常，请联系管理员");
        }
        return baseReqVo;
    }

    @RequestMapping("register")
    public BaseReqVo register(String username, String password, String email, String mobile, String address) {
        BaseReqVo baseReqVo = new BaseReqVo();
        int result = userServiceAPI.register(username, password, email, mobile, address);
        if (result == 0) {
            baseReqVo.setMsg("注册成功");
            baseReqVo.setStatus(0);
        } else if (result == 1) {
            baseReqVo.setMsg("用户已存在");
            baseReqVo.setStatus(1);
        } else if (result == 999) {
            baseReqVo.setMsg("系统出现异常，请联系管理员");
            baseReqVo.setStatus(999);
        }
        return baseReqVo;
    }

    @RequestMapping("logout")
    public BaseReqVo logout(HttpServletRequest request) {
        BaseReqVo baseReqVo = new BaseReqVo();
        String requestHeader = request.getHeader(jwtProperties.getHeader());
        String token = requestHeader.substring(7);
        Object o = redisTemplate.opsForValue().get(token);
        if (o == null) {
            baseReqVo.setStatus(0);
            baseReqVo.setMsg("成功退出");
            return baseReqVo;
        }
        Boolean delete = redisTemplate.delete(token);
        if (delete) {
            //token删除成功
            baseReqVo.setStatus(0);
            baseReqVo.setMsg("成功退出");
        } else {
            baseReqVo.setStatus(1);
            baseReqVo.setMsg("退出失败，用户尚未登陆");
        }
        return baseReqVo;
    }

}

