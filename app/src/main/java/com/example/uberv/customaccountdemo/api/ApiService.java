package com.example.uberv.customaccountdemo.api;

import com.example.uberv.customaccountdemo.api.models.AuthData;
import com.example.uberv.customaccountdemo.api.models.UserCredentials;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("token")
    Call<AuthData> authenticate(@Body UserCredentials credentials);

    @GET("bins/gf62h")
    Call<AuthData> debugAuthenticate();
}
