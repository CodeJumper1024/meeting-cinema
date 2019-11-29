package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class SourceInfoVo implements Serializable {
    private static final long serialVersionUID = -8681693484319536278L;
    private Integer sourceId;
    private String sourceName;
    private Boolean isActive;
}
