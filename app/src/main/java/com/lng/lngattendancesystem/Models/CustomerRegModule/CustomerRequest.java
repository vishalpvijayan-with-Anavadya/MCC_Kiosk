package com.lng.lngattendancesystem.Models.CustomerRegModule;

public class CustomerRequest {

    private String custCode;
    private String brCode;
    private String kioskNo;
    private String kioskCode;


    public CustomerRequest(String custCode, String brCode, String kioskNo, String kioskCode) {
        this.custCode = custCode;
        this.brCode = brCode;
        this.kioskNo = kioskNo;
        this.kioskCode = kioskCode;
    }

    public String getCustCode() {
        return custCode;
    }

    public String getBrCode() {
        return brCode;
    }

    public String getKioskNo() {
        return kioskNo;
    }

    public String getKioskCode() {
        return kioskCode;
    }
}
