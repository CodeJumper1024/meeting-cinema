package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ConditionVo implements Serializable {
    private static final long serialVersionUID = 5152349513703689573L;
    List<CatInfoVo> catInfo;
    List<SourceInfoVo> sourceInfo;
    List<YearInfoVo> yearInfo;
}
