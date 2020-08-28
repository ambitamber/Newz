package com.tamberlab.newz.utils;

import com.tamberlab.newz.model.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("v2/top-headlines")
    Call<News> getheadlines(
            @Query("country") String country,
            @Query("apikey") String key
    );

    @GET("v2/top-headlines")
    Call<News> getheadlinesCat(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apikey") String key
    );

    @GET("v2/everything")
    Call<News> geteverything(
            @Query("sortby") String sortby,
            @Query("pageSize") int pageSize,
            @Query("language") String language,
            @Query("q") String q,
            @Query("apikey") String key);
}
