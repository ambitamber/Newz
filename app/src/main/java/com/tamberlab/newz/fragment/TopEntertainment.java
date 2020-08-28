package com.tamberlab.newz.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tamberlab.newz.R;
import com.tamberlab.newz.WebViewer;
import com.tamberlab.newz.adapter.RecyclerViewAdapter;
import com.tamberlab.newz.model.Articles;
import com.tamberlab.newz.model.News;
import com.tamberlab.newz.utils.NetworkCheck;
import com.tamberlab.newz.utils.ScreenSize;
import com.tamberlab.newz.viewmodel.EntertainmentViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopEntertainment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.no_internt_layout)
    FrameLayout nointernetLayout;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.try_Again_BT)
    Button try_Again_BT;


    private ArrayList<Articles> articlesArrayList;
    RecyclerViewAdapter recyclerViewAdapter;
    GridLayoutManager gridLayoutManager;

    //Save and state of recyclerview
    Bundle mBundleRecyclerViewState;
    Parcelable mListState;
    private final static String KEY_RECYCLER_STATE = "State";
    private boolean dataAvailable = false;

    public TopEntertainment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topfragmentsrecyclerview,container,false);
        ButterKnife.bind(this,view);
        if (savedInstanceState == null) {
            getData();
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        try_Again_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        return view;
    }

    private void getData(){
        if (NetworkCheck.isUp(getContext())){
            EntertainmentViewModel entertainmentViewModel = new ViewModelProvider(requireActivity()).get(EntertainmentViewModel.class);
            entertainmentViewModel.getNews().observe(getViewLifecycleOwner(), new Observer<News>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onChanged(News news) {
                    articlesArrayList = news.getArticles();
                    articlesArrayList.removeIf(articles -> articles.getAuthor() == null
                            || articles.getAuthor().contains(".com")
                            || articles.getAuthor().contains(", ")
                            || articles.getAuthor().contains("]")
                            || articles.getSourceItem().getName().contains("Google News"));
                    createView(articlesArrayList);
                    recyclerViewAdapter.notifyDataSetChanged();
                    showData();
                }
            });
        }else {
            showError();
        }

    }

    private void createView(ArrayList<Articles> articlesArrayList){
        int columnsize = ScreenSize.Size(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(),columnsize);
        recyclerViewAdapter = new RecyclerViewAdapter(articlesArrayList,getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnClickListenerHandler() {
            @Override
            public void onClick(int index) {
                WebViewer.articles = articlesArrayList.get(index);
                Intent intent = new Intent(getContext(), WebViewer.class);
                startActivity(intent);
            }

            @Override
            public void shareButtonClick(int index) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.putExtra(Intent.EXTRA_SUBJECT,articlesArrayList.get(index).getTitle());
                intent.putExtra(Intent.EXTRA_TEXT,  articlesArrayList.get(index).getUrl());
                Intent shareIntent = Intent.createChooser(intent, getString(R.string.share_link));
                startActivity(shareIntent);
            }
        });
    }

    private void showError(){
        nointernetLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showData(){
        nointernetLayout.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        dataAvailable = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dataAvailable && recyclerView.getLayoutManager() != null){
            mBundleRecyclerViewState = new Bundle();
            mListState = recyclerView.getLayoutManager().onSaveInstanceState();
            mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE,mListState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataAvailable && recyclerView.getLayoutManager() != null){
            if (mBundleRecyclerViewState != null){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                        recyclerView.getLayoutManager().onRestoreInstanceState(mListState);
                    }
                },50);
            }
            recyclerView.setLayoutManager(gridLayoutManager);
        }
    }
}
