package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.common.persistence.dao.UserMapper;
import com.stylefeng.guns.rest.user.UserServiceAPI;
import com.stylefeng.guns.rest.user.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(interfaceClass = UserServiceAPI.class)
public class UserServiceImpl implements UserServiceAPI {

    @Autowired
    UserMapper userMapper;

    @Override
    public int updateUserInfo(UserInfoVo userInfoVo) {
        int status = userMapper.updateUserInfo(userInfoVo);
        return status;
    }

    @Override
    public UserInfoVo queryUserInfo(Integer uuid) {
        UserInfoVo userInfoVo = userMapper.queryUserInfo(uuid);
        return userInfoVo;
    }

    @Override
    public int checkUsername(String username) {
        String name = userMapper.checkUsername(username);
        if (name != null) {
            return 1;
        } else {
            return 0;
        }
    }
}
