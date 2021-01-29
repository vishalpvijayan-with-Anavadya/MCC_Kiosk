package com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule;

public class InsertCustomerDitales {

    private String customerId;
    private String branchId;
    private byte[] customerLogo;
    private String customerName;
    private String branchName;
    private String empGroupFaceList;

    public InsertCustomerDitales(String customerId, String branchId, byte[] customerLogo, String customerName, String branchName, String empGroupFaceList) {
        this.customerId = customerId;
        this.branchId = branchId;
        this.customerLogo = customerLogo;
        this.customerName = customerName;
        this.branchName = branchName;
        this.empGroupFaceList = empGroupFaceList;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getBranchId() {
        return branchId;
    }

    public byte[] getCustomerLogo() {
        return customerLogo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getEmpGroupFaceList() {
        return empGroupFaceList;
    }
}
