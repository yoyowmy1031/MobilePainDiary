package com.example.mobilepaindiary.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobilepaindiary.HomeActivity;
import com.example.mobilepaindiary.R;
import com.example.mobilepaindiary.databinding.SignInFragmentBinding;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;


public class SignInFragment extends Fragment {

    private SignInFragmentBinding signInBinding;
    private FirebaseAuth auth;
    String txt_email;
    String txt_pwd;


    public SignInFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment
        auth = FirebaseAuth.getInstance();
        signInBinding = SignInFragmentBinding.inflate(inflater, container, false);
        View view = signInBinding.getRoot();

        // sign in button
        signInBinding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_pwd = signInBinding.password.getText().toString().trim();
                txt_email = signInBinding.email.getText().toString().trim();
                if (verifyUserInfo(txt_email, txt_pwd)) {
                    try {
                        auth.signInWithEmailAndPassword(txt_email, txt_pwd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                signInBinding.notice.setText(txt_email + " Successfully logged in");

                                // redirect to Home Activity with the user email transferred with intent
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                intent.putExtra("email", txt_email);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                signInBinding.notice.setText("The user doesn't exist, or the password doesn't match");
                            }
                        });
                    } catch (Exception e) {
                        signInBinding.notice.setText("Unexpected Error. Please check your network.");
                    }
                } else {
                    signInBinding.notice.setText("Please enter all details correctly");
                }
            }
        });


        // redirect to sign up page with sign up button clicked
        signInBinding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container_view, new SignUpFragment());
                transaction.commit();
            }
        });

        // The password show/hide switch
        signInBinding.showPwdSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signInBinding.showPwdSwitch.isChecked())
                    signInBinding.password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    signInBinding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        return view;
    }

    /**
     * Verify if the email and password are in valid format
     *
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
            signInBinding.notice.setText(getString(R.string.invalid_password));
            valid = false;
        }
        if (!pattern.matcher(email).matches()) {
            signInBinding.notice.setText(getString(R.string.invalid_username));
            valid = false;
        }
        return valid;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        signInBinding = null;
    }

}