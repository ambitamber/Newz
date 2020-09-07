package com.tamberlab.newz.firebaselogins;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tamberlab.newz.R;
import com.tamberlab.newz.WebViewer;
import com.tamberlab.newz.model.Articles;
import com.tamberlab.newz.utils.NetworkCheck;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonInfo extends AppCompatActivity {

    @BindView(R.id.userEmail)
    TextView userEmail;
    @BindView(R.id.signOutcon)
    ConstraintLayout signOutButton;
    @BindView(R.id.person_info_Toolbar)
    Toolbar toolbar;
    @BindView(R.id.person_info_AppBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.person_info_Recycler)
    RecyclerView recyclerView;
    @BindView(R.id.no_internt_layout)
    FrameLayout nointernetLayout;
    @BindView(R.id.try_Again_BT)
    Button try_Again_BT;
    @BindView(R.id.name_tv)
    TextView name_tv;
    @BindView(R.id.chargePasswordcon)
    ConstraintLayout chagePassword;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<Articles,ListAdapterHolder> adapter;
    private FirebaseRecyclerOptions<Articles> options;
    String userId;
    private boolean dataAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(userId);
        userEmail.setText(firebaseAuth.getCurrentUser().getEmail());
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonInfo.this);
                builder.setMessage("Do you want to sign out?");
                builder.setTitle("Sign out?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.signOut();
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        hide_show();
    }
    private void getData(){
        options = new FirebaseRecyclerOptions.Builder<Articles>()
                .setQuery(databaseReference.child("article"),Articles.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Articles,ListAdapterHolder>(options){
            @NonNull
            @Override
            public ListAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.firebase_list, parent, false);
                return new ListAdapterHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ListAdapterHolder holder, int position, @NonNull Articles articles) {
                Picasso.get().load(articles.getUrlToImage()).into(holder.imageView);
                holder.textView.setText(articles.getTitle());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WebViewer.articles = articles;
                        startActivity(new Intent(PersonInfo.this,WebViewer.class));
                    }
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name_tv.setText(snapshot.child("Name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static class ListAdapterHolder extends RecyclerView.ViewHolder  {

        @BindView(R.id.firebase_news_IV)
        ImageView imageView;
        @BindView(R.id.firebase_news_TV)
        TextView textView;
        public ListAdapterHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (NetworkCheck.isUp(this)){
            getData();
            showData();
        } else {
            showError();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkCheck.isUp(this)){
            adapter.startListening();
            showData();
        } else {
            showError();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (NetworkCheck.isUp(this) && dataAvailable){
            adapter.stopListening();
        }
    }

    private void showData(){
        recyclerView.setVisibility(View.VISIBLE);
        nointernetLayout.setVisibility(View.INVISIBLE);
        dataAvailable = true;
    }
    private void showError(){
        recyclerView.setVisibility(View.INVISIBLE);
        nointernetLayout.setVisibility(View.VISIBLE);
    }

    private void hide_show(){
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) >= 95) {
                    // Collapsed
                    signOutButton.setVisibility(View.INVISIBLE);
                } else if (verticalOffset == 0) {
                    // Expanded
                    signOutButton.setVisibility(View.VISIBLE);
                    chagePassword.setVisibility(View.VISIBLE);
                } else {
                    // Somewhere in between
                    chagePassword.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}