package com.stylefeng.guns.rest.user;

import com.stylefeng.guns.rest.user.vo.UserInfoVo;

public interface UserServiceAPI {
    UserInfoVo updateUserInfo(UserInfoVo userInfoVo);
    int register(String username, String password, String email, String mobile, String address);
    boolean login(String userName, String password);
    public UserInfoVo getUserInfoByName(String username);
}
