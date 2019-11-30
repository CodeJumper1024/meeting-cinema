package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import com.stylefeng.guns.rest.user.UserServiceAPI;
import com.stylefeng.guns.rest.vo.BaseReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private JwtProperties jwtProperties;

    @Reference(interfaceClass = UserServiceAPI.class, check = false)
    UserServiceAPI userServiceAPI;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("updateUserInfo")
    public String hello(){
        return "ok";
    }

    @RequestMapping("register")
    public BaseReqVo register(String username, String password, String email, String mobile, String address){
        BaseReqVo baseReqVo = new BaseReqVo();
        int result = userServiceAPI.register(username,password,email,mobile,address);
        if(result == 0){
            baseReqVo.setMsg("注册成功");
            baseReqVo.setStatus(0);
        }else if(result == 1){
            baseReqVo.setMsg("用户已存在");
            baseReqVo.setStatus(1);
        }else if(result == 999){
            baseReqVo.setMsg("系统出现异常，请联系管理员");
            baseReqVo.setStatus(999);
        }
        return baseReqVo;
    }

    @RequestMapping("logout")
    public BaseReqVo logout(HttpServletRequest request){
        BaseReqVo baseReqVo = new BaseReqVo();
        String requestHeader = request.getHeader(jwtProperties.getHeader());
        String token = requestHeader.substring(7);
        Boolean delete = redisTemplate.delete(token);
        if(delete){
            //token删除成功
            baseReqVo.setStatus(0);
            baseReqVo.setMsg("成功退出");
        }else{
            baseReqVo.setStatus(1);
            baseReqVo.setMsg("退出失败，用户尚未登陆");
        }
        return baseReqVo;
    }

}
