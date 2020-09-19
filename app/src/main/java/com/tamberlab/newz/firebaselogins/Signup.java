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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionInflater;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tamberlab.newz.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tamberlab.newz.firebaselogins.Login.TRANSITION_NAME;

public class Signup extends Fragment {

    @BindView(R.id.email)
    EditText signupEmail;
    @BindView(R.id.password)
    EditText signupPassword;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    @BindView(R.id.name)
    EditText personName;
    @BindView(R.id.confirm_password)
    EditText confirm_password;
    @BindView(R.id.close_BT)
    FrameLayout close_BT;
    @BindView(R.id.signup_progressBar)
    ProgressBar progressBar;
    @BindView(R.id.signup_Error)
    TextView signup_Error;

    private FirebaseAuth firebaseAuth;
    FragmentManager fragmentManager;
    private DatabaseReference databaseReference;
    String userId;
    String transitionName;

    public Signup() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup, container, false);
        ButterKnife.bind(this, view);
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
                String confirmPassword = confirm_password.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    signup_Error.setVisibility(View.INVISIBLE);
                    personName.setError("Name is required.");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    signup_Error.setVisibility(View.INVISIBLE);
                    signupEmail.setError("Email is required.");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    signup_Error.setVisibility(View.INVISIBLE);
                    signupPassword.setError("Password is required.");
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    signup_Error.setVisibility(View.INVISIBLE);
                    confirm_password.setError("Password is required.");
                    return;
                }
                if (!confirmPassword.equals(password)) {
                    signup_Error.setVisibility(View.VISIBLE);
                    signup_Error.setText("Password does not match.");
                    return;
                }
                if (password.length() < 6) {
                    signup_Error.setVisibility(View.VISIBLE);
                    signup_Error.setText("Password must be greater than six characters.");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        signup_Error.setVisibility(View.VISIBLE);
                                        signup_Error.setText("There is an error setting up a profile.");
                                    } else {
                                        Toast.makeText(getContext(), "Successfully created", Toast.LENGTH_SHORT).show();
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

    private void setUpLogin() {
        final Bundle bundle = getArguments();
        if (bundle != null) {
            transitionName = bundle.getString(TRANSITION_NAME);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            close_BT.setTransitionName(transitionName);
        }
        close_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Login loginFragment = new Login();
                    setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_image_trans));
                    setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                    loginFragment.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_image_trans));
                    loginFragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                    Bundle bundle = new Bundle();
                    bundle.putString(TRANSITION_NAME, transitionName);
                    loginFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.container, loginFragment).addSharedElement(close_BT, transitionName).commit();
                }
            }
        });
    }
}
