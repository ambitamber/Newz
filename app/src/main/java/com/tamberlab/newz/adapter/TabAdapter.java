package com.tamberlab.newz.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tamberlab.newz.fragment.TopBusiness;
import com.tamberlab.newz.fragment.TopEntertainment;
import com.tamberlab.newz.fragment.TopHealth;
import com.tamberlab.newz.fragment.TopScience;
import com.tamberlab.newz.fragment.TopSports;
import com.tamberlab.newz.fragment.TopTechnology;
import com.tamberlab.newz.fragment.TopHeadlines;

public class TabAdapter extends FragmentPagerAdapter{

    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new TopHeadlines();
                break;
            case 1:
                fragment = new TopBusiness();
                break;
            case 2:
                fragment = new TopEntertainment();
                break;
            case 3:
                fragment = new TopSports();
                break;
            case 4:
                fragment = new TopTechnology();
                break;
            case 5:
                fragment = new TopScience();
                break;
            case 6:
                fragment = new TopHealth();
                break;
        }
        assert fragment != null;
        return fragment;
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position){
            case 0:
                title = "Top Headlines";
                break;
            case 1:
                title = "Business";
                break;
            case 2:
                title = "Entertainment";
                break;
            case 3:
                title = "Sports";
                break;
            case 4:
                title = "Technology";
                break;
            case 5:
                title = "Science";
                break;
            case 6:
                title = "Health";
                break;
        }
        return title;
    }
}
