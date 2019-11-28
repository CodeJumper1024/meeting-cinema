package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

@Data
public class BaseReqVo<T> {
    T data;
    String msg;
    Integer status;
    public static BaseReqVo ok(){
        BaseReqVo baseReqVo = new BaseReqVo();
        baseReqVo.setStatus(0);
        baseReqVo.setMsg("成功");
        return baseReqVo;
    }
}
