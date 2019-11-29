package com.stylefeng.guns.rest.user.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserInfoVo implements Serializable {

    private Integer uuid;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    private Integer sex; // 0-男   1-女

    private String birthday;

    private Integer lifeState; // 0-单身，1-热恋中，2-已婚，3-为人父母

    private String biography;

    private String address;

    private String headAddress;

    private Date createTime;

    private Date updateTime;
}
