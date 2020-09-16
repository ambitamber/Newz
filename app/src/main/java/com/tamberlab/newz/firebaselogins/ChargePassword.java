package com.tamberlab.newz.firebaselogins;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tamberlab.newz.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChargePassword extends Fragment {

    @BindView(R.id.newPassword)
    EditText newPassword;
    @BindView(R.id.confirmPassword)
    EditText confirmPassword;
    @BindView(R.id.passwordCharge_error)
    TextView passwordCharge_error;
    @BindView(R.id.chargepassword_BT)
    Button chargepassword_BT;


    FirebaseAuth firebaseAuth;

    public ChargePassword() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chargepassword, container, false);
        ButterKnife.bind(this, view);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        chargepassword_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = newPassword.getText().toString().trim();
                String comfirmpassword = confirmPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    passwordCharge_error.setVisibility(View.INVISIBLE);
                    newPassword.setError("Password is required");
                    return;
                }
                if (TextUtils.isEmpty(comfirmpassword)) {
                    passwordCharge_error.setVisibility(View.INVISIBLE);
                    confirmPassword.setError("Re-enter your password");
                    return;
                }
                if (password.equals(comfirmpassword) && !TextUtils.isEmpty(password)) {
                    user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                passwordCharge_error.setVisibility(View.INVISIBLE);
                                newPassword.setText(null);
                                confirmPassword.setText(null);
                                Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    passwordCharge_error.setVisibility(View.VISIBLE);
                    passwordCharge_error.setText("Password does not Match.");
                }
            }
        });

        return view;
    }
}
