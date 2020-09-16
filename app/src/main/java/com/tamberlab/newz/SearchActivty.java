package com.tamberlab.newz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.tamberlab.newz.adapter.RecyclerViewAdapter;
import com.tamberlab.newz.model.Articles;
import com.tamberlab.newz.model.News;
import com.tamberlab.newz.prefrences.PreferencesSetting;
import com.tamberlab.newz.utils.Constants;
import com.tamberlab.newz.utils.NetworkCheck;
import com.tamberlab.newz.utils.ScreenSize;
import com.tamberlab.newz.utils.ServiceGenerator;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.mauker.materialsearchview.MaterialSearchView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivty extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.toolbar) Toolbar search_toolbar;
    @BindView(R.id.searchView) MaterialSearchView searchView;
    @BindView(R.id.search_RecyclerView) RecyclerView search_RecyclerView;
    @BindView(R.id.no_Internt) ConstraintLayout no_internet;
    @BindView(R.id.search_bt) ConstraintLayout search_bt;
    @BindView(R.id.search_Word) TextView search_Word;
    @BindView(R.id.search_progressBar) ProgressBar progressBar;

    String mQuery = null;
    GridLayoutManager gridLayoutManager;
    RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Articles> articlesArrayList;
    private ServiceGenerator serviceGenerator = ServiceGenerator.getInstance();
    News news;
    boolean dataAvailable = false;
    SharedPreferences sharedPreferences;

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
                searchView.openSearch();
                addSuggestions();
            }
        }, 300);
        initSearchView();
        search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.openSearch();
                if (mQuery != null){
                    searchView.setHint(mQuery);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        search_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in,R.anim.slide_out_down);
            }
        });
    }

    private void createView(ArrayList<Articles> articles){
        gridLayoutManager = new GridLayoutManager(this, ScreenSize.Size(this));
        recyclerViewAdapter = new RecyclerViewAdapter(articles,this);
        search_RecyclerView.setAdapter(recyclerViewAdapter);
        search_RecyclerView.setLayoutManager(gridLayoutManager);
        search_RecyclerView.setHasFixedSize(true);
        search_RecyclerView.setItemViewCacheSize(20);
        search_RecyclerView.setDrawingCacheEnabled(true);
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnClickListenerHandler() {
            @Override
            public void onClick(int index) {
                WebViewer.articles = articles.get(index);
                startActivity(new Intent(SearchActivty.this, WebViewer.class));
            }

            @Override
            public void shareButtonClick(int index) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.putExtra(Intent.EXTRA_SUBJECT,articles.get(index).getTitle());
                intent.putExtra(Intent.EXTRA_TEXT,  articles.get(index).getUrl());
                startActivity( Intent.createChooser(intent, "Share link"));
            }
        });
    }

    private void getData(final String query,String sortBy, int pageSize,String language) {
        hideKeyboard();
        searchView.closeSearch();
        progressBar.setVisibility(View.VISIBLE);
        if (NetworkCheck.isUp(this)) {
            serviceGenerator.context = this;
            serviceGenerator.getApi().geteverything(sortBy, pageSize,language, query, Constants.API_KEY)
                    .enqueue(new Callback<News>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(@NotNull Call<News> call, @NotNull Response<News> response) {
                            news = response.body();
                            articlesArrayList = new ArrayList<>();
                            articlesArrayList = news.getArticles();
                            articlesArrayList.removeIf(articles -> articles.getAuthor() == null);
                            articlesArrayList.forEach(articles -> articles.setAuthor(articles.getAuthor().replace(".","-")));
                            articlesArrayList.forEach(articles -> articles.setAuthor(articles.getAuthor().replace("]","-")));
                            articlesArrayList.forEach(articles -> articles.setAuthor(articles.getAuthor().replace("[","-")));
                            articlesArrayList.forEach(articles -> articles.setAuthor(articles.getAuthor().replace("$","-")));
                            articlesArrayList.forEach(articles -> articles.setAuthor(articles.getAuthor().replace("#","-")));
                            articlesArrayList.forEach(articles -> articles.setAuthor(articles.getAuthor().replace("/","-")));
                            createView(articlesArrayList);
                        }

                        @Override
                        public void onFailure(@NotNull Call<News> call, @NotNull Throwable t) {
                        }
                    });
            showData();
        }else {
            showError();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter){
            startActivity(new Intent(this, PreferencesSetting.class));
        }
        return true;
    }

    private void initSearchView(){
        if (mQuery != null){
            setupSharedPreferences(mQuery);
            search_Word.setText(mQuery);
            return;
        }
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mQuery = query;
                search_Word.setText(mQuery);
                setupSharedPreferences(mQuery);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupSharedPreferences(final String query) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy = sharedPreferences.getString(
                getString(R.string.settings_sort_by_key),
                getString(R.string.settings_sort_by_publishedAt_value));
        String language = sharedPreferences.getString(
                getString(R.string.settings_language_key),
                getString(R.string.settings_language_en_value));
        String resultSize = sharedPreferences.getString(
                getString(R.string.settings_min_result_key),
                getString(R.string.settings_min_result_default));
        getData(query,sortBy,Integer.parseInt(resultSize),language);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isOpen()){
            searchView.closeSearch();
        }else{
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in,R.anim.slide_out_down);
        }
    }

    public void addSuggestions(){
        if (NetworkCheck.isUp(this)){
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            String countryName = getResources().getConfiguration().locale.getCountry();
            DocumentReference docRef = database.collection("Google Trend Data").document(countryName);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Object objectData = documentSnapshot.getData();
                    String jsonData = new Gson().toJson(objectData);
                    List<String> wordsList = null;
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        wordsList = new ArrayList<>();
                        for (int i = 0; i < jsonObject.length(); i++){
                            JSONObject eachItem = jsonObject.getJSONObject(String.valueOf(i));
                            String eachWord = eachItem.getString("word");
                            wordsList.add(eachWord);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(searchView != null){
                        assert wordsList != null;
                        searchView.addSuggestions(wordsList);
                    }
                }
            });
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_sort_by_key)) || key.equals(getString(R.string.settings_language_key)) || key.equals(getString(R.string.settings_min_result_key))){
            initSearchView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void showData(){
        search_RecyclerView.setVisibility(View.VISIBLE);
        no_internet.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        dataAvailable = true;
    }

    private void showError(){
        search_RecyclerView.setVisibility(View.INVISIBLE);
        no_internet.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
