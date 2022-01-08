package com.buzzware.iridedriver.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.buzzware.iridedriver.databinding.ActivityPrivacyPolicyBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PrivacyPolicyActivity extends AppCompatActivity {

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

        binding.privacyTV.setText(getTermsString());
    }

    private String getTermsString() {
        StringBuilder termsString = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("privacy.txt")));

            String str;
            while ((str = reader.readLine()) != null) {
                termsString.append(str);
            }

            reader.close();
            return termsString.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

    }
}