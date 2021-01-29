package com.lng.lngattendancesystem.Models.ConfigurationResponseModels;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.Status;

public class ConfigurationDetails {

    @SerializedName("custId")
    @Expose
    private Integer custId;
    @SerializedName("brId")
    @Expose
    private Integer brId;
    @SerializedName("custCode")
    @Expose
    private String custCode;
    @SerializedName("brCode")
    @Expose
    private String brCode;
    @SerializedName("custName")
    @Expose
    private String custName;
    @SerializedName("brName")
    @Expose
    private String brName;
    @SerializedName("kioskNo")
    @Expose
    private Integer kioskNo;
    @SerializedName("kioskCode")
    @Expose
    private String kioskCode;
    @SerializedName("custLogoFile")
    @Expose
    private String custLogoFile;
    @SerializedName("configList")
    @Expose
    private List<ConfigList> configList = null;
    @SerializedName("empBeaconsList")
    @Expose
    private EmpBeaconsList empBeaconsList;
    @SerializedName("status")
    @Expose
    private Status status;

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

    public Integer getKioskNo() {
        return kioskNo;
    }

    public void setKioskNo(Integer kioskNo) {
        this.kioskNo = kioskNo;
    }

    public String getKioskCode() {
        return kioskCode;
    }

    public void setKioskCode(String kioskCode) {
        this.kioskCode = kioskCode;
    }

    public String getCustLogoFile() {
        return custLogoFile;
    }

    public void setCustLogoFile(String custLogoFile) {
        this.custLogoFile = custLogoFile;
    }

    public List<ConfigList> getConfigList() {
        return configList;
    }

    public void setConfigList(List<ConfigList> configList) {
        this.configList = configList;
    }

    public EmpBeaconsList getEmpBeaconsList() {
        return empBeaconsList;
    }

    public void setEmpBeaconsList(EmpBeaconsList empBeaconsList) {
        this.empBeaconsList = empBeaconsList;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}