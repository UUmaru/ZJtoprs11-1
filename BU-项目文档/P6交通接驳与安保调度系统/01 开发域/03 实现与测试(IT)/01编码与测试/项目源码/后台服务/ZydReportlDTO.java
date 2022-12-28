package com.example.oldtown.dto;

import com.example.oldtown.modules.zyd.model.*;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author weipeng
 * @name
 * @info
 * @date 2021/03/04
 */


public class ZydReportlDTO {
    @JsonUnwrapped
    private Long placeId;
    private String Name;
    private String AddressName;
    private String Telephone;
    private String Description;
    private String Type;
    private String Area;
    private BigDecimal GmvQuota;
    private Integer orderCount;
    private Integer customerCount;
    private BigDecimal gmv;
    private BigDecimal orderAmount;

    public ZydReportlDTO(Long placeId, String Name, String AddressName, String Telephone, String Description,  String Type,String Area
   ,BigDecimal GmvQuota,Integer orderCount,Integer customerCount,BigDecimal gmv,BigDecimal orderAmount) {
        this.placeId=placeId;
        this.Name=Name;
        this.AddressName=AddressName;
        this.Telephone=Telephone;
        this.Description=Description;
        this.Type=Type;
        this.Area=Area;
        this.GmvQuota=GmvQuota;
        this.orderCount=orderCount;
        this.customerCount=customerCount;
        this.gmv=gmv;
        this.orderAmount=orderAmount;

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getAddressName() {
        return AddressName;
    }

    public void setAddressName(String addressName) {
        AddressName = addressName;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public BigDecimal getGmvQuota() {
        return GmvQuota;
    }

    public void setGmvQuota(BigDecimal gmvQuota) {
        GmvQuota = gmvQuota;
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
