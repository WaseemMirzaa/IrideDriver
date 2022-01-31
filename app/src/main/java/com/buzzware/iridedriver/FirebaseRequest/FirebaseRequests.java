package com.buzzware.iridedriver.FirebaseRequest;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.buzzware.iridedriver.Models.ConversationModel;
import com.buzzware.iridedriver.Models.LastMessageModel;
import com.buzzware.iridedriver.Models.MessageModel;
import com.buzzware.iridedriver.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRequests {

    public static FirebaseRequests firebaseRequests;

    Context context;

    FirebaseAuth mAuth;

    StorageReference storageReference;

    FirebaseFirestore firebaseFirestore;

    public FirebaseRequests() {
        ///init firebase
        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

    }

    public void GetConversationList(ConversationResponseCallback callback, String uID, Context context) {

        final ArrayList<LastMessageModel> list = new ArrayList<>();

        firebaseFirestore
                .collection("Chat")
                .whereEqualTo("participants." + uID, true)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                            LastMessageModel lastMessageModel = snapshot.get("lastMessage",LastMessageModel.class);

                            lastMessageModel.conversationId = snapshot.getId();

                            list.add(lastMessageModel);
                        }

                        callback.onResponse(list, false, null);

                    } else {

                        if (task.getException() == null)
                            return;

                        if ((task.getException().getLocalizedMessage() != null))

                            callback.onResponse(null, true, task.getException().getLocalizedMessage());

                    }

                });
    }


//    void getUsersList()

//    ListenerRegistration userDataListener;

//    private void GetUserData(String conversationID, LastMessageModel lastMessageModel, Context context, String myID, List<ConversationModel> list, ConversationResponseCallback callback) {
//
//        String userID = "";
//
//        if (lastMessageModel.getFromID().equals(myID)) {
//
//            userID = lastMessageModel.getToID();
//
//        }
//
//        if (lastMessageModel.getToID().equals(myID)) {
//
//            userID = lastMessageModel.getFromID();
//
//        }
//
//        final DocumentReference documentReferenceUser = firebaseFirestore.collection("Users").document(userID);
//
//        userDataListener = documentReferenceUser.addSnapshotListener(((Activity) context), (documentSnapshot, error) -> {
//
//            userDataListener.remove();
//
//            userDataListener = null;
//
//            if (documentSnapshot != null) {
//
//                User userModel = documentSnapshot.toObject(User.class);
//
//                String id = documentSnapshot.getId();
//
//                userModel.id = id;
//
//                list.add(new ConversationModel(conversationID, userModel.id, userModel.firstName, userModel.image, lastMessageModel.getContent(), lastMessageModel.getToID()));
//
////                callback.onResponse(list, false, "Null");
//            }
//        });
//    }

    ListenerRegistration loadMessagesListener;

    public void LoadMessages(MessagesResponseCallback callback, Context context, String conversationID) {


        loadMessagesListener = firebaseFirestore.collection("Chat").document(conversationID).collection("Conversations").addSnapshotListener((value, error) -> {

            List<MessageModel> messageModels = new ArrayList<>();

            if (value != null) {

                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

                    MessageModel messageModel = documentSnapshot.toObject(MessageModel.class);

                    messageModels.add(messageModel);

                }

                callback.onResponse(messageModels, false, "Null");

            }
        });
    }

    ListenerRegistration adminListener;

    public void LoadAdminMessages(MessagesResponseCallback callback, Context context, String conversationID) {


        adminListener = firebaseFirestore.collection("AdminChat").document(conversationID).collection("Conversations").addSnapshotListener((value, error) -> {

            List<MessageModel> messageModels = new ArrayList<>();

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

    public void deInit() {

        adminListener = null;
        loadMessagesListener = null;

    }

}
