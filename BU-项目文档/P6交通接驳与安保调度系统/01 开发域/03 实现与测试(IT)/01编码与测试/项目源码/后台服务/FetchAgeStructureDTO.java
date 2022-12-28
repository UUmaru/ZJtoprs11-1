package com.example.oldtown.dto;

import java.util.List;

/**
 * @author ding.yp
 * @name
 * @info
 * @date 2021/04/07
 */


public class FetchAgeStructureDTO {
    String regionId;
    String responseCode;
    String time;
    String responseMessage;
    List<AgeDto> responseData;

    public FetchAgeStructureDTO(String regionId, String responseCode, String time, String responseMessage, List<AgeDto> responseData) {
        this.regionId = regionId;
        this.responseCode = responseCode;
        this.time = time;
        this.responseMessage = responseMessage;
        this.responseData = responseData;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public List<AgeDto> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<AgeDto> responseData) {
        this.responseData = responseData;
    }
}
