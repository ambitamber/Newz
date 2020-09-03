package com.tamberlab.newz.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tamberlab.newz.model.News;
import com.tamberlab.newz.utils.APIClient;
import com.tamberlab.newz.utils.ApiService;
import com.tamberlab.newz.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopViewModel extends ViewModel {
    private MutableLiveData<News> newsMutableLiveData;

    public LiveData<News> getNews(Context context) {

        if (newsMutableLiveData == null){
            newsMutableLiveData = new MutableLiveData<>();
            ApiService apiService = APIClient.getRetrofit().create(ApiService.class);
            Call<News> call = apiService.getheadlines(getCountryName(context), Constants.API_KEY);
            call.enqueue(new Callback<News>() {
                @Override
                public void onResponse(Call<News> call, Response<News> response) {
                    newsMutableLiveData.setValue(response.body());
                }

                @Override
                public void onFailure(Call<News> call, Throwable t) {

                }
            });
        }
        return newsMutableLiveData;
    }

    private static String getCountryName(Context context){
        String countryName = context.getResources().getConfiguration().locale.getCountry();
        return  countryName;
    }
}
