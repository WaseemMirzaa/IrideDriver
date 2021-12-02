package com.buzzware.iridedriver.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    FragmentProfileBinding mBinding;
    Context context;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        try{
            Init();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return mBinding.getRoot();
    }

    private void Init() {
        context= getContext();

        ///init click
        mBinding.btnNotifications.setOnClickListener(this);
        mBinding.btnSettings.setOnClickListener(this);
        mBinding.btnChat.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public void onClick(View v) {
        if(v == mBinding.btnNotifications)
        {
            //startActivity(new Intent(this, Notifications.class));
        }else if(v == mBinding.btnSettings)
        {
            //startActivity(new Intent(this, Settings.class));
        }else if(v == mBinding.btnChat)
        {
            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment()).addToBackStack("chat").commit();
        }
    }
}