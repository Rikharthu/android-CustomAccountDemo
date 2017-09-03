package com.example.uberv.customaccountdemo;

import android.app.Application;

import com.example.uberv.customaccountdemo.api.ApiService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class App extends Application {

    private static Retrofit sRetrofit;
    private static ApiService sApiService;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        sRetrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://api.myjson.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sApiService = sRetrofit.create(ApiService.class);
    }

    public static Retrofit getRetrofit() {
        return sRetrofit;
    }

    public static void setRetrofit(Retrofit retrofit) {
        sRetrofit = retrofit;
    }

    public static ApiService getApiService() {
        return sApiService;
    }

    public static void setApiService(ApiService apiService) {
        sApiService = apiService;
    }
}
