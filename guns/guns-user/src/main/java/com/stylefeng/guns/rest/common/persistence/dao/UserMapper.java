package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.stylefeng.guns.rest.common.persistence.model.User;
import com.stylefeng.guns.rest.user.vo.UserInfoModel;
import com.stylefeng.guns.rest.user.vo.UserInfoVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2017-08-23
 */
public interface UserMapper extends BaseMapper<User> {
    int register(@Param("username") String username, @Param("password") String password, @Param("email") String email, @Param("mobile") String mobile, @Param("address") String address);

    int checkExist(@Param("username") String username);

    int login(@Param("username") String username, @Param("password") String password);

    String checkUsername(@Param("username") String username);

    int updateUserInfo(@Param("userInfoVo") UserInfoModel userInfoModel);

    UserInfoModel queryUserInfo(@Param("uuid") Integer uuid);

    UserInfoVo selectByName(@Param("username") String username);
}
