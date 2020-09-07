package com.tamberlab.newz;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.tamberlab.newz.adapter.RecyclerViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.mauker.materialsearchview.MaterialSearchView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivty extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar search_toolbar;
    @BindView(R.id.searchView) MaterialSearchView searchView;


    private FirebaseFirestore database;
    String mQuery = null;
    GridLayoutManager gridLayoutManager;
    RecyclerViewAdapter recyclerViewAdapter;

    boolean dataAvailable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);
        setSupportActionBar(search_toolbar);
        getSupportActionBar().setTitle(null);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchView.openSearch();
                addSuggestions();
            }
        }, 300);
        searchViewText();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search){
            searchView.openSearch();
        }
        return true;
    }

    private void searchViewText(){
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mQuery = query;
                Log.i("SearchActivty",mQuery);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (searchView.isOpen()){
            searchView.closeSearch();
        }else{
            super.onBackPressed();
        }
    }

    public void addSuggestions(){
        database = FirebaseFirestore.getInstance();
        String countryName = getResources().getConfiguration().locale.getCountry();
        DocumentReference docRef = database.collection("Google Trend Data").document(countryName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Object objectData = documentSnapshot.getData();
                String jsonData = new Gson().toJson(objectData);
                List<String> wordsList = null;
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    wordsList = new ArrayList<>();
                    for (int i = 0; i < jsonObject.length(); i++){
                        JSONObject eachItem = jsonObject.getJSONObject(String.valueOf(i));
                        String eachWord = eachItem.getString("word");
                        wordsList.add(eachWord);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(searchView != null){
                    searchView.addSuggestions(wordsList);
                }
            }
        });
    }
}
