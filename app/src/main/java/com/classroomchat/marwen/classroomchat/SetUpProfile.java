package com.classroomchat.marwen.classroomchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.classroomchat.marwen.classroomchat.custom.views.RoundImageButton;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SetUpProfile extends AppCompatActivity {


    private final int SELECT_PHOTO = 1;
    private final String PROFILE_PICTURE = "profile_picture";
    private RoundImageButton userProfilePic;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_profile);

        userProfilePic = (RoundImageButton) findViewById(R.id.preview_user_profile_picture_settingupaccount);

        userProfilePic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        // scale image to fit imageButton
                        selectedImage = Bitmap.createScaledBitmap(selectedImage, 150, 150, true);
                        userProfilePic.setImageBitmap(selectedImage);
                        // save image URI
                        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        sharedPref.edit().putString(PROFILE_PICTURE, imageUri.toString()).apply();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_setup, menu);

        // return true so that the menu pop up is opened
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_setup: {

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            }
        }
        return false;
    }

}
