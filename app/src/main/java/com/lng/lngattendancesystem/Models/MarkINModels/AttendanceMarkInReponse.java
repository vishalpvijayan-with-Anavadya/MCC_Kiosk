package com.lng.lngattendancesystem.Models.MarkINModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.Status;

public class AttendanceMarkInReponse {

    @SerializedName("empAttendanceInDatetime")
    @Expose
    private String empAttendanceInDatetime;
    @SerializedName("status")
    @Expose
    private Status status;

    public String getEmpAttendanceInDatetime() {
        return empAttendanceInDatetime;
    }

    public void setEmpAttendanceInDatetime(String empAttendanceInDatetime) {
        this.empAttendanceInDatetime = empAttendanceInDatetime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}