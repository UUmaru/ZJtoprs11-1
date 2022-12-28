package com.example.oldtown.dto;

import java.util.List;

/**
 * @author weipeng
 * @name 用于定义适用于echarts格式的返回值
 * @info
 * @date 2021/04/29
 */


public class ChartResponseDto<Tx,Ty> {
    private List<Tx> xList;

    public List<Tx> getxList() {
        return xList;
    }

    public void setxList(List<Tx> xList) {
        this.xList = xList;
    }

    public List<List<Ty>> getYList() {
        return yList;
    }

    public void setyList(List<List<Ty>> yList) {
        this.yList = yList;
    }

    private List<List<Ty>> yList;

    public ChartResponseDto(List<Tx> xList, List<List<Ty>> yList) {
        this.xList = xList;
        this.yList = yList;
    }
}
