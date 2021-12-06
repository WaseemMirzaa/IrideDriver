package com.buzzware.iridedriver.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.ActivityNotificationsBinding;

public class Notifications extends AppCompatActivity {

    ActivityNotificationsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();

    }

    private void setListener() {

        binding.drawerIcon.setOnClickListener(v->{
            finish();
        });

    }
}