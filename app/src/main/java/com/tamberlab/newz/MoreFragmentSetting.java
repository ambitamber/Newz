package com.tamberlab.newz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tamberlab.newz.firebaselogins.LoginActivity;
import com.tamberlab.newz.firebaselogins.PersonInfo;

public class MoreFragmentSetting extends PreferenceFragmentCompat  {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Preference sign_in_key;
    public MoreFragmentSetting() {

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.morefragment);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = firebaseDatabase.getInstance();
        sign_in_key = findPreference("key_sign_in");
        getFirebaseUser();

        Preference about_newz_key = findPreference("key_about_newz");
        about_newz_key.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getContext(),About.class));
                return true;
            }
        });

        Preference share_newz_key = findPreference("key_share_newz");
        share_newz_key.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getContext(),"Coming soon",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Preference send_feedback_key = findPreference("key_send_feedback");
        send_feedback_key.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"ambitamber@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Newz App Feedback");
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                return true;
            }
        });
    }

    private void getFirebaseUser() {
        if (firebaseAuth.getCurrentUser() != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    sign_in_key.setTitle("Signed In as: " + snapshot.child("Name").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            sign_in_key.setSummary("Manage your account and see saved articles.");
            sign_in_key.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getContext(), PersonInfo.class));
                    return true;
                }
            });
        }else {
            sign_in_key.setTitle(getString(R.string.sign_in));
            sign_in_key.setSummary("Sign into account to view saved articles and manage your account.");
            sign_in_key.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    return true;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getFirebaseUser();
    }
}
