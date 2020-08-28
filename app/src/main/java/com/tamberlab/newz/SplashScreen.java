package com.tamberlab.newz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this,MainActivity.class));
                finish();
            }
        },500);
        if (Build.VERSION.SDK_INT >= 21) {
            // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white_color));
            //getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white_color)); //status bar or the time bar at the top
        }
    }
}