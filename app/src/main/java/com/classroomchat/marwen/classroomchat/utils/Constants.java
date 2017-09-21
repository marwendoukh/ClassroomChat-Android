package com.classroomchat.marwen.classroomchat.utils;


/**
 * Defines several constants used between {@link BluetoothChatService} and the UI.
 */
public interface Constants {

    // ChatMessage types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "paired_device_name_list_row";
    public static final String TOAST = "toast";

}
