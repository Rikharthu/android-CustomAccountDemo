package com.example.uberv.customaccountdemo.api.models;

import com.google.gson.annotations.SerializedName;

public class AuthData {
    @SerializedName("token")
    private String mToken;

    public AuthData() {
    }

    public AuthData(String token) {
        mToken = token;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }
}
