package com.tamberlab.newz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
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

public class MoreFragmentSetting extends PreferenceFragmentCompat  implements SharedPreferences.OnSharedPreferenceChangeListener{

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
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Newz App by Tamber Labs");
                intent.putExtra(Intent.EXTRA_TEXT, "Newz App by Tamber Labs" + "\n" + "https://play.google.com/store/apps/details?id=com.tamberlab.newz");
                Intent shareIntent = Intent.createChooser(intent, getString(R.string.share_link));
                startActivity(shareIntent);
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

        Preference version_number = findPreference("version_number");
        version_number.setSummary("v" +BuildConfig.VERSION_NAME);
        version_number.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getContext(),"Always Improving",Toast.LENGTH_SHORT).show();
                return false;
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference != null){
            String value = sharedPreferences.getString(preference.getKey(), "");
            if (preference instanceof ListPreference) {
                // For list preferences, figure out the label of the selected value
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(value);
                if (prefIndex >= 0) {
                    // Set the summary to that label
                    listPreference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else if (preference instanceof EditTextPreference) {
                // For EditTextPreferences, set the summary to the value's simple string representation.
                preference.setSummary(value);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
