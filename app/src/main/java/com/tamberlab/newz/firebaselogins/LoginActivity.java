package com.tamberlab.newz.firebaselogins;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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

    private FirebaseAuth firebaseAuth;
    FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acitivty);

        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();

        personSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = personEmail.getText().toString().trim();
                String password = personPassword.getText().toString().trim();
                
                if (TextUtils.isEmpty(email)){
                   personEmail.setError("Email is required!");
                   return;
                }
                if (TextUtils.isEmpty(password)){
                    personPassword.setError("Password is required.");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)){
                    firebaseAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    if (!task.isSuccessful()){
                                        if (password.length() < 6) {
                                            personPassword.setError("Password must be greater than six characters.");
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                                        }
                                    }else {
                                        finish();
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
                    fragmentManager.beginTransaction().add(R.id.container,new Signup()).commit();
            }
        });
    }
}