package com.joseortale.ortalesoft.ips.api;

import com.joseortale.ortalesoft.ips.model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("students/validate_student")
    Call<ApiResponse> validateStudent(@Query("cardID") String cardID);
}
