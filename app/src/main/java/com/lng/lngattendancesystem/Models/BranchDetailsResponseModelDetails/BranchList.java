package com.lng.lngattendancesystem.Models.BranchDetailsResponseModelDetails;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BranchList {

    @SerializedName("brId")
    @Expose
    private Integer brId;


    @SerializedName("brName")
    @Expose
    private String brName;

    @SerializedName("brCode")
    @Expose
    private String brCode;

    public String getBrCode() {
        return brCode;
    }

    public void setBrCode(String brCode) {
        this.brCode = brCode;
    }

    public Integer getBrId() {
        return brId;
    }

    public void setBrId(Integer brId) {
        this.brId = brId;
    }

    public String getBrName() {
        return brName;
    }

    public void setBrName(String brName) {
        this.brName = brName;
    }

}