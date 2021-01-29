package com.lng.lngattendancesystem.Models.NewLogicMarkAtt;

import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.Status;

public class MarkAttendanceResponseNew {

    @SerializedName("status")
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


}
