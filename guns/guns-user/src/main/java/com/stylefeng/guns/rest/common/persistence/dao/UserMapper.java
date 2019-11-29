package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.stylefeng.guns.rest.common.persistence.model.User;
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

    String checkUsername(@Param("username") String username);

    int updateUserInfo(@Param("userInfoVo") UserInfoVo userInfoVo);

    UserInfoVo queryUserInfo(@Param("uuid") Integer uuid);
}
