package com.tamberlab.newz.utils;

import com.tamberlab.newz.model.News;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static Retrofit retrofit;

    public static Retrofit getRetrofit(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        if (retrofit == null){
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Constants.MAIN_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Call getHeadlineCall(String category) {
        ApiService apiService = getRetrofit().create(ApiService.class);
        Call<News> call = apiService.getheadlinesCat("us",category,Constants.API_KEY);
        return call;
    }

    public static Call getEverythingCall(String sortBy, int pageSize,String query){
        ApiService apiService = getRetrofit().create(ApiService.class);
        Call<News> call = apiService.geteverything(sortBy,pageSize,"en",query,Constants.API_KEY);
        return call;
    }
}
