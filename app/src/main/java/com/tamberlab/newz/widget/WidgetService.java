package com.tamberlab.newz.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import com.bumptech.glide.Glide;
import com.tamberlab.newz.R;
import com.tamberlab.newz.WebViewer;
import com.tamberlab.newz.model.Articles;
import com.tamberlab.newz.model.News;
import com.tamberlab.newz.utils.APIClient;
import com.tamberlab.newz.utils.ApiService;
import com.tamberlab.newz.utils.Constants;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WidgetService extends RemoteViewsService {

    public static final String TAG = WidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetItemFactory(getApplicationContext(),intent);
    }

    public static class WidgetItemFactory implements RemoteViewsFactory{

        private Context context;
        News news;
        private ArrayList<Articles> articlesList = new ArrayList<>();
        public static Articles articles;
        int appWidgetId;

        public WidgetItemFactory(Context applicationContext, Intent intent) {
            this.context = applicationContext;
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Bundle bundle = intent.getBundleExtra("bundle");
            if (bundle != null){
                articlesList = bundle.getParcelableArrayList(NewzWidget.ARTICLELIST);
            }
        }

        private void getData(){
            ApiService apiService = APIClient.getRetrofit().create(ApiService.class);
            Call<News> call = apiService.getheadlines("us", Constants.API_KEY);
            call.enqueue(new Callback<News>() {
                @Override
                public void onResponse(Call<News> call, Response<News> response) {
                    news = response.body();
                    articlesList = news.getArticles();
                    EventBus.getDefault().post(new EventBusResults(1,articlesList));
                }

                @Override
                public void onFailure(Call<News> call, Throwable t) {
                    Log.i(TAG, t.getMessage());
                }
            });
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if (articlesList != null && articlesList.size() > 0){
                return;
            }
            getData();
        }

        @Override
        public void onDestroy() {
            articlesList = null;
        }

        @Override
        public int getCount() {
            if (articlesList == null) {
                return 0;
            }
            return articlesList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list);
            remoteViews.setTextViewText(R.id.widget_title,articlesList.get(position).getTitle());
            try {
                Bitmap bitmap = Glide.with(context).asBitmap().load(articlesList.get(position).getUrlToImage()).submit().get();
                remoteViews.setImageViewBitmap(R.id.widget_image,bitmap);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Bundle extras = new Bundle();

            Intent clickIntent = new Intent(context, WebViewer.class);
            WebViewer.articles = articlesList.get(position);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent,0 );
            remoteViews.setOnClickPendingIntent(R.id.widget_list_view,pendingIntent);
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
