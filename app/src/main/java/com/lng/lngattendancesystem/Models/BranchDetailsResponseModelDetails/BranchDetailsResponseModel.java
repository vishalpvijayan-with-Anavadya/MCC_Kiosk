package com.lng.lngattendancesystem.Models.BranchDetailsResponseModelDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.Status;

import java.util.List;

public class BranchDetailsResponseModel {

    @SerializedName("branchList")
    @Expose
    private List<BranchList> branchList = null;


    @SerializedName("status")
    @Expose
    private Status status;

    public List<BranchList> getBranchList() {
        return branchList;
    }

    public void setBranchList(List<BranchList> branchList) {
        this.branchList = branchList;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}