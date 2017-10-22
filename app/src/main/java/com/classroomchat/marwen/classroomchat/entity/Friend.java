package com.classroomchat.marwen.classroomchat.entity;

/**
 * Created by marwen on 10/22/17.
 */

public class Friend {
    String friendName, uuid, profilePicture;

    public Friend(String friendName, String uuid, String profilePicture) {
        this.friendName = friendName;
        this.uuid = uuid;
        this.profilePicture = profilePicture;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
