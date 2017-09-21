package com.classroomchat.marwen.classroomchat.entity;

import java.util.Date;

/**
 * Created by marwen on 9/21/17.
 */

public class ChatMessage {

    String sender;
    String messageContent;
    Date time;

    public ChatMessage(String sender, String messageContent, Date time) {
        this.sender = sender;
        this.messageContent = messageContent;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
