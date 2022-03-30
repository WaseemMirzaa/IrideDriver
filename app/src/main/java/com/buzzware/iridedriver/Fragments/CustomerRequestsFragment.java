package com.buzzware.iridedriver.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iridedriver.Adapters.RequestAdapter;
import com.buzzware.iridedriver.Models.MyRequests;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.BaseActivity;
import com.buzzware.iridedriver.Screens.CreateNewRequestActivity;
import com.buzzware.iridedriver.Screens.Home;
import com.buzzware.iridedriver.Screens.MessagesActivity;
import com.buzzware.iridedriver.databinding.FragmentCustomerRequestsBinding;
import com.buzzware.iridedriver.interfaces.RequestCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class CustomerRequestsFragment extends BaseActivity implements RequestCallback {

    FragmentCustomerRequestsBinding binding;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    List<MyRequests> myRequests = new ArrayList<>();

    String adminId = "5p4owdD4RkQsRdlZZ8nuQR6u78F2";

    String adminName = "AdminUser";

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(CustomerRequestsFragment.this, R.layout.fragment_customer_requests);

        setListener();

        showRequest();
    }

    private void showRequest() {

        firebaseFirestore.collection("MyRequests").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    myRequests.clear();

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        MyRequests requests = document.toObject(MyRequests.class);

                        requests.id = document.getId();

                        if (requests.userId.equals(getUserId())) {

                            myRequests.add(requests);

                        }

                    }

                    setRecycler();
                }
            }
        });

    }

    private void setRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(CustomerRequestsFragment.this);

        binding.requestRV.setLayoutManager(layoutManager);

        RequestAdapter requestAdapter = new RequestAdapter(CustomerRequestsFragment.this, myRequests, this);

        binding.requestRV.setAdapter(requestAdapter);

    }


    private void setListener() {

        binding.drawerIcon.setOnClickListener(v -> finish());

        binding.createNewRequestBtn.setOnClickListener(v -> startActivity(new Intent(CustomerRequestsFragment.this, CreateNewRequestActivity.class)));

    }

    @Override
    public void onItemClick(String requestId, String ConversationId) {
        Intent intent = new Intent(CustomerRequestsFragment.this, MessagesActivity.class);

        intent.putExtra("conversationID", ConversationId);
        intent.putExtra("selectedUserID", adminId);
        intent.putExtra("selectedUserName", adminName);
        intent.putExtra("checkFrom", "admin");

        startActivity(intent);
    }

    public void OpenCloseDrawer() {

        if (Home.mBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) {

            Home.mBinding.drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            Home.mBinding.drawerLayout.openDrawer(GravityCompat.START);

        }

    }

}
