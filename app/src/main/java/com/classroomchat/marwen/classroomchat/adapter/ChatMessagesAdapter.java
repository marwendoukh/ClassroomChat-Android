package com.classroomchat.marwen.classroomchat.adapter;

/**
 * Created by marwen on 9/15/17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.classroomchat.marwen.classroomchat.R;
import com.classroomchat.marwen.classroomchat.entity.ChatMessage;

import java.util.ArrayList;
import java.util.List;


public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.MyViewHolder> {


    private List<ChatMessage> chatMessages = new ArrayList<>();

    public ChatMessagesAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_messages_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.sender.setText(chatMessages.get(position).getSender());
        holder.messageContent.setText(chatMessages.get(position).getMessageContent());
        holder.messageTime.setText(chatMessages.get(position).getTime().toString());

        // avatar
        if (chatMessages.get(position).getSender().equals("Me")) {
            holder.avatarMe.setVisibility(View.VISIBLE);
            holder.avatarPerson.setVisibility(View.GONE);
        } else {
            holder.avatarMe.setVisibility(View.GONE);
            holder.avatarPerson.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sender, messageContent, messageTime;
        ImageView avatarMe, avatarPerson;

        public MyViewHolder(View view) {
            super(view);

            sender = (TextView) view.findViewById(R.id.chat_message_sender);
            messageContent = (TextView) view.findViewById(R.id.chat_message_content);
            messageTime = (TextView) view.findViewById(R.id.chat_message_time);
            avatarMe = (ImageView) view.findViewById(R.id.chat_message_sender_avatar_me);
            avatarPerson = (ImageView) view.findViewById(R.id.chat_message_sender_avatar_person);

        }
    }
}
