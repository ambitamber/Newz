package com.tamberlab.newz.neaybycities;



import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CitiesModel extends ViewModel {
    private MutableLiveData<List<Cities>> citiesViewModel;

    public LiveData<List<Cities>> getCities (String cityName, String stateName, String countryName){
        citiesViewModel = new MutableLiveData<>();
        CityAPIClient.CityAPIService cityAPIService = CityAPIClient.getRetrofit().create(CityAPIClient.CityAPIService.class);
        Call<List<Cities>> call = cityAPIService.getNearByCities(cityName,stateName,countryName);
        call.enqueue(new Callback<List<Cities>>() {
            @Override
            public void onResponse(Call<List<Cities>> call, Response<List<Cities>> response) {
                if (response.isSuccessful()){
                    citiesViewModel.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Cities>> call, Throwable t) {
                Log.d("NearByCitesList Error",t.getMessage());
            }
        });
        return citiesViewModel;
    }
}
