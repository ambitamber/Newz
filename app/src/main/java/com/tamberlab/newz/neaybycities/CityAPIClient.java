package com.tamberlab.newz.neaybycities;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class CityAPIClient {

    private static Retrofit retrofit;

    public static Retrofit getRetrofit(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        if (retrofit == null){
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl("https://29034.wayscript.io/")
                    //.client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public interface CityAPIService {

        @GET("?")
        Call<List<Cities>> getNearByCities(
                @Query("city") String cityName,
                @Query("state") String stateName,
                @Query("country") String countryName
        );
    }
}
