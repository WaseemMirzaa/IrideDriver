package com.buzzware.iridedriver.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.FragmentCustomerServiceBinding;

public class CustomerServiceFragment extends Fragment {

    FragmentCustomerServiceBinding binding;

    public CustomerServiceFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_service, container, false);

        setListener();

        return binding.getRoot();
    }

    private void setListener() {

        binding.drawerIcon.setOnClickListener(v -> getActivity().onBackPressed());

        binding.submitBtn.setOnClickListener(v -> {
            if (isValid()) {
                sentEmail();
            }
        });

    }

    public boolean isValid() {

        if (binding.nameET.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Name required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.subjectET.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Name required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.emailET.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Email required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.messageET.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Message required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!binding.emailET.getText().toString().contains("@")) {
            Toast.makeText(getContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!binding.emailET.getText().toString().contains(".com")) {
            Toast.makeText(getContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void sentEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"matti.rehman25@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, binding.subjectET.getText().toString());
        i.putExtra(Intent.EXTRA_TEXT, binding.subjectET.getText().toString());
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

}