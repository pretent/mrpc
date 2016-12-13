package org.pretent.mrpc;

/**
 */
public class RegisterConfig {

    public RegisterConfig() {
    }

    public RegisterConfig(String address) {
        this.address = address;
    }

    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}