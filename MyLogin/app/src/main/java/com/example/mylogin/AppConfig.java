package com.example.mylogin;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppConfig {
    private static String BASE_URL = "http://192.249.19.242:7080/";
    static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}


