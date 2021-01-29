package com.lng.lngattendancesystem.Models.MarkOutResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.Status;


public class AttendanceMarkOutReponse {

    @SerializedName("empAttendanceOutDatetime")
    @Expose
    private String empAttendanceOutDatetime;
    @SerializedName("status")
    @Expose
    private Status status;

    public String getEmpAttendanceOutDatetime() {
        return empAttendanceOutDatetime;
    }

    public void setEmpAttendanceOutDatetime(String empAttendanceOutDatetime) {
        this.empAttendanceOutDatetime = empAttendanceOutDatetime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}