package com.classroomchat.marwen.classroomchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private final String FIRST_RUN = "first_run";
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (sharedPref.getBoolean(FIRST_RUN, true)) {
            sharedPref.edit().putBoolean(FIRST_RUN, false).apply();
            startActivity(new Intent(getApplicationContext(), IntroActivity.class));
            this.finish();
        } else {
            if (savedInstanceState == null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                BluetoothChatFragment fragment = new BluetoothChatFragment();
                transaction.replace(R.id.sample_content_fragment, fragment);
                transaction.commit();
            }
        }
    }

}
