package com.tamberlab.newz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.tamberlab.newz.firebaselogins.LoginActivity;
import com.tamberlab.newz.firebaselogins.PersonInfo;
import com.tamberlab.newz.model.Articles;
import com.tamberlab.newz.model.SourceItem;
import com.tamberlab.newz.utils.NetworkCheck;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewer extends AppCompatActivity {

    public static Articles articles;
    SourceItem sourceItem;
    Menu menu;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    boolean isSaved;
    String userID;

    @BindView(R.id.webviewer)
    WebView webView;
    @BindView(R.id.webviewer_toolbar)
    Toolbar toolbar;
    @BindView(R.id.webviewer_appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_text)
    TextView toolbar_text;
    @BindView(R.id.webviewer_progressBar)
    ProgressBar progressBar;
    @BindView(R.id.appBarImage)
    ImageView appBarImage;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.webviewer_SwipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.no_internt_layout)
    FrameLayout nointernetLayout;
    @BindView(R.id.try_Again_BT)
    Button try_Again_BT;
    @BindView(R.id.adView)
    AdView adView;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_viewer);

        ButterKnife.bind(this);
        sourceItem = articles.getSourceItem();
        //For Firebase
        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            userID = firebaseAuth.getCurrentUser().getUid();
            databaseReference.child(userID).child("article").child(articles.getAuthor() + " " + articles.getPublishedAt()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()){
                        isSaved = data.exists();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        //For toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar_text.setText(sourceItem.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
        Picasso.get().load(articles.getUrlToImage()).fit().centerCrop().into(appBarImage, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                appBarImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                appBarImage.setImageResource(R.drawable.error_image);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadWebViewer();
            swipeRefreshLayout.setRefreshing(false);
        });

        try_Again_BT.setOnClickListener(v -> loadWebViewer());

        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(Intent.EXTRA_SUBJECT, articles.getTitle());
            intent.putExtra(Intent.EXTRA_TEXT, articles.getUrl());
            Intent shareIntent = Intent.createChooser(intent, getString(R.string.share_link));
            startActivity(shareIntent);
        });


        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        loadWebViewer();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webviewer_menu, menu);
        this.menu = menu;
        if (menu != null){
            if (isSaved){
                menu.findItem(R.id.save_BT).setIcon(R.drawable.ic_baseline_bookmark_24);
            }else{
                menu.findItem(R.id.save_BT).setIcon(R.drawable.baseline_bookmark_border_white_24);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_BT:
                saveItem();
                break;
            case R.id.about_BT:
                startActivity(new Intent(WebViewer.this, About.class));
                break;
            case R.id.savedArticles_BT:
                if (isSignedIn()){
                    startActivity(new Intent(WebViewer.this, PersonInfo.class));
                }else{
                    startActivity(new Intent(WebViewer.this,LoginActivity.class));
                }
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case R.id.open_browser:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(articles.getUrl()));
                startActivity(browserIntent);
                break;
        }
        return true;
    }

    private void showError() {
        progressBar.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.INVISIBLE);
        nointernetLayout.setVisibility(View.VISIBLE);
    }

    private void showData() {
        progressBar.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.VISIBLE);
        nointernetLayout.setVisibility(View.INVISIBLE);
    }

    private void saveItem() {
        if (isSignedIn()) {
            if (isSaved){
                databaseReference.child(userID).child("article").child(articles.getAuthor() + " " + articles.getPublishedAt()).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(WebViewer.this, "Article removed",Toast.LENGTH_SHORT).show();
                            isSaved = false;
                            menu.findItem(R.id.save_BT).setIcon(R.drawable.baseline_bookmark_border_white_24);
                        });
            }else{
                databaseReference.child(userID).child("article").child(articles.getAuthor() + " " + articles.getPublishedAt()).setValue(articles)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(WebViewer.this, "Article saved",Toast.LENGTH_SHORT).show();
                            isSaved = true;
                            menu.findItem(R.id.save_BT).setIcon(R.drawable.ic_baseline_bookmark_24);
                        })
                        .addOnFailureListener(e -> Toast.makeText(WebViewer.this, "Article failed to save",Toast.LENGTH_SHORT).show());
            }
        }else {
            startActivity(new Intent(WebViewer.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void loadWebViewer(){
        if (NetworkCheck.isUp(this)){
            webView.setWebViewClient(new MyBrowser());
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    progressBar.setProgress(newProgress);
                    if (newProgress == 100) {
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            });
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            webView.loadUrl(articles.getUrl());
            showData();
        }else {
            showError();
        }
    }
    private static class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }
    }

    private boolean isSignedIn(){
        return firebaseAuth.getCurrentUser() != null;
    }
}