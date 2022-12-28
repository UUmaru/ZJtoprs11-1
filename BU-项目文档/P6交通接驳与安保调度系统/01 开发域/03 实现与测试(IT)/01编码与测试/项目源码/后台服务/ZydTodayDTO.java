package com.example.oldtown.dto;

import cn.hutool.core.lang.Pair;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * @author weipeng
 * @name
 * @info
 * @date 2021/03/04
 */


public class ZydTodayDTO {
    @JsonUnwrapped
    private Integer orderCount;
    private Integer customerCount;
    private BigDecimal gmv;
    private BigDecimal orderAmount;
    private Map<String, Integer> popularClass;
    private List<Pair<String, String>> popularItem;

    public ZydTodayDTO(Integer orderCount, Integer customerCount, BigDecimal gmv, BigDecimal orderAmount,
                       Map<String, Integer> popularClass,List<Pair<String, String>> popularItem) {
        this.orderCount = orderCount;
        this.customerCount = customerCount;
        this.gmv = gmv;
        this.orderAmount = orderAmount;
        this.popularClass = popularClass;
        this.popularItem = popularItem;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public Integer getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(Integer customerCount) {
        this.customerCount = customerCount;
    }

    public BigDecimal getGmv() {
        return gmv;
    }

    public void setGmv(BigDecimal gmv) {
        this.gmv = gmv;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Map<String, Integer> getPopularClass() {
        return popularClass;
    }

    public void setPopularClass(Map<String, Integer> popularClass) {
        this.popularClass = popularClass;
    }

    public List<Pair<String, String>> getPopularItem() {
        return popularItem;
    }

    public void setPopularItem(List<Pair<String, String>> popularItem) {
        this.popularItem = popularItem;
    }
}
