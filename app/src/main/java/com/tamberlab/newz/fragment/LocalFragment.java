package com.tamberlab.newz.fragment;

import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.tamberlab.newz.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @BindView(R.id.nearbyLocation)
    Button nearbyLocation;
    @BindView(R.id.currentLocation)
    Button currentLocation;

    public static GoogleApiClient client;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    public ArrayList<String> permissionsToRequest;
    public ArrayList<String> permissions = new ArrayList<>();
    public ArrayList<String> permissionsRejected = new ArrayList<>();
    public static final long UPDATE_INTERVAL = 5000;
    private LocationRequest locationRequest;
    private static final int ALL_PERMISSIONS_RESULT = 1111;
    private LocationCallback locationCallback;

    public LocalFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentlocal, container, false);
        ButterKnife.bind(this,view);


        return view;
    }



    public void nearbyLocation(List<Address> addresses){

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
