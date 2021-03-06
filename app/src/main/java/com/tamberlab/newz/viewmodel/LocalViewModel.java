package com.tamberlab.newz.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tamberlab.newz.model.News;
import com.tamberlab.newz.utils.APIClient;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocalViewModel  extends ViewModel {
    private MutableLiveData<News> newsMutableLiveData;

    public LiveData<News> getNews(String query, Context context) {

        if (newsMutableLiveData == null) {
            newsMutableLiveData = new MutableLiveData<>();
            APIClient.getEverythingCall("publishedAt", 20, query,context).enqueue(new Callback<News>() {
                @Override
                public void onResponse(@NotNull Call<News> call, @NotNull Response<News> response) {
                    newsMutableLiveData.setValue(response.body());
                }

                @Override
                public void onFailure(Call<News> call, Throwable t) {

                }
            });
        }
        return newsMutableLiveData;
    }
}
