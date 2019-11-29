package com.stylefeng.guns.rest.user;

import com.stylefeng.guns.rest.user.vo.UserInfoVo;

public interface UserServiceAPI {
    int updateUserInfo(UserInfoVo userInfoVo);

    int checkUsername(String username);

    UserInfoVo queryUserInfo(Integer uuid);
}
