package com.example.safepark.obj;

public class BankDetails {
    private String
            bankName, bankCode,
            branchName, branchCode,
            fullName, accountNumber, nicNumber;

    public BankDetails() {
    }

    public BankDetails(String bankName, String bankCode, String branchName, String branchCode, String fullName, String accountNumber, String nicNumber) {
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.branchName = branchName;
        this.branchCode = branchCode;
        this.fullName = fullName;
        this.accountNumber = accountNumber;
        this.nicNumber = nicNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getNicNumber() {
        return nicNumber;
    }

    public void setNicNumber(String nicNumber) {
        this.nicNumber = nicNumber;
    }
}
