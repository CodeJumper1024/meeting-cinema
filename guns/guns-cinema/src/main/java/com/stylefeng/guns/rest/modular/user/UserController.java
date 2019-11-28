package com.stylefeng.guns.rest.modular.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @RequestMapping("hello123")
    public String hello(){
        return "ok";
    }
}
