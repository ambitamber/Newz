package com.tamberlab.newz.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tamberlab.newz.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalFragment extends Fragment {

    @BindView(R.id.nearbyLocation)
    Button nearbyLocation;
    @BindView(R.id.currentLocation)
    Button currentLocation;

    public LocalFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentlocal, container, false);
        ButterKnife.bind(this,view);
        

        return view;
    }
}
