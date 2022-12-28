package com.example.oldtown.dto;

import com.example.oldtown.modules.com.model.ComRoute;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

/**
 * @author ding.yp
 * @name
 * @info
 * @date 2021/03/25
 */


public class ComRouteDTO {
    @JsonUnwrapped
    private ComRoute comRoute;
    List<MinPointDTO> pointList;

    public ComRouteDTO(ComRoute comRoute, List<MinPointDTO> pointList) {
        this.comRoute = comRoute;
        this.pointList = pointList;
    }

    public ComRoute getComRoute() {
        return comRoute;
    }

    public void setComRoute(ComRoute comRoute) {
        this.comRoute = comRoute;
    }

    public List<MinPointDTO> getPointList() {
        return pointList;
    }

    public void setPointList(List<MinPointDTO> pointList) {
        this.pointList = pointList;
    }
}
