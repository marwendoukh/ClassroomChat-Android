package com.classroomchat.marwen.classroomchat.entity;

/**
 * Created by marwen on 10/22/17.
 */

public class Friend {
    String friendName, uuid;
    Integer connectionCount, messagesSentCount, messagesReceivedCount;

    public Friend() {
        this.friendName = "";
        this.uuid = "";
        this.connectionCount = 0;
        this.messagesSentCount = 0;
        this.messagesReceivedCount = 0;

    }

    public Friend(String friendName, String uuid) {
        this.friendName = friendName;
        this.uuid = uuid;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getConnectionCount() {
        return connectionCount;
    }

    public void setConnectionCount(Integer connectionCount) {
        this.connectionCount = connectionCount;
    }

    public Integer getMessagesSentCount() {
        return messagesSentCount;
    }

    public void setMessagesSentCount(Integer messagesSentCount) {
        this.messagesSentCount = messagesSentCount;
    }

    public Integer getMessagesReceivedCount() {
        return messagesReceivedCount;
    }

    public void setMessagesReceivedCount(Integer messagesReceivedCount) {
        this.messagesReceivedCount = messagesReceivedCount;
    }
}
