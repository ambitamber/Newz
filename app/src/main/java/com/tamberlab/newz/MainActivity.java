package com.tamberlab.newz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tamberlab.newz.adapter.FragmentUpdateCallback;
import com.tamberlab.newz.adapter.MainFragmentPagerAdapter;
import com.tamberlab.newz.adapter.NoSwipePager;
import com.tamberlab.newz.fragment.HomeFragment;
import com.tamberlab.newz.fragment.MoreFragment;
import com.tamberlab.newz.fragment.VideosFragment;
import com.tamberlab.newz.localfragments.LocalFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements FragmentUpdateCallback {

    @BindView(R.id.bottomNavigationView)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.main_appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_text)
    TextView toolbar_text;
    @BindView(R.id.main_Container)
    NoSwipePager viewPager;

    Menu menu;
    MainFragmentPagerAdapter mPagerAdapter;
    private int mCurrentTabPosition;
    AppBarLayout.LayoutParams params;
    LocalFragment localFragment = new LocalFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        mPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setPagingEnabled(false);

        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        toolbar_text.setText("Newz");

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_Home:
                        mCurrentTabPosition = HomeFragment.TAB_POSITION;
                        viewPager.setCurrentItem(mCurrentTabPosition);
                        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                        menu.findItem(R.id.app_bar_search).setVisible(true);
                        toolbar_text.setText("Newz");
                        return true;
                    case R.id.navigation_Local:
                        mCurrentTabPosition = LocalFragment.TAB_POSITION;
                        viewPager.setCurrentItem(mCurrentTabPosition);
                        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
                        menu.findItem(R.id.app_bar_search).setVisible(true);
                        toolbar_text.setText("Local");
                        return true;
                    case R.id.navigation_Videos:
                        mCurrentTabPosition = VideosFragment.TAB_POSITION;
                        viewPager.setCurrentItem(mCurrentTabPosition);
                        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
                        toolbar_text.setText("Videos");
                        menu.findItem(R.id.app_bar_search).setVisible(true);
                        return true;
                    case R.id.navigation_More:
                        mCurrentTabPosition = MoreFragment.TAB_POSITION;
                        viewPager.setCurrentItem(mCurrentTabPosition);
                        toolbar_text.setText("Setting");
                        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
                        menu.findItem(R.id.app_bar_search).setVisible(false);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!LocalFragment.isActive){
            if (mCurrentTabPosition != HomeFragment.TAB_POSITION){
                mCurrentTabPosition = HomeFragment.TAB_POSITION;
                viewPager.setCurrentItem(mCurrentTabPosition);
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                menu.findItem(R.id.app_bar_search).setVisible(true);
                toolbar_text.setText("Newz");
                bottomNavigationView.getMenu().findItem(R.id.navigation_Home).setChecked(true);
            }else{
                if (!mPagerAdapter.removeFragment(mPagerAdapter.getItem(mCurrentTabPosition), mCurrentTabPosition)) {
                    finish();
                }
            }
        }else{
            getSupportFragmentManager().popBackStack();
            LocalFragment.isActive = false;
            localFragment.showHomeLocal();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_search){
            startActivity(new Intent(MainActivity.this, SearchActivty.class));
            overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addFragment(Fragment fragment, int tabPosition) {
        mPagerAdapter.updateFragment(fragment,tabPosition);
    }
}