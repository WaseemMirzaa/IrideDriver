package com.buzzware.iridedriver.Screens;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.buzzware.iridedriver.databinding.ActivityAboutBinding;

public class AboutUs extends BaseActivity {

    ActivityAboutBinding binding;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAboutBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.include.backIcon.setOnClickListener(v -> onBackPressed());
    }
}
