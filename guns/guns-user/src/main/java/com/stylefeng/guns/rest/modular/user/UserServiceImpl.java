package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.common.persistence.dao.UserMapper;
import com.stylefeng.guns.rest.user.UserServiceAPI;
import com.stylefeng.guns.rest.user.vo.UserInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Wrapper;

@Component
@Service(interfaceClass = UserServiceAPI.class)
public class UserServiceImpl implements UserServiceAPI {

    @Autowired
    UserMapper userMapper;

    @Override
    public UserInfoVo updateUserInfo(UserInfoVo userInfoVo) {
        //int status = userMapper.updateUserInfo(userInfoVo);
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
