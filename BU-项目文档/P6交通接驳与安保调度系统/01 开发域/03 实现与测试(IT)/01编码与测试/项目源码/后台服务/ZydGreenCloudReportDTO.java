package com.example.oldtown.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ZydGreenCloudReportDTO {
//            "tax_month": 0,
//            "tax_year": 0,
//            "DAY": 0,
//            "tax_day": 0,
//            "descript": "房费收入",
//            "YEAR": 37966.9,
//            "MONTH": 37966.9,
//            "biz_date": "2021-01-22 00:00:00",
//            "CODE": "000001"

    private  Integer hotelID;
    private  Integer  tax_month;
    private  Integer tax_year;
    private  Integer DAY;
    private  Integer  tax_day;
    private  String descript;
    private  Integer YEAR;
    private  Integer MONTH;
    private  Date biz_date;
    private  String CODE;
}
