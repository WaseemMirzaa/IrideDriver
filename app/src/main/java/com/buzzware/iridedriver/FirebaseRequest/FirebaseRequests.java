package com.buzzware.iridedriver.FirebaseRequest;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;

import com.buzzware.iridedriver.Models.ConversationModel;
import com.buzzware.iridedriver.Models.LastMessageModel;
import com.buzzware.iridedriver.Models.MessageModel;
import com.buzzware.iridedriver.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRequests {

    public static FirebaseRequests firebaseRequests;

    Context context;

    FirebaseAuth mAuth;

    StorageReference storageReference;

    FirebaseFirestore firebaseFirestore;

    public static FirebaseRequests GetFirebaseRequests(Context context) {

        if (firebaseRequests == null) {

            firebaseRequests = new FirebaseRequests(context);

        }

        return firebaseRequests;
    }

    public FirebaseRequests(Context context) {

        this.context = context;
        ///init firebase
        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

    }

    ListenerRegistration conversationListener;

    public void GetConversationList(ConversationResponseCallback callback, String uID, Context context) {

        final List<ConversationModel>[] list = new List[]{new ArrayList<>()};

        conversationListener = firebaseFirestore.collection("Chat").whereEqualTo("participants." + uID, true)

                .addSnapshotListener((value, error) -> {

                    conversationListener.remove();

                    conversationListener = null;

                    if (value != null) {

                        list[0].clear();

                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

                            LastMessageModel lastMessageModel = documentSnapshot.get("lastMessage", LastMessageModel.class);

                            GetUserData(documentSnapshot.getId(), lastMessageModel, context, uID, list[0], callback);

                        }
                    } else {

                        callback.onResponse(list[0], true, error.getMessage());

                    }
                });
    }

    ListenerRegistration userDataListener;

    private void GetUserData(String conversationID, LastMessageModel lastMessageModel, Context context, String myID, List<ConversationModel> list, ConversationResponseCallback callback) {

        String userID = "";

        if (lastMessageModel.getFromID().equals(myID)) {

            userID = lastMessageModel.getToID();

        }

        if (lastMessageModel.getToID().equals(myID)) {

            userID = lastMessageModel.getFromID();

        }

        final DocumentReference documentReferenceUser = firebaseFirestore.collection("Users").document(userID);

        userDataListener =  documentReferenceUser.addSnapshotListener(((Activity) context), (documentSnapshot, error) -> {

            userDataListener.remove();

            userDataListener = null;

            if (documentSnapshot != null) {

                User userModel = documentSnapshot.toObject(User.class);

                String id = documentSnapshot.getId();

                userModel.id = id;

                list.add(new ConversationModel(conversationID, userModel.id, userModel.firstName, userModel.image, lastMessageModel.getContent(), lastMessageModel.getToID()));

                callback.onResponse(list, false, "Null");
            }
        });
    }

    ListenerRegistration loadMessagesListener;

    public void LoadMessages(MessagesResponseCallback callback, Context context, String conversationID) {

        List<MessageModel> messageModels = new ArrayList<>();

        loadMessagesListener = firebaseFirestore.collection("Chat").document(conversationID).collection("Conversations").addSnapshotListener((value, error) -> {

//            loadMessagesListener.remove();

            loadMessagesListener = null;

            if (value != null) {

                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

                    MessageModel messageModel = documentSnapshot.toObject(MessageModel.class);

                    messageModels.add(messageModel);

                }

                callback.onResponse(messageModels, false, "Null");

            } else {

                callback.onResponse(messageModels, true, error.getMessage());

            }
        });
    }

    ListenerRegistration adminListener;

    public void LoadAdminMessages(MessagesResponseCallback callback, Context context, String conversationID) {

        List<MessageModel> messageModels = new ArrayList<>();

        adminListener = firebaseFirestore.collection("AdminChat").document(conversationID).collection("Conversations").addSnapshotListener((value, error) -> {

            adminListener = null;

            if (value != null) {

                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

                    MessageModel messageModel = documentSnapshot.toObject(MessageModel.class);

                    messageModels.add(messageModel);

                }

                callback.onResponse(messageModels, false, "Null");

            } else {

                callback.onResponse(messageModels, true, error.getMessage());

            }

        });
    }


}
