package com.buzzware.iridedriver.Screens;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.buzzware.iridedriver.databinding.ActivityCovidBinding;


public class Covid extends BaseActivity {

    ActivityCovidBinding binding;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCovidBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.include.backIcon.setOnClickListener(v -> onBackPressed());
    }
}
