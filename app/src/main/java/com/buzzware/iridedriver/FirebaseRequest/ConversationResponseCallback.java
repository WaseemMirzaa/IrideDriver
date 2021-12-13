package com.buzzware.iridedriver.FirebaseRequest;

import com.buzzware.iridedriver.Models.ConversationModel;
import com.buzzware.iridedriver.Models.LastMessageModel;

import java.util.List;

public interface ConversationResponseCallback {

    void onResponse(List<LastMessageModel> list, boolean isError, String message);

}
