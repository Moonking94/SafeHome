package com.fyp.imranabdulhadi.safehome;

/**
 * Created by Imran Abdulhadi on 10/23/2016.
 */

public class Address {
    private int addressId;
    private String addressIp;
    private String addressDesc;
    private String addressChecked;

    public Address(){}

    public Address(int addressId, String addressIp, String addressDesc, String addressChecked) {
        this.addressId = addressId;
        this.addressIp = addressIp;
        this.addressDesc = addressDesc;
        this.addressChecked = addressChecked;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getAddressIp() {
        return addressIp;
    }

    public void setAddressIp(String addressIp) {
        this.addressIp = addressIp;
    }

    public String getAddressDesc() {
        return addressDesc;
    }

    public void setAddressDesc(String addressDesc) {
        this.addressDesc = addressDesc;
    }

    public String getAddressChecked() {
        return addressChecked;
    }

    public void setAddressChecked(String addressChecked) {
        this.addressChecked = addressChecked;
    }
}
