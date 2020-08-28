package com.tamberlab.newz.prefrences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.os.Build;
import android.os.Bundle;

import com.tamberlab.newz.R;
import com.google.android.material.appbar.AppBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreferencesSetting extends AppCompatActivity {

    @BindView(R.id.pref_appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.pref_toolbar)
    Toolbar toolbar;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_setting);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle("Filters");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.pref_container,new PreferencesFragment()).commit();

    }
}