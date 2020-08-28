package com.tamberlab.newz.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class News implements Parcelable {

    private String status;
    private int totalResults;
    private ArrayList<Articles> articles;

    protected News(Parcel in) {
        status = in.readString();
        totalResults = in.readInt();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    //For api status
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    //For total results
    public int getTotalResults() {
        return totalResults;
    }
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    //For list all of the news articles
    public ArrayList<Articles> getArticles() {
        return articles;
    }
    public void setArticles(ArrayList<Articles> articles) {
        this.articles = articles;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeInt(totalResults);
    }
}