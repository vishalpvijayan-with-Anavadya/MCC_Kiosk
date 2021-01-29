package com.lng.lngattendancesystem.Models.CustomerRegModule;

import com.google.gson.annotations.SerializedName;

public class OtpDto {

    @SerializedName("otp")
    private Integer otp;

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }


}
