package com.tamberlab.newz.utils;

import com.microsoft.azure.cognitiveservices.newssearch.Freshness;
import com.microsoft.azure.cognitiveservices.newssearch.SafeSearch;
import com.microsoft.azure.cognitiveservices.newssearch.implementation.NewsInner;
import com.microsoft.azure.cognitiveservices.newssearch.implementation.NewsSearchAPIImpl;
import com.microsoft.rest.credentials.ServiceClientCredentials;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewAPIClient {

    private final boolean isSearchedQuery;

    private String searchTerm;
    private int resultCount;
    private String sortBy;
    private int freshnessTerm;

    private String categoryTerm;
    

    public NewAPIClient(String searchTerm,boolean isSearchedQuery,int resultCount,String sortBy, int freshnessTerm) {
        this.isSearchedQuery = isSearchedQuery;
        this.searchTerm = searchTerm;
        this.resultCount = resultCount;
        this.sortBy = sortBy;
        this.freshnessTerm = freshnessTerm;
    }
    public NewAPIClient(boolean isSearchedQuery,String categoryTerm){
        this.isSearchedQuery = isSearchedQuery;
        this.categoryTerm = categoryTerm;
    }

    private NewsSearchAPIImpl getClient() {
        return new NewsSearchAPIImpl("https://api.bing.microsoft.com/v7.0/", new ServiceClientCredentials() {
            @Override
            public void applyCredentialsFilter(OkHttpClient.Builder clientBuilder) {
                clientBuilder.addNetworkInterceptor(new Interceptor(){
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        Request request;
                        Request original = chain.request();
                        // Request customization: add request headers.
                        Request.Builder requestBuilder = original.newBuilder()
                                .addHeader("Ocp-Apim-Subscription-Key", Constants.BINGNEWSKEY);
                        request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });
            }
        });
    }

    public NewsInner getSearchedNews(){
        NewsSearchAPIImpl client = getClient();
        Freshness freshNess = getFreshness();
        Locale locale = Locale.getDefault();
        String newsMarket = locale.getLanguage() + "-" + locale.getCountry();
        NewsInner newsResult;
        if (isSearchedQuery){
            newsResult = client.searchs().list(searchTerm,null,null,null,null,null,resultCount,
                    freshNess,newsMarket, null,true, SafeSearch.MODERATE,null,sortBy,null,null);
        }else {
            newsResult = client.categorys().list(null,null,null,null,null,categoryTerm,25,
                    null,newsMarket, null,true,SafeSearch.MODERATE,null,null,null);
        }
        return newsResult;
    }

    private Freshness getFreshness(){
        Freshness freshNess;
        switch(freshnessTerm){
            case 1:
                freshNess = Freshness.DAY;
                break;
            case 2:
                freshNess =Freshness.MONTH;
                break;
            case 3:
                freshNess = Freshness.WEEK;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + freshnessTerm);
        }
        return freshNess;
    }
}
