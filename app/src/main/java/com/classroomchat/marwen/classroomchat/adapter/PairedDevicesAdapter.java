package com.classroomchat.marwen.classroomchat.adapter;

/**
 * Created by marwen on 9/15/17.
 */

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.classroomchat.marwen.classroomchat.R;

import java.util.ArrayList;
import java.util.List;


public class PairedDevicesAdapter extends RecyclerView.Adapter<PairedDevicesAdapter.MyViewHolder> {


    private List<BluetoothDevice> pairedDevices = new ArrayList<>();

    public PairedDevicesAdapter(List<BluetoothDevice> pairedDevices) {
        this.pairedDevices = pairedDevices;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.paired_device_name, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.pairedDeviceName.setText(pairedDevices.get(position).getName());
        holder.pairedDeviceAdress.setText(pairedDevices.get(position).getAddress());

    }


    @Override
    public int getItemCount() {
        return pairedDevices.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView pairedDeviceName, pairedDeviceAdress;

        public MyViewHolder(View view) {
            super(view);

            pairedDeviceName = (TextView) view.findViewById(R.id.paired_device_name);
            pairedDeviceAdress = (TextView) view.findViewById(R.id.paired_device_address);

        }
    }
}
