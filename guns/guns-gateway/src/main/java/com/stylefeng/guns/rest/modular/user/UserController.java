package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import com.stylefeng.guns.rest.user.UserServiceAPI;
import com.stylefeng.guns.rest.user.vo.UserInfoVo;
import com.stylefeng.guns.rest.vo.BaseReqVo;
import org.springframework.beans.factory.annotation.Autowired;
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
    JwtProperties jwtProperties;


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
    public BaseReqVo userInfoUpdate(UserInfoVo userInfoVo) {
        BaseReqVo baseReqVo = new BaseReqVo();
        int status = userServiceAPI.updateUserInfo(userInfoVo);
        UserInfoVo userInfo = userServiceAPI.queryUserInfo(userInfoVo.getUuid());
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

    }
}
