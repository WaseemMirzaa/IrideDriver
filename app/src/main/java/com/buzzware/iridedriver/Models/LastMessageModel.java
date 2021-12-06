package com.buzzware.iridedriver.Models;

public class LastMessageModel {
    String content, fromID, toID, type;

    public LastMessageModel() {
    }

    public LastMessageModel(String content, String fromID, String toID, String type) {
        this.content = content;
        this.fromID = fromID;
        this.toID = toID;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
