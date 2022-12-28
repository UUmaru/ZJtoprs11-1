package com.example.oldtown.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

/**
 * @author ding.yp
 * @name
 * @info
 * @date 2021/03/16
 */

@ApiModel(value="TrfAppConfig对象", description="交通接驳app配置")
public class TrfAppConfig implements Serializable {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "定位间隔秒")
    private Integer locationFrequency;

    @ApiModelProperty(value = "检查间隔秒")
    private Integer checkFrequency;

    @ApiModelProperty(value = "起始时间点")
    private Integer fromHour;

    @ApiModelProperty(value = "截止时间点")
    private Integer toHour;

    @ApiModelProperty(value = "上传域名,英文逗号分隔")
    private String uploadDomains;

    public TrfAppConfig(Integer locationFrequency, Integer checkFrequency, Integer fromHour, Integer toHour, String uploadDomains) {
        this.locationFrequency = locationFrequency;
        this.checkFrequency = checkFrequency;
        this.fromHour = fromHour;
        this.toHour = toHour;
        this.uploadDomains = uploadDomains;
    }

    public TrfAppConfig() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getLocationFrequency() {
        return locationFrequency;
    }

    public void setLocationFrequency(Integer locationFrequency) {
        this.locationFrequency = locationFrequency;
    }

    public Integer getCheckFrequency() {
        return checkFrequency;
    }

    public void setCheckFrequency(Integer checkFrequency) {
        this.checkFrequency = checkFrequency;
    }

    public Integer getFromHour() {
        return fromHour;
    }

    public void setFromHour(Integer fromHour) {
        this.fromHour = fromHour;
    }

    public Integer getToHour() {
        return toHour;
    }

    public void setToHour(Integer toHour) {
        this.toHour = toHour;
    }

    public String getUploadDomains() {
        return uploadDomains;
    }

    public void setUploadDomains(String uploadDomains) {
        this.uploadDomains = uploadDomains;
    }
}
