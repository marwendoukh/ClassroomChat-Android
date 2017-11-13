package com.classroomchat.marwen.classroomchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.classroomchat.marwen.classroomchat.adapter.StatsAdapter;
import com.classroomchat.marwen.classroomchat.utils.LocalStorage;

public class StatsActivity extends AppCompatActivity {

    TextView totalMessagesSent, totalMessagesReceived;
    RecyclerView friendsStatsRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        totalMessagesSent = (TextView) findViewById(R.id.total_messages_sent_stats);
        totalMessagesReceived = (TextView) findViewById(R.id.total_messages_received_stats);

        totalMessagesSent.setText("" + LocalStorage.getInstance(getApplicationContext()).totalMessagesSent());
        totalMessagesReceived.setText("" + LocalStorage.getInstance(getApplicationContext()).totalMessagesReceived());


        //// friends stats

        StatsAdapter statsAdapter = new StatsAdapter(LocalStorage.getInstance(getApplicationContext()).findAllFriends());
        friendsStatsRV = (RecyclerView) findViewById(R.id.friends_stats_rv);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        friendsStatsRV.setLayoutManager(mLayoutManager2);
        friendsStatsRV.setAdapter(statsAdapter);


    }
}
