package com.classroomchat.marwen.classroomchat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.classroomchat.marwen.classroomchat.adapter.ChatMessagesAdapter;
import com.classroomchat.marwen.classroomchat.entity.ChatMessage;
import com.classroomchat.marwen.classroomchat.utils.BluetoothChatService;
import com.classroomchat.marwen.classroomchat.utils.Constants;

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
    SharedPreferences sharedPref;
    // Layout Views
    private RecyclerView mConversationRecyclerView;
    private ImageView status_connected, status_not_connected;
    private TextView connected_to;
    private LinearLayout messagesGuide;
    private TextView messageGuide1, messageGuide2, messageGuide3, messageGuide4;
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
                        setupMessagesGuide();
                        connected_to.setText(mConnectedDeviceName);
                        vibrate(200);
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
                    }
                    break;
            }
        }
    };
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
        messageGuide1 = (TextView) view.findViewById(R.id.message_guide_1);
        messageGuide2 = (TextView) view.findViewById(R.id.message_guide_2);
        messageGuide3 = (TextView) view.findViewById(R.id.message_guide_3);
        messageGuide4 = (TextView) view.findViewById(R.id.message_guide_4);

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

        // X axis
        if (Math.round(event.values[1]) >= 10) {
            System.out.println("msg1");
            sendMessage(sharedPref.getString(SettingsActivity.MESSAGE1, "msg1"));
            sleep();
        }

        // Z axis
        if (Math.round(event.values[0]) >= 10) {
            System.out.println("msg2");
            sendMessage(sharedPref.getString(SettingsActivity.MESSAGE2, "msg2"));
            sleep();


        }

        // Z axis
        if (Math.round(event.values[0]) <= -10) {
            sendMessage(sharedPref.getString(SettingsActivity.MESSAGE3, "msg3"));
            sleep();


        }

        // X axis
        if (Math.round(event.values[1]) <= -10) {
            sendMessage(sharedPref.getString(SettingsActivity.MESSAGE4, "msg4"));
            sleep();


        }


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
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(time);
    }

    private void setupMessagesGuide() {
        messageGuide1.setText(sharedPref.getString(SettingsActivity.MESSAGE1, "msg1"));
        messageGuide2.setText(sharedPref.getString(SettingsActivity.MESSAGE2, "msg2"));
        messageGuide3.setText(sharedPref.getString(SettingsActivity.MESSAGE3, "msg3"));
        messageGuide4.setText(sharedPref.getString(SettingsActivity.MESSAGE4, "msg4"));

    }
}
