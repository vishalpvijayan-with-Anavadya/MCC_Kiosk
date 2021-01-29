package com.lng.lngattendancesystem.Models.CustomerDietales;

import com.google.gson.annotations.SerializedName;

public class Data1 {

    @SerializedName("custCode")

    private Object custCode;
    @SerializedName("brCode")

    private String brCode;
    @SerializedName("custId")

    private Integer custId;
    @SerializedName("brId")

    private Integer brId;
    @SerializedName("custLogoFile")

    private String custLogoFile;
    @SerializedName("custName")

    private String custName;
    @SerializedName("brName")

    private String brName;

    public Object getCustCode() {
        return custCode;
    }

    public void setCustCode(Object custCode) {
        this.custCode = custCode;
    }

    public String getBrCode() {
        return brCode;
    }

    public void setBrCode(String brCode) {
        this.brCode = brCode;
    }

    public Integer getCustId() {
        return custId;
    }

    public void setCustId(Integer custId) {
        this.custId = custId;
    }

    public Integer getBrId() {
        return brId;
    }

    public void setBrId(Integer brId) {
        this.brId = brId;
    }

    public String getCustLogoFile() {
        return custLogoFile;
    }

    public void setCustLogoFile(String custLogoFile) {
        this.custLogoFile = custLogoFile;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getBrName() {
        return brName;
    }

    public void setBrName(String brName) {
        this.brName = brName;
    }
}
