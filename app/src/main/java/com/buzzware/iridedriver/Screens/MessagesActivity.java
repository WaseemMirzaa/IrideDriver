package com.buzzware.iridedriver.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.buzzware.iridedriver.Adapters.MessagesAdapter;
import com.buzzware.iridedriver.FirebaseRequest.ConversationResponseCallback;
import com.buzzware.iridedriver.FirebaseRequest.FirebaseRequests;
import com.buzzware.iridedriver.FirebaseRequest.MessagesResponseCallback;
import com.buzzware.iridedriver.Models.ConversationModel;
import com.buzzware.iridedriver.Models.MessageModel;
import com.buzzware.iridedriver.Models.SendConversationModel;
import com.buzzware.iridedriver.Models.SendLastMessageModel;
import com.buzzware.iridedriver.databinding.ActivityMessagesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MessagesActivity extends AppCompatActivity {

    ActivityMessagesBinding binding;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    String conversationID = "";

    String selectedUserName = "";

    String selectedUserId = "";

    String currentUserId = "";

    String isFromNew = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getDataFromExtra();

        setListener();


    }

    private void setListener() {

        binding.sendBtn.setOnClickListener(v -> {
            if (!binding.messageET.getText().toString().isEmpty()) {
                sendMessage();
            }
        });

    }

    private void getDataFromExtra() {

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            isFromNew = getIntent().getStringExtra("checkFrom");
            selectedUserId = getIntent().getStringExtra("selectedUserID");
            selectedUserName = getIntent().getStringExtra("selectedUserName");

            binding.tvTitle.setText(selectedUserName);

            if (isFromNew.equals("false")) {
                conversationID = getIntent().getStringExtra("conversationID");
            } else {
                conversationID = UUID.randomUUID().toString();
                checkAlreadyHaveChatOrNot();
            }

        }

        currentUserId = mAuth.getCurrentUser().getUid();

        loadMessages();


    }

    private void checkAlreadyHaveChatOrNot() {
        getListToCheck();
    }

    private void getListToCheck() {

        FirebaseRequests.GetFirebaseRequests(MessagesActivity.this).GetConversationList(callbackCheck, mAuth.getCurrentUser().getUid(), MessagesActivity.this);

    }

    ConversationResponseCallback callbackCheck = new ConversationResponseCallback() {
        @Override
        public void onResponse(List<ConversationModel> list, boolean isError, String message) {
            if (!isError) {

                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getToID().equals(selectedUserId)) {
                            conversationID = list.get(i).getConversationID();
                            isFromNew = "false";
                            return;
                        }
                    }
                }
                Log.e("data", "NotEmpty");
            } else {

                Log.e("data", "Empty");

            }
        }
    };

    private void loadMessages() {

        FirebaseRequests.GetFirebaseRequests(MessagesActivity.this).LoadMessages(callback, MessagesActivity.this, conversationID);

    }

    MessagesResponseCallback callback = new MessagesResponseCallback() {
        @Override
        public void onResponse(List<MessageModel> list, boolean isError, String message) {
            if (!isError) {
                Sorting(list);
            }
        }
    };

    private void Sorting(List<MessageModel> list) {
        Collections.sort(list, new Comparator<MessageModel>() {
            @Override
            public int compare(MessageModel o1, MessageModel o2) {
                return Long.compare(o1.getTimestamp(), o2.getTimestamp());
            }
        });

        ///set data
        SetRecyclerView(list);
    }

    public void SetRecyclerView(List<MessageModel> list) {

        binding.rvMessages.setLayoutManager(new LinearLayoutManager(MessagesActivity.this));

        MessagesAdapter messagesAdapter = new MessagesAdapter(MessagesActivity.this, list, currentUserId);

        binding.rvMessages.setAdapter(messagesAdapter);

        binding.rvMessages.scrollToPosition(list.size() - 1);

    }


    private void sendMessage() {
        if (isFromNew.equals("true")) {

            long currentTimeStamp = System.currentTimeMillis();
            SendLastMessageModel sendLastMessageModel = new SendLastMessageModel(binding.messageET.getText().toString(),
                    currentUserId, String.valueOf(currentTimeStamp), selectedUserId, "text", false, (int) currentTimeStamp);

            HashMap<String, Boolean> participents = new HashMap<>();
            participents.put(currentUserId, true);
            participents.put(selectedUserId, true);

            SendConversationModel sendConversationModel = new SendConversationModel(binding.messageET.getText().toString(),
                    currentUserId, String.valueOf(currentTimeStamp), "text", false, currentTimeStamp);

            HashMap<String, Object> lasthashMap = new HashMap<>();
            lasthashMap.put("lastMessage", sendLastMessageModel);
            lasthashMap.put("participants", participents);

            conversationID = UUID.randomUUID().toString();

            firebaseFirestore.collection("Chat").document(conversationID).collection("Conversations").document(String.valueOf(currentTimeStamp)).set(sendConversationModel);
            firebaseFirestore.collection("Chat").document(conversationID).set(lasthashMap);

        } else {
            SendAlreadyExist();
        }

        loadMessages();
        checkAlreadyHaveChatOrNot();
    }

    public void SendAlreadyExist() {

        long currentTimeStamp = System.currentTimeMillis();

        SendConversationModel sendConversationModel = new SendConversationModel(binding.messageET.getText().toString(),
                currentUserId, String.valueOf(currentTimeStamp), "text", false, currentTimeStamp);
        SendLastMessageModel sendLastMessageModel = new SendLastMessageModel(binding.messageET.getText().toString(),
                currentUserId, String.valueOf(currentTimeStamp), selectedUserId, "text", false, currentTimeStamp);
        firebaseFirestore.collection("Chat").document(conversationID).collection("Conversations").document(String.valueOf(currentTimeStamp)).set(sendConversationModel);
        firebaseFirestore.collection("Chat").document(conversationID).update("lastMessage", sendLastMessageModel);

        loadMessages();

    }

}