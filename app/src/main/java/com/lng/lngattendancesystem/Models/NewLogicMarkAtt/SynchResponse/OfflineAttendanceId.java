package com.lng.lngattendancesystem.Models.NewLogicMarkAtt.SynchResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OfflineAttendanceId {

    @SerializedName("attendanceId")
    @Expose
    private Integer attendanceId;

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
    }
}
