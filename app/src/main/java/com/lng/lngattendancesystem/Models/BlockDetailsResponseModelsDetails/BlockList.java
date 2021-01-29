package com.lng.lngattendancesystem.Models.BlockDetailsResponseModelsDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlockList {

    @SerializedName("blkId")
    @Expose
    private Integer blkId;
    @SerializedName("blkLogicalName")
    @Expose
    private String blkLogicalName;

    public Integer getBlkId() {
        return blkId;
    }

    public void setBlkId(Integer blkId) {
        this.blkId = blkId;
    }

    public String getBlkLogicalName() {
        return blkLogicalName;
    }

    public void setBlkLogicalName(String blkLogicalName) {
        this.blkLogicalName = blkLogicalName;
    }

}
