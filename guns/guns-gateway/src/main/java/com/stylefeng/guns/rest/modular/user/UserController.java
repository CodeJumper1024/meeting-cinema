package com.stylefeng.guns.rest.modular.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    @RequestMapping("updateUserInfo")
    public String hello(){
        return "ok";
    }
}
