package com.lng.lngattendancesystem.Models.ConfigurationResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BeaconMapDetailslist {

    @SerializedName("blkBeaconMapId")
    @Expose
    private Integer blkBeaconMapId;
    @SerializedName("beaconCode")
    @Expose
    private String beaconCode;

    public Integer getBlkBeaconMapId() {
        return blkBeaconMapId;
    }

    public void setBlkBeaconMapId(Integer blkBeaconMapId) {
        this.blkBeaconMapId = blkBeaconMapId;
    }

    public String getBeaconCode() {
        return beaconCode;
    }

    public void setBeaconCode(String beaconCode) {
        this.beaconCode = beaconCode;
    }

}