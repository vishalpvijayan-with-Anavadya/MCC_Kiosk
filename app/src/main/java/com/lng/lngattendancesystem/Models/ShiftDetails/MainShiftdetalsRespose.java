package com.lng.lngattendancesystem.Models.ShiftDetails;

import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.Status;

public class MainShiftdetalsRespose {
    @SerializedName("shiftData")

    private ShiftData shiftData;



    @SerializedName("status")

    private Status status;

    public ShiftData getShiftData() {
        return shiftData;
    }

    public void setShiftData(ShiftData shiftData) {
        this.shiftData = shiftData;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
