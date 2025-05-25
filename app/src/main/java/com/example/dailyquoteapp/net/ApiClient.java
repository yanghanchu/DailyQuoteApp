package com.example.dailyquoteapp.net;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final HttpLoggingInterceptor LOG =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC);

    private static final OkHttpClient CLIENT =
            new OkHttpClient.Builder().addInterceptor(LOG).build();

    public static QuoteApiService getChineseService() {
        return new Retrofit.Builder()
                .baseUrl("https://v1.hitokoto.cn/")
                .client(CLIENT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QuoteApiService.class);
    }
}
