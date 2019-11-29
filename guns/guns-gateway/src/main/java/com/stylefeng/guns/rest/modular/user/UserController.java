package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.common.persistence.model.User;
import com.stylefeng.guns.rest.user.UserServiceAPI;
import com.stylefeng.guns.rest.user.vo.UserInfoVo;
import com.stylefeng.guns.rest.vo.BaseReqVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    @Reference(interfaceClass = UserServiceAPI.class, check = false)
    UserServiceAPI userServiceAPI;

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

    @RequestMapping("updateUserInfo")
    public BaseReqVo userInfoUpdate(UserInfoVo userInfoVo) {
        BaseReqVo baseReqVo = new BaseReqVo();
        UserInfoVo userInfo = userServiceAPI.updateUserInfo(userInfoVo);
        baseReqVo.setStatus(0);
        baseReqVo.setData(userInfo);
        return baseReqVo;
    }
}
