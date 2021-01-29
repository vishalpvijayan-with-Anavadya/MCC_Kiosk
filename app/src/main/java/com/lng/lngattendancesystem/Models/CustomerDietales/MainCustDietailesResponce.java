package com.lng.lngattendancesystem.Models.CustomerDietales;

import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.Status;

import java.util.List;

public class MainCustDietailesResponce {
    @SerializedName("data1")

    private List<Data1> data1 = null;
    @SerializedName("status")

    private Status status;

    public List<Data1> getData1() {
        return data1;
    }

    public void setData1(List<Data1> data1) {
        this.data1 = data1;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
