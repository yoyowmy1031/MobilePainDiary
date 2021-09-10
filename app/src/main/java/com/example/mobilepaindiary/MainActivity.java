package com.example.mobilepaindiary;


import android.os.Bundle;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobilepaindiary.databinding.ActivityMainBinding;

import com.example.mobilepaindiary.login.SignInFragment;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        replaceFragment(new SignInFragment());
    }

    private void replaceFragment(Fragment nextFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, nextFragment)
                .commit();
    }
}
