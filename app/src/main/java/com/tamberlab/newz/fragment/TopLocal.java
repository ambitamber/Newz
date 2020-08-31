package com.tamberlab.newz.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tamberlab.newz.R;
import com.tamberlab.newz.WebViewer;
import com.tamberlab.newz.adapter.RecyclerViewAdapter;
import com.tamberlab.newz.model.Articles;
import com.tamberlab.newz.utils.NetworkCheck;
import com.tamberlab.newz.utils.ScreenSize;
import com.tamberlab.newz.viewmodel.LocalViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopLocal extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.no_internt_layout)
    FrameLayout nointernetLayout;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    //createview
    private ArrayList<Articles> articlesArrayList;
    RecyclerViewAdapter recyclerViewAdapter;
    GridLayoutManager gridLayoutManager;
    //Save and state of recyclerview
    Bundle mBundleRecyclerViewState;
    Parcelable mListState;
    private final static String KEY_RECYCLER_STATE = "State";
    private boolean dataAvailable = false;
    private String locationName;
    //To get location
    public static GoogleApiClient client;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    public ArrayList<String> permissionsToRequest;
    public ArrayList<String> permissions = new ArrayList<>();
    public ArrayList<String> permissionsRejected = new ArrayList<>();
    public static final long UPDATE_INTERVAL = 5000;
    private LocationRequest locationRequest;
    private static final int ALL_PERMISSIONS_RESULT = 1111;
    private LocationCallback locationCallback;

    public TopLocal() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topfragmentsrecyclerview, container, false);
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (NetworkCheck.isUp(getContext())){
                if (!dataAvailable ) {
                    startLocationUpdates();
                } else {
                    getData(locationName);
                }
            }else {
                showError();
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        if (NetworkCheck.isUp(getContext())){
            if (!dataAvailable) {
                startLocationUpdates();
            } else {
                getData(locationName);
            }
        }else {
            showError();
        }

        client = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .build();
        fusedLocationProviderClient =LocationServices.getFusedLocationProviderClient(getActivity());
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = permissionsToRequestMtd(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }
        return view;
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("LOCATION", "NO PERMISSION+++++++++++++++++++++++++++++++++");
        }
        //request location update
        locationCallback = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    Geocoder geocoder;
                    List<Address> addresses;
                    String address = null;
                    geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        address = addresses.get(0).getLocality() + " " + addresses.get(0).getAdminArea();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (address != null){
                        locationName = address;
                        getData(locationName);
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                        client.disconnect();
                    }
                }
            }
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if (locationAvailability.isLocationAvailable()){
                    Log.d("LOCATION", "Location is available and locationAvailability is " + locationAvailability.isLocationAvailable());
                }
            }
        };
        LocationServices.getFusedLocationProviderClient(getActivity())
                .requestLocationUpdates(locationRequest, locationCallback, null);

    }

    private ArrayList<String> permissionsToRequestMtd(ArrayList<String> permissions) {
        ArrayList<String> results=new ArrayList<>();
        for (String permission : permissions){
            if (!hasPermission(permission)){
                results.add(permission);
            }
        }
        return results;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
            return ActivityCompat.checkSelfPermission(getContext(),permission)== PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            for (String perm : permissionsToRequest) {
                if (!hasPermission(perm)) {
                    permissionsRejected.add(perm);
                }
            }
            if (permissionsRejected.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("These permissions are mandatory to get location")
                                .setPositiveButton("OK", (dialog, which) -> requestPermissions(permissionsRejected.toArray(
                                        new String[permissionsRejected.size()]),
                                        ALL_PERMISSIONS_RESULT)).setNegativeButton("Cancel", null).create().show();
                    }
                }
            } else {
                if (client != null) {
                    client.connect();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getData(String query) {
        LocalViewModel localViewModel = new ViewModelProvider(requireActivity()).get(LocalViewModel.class);
        localViewModel.getNews(query).observe(getViewLifecycleOwner(), news -> {
            articlesArrayList = news.getArticles();
            articlesArrayList.removeIf(articles -> articles.getAuthor()== null
                    || articles.getAuthor().contains(".com")
                    || articles.getAuthor().contains(", ")
                    || articles.getAuthor().contains("]")
                    || articles.getSourceItem().getName().contains("Google News"));
            createView(articlesArrayList);
            recyclerViewAdapter.notifyDataSetChanged();
            showData();
        });
    }

    private void createView(ArrayList<Articles> articlesList) {
        int columnsize = ScreenSize.Size(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), columnsize);
        recyclerViewAdapter = new RecyclerViewAdapter(articlesList,getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnClickListenerHandler() {
            @Override
            public void onClick(int index) {
                WebViewer.articles = articlesList.get(index);
                startActivity(new Intent(getContext(), WebViewer.class));
            }

            @Override
            public void shareButtonClick(int index) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.putExtra(Intent.EXTRA_SUBJECT,articlesList.get(index).getTitle());
                intent.putExtra(Intent.EXTRA_TEXT,  articlesList.get(index).getUrl());
                startActivity(Intent.createChooser(intent, getString(R.string.share_link)));
            }
        });
    }

    private void showError() {
        nointernetLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showData() {
        nointernetLayout.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        dataAvailable = true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location!=null){
                        Log.d("LOCATION","in onConnected--------------------"+location.getLatitude()+"----------------");
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
    public void onStart() {
        super.onStart();
        if (client != null){
            client.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        super.onPause();
        if (dataAvailable && recyclerView.getLayoutManager() != null) {
            client.disconnect();
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            mBundleRecyclerViewState = new Bundle();
            mListState = recyclerView.getLayoutManager().onSaveInstanceState();
            mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, mListState);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        if (dataAvailable && recyclerView.getLayoutManager() != null) {
            if (mBundleRecyclerViewState != null) {
                new Handler().postDelayed(() -> {
                    mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    recyclerView.getLayoutManager().onRestoreInstanceState(mListState);
                }, 50);
            }
            recyclerView.setLayoutManager(gridLayoutManager);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (client == null){
            client.disconnect();
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }
}

