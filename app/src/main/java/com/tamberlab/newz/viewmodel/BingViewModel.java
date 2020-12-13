package com.tamberlab.newz.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.microsoft.azure.cognitiveservices.newssearch.implementation.NewsInner;
import com.tamberlab.newz.utils.NewAPIClient;

public class BingViewModel extends ViewModel {
    private MutableLiveData<NewsInner>  mutableLiveData;
    private NewAPIClient newAPIClient;
    public LiveData<NewsInner> getNews(){

        if(mutableLiveData == null){
            mutableLiveData = new MutableLiveData<>();
            newAPIClient = new NewAPIClient(false,null);
            mutableLiveData.setValue(newAPIClient.getSearchedNews());
        }
        return mutableLiveData;
    }
}

