package com.lng.lngattendancesystem.Models.CustomerBranchDetailsResponseModels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustBranchDetail {

    @SerializedName("custCode")
    @Expose
    private String custCode;
    @SerializedName("brCode")
    @Expose
    private String brCode;
    @SerializedName("custId")
    @Expose
    private Integer custId;
    @SerializedName("brId")
    @Expose
    private Integer brId;
    @SerializedName("custLogoFile")
    @Expose
    private String custLogoFile;
    @SerializedName("custName")
    @Expose
    private String custName;
    @SerializedName("brName")
    @Expose
    private String brName;

    public String getCustCode() {
        return custCode;
    }

    public void setCustCode(String custCode) {
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