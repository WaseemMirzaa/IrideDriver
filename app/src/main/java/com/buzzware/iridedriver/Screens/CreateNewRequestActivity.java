package com.buzzware.iridedriver.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.buzzware.iridedriver.Firebase.FirebaseInstances;
import com.buzzware.iridedriver.Fragments.CustomerRequestsFragment;
import com.buzzware.iridedriver.Models.MyRequests;
import com.buzzware.iridedriver.Models.SendConversationModel;
import com.buzzware.iridedriver.Models.SendLastMessageModel;
import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.ActivityCreateNewRequestBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CreateNewRequestActivity extends BaseActivity {

    ActivityCreateNewRequestBinding binding;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    String adminId = "5p4owdD4RkQsRdlZZ8nuQR6u78F2";

    String adminName = "AdminUser";

    String conversationID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityCreateNewRequestBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setListeners();

    }

    private void setListeners() {

        binding.requestsList.setOnClickListener(v -> {

            startActivity(new Intent(CreateNewRequestActivity.this, CustomerRequestsFragment.class));

        });

        binding.btnContinue.setOnClickListener(v -> validateAndSendMessage());

        binding.drawerIcon.setOnClickListener(v -> finish());

    }

    private void createRequest(String cId) {

        hideKeyboard();

        MyRequests myRequests = new MyRequests();

        myRequests.email = binding.emailET.getText().toString();
        myRequests.message = binding.messageET.getText().toString();
        myRequests.subject = binding.subjectET.getText().toString();
        myRequests.name = binding.nameET.getText().toString();
        myRequests.timeStamp = new Date().getTime();
        myRequests.conversationId = cId;
        myRequests.userId = getUserId();

        showLoader();

        FirebaseFirestore.getInstance().collection("MyRequests")
                .document()
                .set(myRequests)
                .addOnCompleteListener(task -> {

                    hideLoader();

                    Toast.makeText(CreateNewRequestActivity.this, "Request Submitted", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);

                    intent.putExtra("conversationID", cId);
                    intent.putExtra("selectedUserID", adminId);
                    intent.putExtra("selectedUserName", adminName);
                    intent.putExtra("checkFrom", "admin");

                    startActivity(intent);

//                    finish();

                });

    }

    private void validateAndSendMessage() {

        if (validate()) {
            getAdmin();
        }

    }

    private void getAdmin() {

        FirebaseInstances.usersCollection
            .whereEqualTo("userRole", "admin")
                .get()
                .addOnCompleteListener(task -> {

                    List<DocumentSnapshot> documents = task.getResult().getDocuments();

                    if(documents.size() > 0) {

                        User admin = documents.get(0).toObject(User.class);

                        if(admin != null) {

                            admin.id = documents.get(0).getId();

                            adminId = admin.id;

                            adminName = admin.firstName + " "+ admin.lastName;

                            sendAdminMessage();
                        }

                    }

                });
    }

    private void sendAdminMessage() {

        long currentTimeStamp = System.currentTimeMillis();

        String message = "Name : " + binding.nameET.getText().toString() + "\nSubject : " + binding.subjectET.getText().toString()
                + "\nEmail : " + binding.emailET.getText().toString() + "\nMessage : " + binding.messageET.getText().toString();

        SendLastMessageModel sendLastMessageModel = new SendLastMessageModel(binding.messageET.getText().toString(),

                getUserId(), String.valueOf(currentTimeStamp), adminId, "text", false, (int) currentTimeStamp);

        HashMap<String, Boolean> participents = new HashMap<>();

        participents.put(getUserId(), true);

        participents.put(adminId, true);

        SendConversationModel sendConversationModel = new SendConversationModel(message,
                getUserId(), String.valueOf(currentTimeStamp), "text", false, currentTimeStamp, adminId);

        HashMap<String, Object> lasthashMap = new HashMap<>();

        lasthashMap.put("lastMessage", sendLastMessageModel);

        lasthashMap.put("participants", participents);

        conversationID = UUID.randomUUID().toString();

        firebaseFirestore.collection("AdminChat").document(conversationID).collection("Conversations").document(String.valueOf(currentTimeStamp)).set(sendConversationModel);
        firebaseFirestore.collection("AdminChat").document(conversationID).set(lasthashMap);

        createRequest(conversationID);

    }

    private boolean validate() {

        if (binding.nameET.getText().toString().isEmpty()) {

            showErrorAlert("Name Required");

            return false;
        }


        if (binding.emailET.getText().toString().isEmpty()) {

            showErrorAlert("Email Required");

            return false;
        }


        if (binding.subjectET.getText().toString().isEmpty()) {

            showErrorAlert("Subject Required");

            return false;
        }


        if (binding.messageET.getText().toString().isEmpty()) {

            showErrorAlert("Message Required");

            return false;
        }

        return true;

    }
}