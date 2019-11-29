package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.UserMapper;
import com.stylefeng.guns.rest.user.UserServiceAPI;
import com.stylefeng.guns.rest.user.vo.UserInfoVo;
import com.stylefeng.guns.rest.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

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
    public int register(String username, String password, String email, String mobile, String address) {
        int exist = userMapper.checkExist(username);
        if(exist == 1){
            //用户名已被注册返回1
            return 1;
        }

        //md5加密
        try {
             password = Md5Util.getMd5(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //注册
        int insert = userMapper.register(username,password,email,mobile,address);
        if(insert == 1){
            //注册成功返回0
            return 0;
        }

        //注册失败返回999
        return 999;
    }

    @Override
    public boolean login(String userName, String password) {
        //把密码加密先
        try {
            password = Md5Util.getMd5(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //登录
        int result  = userMapper.login(userName,password);
        if(result != 0){
            //登录成功
            return true;
        }
        //登录失败
        return false;
    }

    @Override
    public UserInfoVo getUserInfoByName(String username) {
        return userMapper.selectByName(username);
    }

}
