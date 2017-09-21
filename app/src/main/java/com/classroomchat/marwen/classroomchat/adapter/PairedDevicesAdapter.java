package com.classroomchat.marwen.classroomchat.adapter;

/**
 * Created by marwen on 9/15/17.
 */

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.classroomchat.marwen.classroomchat.DeviceListActivity;
import com.classroomchat.marwen.classroomchat.R;

import java.util.ArrayList;
import java.util.List;

import static com.classroomchat.marwen.classroomchat.DeviceListActivity.EXTRA_DEVICE_ADDRESS;


public class PairedDevicesAdapter extends RecyclerView.Adapter<PairedDevicesAdapter.MyViewHolder> {


    private List<BluetoothDevice> pairedDevices = new ArrayList<>();
    private Context context;

    public PairedDevicesAdapter(List<BluetoothDevice> pairedDevices, Context context) {
        this.pairedDevices = pairedDevices;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.paired_device_name_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.pairedDeviceName.setText(pairedDevices.get(position).getName());
        holder.pairedDeviceAdress.setText(pairedDevices.get(position).getAddress());

        holder.paired_devices_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cancel discovery because it's costly and we're about to connect
                DeviceListActivity.mBtAdapter.cancelDiscovery();


                // Create the result Intent and include the MAC address
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, pairedDevices.get(position).getAddress());

                // Set result and finish this Activity
                ((Activity) context).setResult(Activity.RESULT_OK, intent);
                ((Activity) context).finish();
            }
        });
    }


    @Override
    public int getItemCount() {
        return pairedDevices.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView pairedDeviceName, pairedDeviceAdress;
        CardView paired_devices_cardview;

        public MyViewHolder(View view) {
            super(view);

            pairedDeviceName = (TextView) view.findViewById(R.id.paired_device_name);
            pairedDeviceAdress = (TextView) view.findViewById(R.id.paired_device_address);
            paired_devices_cardview = (CardView) view.findViewById(R.id.paired_devices_cardview);

        }
    }
}
