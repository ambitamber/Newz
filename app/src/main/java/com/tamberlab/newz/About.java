package com.tamberlab.newz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class About extends AppCompatActivity {

    @BindView(R.id.about_newsapi)
    TextView about_newsapi;
    @BindView(R.id.about_slack)
    TextView slack;
    @BindView(R.id.about_freelogodesign)
    TextView freelogodesign;
    @BindView(R.id.about_udacity)
    TextView about_udacity;
    @BindView(R.id.about_material)
    TextView about_material;
    @BindView(R.id.about_github)
    TextView about_github;
    @BindView(R.id.about_androidStudio)
    TextView about_androidStudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);

        //News APi link
        clickLink(about_newsapi,"https://newsapi.org/");

        //Slack Overflow link
        clickLink(slack,"https://stackoverflow.com/");

        // Free Logo Design
        clickLink(freelogodesign,"https://www.freelogodesign.org/");

        // Udacity's Android Developer Link
        clickLink(about_udacity,"https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801");

        // Material Desing link
        clickLink(about_material, "https://material.io/");

        //Github link
        clickLink(about_github,"https://github.com/");

        //Android Studio Link
        clickLink(about_androidStudio,"https://developer.android.com/studio/");
    }

    private void clickLink(TextView name, String link){
        name.setOnClickListener(view -> {
             Intent browser= new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browser);
        });
    }
}