package com.tamberlab.newz.widget;

import com.tamberlab.newz.model.Articles;

import java.util.ArrayList;

public class EventBusResults {

    int mResult;
    ArrayList<Articles> mArticlesArrayList;

    EventBusResults(int resultCode, ArrayList<Articles> articlesArrayList){
        this.mResult = resultCode;
        this.mArticlesArrayList = articlesArrayList;
    }

    public int getmResult() {
        return mResult;
    }

    public ArrayList<Articles> getmArticlesArrayList() {
        return mArticlesArrayList;
    }
}
