package com.example.apitranscations;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.apitranscations.Adapter.TransactionAdapter;
import com.example.apitranscations.Model.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.prepstripe.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        RecyclerView recycler = findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        Button logoutBtn = findViewById(R.id.btnLogout);

        String token = getIntent().getStringExtra("TOKEN");
        if (token != null) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            ApiService api = retrofit.create(ApiService.class);
            api.getTransactions(token).enqueue(new Callback<List<Transaction>>() {
                @Override
                public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        recycler.setAdapter(new TransactionAdapter(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<List<Transaction>> call, Throwable t) {
                    Toast.makeText(TransactionActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
                }
            });
        }

        logoutBtn.setOnClickListener(v -> {
            clearToken();
            startActivity(new Intent(TransactionActivity.this, MainActivity.class));
            finish();
        });
    }

    private void clearToken() {
        try {
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    "secure_prefs",
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            sharedPreferences.edit().clear().apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}