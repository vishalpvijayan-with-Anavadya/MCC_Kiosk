package com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule;

public class getCustomerAllDitailes {
    String customerId;
    String branchId;
    byte[] customerLogo;
    String customerName;
    String branchName;
    String empGroupFaceList;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public byte[] getCustomerLogo() {
        return customerLogo;
    }

    public void setCustomerLogo(byte[] customerLogo) {
        this.customerLogo = customerLogo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getEmpGroupFaceList() {
        return empGroupFaceList;
    }

    public void setEmpGroupFaceList(String empGroupFaceList) {
        this.empGroupFaceList = empGroupFaceList;
    }
}
