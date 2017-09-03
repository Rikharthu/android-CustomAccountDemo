package com.example.uberv.customaccountdemo.api.models;

import com.google.gson.annotations.SerializedName;

public class UserCredentials {
    @SerializedName("email")
    private String mEmail;
    @SerializedName("password")
    private String mPassword;

    public UserCredentials() {
    }

    public UserCredentials(String email, String password) {
        mEmail = email;
        mPassword = password;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }
}
