package com.example.oldtown.dto;

import cn.hutool.core.lang.Pair;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author weipeng
 * @name
 * @info
 * @date 2021/03/04
 */


public class ZydMonthDTO {
    @JsonUnwrapped
    private BigDecimal totalGmv;
    private Float huanbi;
    private Float tongbi;
    private HashMap<Integer,BigDecimal> dayGmv;

    public ZydMonthDTO( BigDecimal totalGmv, Float huanbi, Float tongbi,HashMap<Integer,BigDecimal> dayGmv) {
        this.totalGmv=totalGmv;
        this.huanbi=huanbi;
        this.tongbi=tongbi;
        this.dayGmv=dayGmv;
    }

    public BigDecimal getTotalGmv() {
        return totalGmv;
    }

    public void setTotalGmv(BigDecimal totalGmv) {
        this.totalGmv = totalGmv;
    }

    public Float getHuanbi() {
        return huanbi;
    }

    public void setHuanbi(Float huanbi) {
        this.huanbi = huanbi;
    }

    public Float getTongbi() {
        return tongbi;
    }

    public void setTongbi(Float tongbi) {
        this.tongbi = tongbi;
    }

    public HashMap<Integer, BigDecimal> getDayGmv() {
        return dayGmv;
    }

    public void setDayGmv(HashMap<Integer, BigDecimal> dayGmv) {
        this.dayGmv = dayGmv;
    }
}
