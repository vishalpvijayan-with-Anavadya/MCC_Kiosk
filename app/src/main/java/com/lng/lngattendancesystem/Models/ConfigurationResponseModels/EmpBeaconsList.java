package com.lng.lngattendancesystem.Models.ConfigurationResponseModels;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.Status;

public class EmpBeaconsList {

    @SerializedName("beaconMapDetailslist")
    @Expose
    private List<BeaconMapDetailslist> beaconMapDetailslist = null;
    @SerializedName("status")
    @Expose
    private Status status;

    public List<BeaconMapDetailslist> getBeaconMapDetailslist() {
        return beaconMapDetailslist;
    }

    public void setBeaconMapDetailslist(List<BeaconMapDetailslist> beaconMapDetailslist) {
        this.beaconMapDetailslist = beaconMapDetailslist;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}