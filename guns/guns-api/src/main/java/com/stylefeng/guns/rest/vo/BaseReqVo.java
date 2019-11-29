package com.stylefeng.guns.rest.vo;

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
}
