package com.tamberlab.newz;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tamberlab.newz.fragment.HomeFragment;
import com.tamberlab.newz.fragment.MoreFragment;
import com.tamberlab.newz.localfragments.LocalFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tamberlab.newz.localfragments.LocalFragment.isActive;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottomNavigationView)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.main_appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_text)
    TextView toolbar_text;

    HomeFragment homeFragment = new HomeFragment();
    LocalFragment localFragment = new LocalFragment();
    MoreFragment moreFragment = new MoreFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        fm.beginTransaction().add(R.id.main_Container, moreFragment, "3").hide(moreFragment).commit();
        fm.beginTransaction().add(R.id.main_Container, localFragment, "2").hide(localFragment).commit();
        fm.beginTransaction().add(R.id.main_Container,homeFragment, "1").commit();
        bottomNavigationView.getMenu().findItem(R.id.navigation_Home).setChecked(true);
        toolbar_text.setText("Newz");
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_Home:
                        fm.beginTransaction().hide(active).show(homeFragment).commit();
                        active = homeFragment;
                        item.setChecked(true);
                        toolbar_text.setText("Newz");
                        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                        menu.findItem(R.id.app_bar_search).setVisible(true);
                        return true;
                    case R.id.navigation_Local:
                        fm.beginTransaction().hide(active).show(localFragment).commit();
                        active = localFragment;
                        item.setChecked(true);
                        toolbar_text.setText("Local");
                        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
                        menu.findItem(R.id.app_bar_search).setVisible(true);
                        return true;
                    case R.id.navigation_More:
                        fm.beginTransaction().hide(active).show(moreFragment).commit();
                        active = moreFragment;
                        item.setChecked(true);
                        toolbar_text.setText("Setting");
                        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
                        menu.findItem(R.id.app_bar_search).setVisible(false);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (active == homeFragment){
            super.onBackPressed();
        }else if (active == moreFragment){
            fm.beginTransaction().hide(active).show(homeFragment).commit();
            active = homeFragment;
            bottomNavigationView.getMenu().findItem(R.id.navigation_Home).setChecked(true);
            toolbar_text.setText("Newz");
            menu.findItem(R.id.app_bar_search).setVisible(true);
        }else {
            if (isActive){
                getSupportFragmentManager().popBackStack();
                isActive = false;
                localFragment.showHomeLocal();
            }else{
                fm.beginTransaction().hide(active).show(homeFragment).commit();
                active = homeFragment;
                bottomNavigationView.getMenu().findItem(R.id.navigation_Home).setChecked(true);
                toolbar_text.setText("Newz");
                menu.findItem(R.id.app_bar_search).setVisible(true);
            }
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
}