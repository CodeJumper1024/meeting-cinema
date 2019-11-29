package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class ActorsVO implements Serializable {
    private static final long serialVersionUID = -4044827493631015168L;
    private DirectorVo director;
    private List<RealActorsVo> actors;
}
