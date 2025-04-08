package com.example.apitranscations;

import com.example.apitranscations.Model.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);


    @GET("transactions")
    Call<List<Transaction>> getTransactions(@Header("Authorization") String token);
}
