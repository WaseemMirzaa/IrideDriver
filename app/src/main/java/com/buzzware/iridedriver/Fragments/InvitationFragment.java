package com.buzzware.iridedriver.Fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.FragmentInvitationBinding;

public class InvitationFragment extends Fragment {

    FragmentInvitationBinding binding;

    public InvitationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invitation, container, false);

        setListener();

        return binding.getRoot();


    }

    private void setListener() {

        binding.drawerIcon.setOnClickListener(v->{
            getActivity().onBackPressed();
        });

    }
}