package com.tamberlab.newz.utils;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.tamberlab.newz.SearchActivty;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FireStoreLoader {

    private FirebaseFirestore database;
    SearchActivty searchActivty;

    public void getWordsList(){
        database = FirebaseFirestore.getInstance();
        searchActivty = new SearchActivty();
        DocumentReference docRef = database.collection("Google Trend Data").document("india");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Object objectData = documentSnapshot.getData();
                String jsonData = new Gson().toJson(objectData);

            }
        });
    }

    public class Words {
        String word;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }
    }
}
