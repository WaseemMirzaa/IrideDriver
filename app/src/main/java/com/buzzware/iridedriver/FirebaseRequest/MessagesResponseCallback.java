package com.buzzware.iridedriver.FirebaseRequest;


import com.buzzware.iridedriver.Models.MessageModel;

import java.util.List;

public interface MessagesResponseCallback {
    void onResponse(List<MessageModel> list, boolean isError, String message);
}
