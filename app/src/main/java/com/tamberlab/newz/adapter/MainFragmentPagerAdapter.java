package com.tamberlab.newz.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tamberlab.newz.fragment.HomeFragment;
import com.tamberlab.newz.fragment.MoreFragment;
import com.tamberlab.newz.fragment.VideosFragment;
import com.tamberlab.newz.localfragments.LocalFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final List<Fragment> BASE_FRAGMENTS = Arrays.asList(new HomeFragment(), new LocalFragment(), new VideosFragment(),new MoreFragment());
    private static final int HOME_POSITION = 0;
    private static final int LOCAL_POSITION = 1;
    private static final int VIDEO_POSITION = 2;
    private static final int MORE_POSITION = 3;
    //endregion
    //region Fields
    private List<Fragment> mHomeFragment;
    private List<Fragment> mSearchFragments;
    private List<Fragment> mVideoFragments;
    private List<Fragment> mMoreFragment;
    //endregion
    //region constructor
    public MainFragmentPagerAdapter(@NonNull FragmentManager fragmentManager) {
        super(fragmentManager);
        mHomeFragment = new ArrayList<>();
        mSearchFragments = new ArrayList<>();
        mVideoFragments = new ArrayList<>();
        mMoreFragment = new ArrayList<>();
    }

    @Override
    @NonNull
    public Fragment getItem(int position) {
        if (position == HOME_POSITION) {
            if (mHomeFragment.isEmpty()) {
                return BASE_FRAGMENTS.get(position);
            }
            return mHomeFragment.get(mHomeFragment.size() - 1);
        } else if (position == LOCAL_POSITION) {
            if (mSearchFragments.isEmpty()) {
                return BASE_FRAGMENTS.get(position);
            }
            return mSearchFragments.get(mSearchFragments.size() - 1);
        } else if(position == VIDEO_POSITION){
            if (mVideoFragments.isEmpty()) {
                return BASE_FRAGMENTS.get(position);
            }
            return mVideoFragments.get(mVideoFragments.size() - 1);
        }else {
            if (mMoreFragment.isEmpty()) {
                return BASE_FRAGMENTS.get(position);
            }
            return mMoreFragment.get(mMoreFragment.size() - 1);
        }
    }

    @Override
    public int getCount() {
        return BASE_FRAGMENTS.size();
    }

    @Override
    public long getItemId(int position) {
        if (position == HOME_POSITION
                && getItem(position).equals(BASE_FRAGMENTS.get(position))) {
            return HOME_POSITION;
        } else if (position == LOCAL_POSITION
                && getItem(position).equals(BASE_FRAGMENTS.get(position))) {
            return LOCAL_POSITION;
        } else if (position == LOCAL_POSITION
                && getItem(position).equals(BASE_FRAGMENTS.get(position))) {
            return VIDEO_POSITION;
        }else if (position == MORE_POSITION
                && getItem(position).equals(BASE_FRAGMENTS.get(position))) {
            return MORE_POSITION;
        }
        return getItem(position).hashCode();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public void updateFragment(Fragment fragment, int position) {
        if (!BASE_FRAGMENTS.contains(fragment)) {
            addInnerFragment(fragment, position);
        }
        notifyDataSetChanged();
    }


    public boolean removeFragment(Fragment fragment, int position) {
        if (position == HOME_POSITION) {
            if (mHomeFragment.contains(fragment)) {
                removeInnerFragment(fragment, mHomeFragment);
                return true;
            }
        } else if (position == LOCAL_POSITION) {
            if (mSearchFragments.contains(fragment)) {
                removeInnerFragment(fragment, mSearchFragments);
                return true;
            }
        } else if (position == MORE_POSITION) {
            if (mMoreFragment.contains(fragment)) {
                removeInnerFragment(fragment, mMoreFragment);
                return true;
            }
        }
        return false;
    }

    private void removeInnerFragment(Fragment fragment, List<Fragment> tabFragments) {
        tabFragments.remove(fragment);
        notifyDataSetChanged();
    }
    private void addInnerFragment(Fragment fragment, int position) {
        if (position == HOME_POSITION) {
            mHomeFragment.add(fragment);
        } else if (position == LOCAL_POSITION) {
            mSearchFragments.add(fragment);
        }else if(position == VIDEO_POSITION){
            mVideoFragments.add(fragment);
        } else {
            mMoreFragment.add(fragment);
        }
    }

}
