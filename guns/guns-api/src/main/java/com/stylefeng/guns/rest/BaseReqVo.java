package com.stylefeng.guns.rest;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseReqVo<T> implements Serializable {
    T data;
    String msg;
    Integer status;
    String imgPre;
    String nowPage;
    String totalPage;
    public static BaseReqVo ok(){
        BaseReqVo baseReqVo = new BaseReqVo();
        baseReqVo.setStatus(0);
        baseReqVo.setMsg("成功");
        return baseReqVo;
    }
    public static BaseReqVo queryFail(){
        BaseReqVo baseReqVo = new BaseReqVo();
        baseReqVo.setStatus(1);
        baseReqVo.setMsg("查询失败，无条件可加载");
        return baseReqVo;
    }
    public static BaseReqVo fail(){
        BaseReqVo baseReqVo = new BaseReqVo();
        baseReqVo.setStatus(999);
        baseReqVo.setMsg("系统出现异常，请联系管理员");
        return baseReqVo;
    }
}
