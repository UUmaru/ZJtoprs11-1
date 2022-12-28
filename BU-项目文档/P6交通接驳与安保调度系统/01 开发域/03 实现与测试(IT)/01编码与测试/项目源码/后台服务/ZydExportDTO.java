package com.example.oldtown.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.math.BigDecimal;

/**
 * @author weipeng
 * @name
 * @info
 * @date 2021/03/04
 */


public class ZydExportDTO {
    @JsonUnwrapped
    @ExcelProperty("店铺编号")
    private Long placeId;
    @ExcelProperty("店铺名称")
    private String name;
    @ExcelProperty("类型")
    private String type;
    @ExcelProperty("订单量")
    private Integer orderCount;
    @ExcelProperty("客户量")
    private Integer customerCount;
    @ExcelProperty("营业额")
    private BigDecimal gmv;
    @ExcelProperty("订单总额")
    private BigDecimal orderAmount;

    public ZydExportDTO(Long placeId, String name, String type,Integer orderCount, Integer customerCount, BigDecimal gmv, BigDecimal orderAmount) {
        this.placeId=placeId;
        this.name =name;
        this.type =type;
        this.orderCount=orderCount;
        this.customerCount=customerCount;
        this.gmv=gmv;
        this.orderAmount=orderAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
