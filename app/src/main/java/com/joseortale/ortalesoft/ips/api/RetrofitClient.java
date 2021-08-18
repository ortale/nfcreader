package com.joseortale.ortalesoft.ips.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String API_BASE_URL = "http://192.168.1.119:8081/api/";

    public static ApiService getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS).build();
        return new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }
}
