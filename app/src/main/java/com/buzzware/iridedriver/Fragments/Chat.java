package com.buzzware.iridedriver.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iridedriver.Adapters.ChatAddapter;
import com.buzzware.iridedriver.Adapters.ConversationAdapter;
import com.buzzware.iridedriver.FirebaseRequest.ConversationResponseCallback;
import com.buzzware.iridedriver.FirebaseRequest.FirebaseRequests;
import com.buzzware.iridedriver.Models.ChatModel;
import com.buzzware.iridedriver.Models.ConversationModel;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.BaseActivity;
import com.buzzware.iridedriver.Screens.MessagesActivity;
import com.buzzware.iridedriver.databinding.FragmentChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Chat extends BaseActivity {

    FragmentChatBinding binding;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public Chat() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentChatBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setListener();

        getList();

    }

    private void getList() {

        FirebaseRequests.GetFirebaseRequests(Chat.this).GetConversationList(callback, mAuth.getCurrentUser().getUid(), Chat.this);

    }

    private void setListener() {

        binding.drawerIcon.setOnClickListener(v -> finish());

    }

    ConversationResponseCallback callback= new ConversationResponseCallback() {
        @Override
        public void onResponse(List<ConversationModel> list, boolean isError, String message) {
            if(!isError){

                SetConversationList(list);
                Log.e("data", "NotEmpty");
            }else{

                Log.e("data", "Empty");

            }
        }
    };

    public void SetConversationList(List<ConversationModel> list){

        binding.rvMessages.setLayoutManager(new LinearLayoutManager(Chat.this));
        ConversationAdapter conversationAdapter= new ConversationAdapter(Chat.this, list, listener);
        binding.rvMessages.setAdapter(conversationAdapter);
        conversationAdapter.notifyDataSetChanged();

    }

    ConversationAdapter.OnClickListener listener= new ConversationAdapter.OnClickListener() {
        @Override
        public void onClick(ConversationModel conversationModel) {

            Intent intent= new Intent(Chat.this, MessagesActivity.class);

            intent.putExtra("conversationID", conversationModel.getConversationID());
            intent.putExtra("selectedUserID", conversationModel.getId());
            intent.putExtra("selectedUserName", conversationModel.getName());
            intent.putExtra("checkFrom", "false");

            startActivity(intent);

        }
    };

}