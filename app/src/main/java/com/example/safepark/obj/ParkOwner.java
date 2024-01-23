package com.example.safepark.obj;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ParkOwner extends VehicleOwner {
    private BankDetails bankDetails;
    private ParkDetails parkDetails;

    public ParkOwner() {
    }

    public ParkOwner(String name, String email, String phoneNumber, String password, BankDetails bankDetails, ParkDetails parkDetails) {
        super(name, email, phoneNumber, password);
        this.bankDetails = bankDetails;
        this.parkDetails = parkDetails;
    }

    public BankDetails getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(BankDetails bankDetails) {
        this.bankDetails = bankDetails;
    }

    public ParkDetails getParkDetails() {
        return parkDetails;
    }

    public void setParkDetails(ParkDetails parkDetails) {
        this.parkDetails = parkDetails;
    }

}
