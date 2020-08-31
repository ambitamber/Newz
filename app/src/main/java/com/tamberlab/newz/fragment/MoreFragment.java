package com.tamberlab.newz.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tamberlab.newz.About;
import com.tamberlab.newz.MoreFragmentSetting;
import com.tamberlab.newz.firebaselogins.LoginActivity;
import com.tamberlab.newz.R;
import com.tamberlab.newz.firebaselogins.PersonInfo;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.tamberlab.newz.prefrences.PreferencesFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreFragment extends Fragment {


    FragmentManager fragmentManager;

    public MoreFragment(){

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentmore, container, false);
        ButterKnife.bind(this,view);
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.morecontainer,new MoreFragmentSetting()).commit();
        return view;
    }
}
