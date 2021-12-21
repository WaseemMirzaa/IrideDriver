package com.buzzware.iridedriver.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iridedriver.Adapters.ConversationAdapter;
import com.buzzware.iridedriver.Firebase.FirebaseInstances;
import com.buzzware.iridedriver.FirebaseRequest.ConversationResponseCallback;
import com.buzzware.iridedriver.FirebaseRequest.FirebaseRequests;
import com.buzzware.iridedriver.Models.ConversationModel;
import com.buzzware.iridedriver.Models.LastMessageModel;
import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.Screens.BaseActivity;
import com.buzzware.iridedriver.Screens.MessagesActivity;
import com.buzzware.iridedriver.databinding.FragmentChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Chat extends BaseActivity {

    FragmentChatBinding binding;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    List<LastMessageModel> lastMessages;

    FirebaseRequests firebaseRequests;

    public Chat() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentChatBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        init();

        setListener();

        getList();

    }

    private void init() {

        firebaseRequests = new FirebaseRequests();

    }

    private void getList() {

        firebaseRequests.GetConversationList((list, isError, message) -> {

            if (!isError) {

                lastMessages = list;

                getUsersList();

            } else {

                showErrorAlert(message);

            }

        }, mAuth.getCurrentUser().getUid(), Chat.this);

    }

    private void setListener() {

        binding.drawerIcon.setOnClickListener(v -> finish());

    }

    List<ConversationModel> conversations = new ArrayList<>();

    void getUsersList() {

        conversations.clear();

        FirebaseInstances.usersCollection
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                            User user = documentSnapshot.toObject(User.class);

                            user.id = documentSnapshot.getId();

                            for (int i = 0; i < lastMessages.size(); i++) {

                                String otherUserId = lastMessages.get(i).fromID;

                                if (getUserId().equalsIgnoreCase(lastMessages.get(i).fromID))

                                    otherUserId = lastMessages.get(i).toID;

                                if (user.id.equalsIgnoreCase(otherUserId)) {

                                    ConversationModel conversation = getConversationModel(lastMessages.get(i), user);

                                    conversations.add(conversation);
                                }

                            }

                        }

                    }

                    setConversations(conversations);

                });

    }

    private ConversationModel getConversationModel(LastMessageModel lastMessageModel, User user) {

        ConversationModel model = new ConversationModel();

        model.conversationID = lastMessageModel.conversationId;

        model.name = user.firstName + " " + user.lastName;

        model.image = user.image;

        model.lastMessage = lastMessageModel.content;

        model.id = lastMessageModel.conversationId;

        if (!lastMessageModel.toID.equalsIgnoreCase(getUserId()))

            model.toID = lastMessageModel.toID;

        else

            model.toID = lastMessageModel.fromID;

        return model;

    }

    public void setConversations(List<ConversationModel> list) {

        binding.rvMessages.setLayoutManager(new LinearLayoutManager(Chat.this));

        ConversationAdapter conversationAdapter = new ConversationAdapter(Chat.this, list, listener);

        binding.rvMessages.setAdapter(conversationAdapter);

        conversationAdapter.notifyDataSetChanged();

    }

    ConversationAdapter.OnClickListener listener = conversationModel -> {

        Intent intent = new Intent(Chat.this, MessagesActivity.class);

        intent.putExtra("conversationID", conversationModel.getConversationID());
        intent.putExtra("selectedUserID", conversationModel.toID);
        intent.putExtra("selectedUserName", conversationModel.getName());
        intent.putExtra("checkFrom", "false");

        startActivity(intent);

    };

}