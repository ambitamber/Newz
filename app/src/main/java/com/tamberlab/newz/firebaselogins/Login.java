package com.tamberlab.newz.firebaselogins;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.transition.TransitionInflater;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tamberlab.newz.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends Fragment {

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.email)
    EditText emailEditText;
    @BindView(R.id.password)
    EditText passwordEditText;
    @BindView(R.id.login)
    Button loginButton;
    @BindView(R.id.forgotPassword)
    Button forgotPasswordButton;
    @BindView(R.id.login_Error)
    TextView login_Error;
    @BindView(R.id.login_progressBar)
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    LoginActivity loginActivity;
    String TransitionName;
    public static final String TRANSITION_NAME = "TRANS_NAME";

    public Login() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);
        ButterKnife.bind(this, view);

        firebaseAuth = FirebaseAuth.getInstance();
        loginActivity = new LoginActivity();
        openSignup();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    login_Error.setVisibility(View.INVISIBLE);
                    emailEditText.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    login_Error.setVisibility(View.INVISIBLE);
                    passwordEditText.setError("Password is required.");
                    return;
                }
                if (password.length() < 6) {
                    login_Error.setVisibility(View.VISIBLE);
                    login_Error.setText("Password must be 6 or more characters");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    if (!task.isSuccessful()) {
                                        login_Error.setVisibility(View.VISIBLE);
                                        login_Error.setText("Incorrect Email or Password.");
                                    } else {
                                        getActivity().finish();
                                        startActivity(new Intent(getContext(), PersonInfo.class));
                                    }
                                }
                            });
                }
            }
        });
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = emailEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(email)) {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Email sent", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                } else {
                    emailEditText.setError("Please enter your email.");
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        return view;
    }

    private void openSignup() {
        if (getArguments() != null) {
            final Bundle bundle = getArguments();
            TransitionName = bundle.getString(TRANSITION_NAME);

            //set transition
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                floatingActionButton.setTransitionName(TransitionName);
            }
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Signup signup = new Signup();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    floatingActionButton.setTransitionName("trans_clear");
                    setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_image_trans));
                    setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                    signup.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_image_trans));
                    signup.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                    TransitionName = floatingActionButton.getTransitionName();
                    Bundle bundle = new Bundle();
                    bundle.putString(TRANSITION_NAME, TransitionName);
                    signup.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, signup).addSharedElement(floatingActionButton, TransitionName).commit();
                }
            }
        });
    }
}
