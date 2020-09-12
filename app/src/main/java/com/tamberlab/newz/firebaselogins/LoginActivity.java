package com.tamberlab.newz.firebaselogins;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tamberlab.newz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.personEmail)
    EditText personEmail;
    @BindView(R.id.personPassword)
    EditText personPassword;
    @BindView(R.id.personSignup)
    Button personSignup;
    @BindView(R.id.personSignin)
    Button personSignin;
    @BindView(R.id.login_progressBar)
    ProgressBar progressBar;
    @BindView(R.id.login_container)
    FrameLayout login_container;
    @BindView(R.id.container)
    FrameLayout signup_container;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.error_Login)
    TextView error_Login;

    private FirebaseAuth firebaseAuth;
    FragmentManager fragmentManager;
    boolean isOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acitivty);

        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
            }
        });

        personSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = personEmail.getText().toString().trim();
                String password = personPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    error_Login.setVisibility(View.INVISIBLE);
                    personEmail.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    error_Login.setVisibility(View.INVISIBLE);
                    personPassword.setError("Password is required.");
                    return;
                }
                if (password.length() < 5){
                    error_Login.setVisibility(View.VISIBLE);
                    error_Login.setText("Password must be greater than 5 characters");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    if (!task.isSuccessful()) {
                                        error_Login.setVisibility(View.VISIBLE);
                                        error_Login.setText("Incorrect Email or Password.");
                                    } else {
                                        finish();
                                        Toast.makeText(LoginActivity.this,"Successfully Logged In",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this,PersonInfo.class));
                                    }
                                }
                            });
                }
            }
        });

        personSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.container, new Signup()).commit();
                isOpened = true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isOpened) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment signUpfragment = fragmentManager.findFragmentById(R.id.container);
            assert signUpfragment != null;
            fragmentTransaction.hide(signUpfragment).commit();
            isOpened = false;
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
        }
    }
}