package com.tamberlab.newz.firebaselogins;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tamberlab.newz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Signup extends Fragment {

    @BindView(R.id.signup_Email)
    EditText signupEmail;
    @BindView(R.id.signup_password)
    EditText signupPassword;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    @BindView(R.id.sign_in_button)
    Button signInButton;
    @BindView(R.id.signin_ProgressBar)
    ProgressBar progressBar;
    @BindView(R.id.personName)
    TextInputEditText personName;

    private FirebaseAuth firebaseAuth;
    FragmentManager fragmentManager;
    private DatabaseReference databaseReference;
    String userId;
    boolean signedup = false;

    public Signup(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup, container, false);

        ButterKnife.bind(this,view);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fragmentManager = getActivity().getSupportFragmentManager();
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                String name = personName.getText().toString().trim();

                if (TextUtils.isEmpty(name)){
                    personName.setError("Name is required.");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    signupEmail.setError("Email is required.");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    signupPassword.setError("Password is required.");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    firebaseAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        if (password.length() < 6){
                                            signupPassword.setError("Password must be greater than six characters.");
                                        }
                                    }else {
                                        Toast.makeText(getContext(),"Successfully created",Toast.LENGTH_SHORT).show();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
                                        fragmentTransaction.hide(fragment).commit();
                                        userId = firebaseAuth.getCurrentUser().getUid();
                                        databaseReference.child(userId).child("Name").setValue(name);
                                    }
                                }
                            });
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment signUpfragment = fragmentManager.findFragmentById(R.id.container);
                fragmentTransaction.hide(signUpfragment).commit();
            }
        });

        return view;
    }
}
