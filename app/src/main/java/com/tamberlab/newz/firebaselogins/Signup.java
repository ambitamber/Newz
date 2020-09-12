package com.tamberlab.newz.firebaselogins;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionInflater;

import com.tamberlab.newz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Signup extends Fragment {

    @BindView(R.id.email)
    EditText signupEmail;
    @BindView(R.id.password)
    EditText signupPassword;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    @BindView(R.id.name)
    EditText personName;
    @BindView(R.id.clear)
    FrameLayout clear;
    @BindView(R.id.signup_progressBar)
    ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    FragmentManager fragmentManager;
    private DatabaseReference databaseReference;
    String userId;
    String transitionName;

    public Signup(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup, container, false);
        ButterKnife.bind(this,view);
        setUpLogin();
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

        return view;
    }

    private void setUpLogin(){
        final Bundle bundle = getArguments();
        if (bundle != null) {
            transitionName = bundle.getString("TRANS_NAME");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clear.setTransitionName(transitionName);
        }
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    Login loginFragment = new Login();
                    setSharedElementReturnTransition(TransitionInflater.from(
                            getActivity()).inflateTransition(R.transition.change_image_trans));
                    setExitTransition(TransitionInflater.from(
                            getActivity()).inflateTransition(android.R.transition.fade));

                    loginFragment.setSharedElementEnterTransition(TransitionInflater.from(
                            getActivity()).inflateTransition(R.transition.change_image_trans));
                    loginFragment.setEnterTransition(TransitionInflater.from(
                            getActivity()).inflateTransition(android.R.transition.fade));

                    Bundle bundle = new Bundle();
                    bundle.putString("TRANS_NAME", transitionName);
                    loginFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, loginFragment)
                            .addSharedElement(clear, transitionName)
                            .commit();
                }
            }
        });
    }
}
