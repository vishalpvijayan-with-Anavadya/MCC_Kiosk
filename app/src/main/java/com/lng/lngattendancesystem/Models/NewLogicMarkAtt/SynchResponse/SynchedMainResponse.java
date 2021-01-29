package com.lng.lngattendancesystem.Models.NewLogicMarkAtt.SynchResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lng.lngattendancesystem.Models.Status;

import java.util.List;

public class SynchedMainResponse {
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("offlineAttendanceIds")
    @Expose
    private List<OfflineAttendanceId> offlineAttendanceIds = null;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<OfflineAttendanceId> getOfflineAttendanceIds() {
        return offlineAttendanceIds;
    }

    public void setOfflineAttendanceIds(List<OfflineAttendanceId> offlineAttendanceIds) {
        this.offlineAttendanceIds = offlineAttendanceIds;
    }

}
