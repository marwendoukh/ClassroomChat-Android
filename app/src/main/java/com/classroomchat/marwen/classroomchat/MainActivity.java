package com.classroomchat.marwen.classroomchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.classroomchat.marwen.classroomchat.custom.views.RoundImageButton;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private final String FIRST_RUN = "first_run";
    private final String PROFILE_PICTURE = "profile_picture";
    private final String USER_NAME = "user_name";
    ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences sharedPref;
    private DrawerLayout mDrawerLayout;


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


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                null, R.string.send, R.string.send) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        // drawer actions

        LinearLayout settings = (LinearLayout) findViewById(R.id.settings_navigation_drawer);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view_drawer);
                mDrawerLayout.closeDrawer(navigationView);

            }
        });


        // Drawer Menu

        //profile pic
        RoundImageButton profilePic = (RoundImageButton) findViewById(R.id.profile_picture_drawer);
        try {
            final Uri imageUri = Uri.parse(sharedPref.getString(PROFILE_PICTURE, ""));
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            // scale image to fit imageButton
            selectedImage = Bitmap.createScaledBitmap(selectedImage, 150, 150, true);
            profilePic.setImageBitmap(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // name

        TextView userName = (TextView) findViewById(R.id.drawer_user_name);
        userName.setText(sharedPref.getString(USER_NAME, ""));

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
