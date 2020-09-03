package com.tamberlab.newz.localfragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tamberlab.newz.R;
import com.tamberlab.newz.WebViewer;
import com.tamberlab.newz.adapter.RecyclerViewAdapter;
import com.tamberlab.newz.model.Articles;
import com.tamberlab.newz.utils.ScreenSize;
import com.tamberlab.newz.viewmodel.LocalViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalQuery extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public static String searchWord;
    GridLayoutManager gridLayoutManager;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<Articles> articlesArrayList;
    public LocalQuery(){

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.localquery, container, false);
        ButterKnife.bind(this,view);
        getData(searchWord);
        return view;
    }

    private void createView(ArrayList<Articles> articlesList) {
        int columnsize = ScreenSize.Size(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), columnsize);
        recyclerViewAdapter = new RecyclerViewAdapter(articlesList,getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnClickListenerHandler() {
            @Override
            public void onClick(int index) {
                WebViewer.articles = articlesList.get(index);
                startActivity(new Intent(getContext(), WebViewer.class));
            }

            @Override
            public void shareButtonClick(int index) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.putExtra(Intent.EXTRA_SUBJECT,articlesList.get(index).getTitle());
                intent.putExtra(Intent.EXTRA_TEXT,  articlesList.get(index).getUrl());
                startActivity(Intent.createChooser(intent, getString(R.string.share_link)));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getData(String query){
        LocalViewModel localViewModel = new ViewModelProvider(requireActivity()).get(LocalViewModel.class);
        localViewModel.getNews(query,getContext()).observe(getViewLifecycleOwner(), news -> {
            articlesArrayList = news.getArticles();
            articlesArrayList.removeIf(articles -> articles.getAuthor() == null
                    || articles.getAuthor().contains(".com")
                    || articles.getAuthor().contains(", ")
                    || articles.getAuthor().contains("]")
                    || articles.getSourceItem().getName().contains("Google News"));
            createView(articlesArrayList);
            recyclerViewAdapter.notifyDataSetChanged();
            showData();
        });
    }

    private void showData() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
