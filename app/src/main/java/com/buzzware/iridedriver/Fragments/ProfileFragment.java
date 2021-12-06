package com.buzzware.iridedriver.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.EditProfileActivity;
import com.buzzware.iridedriver.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    FragmentProfileBinding binding;

    Context context;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        try {
            Init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getCurrentUserData();

        setListener();

        return binding.getRoot();
    }


    private void setListener() {

        binding.editIcon.setOnClickListener(v->{

            startActivity(new Intent(getContext(), EditProfileActivity.class));

        });

        binding.btnChat.setOnClickListener(v->{

        });

    }

    private void getCurrentUserData() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && user.getUid() != null) {

            DocumentReference documentReferenceBuisnessUser = firebaseFirestore.collection("Users").document(user.getUid());
            documentReferenceBuisnessUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    User user = value.toObject(User.class);

                    setUserData(user);

                }
            });

        }

    }

    private void setUserData(User user) {

        if (user.image != null && !user.image.isEmpty()) {
            Glide.with(getContext()).load(user.image).apply(new RequestOptions().centerCrop()).into(binding.userImageIV);
        }
        binding.userNameTV.setText(user.firstName + " " + user.lastName);
        binding.userAddressTV.setText(user.address);
        binding.userPhoneNumberTV.setText(user.phoneNumber);

    }

    private void Init() {
        context = getContext();

        ///init click
        binding.btnNotifications.setOnClickListener(this);
        binding.btnSettings.setOnClickListener(this);
        binding.btnChat.setOnClickListener(this);
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
        if (v == binding.btnNotifications) {
            //startActivity(new Intent(this, Notifications.class));
        } else if (v == binding.btnSettings) {
            //startActivity(new Intent(this, Settings.class));
        } else if (v == binding.btnChat) {
            startActivity(new Intent(getContext(), Chat.class));
        }
    }
}