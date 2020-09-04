package com.tamberlab.newz;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.tamberlab.newz.utils.FireStoreLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivty extends AppCompatActivity {

    @BindView(R.id.search_toolbar)
    Toolbar search_toolbar;
    @BindView(R.id.search_appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.searchView)
    MaterialSearchView searchView;
    @BindView(R.id.search_Progressbar)
    ProgressBar progressBar;
    @BindView(R.id.search_Recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.search_results)
    TextView searchResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        setSupportActionBar(search_toolbar);
        getSupportActionBar().setTitle(null);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchView.showSearch();
            }
        }, 300);

    }

    
}
