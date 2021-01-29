package com.lng.lngattendancesystem.Models.BlockDetailsResponseModelsDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.Status;

import java.util.List;

public class BlockDetailsResponseModel {

    @SerializedName("blockList")
    @Expose
    private List<BlockList> blockList = null;
    @SerializedName("status")
    @Expose
    private Status status;

    public List<BlockList> getBlockList() {
        return blockList;
    }

    public void setBlockList(List<BlockList> blockList) {
        this.blockList = blockList;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
