package com.lng.lngattendancesystem.Models.FaceRegistration;

import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.Status;

public class MainRegresponce {
    @SerializedName("data1")

    private Data1 data1;
    @SerializedName("status")
    private Status status;

    public Data1 getData1() {
        return data1;
    }

    public void setData1(Data1 data1) {
        this.data1 = data1;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
