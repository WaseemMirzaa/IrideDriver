package com.buzzware.iridedriver.Screens;

import androidx.annotation.Nullable;
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
import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.databinding.ActivityMessagesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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

    String myImageUrl;
    String otherUserImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getDataFromExtra();

        setListener();


    }

    public void getMyImage() {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("Users").document(currentUserId);

        reference.addSnapshotListener((value, error) -> {


            User user = null;

            if (value != null) {

                user = value.toObject(User.class);

                myImageUrl = user.image;

            } else {

                myImageUrl = "";

            }

            getOtherUserImage();

        });
    }

    public void getOtherUserImage() {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReferenceBuisnessUser = firebaseFirestore.collection("Users").document(selectedUserId);
        documentReferenceBuisnessUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                User user = null;
                if (value != null) {

                    user = value.toObject(User.class);

                    otherUserImageUrl = user.image;
                } else {

                    otherUserImageUrl = "";

                }

                loadMessages();

            }
        });
    }

    private void setListener() {

        binding.sendBtn.setOnClickListener(v -> {
            if (!binding.messageET.getText().toString().isEmpty()) {
                sendMessage();
            }
        });

        binding.backIV.setOnClickListener(v->{
            finish();
        });

    }

    private void getDataFromExtra() {

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            isFromNew = getIntent().getStringExtra("checkFrom");

            selectedUserId = getIntent().getStringExtra("selectedUserID");

            selectedUserName = getIntent().getStringExtra("selectedUserName");

            binding.tvTitle.setText(selectedUserName);

            currentUserId = mAuth.getCurrentUser().getUid();

            if (isFromNew.equals("false")) {

                conversationID = getIntent().getStringExtra("conversationID");

                getMyImage();

            } else {

                conversationID = UUID.randomUUID().toString();

                checkAlreadyHaveChatOrNot();

            }

        }

    }

    private void checkAlreadyHaveChatOrNot() {

        getListToCheck();

    }

    private void getListToCheck() {

        FirebaseRequests.GetFirebaseRequests(MessagesActivity.this).GetConversationList(callbackCheck, mAuth.getCurrentUser().getUid(), MessagesActivity.this);

    }

    ConversationResponseCallback callbackCheck = (list, isError, message) -> {

        if (!isError) {

            if (list.size() > 0) {

                for (int i = 0; i < list.size(); i++) {

                    if (list.get(i).getToID().equals(selectedUserId)) {

                        conversationID = list.get(i).getConversationID();

                        isFromNew = "false";

                        getMyImage();

                        return;
                    }
                }
            }

            Log.e("data", "NotEmpty");

        } else {

            Log.e("data", "Empty");

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

        Collections.sort(list, (o1, o2) -> Long.compare(o1.getTimestamp(), o2.getTimestamp()));

        ///set data
        SetRecyclerView(list);
    }

    public void SetRecyclerView(List<MessageModel> list) {

        binding.rvMessages.setLayoutManager(new LinearLayoutManager(MessagesActivity.this));

        MessagesAdapter messagesAdapter = new MessagesAdapter(MessagesActivity.this, list, currentUserId, myImageUrl, otherUserImageUrl);

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

        binding.messageET.setText("");
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