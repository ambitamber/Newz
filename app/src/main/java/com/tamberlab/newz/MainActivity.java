package com.tamberlab.newz;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tamberlab.newz.fragment.HomeFragment;
import com.tamberlab.newz.fragment.LocalFragment;
import com.tamberlab.newz.fragment.MoreFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottomNavigationView)
    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    LocalFragment localFragment = new LocalFragment();
    MoreFragment moreFragment = new MoreFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        fm.beginTransaction().add(R.id.main_Container, moreFragment, "3").hide(moreFragment).commit();
        fm.beginTransaction().add(R.id.main_Container, localFragment, "2").hide(localFragment).commit();
        fm.beginTransaction().add(R.id.main_Container,homeFragment, "1").commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        fm.beginTransaction().hide(active).show(homeFragment).commit();
                        active = homeFragment;
                        return true;
                    case R.id.navigation_local:
                        fm.beginTransaction().hide(active).show(localFragment).commit();
                        active = localFragment;
                        return true;
                    case R.id.navigation_more:
                        fm.beginTransaction().hide(active).show(moreFragment).commit();
                        active = moreFragment;
                        return true;
                }
                return false;
            }
        });
    }
}