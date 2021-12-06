package com.buzzware.iridedriver.FirebaseRequest;

import com.buzzware.iridedriver.Models.ConversationModel;

import java.util.List;

public interface ConversationResponseCallback {

    void onResponse(List<ConversationModel> list, boolean isError, String message);

}
