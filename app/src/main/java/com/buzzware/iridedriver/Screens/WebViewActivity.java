package com.buzzware.iridedriver.Screens;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.buzzware.iridedriver.databinding.ActivityWebViewBinding;

public class WebViewActivity extends BaseActivity {

    ActivityWebViewBinding binding;

    String url;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWebViewBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        getExtrasFromIntent();

        init();

        binding.webView.loadUrl(url);


    }

    private void init() {

        binding.include.backIcon.setOnClickListener(v -> onBackPressed());

        binding.include.backAppBarTitle.setText("Stripe Connect");

        binding.webView.getSettings().setJavaScriptEnabled(true);

        binding.webView.setWebViewClient(new WebViewController());
    }

    public class WebViewController extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);

            Log.d("WebView", "shouldOverrideUrlLoading: "+url);

            return true;
        }
    }
    private void getExtrasFromIntent() {

        url = getIntent().getStringExtra("url");
    }
}
