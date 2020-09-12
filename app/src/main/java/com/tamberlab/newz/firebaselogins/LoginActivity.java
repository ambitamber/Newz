package com.tamberlab.newz.firebaselogins;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tamberlab.newz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    FragmentManager fragmentManager;
    boolean isOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acitivty);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new Login()).commit();
    }

    @Override
    public void onBackPressed() {
        if (isOpened) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment signUpfragment = fragmentManager.findFragmentById(R.id.container);
            assert signUpfragment != null;
            fragmentTransaction.hide(signUpfragment).commit();
            isOpened = false;
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
        }
    }
}