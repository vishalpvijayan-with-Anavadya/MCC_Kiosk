package com.lng.lngattendancesystem.Models.CustomerRegModule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.Status;

public class MainCustRegResponce {

    @SerializedName("otpDto")
    private OtpDto otpDto;
    @SerializedName("status")
    private Status status;

    @SerializedName("custId")
    @Expose
    private Integer custId;
    @SerializedName("brId")
    @Expose
    private Integer brId;
    public OtpDto getOtpDto() {
        return otpDto;
    }

    public void setOtpDto(OtpDto otpDto) {
        this.otpDto = otpDto;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    public Integer getCustId() {
        return custId;
    }

    public void setCustId(Integer custId) {
        this.custId = custId;
    }

    public Integer getBrId() {
        return brId;
    }

    public void setBrId(Integer brId) {
        this.brId = brId;
    }

}
