package com.buzzware.iridedriver.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.buzzware.iridedriver.databinding.ActivityPrivacyPolicyBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PrivacyPolicyActivity extends BaseActivity {

    ActivityPrivacyPolicyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setView();
        setListener();

    }

    private void setListener() {

        binding.appBar.backIcon.setOnClickListener(v->{
            finish();
        });

    }

    private void setView() {

        binding.appBar.backAppBarTitle.setText("Privacy Policy");

//        binding.webView.getSettings().setJavaScriptEnabled(true); // enable javascript

//        final Activity activity = this;
//
//        binding.webView.setWebViewClient(new WebViewClient() {
//            @SuppressWarnings("deprecation")
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//
//                showErrorAlert(description);
//
//            }
//            @TargetApi(android.os.Build.VERSION_CODES.M)
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
//
//                showErrorAlert(rerr.getDescription().toString());
//
//                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
//            }
//        });

//        binding.webView.loadUrl("https://www.irideshareusa.com");
    }
//
//    private String getTermsString() {
//        StringBuilder termsString = new StringBuilder();
//        BufferedReader reader;
//        try {
//            reader = new BufferedReader(
//                    new InputStreamReader(getAssets().open("privacy.txt")));
//
//            String str;
//            while ((str = reader.readLine()) != null) {
//                termsString.append(str);
//            }
//
//            reader.close();
//            return termsString.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        finish();
//
//    }
//
//    public class WebViewController extends WebViewClient {
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
////            view.loadUrl(url);
//            return true;
//        }
//    }
}