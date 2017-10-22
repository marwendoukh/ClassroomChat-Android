package com.classroomchat.marwen.classroomchat.adapter;

/**
 * Created by marwen on 9/15/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.classroomchat.marwen.classroomchat.R;
import com.classroomchat.marwen.classroomchat.entity.ChatMessage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.MyViewHolder> {


    private final String PROFILE_PICTURE = "profile_picture";
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private Context context;
    private SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
    private Bitmap friendPicture;
    private String friendName = "";
    private SharedPreferences sharedPref;
    private boolean establishingConnection = true;
    private boolean receivedFriendName = false;


    public ChatMessagesAdapter(List<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_messages_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {


        //receive friend name and profile picture
        if (establishingConnection && !chatMessages.get(position).getSender().equals("Me")) {
            String msg = chatMessages.get(position).getMessageContent();
            friendName = msg.substring(0, msg.indexOf("PICTURE") - 1);
            friendPicture = decodeBase64(msg.substring(msg.indexOf("PICTURE") + 7));
            establishingConnection = false;

        } else if (!establishingConnection) {


            // set sender name in chat UI
            if (chatMessages.get(position).getSender().equals("Me")) {
                holder.sender.setText(chatMessages.get(position).getSender());
            } else {
                holder.sender.setText(friendName);

            }
            holder.messageContent.setText(chatMessages.get(position).getMessageContent());
            holder.messageTime.setText(localDateFormat.format(chatMessages.get(position).getTime()));

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

                sharedPref = PreferenceManager.getDefaultSharedPreferences((Activity) context);
                // set profile picture
                try {
                    final Uri imageUri = Uri.parse(sharedPref.getString(PROFILE_PICTURE, ""));
                    final InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
                    Bitmap profilePic = BitmapFactory.decodeStream(imageStream);
                    // scale image to fit imageButton
                    profilePic = Bitmap.createScaledBitmap(profilePic, 50, 50, true);
                    holder.avatarPerson.setImageBitmap(profilePic);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


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
                // set profile picture
                try {
                    holder.avatarPerson.setImageBitmap(friendPicture);
                } catch (NullPointerException e) {
                    System.out.println(e.toString());
                }

            }
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
