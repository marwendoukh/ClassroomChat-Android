package com.classroomchat.marwen.classroomchat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.classroomchat.marwen.classroomchat.adapter.ChatMessagesAdapter;
import com.classroomchat.marwen.classroomchat.entity.ChatMessage;
import com.classroomchat.marwen.classroomchat.utils.BluetoothChatService;
import com.classroomchat.marwen.classroomchat.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    // shake threshold
    private static final int SHAKE_THRESHOLD = 700;
    private final String PROFILE_PICTURE = "profile_picture";
    private final String USER_NAME = "user_name";
    SharedPreferences sharedPref;
    // Layout Views
    private RecyclerView mConversationRecyclerView;
    private ImageView status_connected, status_not_connected;
    private TextView connected_to;
    private LinearLayout messagesGuide;
    private Button messageGuide1, messageGuide2, messageGuide3, messageGuide4;
    private ImageButton showNextMessagesMenu;
    private FloatingActionButton fab;
    // sensor
    private SensorManager mSensorManager;
    private Sensor mLight;
    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;
    /**
     * Array adapter for the conversation thread
     */
    private ChatMessagesAdapter mConversationAdapter;
    private List<ChatMessage> conversationChatMessages = new ArrayList<>();
    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;
    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;
    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;
    // last shake time
    private long lastUpdate;
    private float last_x, last_y, last_z, x, y, z;
    //messages menus
    private Integer messagesMenu = 1;
    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {


            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            break;
                        case BluetoothChatService.STATE_NONE:
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    conversationChatMessages.add(new ChatMessage("Me", writeMessage, new Date()));
                    mConversationAdapter.notifyDataSetChanged();
                    mConversationRecyclerView.smoothScrollToPosition(View.FOCUS_DOWN);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    conversationChatMessages.add(new ChatMessage(mConnectedDeviceName, readMessage, new Date()));
                    mConversationAdapter.notifyDataSetChanged();
                    mConversationRecyclerView.smoothScrollToPosition(View.FOCUS_DOWN);
                    vibrate(600);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Snackbar
                                .make(getView(), "Connected to " + mConnectedDeviceName, Snackbar.LENGTH_SHORT)
                                .show();
                        // change connection status
                        status_connected.setVisibility(View.VISIBLE);
                        status_not_connected.setVisibility(View.GONE);
                        messagesGuide.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.VISIBLE);
                        setupMessagesGuide();
                        connected_to.setText(mConnectedDeviceName);
                        vibrate(200);
                        establishConnection();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Snackbar
                                .make(getView(), msg.getData().getString(Constants.TOAST), Snackbar.LENGTH_SHORT)
                                .show();
                        // change connection status
                        status_connected.setVisibility(View.GONE);
                        status_not_connected.setVisibility(View.VISIBLE);
                        messagesGuide.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {

        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Snackbar
                    .make(getView(), "Bluetooth is not available", Snackbar.LENGTH_SHORT)
                    .show();
            activity.finish();
        }

        // setup the sensor
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (mLight != null) {
            // Success! There's a pressure sensor.
            System.out.println("position : there is sensor ");
        } else {
            // Failure! No pressure sensor.
            System.out.println("position : no sensor ");

        }
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        setupMessagesGuide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mConversationRecyclerView = (RecyclerView) view.findViewById(R.id.conversation);
        status_connected = (ImageView) view.findViewById(R.id.status_connected);
        status_not_connected = (ImageView) view.findViewById(R.id.status_not_connected);
        connected_to = (TextView) view.findViewById(R.id.you_are_speaking_to);
        messagesGuide = (LinearLayout) view.findViewById(R.id.messages_guide);
        messageGuide1 = (Button) view.findViewById(R.id.message_guide_1);
        messageGuide2 = (Button) view.findViewById(R.id.message_guide_2);
        messageGuide3 = (Button) view.findViewById(R.id.message_guide_3);
        messageGuide4 = (Button) view.findViewById(R.id.message_guide_4);
        showNextMessagesMenu = (ImageButton) view.findViewById(R.id.show_next_messaging_menu);
        fab = (FloatingActionButton) view.findViewById(R.id.send_typed_message_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTypedMessage();
            }
        });

        // send message when message guide is clicked
        messageGuide1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(messageGuide1.getText().toString());
            }
        });

        messageGuide2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(messageGuide2.getText().toString());
            }
        });

        messageGuide3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(messageGuide3.getText().toString());
            }
        });

        messageGuide4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(messageGuide4.getText().toString());
            }
        });

        // change next messages menu

        showNextMessagesMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNextMessagesMenu();
            }
        });
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationAdapter = new ChatMessagesAdapter(conversationChatMessages, getActivity());

        mConversationRecyclerView.setAdapter(mConversationAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mConversationRecyclerView.setLayoutManager(mLayoutManager);

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        Log.d("chat", "message to send " + message);
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {

            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
        vibrate(200);


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving, Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }

            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }

            case R.id.settings: {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    // monitor sensor change
    @Override
    public final void onSensorChanged(SensorEvent event) {


        //detect phone shake
        if (detectPhoneShake(event))
            showNextMessagesMenu();


        // X axis
        if (Math.round(event.values[1]) >= 10) {
            System.out.println("msg1");
            sendMessage(sharedPref.getString(SettingsActivity.MESSAGE1 + messagesMenu, "msg1"));
            sleep();
        }

        // Z axis
        if (Math.round(event.values[0]) >= 10) {
            System.out.println("msg2");
            sendMessage(sharedPref.getString(SettingsActivity.MESSAGE2 + messagesMenu, "msg2"));
            sleep();


        }

        // Z axis
        if (Math.round(event.values[0]) <= -10) {
            sendMessage(sharedPref.getString(SettingsActivity.MESSAGE3 + messagesMenu, "msg3"));
            sleep();


        }

        // X axis
        if (Math.round(event.values[1]) <= -10) {
            sendMessage(sharedPref.getString(SettingsActivity.MESSAGE4 + messagesMenu, "msg4"));
            sleep();


        }


    }

    //detect phone shake


    public boolean detectPhoneShake(SensorEvent event) {
        long curTime = System.currentTimeMillis();
        // only allow one update every 100ms.
        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            x = event.values[SensorManager.DATA_X];
            y = event.values[SensorManager.DATA_Y];
            z = event.values[SensorManager.DATA_Z];

            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;


            last_x = x;
            last_y = y;
            last_z = z;

            if (speed > SHAKE_THRESHOLD) {
                return true;
            }
        }
        return false;
    }


    // show next Messages menu
    public void showNextMessagesMenu() {
        Snackbar
                .make(getView(), R.string.shake_detected, Snackbar.LENGTH_SHORT)
                .show();


        if (messagesMenu == 3) {
            messagesMenu = 1;
        } else {
            messagesMenu++;
        }

        // change messages guide content
        messageGuide1.setText(sharedPref.getString(SettingsActivity.MESSAGE1 + messagesMenu, "msg1"));
        messageGuide2.setText(sharedPref.getString(SettingsActivity.MESSAGE2 + messagesMenu, "msg2"));
        messageGuide3.setText(sharedPref.getString(SettingsActivity.MESSAGE3 + messagesMenu, "msg3"));
        messageGuide4.setText(sharedPref.getString(SettingsActivity.MESSAGE4 + messagesMenu, "msg4"));


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void sleep() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    mSensorManager.unregisterListener(BluetoothChatFragment.this);
                    Thread.sleep(2000);
                    mSensorManager.registerListener(BluetoothChatFragment.this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        new Thread(runnable).start();

    }

    private void vibrate(Integer time) {
        try {
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(time);
        } catch (NullPointerException e) {
            System.out.println("error occured when vibrating");
        }
    }

    private void setupMessagesGuide() {
        messageGuide1.setText(sharedPref.getString(SettingsActivity.MESSAGE1 + messagesMenu, "msg1"));
        messageGuide2.setText(sharedPref.getString(SettingsActivity.MESSAGE2 + messagesMenu, "msg2"));
        messageGuide3.setText(sharedPref.getString(SettingsActivity.MESSAGE3 + messagesMenu, "msg3"));
        messageGuide4.setText(sharedPref.getString(SettingsActivity.MESSAGE4 + messagesMenu, "msg4"));

    }

    private void sendTypedMessage() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertDialogView = inflater.inflate(R.layout.send_typed_message_dialog, null);
        alertDialog.setView(alertDialogView);

        final EditText textDialog = (EditText) alertDialogView.findViewById(R.id.typed_message);

        alertDialog.setPositiveButton(getResources().getString(R.string.send), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                sendMessage(textDialog.getText().toString());
            }

        });

        alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    private void establishConnection() {

        try {
            String profilePicURI = sharedPref.getString(PROFILE_PICTURE, "profilePictureNotFound");

            //if profile picture found
            if (!profilePicURI.equals("profilePictureNotFound")) {
                final Uri imageUri = Uri.parse(sharedPref.getString(PROFILE_PICTURE, Uri.parse("android.resource://com.classroomchat.marwen.classroomchat/drawable/ic_person_black_24dp").toString()));

                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                Bitmap profilePic = BitmapFactory.decodeStream(imageStream);
                // scale image to fit imageButton
                profilePic = Bitmap.createScaledBitmap(profilePic, 50, 50, true);
                //transfert profile picture and  name
                sendMessage("1" + sharedPref.getString(USER_NAME, "") + "PICTURE" + encodeToBase64(profilePic, Bitmap.CompressFormat.JPEG, 0));

            } else {
                sendMessage("0" + sharedPref.getString(USER_NAME, ""));

            }
            // pause to separate msgs
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}