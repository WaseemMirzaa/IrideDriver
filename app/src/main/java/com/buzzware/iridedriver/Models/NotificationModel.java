package com.buzzware.iridedriver.Models;

import java.util.Map;

public class NotificationModel {

    public Map<String, Boolean> isRead;

    String id, message, title, type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
