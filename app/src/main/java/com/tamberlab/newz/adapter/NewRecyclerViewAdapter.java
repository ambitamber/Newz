package com.tamberlab.newz.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.microsoft.azure.cognitiveservices.newssearch.implementation.NewsInner;
import com.tamberlab.newz.R;
import com.tamberlab.newz.model.Articles;
import com.tamberlab.newz.model.SourceItem;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewRecyclerViewAdapter extends RecyclerView.Adapter<NewRecyclerViewAdapter.RecyclerViewHolder>  {

    private NewsInner newsInner;
    private OnClickListenerHandler onClickListenerHandler;
    private Context context;

    public NewRecyclerViewAdapter(NewsInner newsInner,Context context) {
        this.newsInner = newsInner;
        this.context = context;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumbnail_IV)
        ImageView thumbnail;
        @BindView(R.id.title_TV)
        TextView title;
        @BindView(R.id.publishedAt_TV)
        TextView publishedat;
        @BindView(R.id.cardView)
        CardView cardView;
        @BindView(R.id.source_TV)
        TextView source;
        @BindView(R.id.share_IV)
        ImageButton share_IV;
        @BindView(R.id.progressBar_image)
        ProgressBar progressBar_image;

        public RecyclerViewHolder(@NonNull View itemView,final OnClickListenerHandler listenerHandler) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            thumbnail.setMaxHeight(600);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenerHandler != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listenerHandler.onClick(position);
                        }
                    }
                }
            });
            share_IV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenerHandler != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listenerHandler.shareButtonClick(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForItem = R.layout.list;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(layoutForItem, parent, false);
        return new RecyclerViewHolder(view,onClickListenerHandler);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.title.setText(newsInner.value().get(position).name());
        holder.publishedat.setText(dateTime(newsInner.value().get(position).datePublished()));
        holder.source.setText(newsInner.value().get(position).provider().get(0).name());

        String imageurl = null;
        if (newsInner.value().get(position).image().contentUrl() != null){
            imageurl = newsInner.value().get(position).image().contentUrl();
        }else{
            imageurl = newsInner.value().get(position).image().thumbnail().contentUrl();
        }
        Glide.with(context).load(imageurl)
                .apply(new RequestOptions())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar_image.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar_image.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        if (newsInner.value() == null) {
            return 0;
        }
        return newsInner.value().size();
    }

    public interface OnClickListenerHandler {
        void onClick(int index);
        void shareButtonClick(int index);
    }
    public void setOnItemClickListener(OnClickListenerHandler listener) {
        onClickListenerHandler = listener;
    }
    private String dateTime(String newsDate) {
        Locale locale = Locale.getDefault();

        PrettyTime prettyTime = new PrettyTime(new Locale(locale.getCountry().toLowerCase()));
        String time = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:", Locale.ENGLISH);
            Date date = simpleDateFormat.parse(newsDate);
            time = prettyTime.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
}


