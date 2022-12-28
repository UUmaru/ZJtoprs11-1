package com.example.oldtown.dto;

import com.example.oldtown.modules.com.model.ComSpecialty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.io.Serializable;
import java.util.List;

/**
 * @author wei.peng
 * @name  景点实时人数DTO
 * @info
 * @date 2021/3/30
 */


public class ComScenicDataDTO implements Serializable {
    private String name;
    private Integer limitNum;
    private Integer checkNum;
    private Integer leaveNum;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public Integer getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(Integer checkNum) {
        this.checkNum = checkNum;
    }

    public Integer getLeaveNum() {
        return leaveNum;
    }

    public void setLeaveNum(Integer leaveNum) {
        this.leaveNum = leaveNum;
    }

    public Integer getInNum() {
        return Math.max(checkNum-leaveNum,0);
    }
}
