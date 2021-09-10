package com.example.mobilepaindiary.login;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mobilepaindiary.R;
import com.example.mobilepaindiary.databinding.SignUpFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;


public class SignUpFragment extends Fragment {

    private SignUpFragmentBinding signUpBinding;
    private FirebaseAuth auth;
    String email_txt;
    String password_txt;

    public SignUpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment
        auth = FirebaseAuth.getInstance();
        signUpBinding = SignUpFragmentBinding.inflate(inflater, container, false);
        View view = signUpBinding.getRoot();

        // The password show/hide switch
        signUpBinding.showPwdSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signUpBinding.showPwdSwitch.isChecked())
                    signUpBinding.password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    signUpBinding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        // sign up button
        signUpBinding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password_txt = signUpBinding.password.getText().toString().trim();
                email_txt = signUpBinding.email.getText().toString().trim();

                if (verifyUserInfo(email_txt, password_txt)) {
                    try {
                        auth.createUserWithEmailAndPassword(email_txt, password_txt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Successfully signed up", Toast.LENGTH_LONG).show();
                                    // start the sign in fragment to sign in
                                    replaceSignInFragment();
                                } else {
                                    Toast.makeText(getActivity(), "Registration failed.", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Unknown exception happened", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(getActivity(), "Please enter all details correctly", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    /**
     * @param email
     * @param password
     * @return true if email and password are both valid
     */
    public boolean verifyUserInfo(String email, String password) {
        boolean valid = true;
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        // At least 8 characters 1 uppercase 1 lowercase 1 number
        String pwdPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$";
        if (!password.matches(pwdPattern) || password == null) {
            signUpBinding.notice.setText(getString(R.string.invalid_password));
            valid = false;
        }
        if (!pattern.matcher(email).matches()) {
            signUpBinding.notice.setText(getString(R.string.invalid_username));
            valid = false;
        }
        return valid;
    }

    /**
     * replace fragment in main activity to sign in fragment
     */
    public void replaceSignInFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_view, new SignInFragment());
        transaction.commit();
    }

    /**
     * default on Destroy method
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        signUpBinding = null;
    }
}

