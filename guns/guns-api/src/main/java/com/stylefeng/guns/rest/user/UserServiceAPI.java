package com.stylefeng.guns.rest.user;

import com.stylefeng.guns.rest.user.vo.UserInfoModel;
import com.stylefeng.guns.rest.user.vo.UserInfoVo;

public interface UserServiceAPI {

    int updateUserInfo(UserInfoModel userInfoModel);

    int checkUsername(String username);

    UserInfoModel queryUserInfo(Integer uuid);

    int register(String username, String password, String email, String mobile, String address);

    boolean login(String userName, String password);

    UserInfoVo getUserInfoByName(String username);
}
