package com.lng.lngattendancesystem.Models;

import com.google.gson.annotations.SerializedName;

public class Status {
    @SerializedName("error")
    private Boolean error;
    @SerializedName("code")
    private Integer code;
    @SerializedName("message")
    private String message;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
