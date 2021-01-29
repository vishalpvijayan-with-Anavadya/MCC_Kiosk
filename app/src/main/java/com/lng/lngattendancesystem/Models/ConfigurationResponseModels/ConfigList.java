package com.lng.lngattendancesystem.Models.ConfigurationResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConfigList {

    @SerializedName("config")
    @Expose
    private String config;
    @SerializedName("statusFlag")
    @Expose
    private Boolean statusFlag;

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public Boolean getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(Boolean statusFlag) {
        this.statusFlag = statusFlag;
    }

}