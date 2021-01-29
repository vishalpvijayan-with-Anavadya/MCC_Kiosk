package com.lng.lngattendancesystem.Models.GetDateTimeFromSerevr;

import com.google.gson.annotations.SerializedName;

public class GetServerDateTimeResponce {
    @SerializedName("currentDate")

    private String currentDate;

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }


}
