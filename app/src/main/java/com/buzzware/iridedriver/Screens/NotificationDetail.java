package com.buzzware.iridedriver.Screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iridedriver.databinding.ActivityNotificationDetailBinding;

public class NotificationDetail extends BaseActivity {

    ActivityNotificationDetailBinding binding;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotificationDetailBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.include.backIcon.setOnClickListener(v -> onBackPressed());

        getAndDisplayData();
    }

    private void getAndDisplayData() {

        if (getIntent().getExtras() != null) {

            if (getIntent().getStringExtra("title") != null) {

                binding.titleTV.setText(getIntent().getStringExtra("title"));

            }

            if (getIntent().getStringExtra("msg") != null) {

                binding.messageTV.setText(getIntent().getStringExtra("msg"));

            }

            if (getIntent().getStringExtra("url") != null) {

                binding.picIV.setVisibility(View.VISIBLE);

                Glide.with(NotificationDetail.this)
                        .load(getIntent().getStringExtra("url"))
                        .apply(new RequestOptions().centerCrop())
                        .into(binding.picIV);
            }

        }

    }

    public static void startNotificationDetail(Context c, String title, String msg, String url) {

        c.startActivity(new Intent(c, NotificationDetail.class)
                .putExtra("title", title)
                .putExtra("msg", msg)
                .putExtra("url", url));

    }
}
