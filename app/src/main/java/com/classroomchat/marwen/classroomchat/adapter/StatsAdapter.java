package com.classroomchat.marwen.classroomchat.adapter;

/**
 * Created by marwen on 9/15/17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.classroomchat.marwen.classroomchat.R;
import com.classroomchat.marwen.classroomchat.entity.Friend;

import java.util.ArrayList;
import java.util.List;


public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.MyViewHolder> {


    private List<Friend> friends = new ArrayList<>();


    public StatsAdapter(List<Friend> friends) {
        this.friends = friends;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.friendName.setText(friends.get(position).getFriendName());
        holder.messagesSentCount.setText("" + friends.get(position).getMessagesSentCount());
        holder.messagesReceivedCount.setText("" + friends.get(position).getMessagesReceivedCount());

    }


    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView friendName, messagesSentCount, messagesReceivedCount;

        public MyViewHolder(View view) {
            super(view);

            friendName = (TextView) view.findViewById(R.id.friend_name_stats);
            messagesSentCount = (TextView) view.findViewById(R.id.messages_sent_count_stats);
            messagesReceivedCount = (TextView) view.findViewById(R.id.messages_received_count_stats);

        }
    }
}
