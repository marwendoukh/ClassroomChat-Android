package com.classroomchat.marwen.classroomchat.adapter;

/**
 * Created by marwen on 9/15/17.
 */

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.classroomchat.marwen.classroomchat.R;
import com.classroomchat.marwen.classroomchat.entity.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.MyViewHolder> {


    private List<ChatMessage> chatMessages = new ArrayList<>();
    private Context context;
    private SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");

    public ChatMessagesAdapter(List<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;
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
        holder.messageTime.setText(localDateFormat.format(chatMessages.get(position).getTime()));

        // avatar
        if (chatMessages.get(position).getSender().equals("Me")) {
            //cardview
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(((Activity) context), R.color.cardview_background_me));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(70, 5, 10, 5);
            holder.cardView.setLayoutParams(lp);
            // cardview text color
            holder.messageContent.setTextColor(ContextCompat.getColor(((Activity) context), R.color.conversation_chat_text_color_me));
            holder.messageTime.setTextColor(ContextCompat.getColor(((Activity) context), R.color.conversation_chat_text_color_me));
            holder.sender.setTextColor(ContextCompat.getColor(((Activity) context), R.color.conversation_chat_text_color_me));

        } else {
            //cardview
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(((Activity) context), R.color.cardview_background_person));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 5, 70, 5);
            holder.cardView.setLayoutParams(lp);
            // cardview text color
            holder.messageContent.setTextColor(ContextCompat.getColor(((Activity) context), R.color.conversation_chat_text_color_person));
            holder.messageTime.setTextColor(ContextCompat.getColor(((Activity) context), R.color.conversation_chat_text_color_person));
            holder.sender.setTextColor(ContextCompat.getColor(((Activity) context), R.color.conversation_chat_text_color_person));
        }

    }


    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sender, messageContent, messageTime;
        ImageView avatarPerson;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);

            sender = (TextView) view.findViewById(R.id.chat_message_sender);
            messageContent = (TextView) view.findViewById(R.id.chat_message_content);
            messageTime = (TextView) view.findViewById(R.id.chat_message_time);
            avatarPerson = (ImageView) view.findViewById(R.id.chat_message_sender_avatar_person);
            cardView = (CardView) view.findViewById(R.id.chat_messages_cardview);

        }
    }
}
