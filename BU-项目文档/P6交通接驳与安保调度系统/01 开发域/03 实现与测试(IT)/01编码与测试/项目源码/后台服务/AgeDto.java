package com.example.oldtown.dto;

/**
 * @author ding.yp
 * @name
 * @info
 * @date 2021/04/07
 */


public class AgeDto {
    private Integer NUM;
    private String NAME;
    private Integer ID;

    public AgeDto(Integer NUM, String NAME, Integer ID) {
        this.NUM = NUM;
        this.NAME = NAME;
        this.ID = ID;
    }

    public Integer getNUM() {
        return NUM;
    }

    public void setNUM(Integer NUM) {
        this.NUM = NUM;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }
}
