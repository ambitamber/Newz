package com.tamberlab.newz.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tamberlab.newz.R;
import com.tamberlab.newz.WebViewer;
import com.tamberlab.newz.adapter.RecyclerViewAdapter;
import com.tamberlab.newz.model.Articles;
import com.tamberlab.newz.model.News;
import com.tamberlab.newz.prefrences.PreferencesSetting;
import com.tamberlab.newz.utils.Constants;
import com.tamberlab.newz.utils.NetworkCheck;
import com.tamberlab.newz.utils.ScreenSize;
import com.tamberlab.newz.utils.ServiceGenerator;
import com.google.android.material.appbar.AppBarLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = SearchFragment.class.getSimpleName();

    @BindView(R.id.search_ErrorTV)
    TextView errorTV;
    @BindView(R.id.search_Progressbar)
    ProgressBar progressBar;
    @BindView(R.id.search_Recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.search_results)
    TextView searchResults;

    GridLayoutManager gridLayoutManager;
    private ArrayList<Articles> articlesArrayList;
    RecyclerViewAdapter recyclerViewAdapter;
    private ServiceGenerator serviceGenerator = ServiceGenerator.getInstance();
    News news;
    String mQuery;
    //For Lifecycle
    Bundle mBundleRecyclerViewState;
    Parcelable mListState;
    private final static String KEY_RECYCLER_STATE = "State";
    private boolean dataAvailable = false;

    public SearchFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentsearch, container, false);

        ButterKnife.bind(this,view);


        if (dataAvailable){
            setupSharedPreferences(mQuery);
        }else {
            initSearchView();
        }

        return view;
    }

    private void getData(final String query,String sortBy, int pageSize,String language) {
        hideKeyboard();
        if (NetworkCheck.isUp(getContext())) {
            ServiceGenerator.context = getContext();
            serviceGenerator.getApi().geteverything(sortBy, pageSize,language, query, Constants.API_KEY)
                    .enqueue(new Callback<News>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(@NotNull Call<News> call, @NotNull Response<News> response) {
                            news = response.body();
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.setProgress(100);
                            articlesArrayList = new ArrayList<>();
                            articlesArrayList = news.getArticles();
                            articlesArrayList.removeIf(articles -> articles.getAuthor() == null
                                    || articles.getAuthor().contains(".com")
                                    || articles.getAuthor().contains(", ")
                                    || articles.getAuthor().contains("]")
                                    || articles.getSourceItem().getName().contains("Google News"));
                            searchResults.setText("Total Results: " + news.getTotalResults());
                            createView(articlesArrayList);
                            showData();
                        }

                        @Override
                        public void onFailure(@NotNull Call<News> call, @NotNull Throwable t) {
                            showError(t.getMessage());
                        }
                    });
        }else {
            showError(getString(R.string.no_internet));
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view == null) {
            view = new View(getContext());
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void createView(ArrayList<Articles> articles){
        int columnSize = ScreenSize.Size(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), columnSize);
        recyclerViewAdapter = new RecyclerViewAdapter(articles,getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        onClicked(articles);
    }

    private void showData(){
        progressBar.setVisibility(View.INVISIBLE);
        errorTV.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        searchResults.setVisibility(View.VISIBLE);
        dataAvailable = true;
    }

    private void showError(String error){
        progressBar.setVisibility(View.INVISIBLE);
        errorTV.setVisibility(View.VISIBLE);
        errorTV.setText(error);
        recyclerView.setVisibility(View.INVISIBLE);
        searchResults.setVisibility(View.INVISIBLE);
    }

    private void initSearchView(){
        if (mQuery != null){
            setupSharedPreferences(mQuery);
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_fragment_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
    }

    private void onClicked(ArrayList<Articles> articles){
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnClickListenerHandler() {
            @Override
            public void onClick(int index) {
                WebViewer.articles = articlesArrayList.get(index);
                startActivity(new Intent(getContext(), WebViewer.class));
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_sort_by_key)) || key.equals(getString(R.string.settings_language_key)) || key.equals(getString(R.string.settings_min_result_key))){
            initSearchView();
        }
    }

    private void setupSharedPreferences(final String query) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
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
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
        PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
        if (dataAvailable && recyclerView.getLayoutManager() != null){
            mBundleRecyclerViewState = new Bundle();
            mListState = recyclerView.getLayoutManager().onSaveInstanceState();
            mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE,mListState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        if (dataAvailable && recyclerView.getLayoutManager() != null){
            if (mBundleRecyclerViewState != null){
                new Handler().postDelayed(() -> {
                    mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    recyclerView.getLayoutManager().onRestoreInstanceState(mListState);
                },50);
            }
            recyclerView.setLayoutManager(gridLayoutManager);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState called");
        outState.putString("query",mQuery);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated called");
        if (savedInstanceState != null){
            String query = savedInstanceState.getString("query");
            if (query != null){
                setupSharedPreferences(query);
            }
        }
    }

}
