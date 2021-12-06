package com.buzzware.iridedriver.Models;

public class SendConversationModel {
    String content, fromID, messageID, type;
    boolean isRead;
    long timestamp;

    public SendConversationModel() {
    }

    public SendConversationModel(String content, String fromID, String messageID, String type, boolean isRead, long timestamp) {
        this.content = content;
        this.fromID = fromID;
        this.messageID = messageID;
        this.type = type;
        this.isRead = isRead;
        this.timestamp = timestamp;
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

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
