package com.buzzware.iridedriver.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iridedriver.Adapters.ChatAddapter;
import com.buzzware.iridedriver.Models.ChatModel;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.FragmentChatBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    FragmentChatBinding mBinding;
    ChatAddapter messageAddapter;
    List<ChatModel> messageModels;
    Context context;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        try{
            Init();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return mBinding.getRoot();
    }

    private void Init() {
        context= getContext();
        messageModels= new ArrayList<>();
        SetDummyList();
        mBinding.rvMessages.setLayoutManager(new LinearLayoutManager(context));
        messageAddapter= new ChatAddapter(context, messageModels);
        mBinding.rvMessages.setAdapter(messageAddapter);
        messageAddapter.notifyDataSetChanged();
    }

    private void SetDummyList() {
        messageModels.add(new ChatModel());
        messageModels.add(new ChatModel());
        messageModels.add(new ChatModel());
        messageModels.add(new ChatModel());
        messageModels.add(new ChatModel());
        messageModels.add(new ChatModel());
    }
}