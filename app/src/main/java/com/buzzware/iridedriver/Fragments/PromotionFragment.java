package com.buzzware.iridedriver.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iridedriver.Adapters.PromotionsAdapter;
import com.buzzware.iridedriver.Firebase.FirebaseInstances;
import com.buzzware.iridedriver.Models.Promotion.PromotionObj;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.Home;
import com.buzzware.iridedriver.databinding.FragmentPromotionBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PromotionFragment extends Fragment {


    FragmentPromotionBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_promotion, container, false);

        setListener();

        getPromotions();

        return binding.getRoot();

    }

    private void getPromotions() {

        FirebaseInstances.promotionsCollection
                .get()
                .addOnCompleteListener(this::parsePromotions);
    }

    List<PromotionObj> promotionObjList;

    private void parsePromotions(Task<QuerySnapshot> task) {

        promotionObjList = new ArrayList<>();

        if(!task.isSuccessful() || task.getResult() == null)

            return;

        List<DocumentSnapshot> snapshots = task.getResult().getDocuments();

        for (DocumentSnapshot snapshot: snapshots) {

            PromotionObj promotionObj = snapshot.toObject(PromotionObj.class);

            promotionObjList.add(promotionObj);

        }

        setAdapter();

    }

    private void setAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        binding.promotionRV.setLayoutManager(layoutManager);

        PromotionsAdapter normalBottleAdapter = new PromotionsAdapter(getContext(), promotionObjList);

        binding.promotionRV.setAdapter(normalBottleAdapter);

    }

    private void setListener() {

        binding.drawerIcon.setOnClickListener(v-> OpenCloseDrawer() );

    }

    public static void OpenCloseDrawer() {

        if (Home.mBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) {

            Home.mBinding.drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            Home.mBinding.drawerLayout.openDrawer(GravityCompat.START);

        }

    }

}