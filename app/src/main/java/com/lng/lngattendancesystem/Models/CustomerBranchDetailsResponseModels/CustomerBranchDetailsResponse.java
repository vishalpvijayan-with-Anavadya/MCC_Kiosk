package com.lng.lngattendancesystem.Models.CustomerBranchDetailsResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.Status;

import java.util.List;

public class CustomerBranchDetailsResponse {


    @SerializedName("status")
    private Status status;

    @SerializedName("custBranchDetails")
    @Expose
    private List<CustBranchDetail> custBranchDetails = null;

    public List<CustBranchDetail> getCustBranchDetails() {
        return custBranchDetails;
    }

    public void setCustBranchDetails(List<CustBranchDetail> custBranchDetails) {
        this.custBranchDetails = custBranchDetails;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}