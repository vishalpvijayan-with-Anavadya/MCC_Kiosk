package com.lng.lngattendancesystem.Models.EmployeeDetalies;

import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.CustomerRegModule.OtpDto;
import com.lng.lngattendancesystem.Models.Status;

public class EmpMainOtpResponce {
    @SerializedName("otpDto")
    private OtpDto otpDto;
    @SerializedName("status")

    private Status status;

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
}
