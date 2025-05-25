package com.example.dailyquoteapp.net;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApiService {
    @GET("?encode=json")
    Call<HitokotoResponse> fetchChinese();
}
