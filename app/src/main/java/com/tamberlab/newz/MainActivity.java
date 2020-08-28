package com.tamberlab.newz;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.tamberlab.newz.adapter.BottomNavFragmentAdapter;
import com.tamberlab.newz.fragment.HomeFragment;
import com.tamberlab.newz.fragment.MoreFragment;
import com.tamberlab.newz.fragment.SearchFragment;
import com.tamberlab.newz.utils.NoSwipePager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottomNavigationView)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.container)
    NoSwipePager viewPager;

    private BottomNavFragmentAdapter pagerAdapter;
    HomeFragment homeFragment = new HomeFragment();
    SearchFragment searchFragment = new SearchFragment();
    MoreFragment moreFragment = new MoreFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPagingEnabled(false);
        pagerAdapter = new BottomNavFragmentAdapter(getSupportFragmentManager());

        pagerAdapter.addFragments(homeFragment);
        pagerAdapter.addFragments(searchFragment);
        pagerAdapter.addFragments(moreFragment);

        viewPager.setAdapter(pagerAdapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_search:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_more:
                        viewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });
    }
}