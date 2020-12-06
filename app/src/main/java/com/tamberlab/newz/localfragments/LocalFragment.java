package com.tamberlab.newz.localfragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.tamberlab.newz.R;
import com.tamberlab.newz.neaybycities.Cities;
import com.tamberlab.newz.neaybycities.CitiesModel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LocalFragment extends Fragment {

    private static final String TAG = LocalFragment.class.getSimpleName();
    @BindView(R.id.nearbyLocation)
    Button nearbyLocation;
    @BindView(R.id.currentLocation)
    Button currentLocation;
    @BindView(R.id.no_internt_layout)
    FrameLayout no_internt_layout;
    @BindView(R.id.local_search)
    FrameLayout local_search;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.ConstraintLayout)
    ConstraintLayout constraintLayout;

    //For location
    private static final int REQUEST_CODE_LOCATION = 11;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    // Location Data
    private CitiesModel citiesModel;

    private FragmentManager fragmentManager;
    public static boolean isActive = false;
    public static final int TAB_POSITION = 1;

    public LocalFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentlocal, container, false);
        ButterKnife.bind(this, view);

        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                    progressBar.setVisibility(View.VISIBLE);
                    currentLocation.setVisibility(View.INVISIBLE);
                } else {
                    requestLocationPermission();
                }
            }
        });
        nearbyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Getting cities data");
                CitiesModel citiesData = new ViewModelProvider(requireActivity()).get(CitiesModel.class);
                citiesData.getCities("weymouth","ma","United States").observe(getViewLifecycleOwner(), new Observer<List<Cities>>() {
                    @Override
                    public void onChanged(List<Cities> cities) {

                    }
                });
            }
        });
        return view;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to get local news.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("LOCATION", "NO PERMISSION+++++++++++++++++++++++++++++++++");
        }
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(locationCallback);
                if (locationResult != null && locationResult.getLocations().size() > 0){
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    double locationLatitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    double locationLongitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                    Log.d(TAG, "Results: "+ locationLatitude + " : " + locationLongitude);
                    Geocoder geocoder;
                    List<Address> addresses;
                    String address = null;
                    geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        addresses = geocoder.getFromLocation(locationLatitude, locationLongitude, 1);
                        address = addresses.get(0).getLocality() + " " + addresses.get(0).getAdminArea();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (address != null){
                        progressBar.setVisibility(View.INVISIBLE);
                        constraintLayout.setVisibility(View.INVISIBLE);
                        LocalQuery.searchWord = address;
                        fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.local_search,new LocalQuery()).addToBackStack(null).commit();
                        isActive = true;
                    }
                }
            }
        };
        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showHomeLocal(){
        constraintLayout.setVisibility(View.VISIBLE);
        currentLocation.setVisibility(View.VISIBLE);
    }
}
