package com.tamberlab.newz.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tamberlab.newz.model.News;
import com.tamberlab.newz.utils.APIClient;
import com.tamberlab.newz.utils.Constants;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScienceViewModel extends ViewModel {
    private MutableLiveData<News> newsMutableLiveData;

    public LiveData<News> getNews() {

        if (newsMutableLiveData == null){
            newsMutableLiveData = new MutableLiveData<>();
            APIClient.getHeadlineCall(Constants.SCIENCE).enqueue(new Callback<News>() {
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
