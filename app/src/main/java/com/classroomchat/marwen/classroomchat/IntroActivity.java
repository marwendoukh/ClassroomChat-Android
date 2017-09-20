package com.classroomchat.marwen.classroomchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addSlide(AppIntroFragment.newInstance("Rotate to send", "Rotate your phone to one of the 4 directions to send a message", R.drawable.rotate_your_phone, ContextCompat.getColor(getApplicationContext(), R.color.intro_first_slide)));
        addSlide(AppIntroFragment.newInstance("You could turn off your screen", "Send your message even if your screen is turned off", R.drawable.turn_off_screen, ContextCompat.getColor(getApplicationContext(), R.color.intro_second_slide)));
        addSlide(AppIntroFragment.newInstance("Customize your messages", "Set your custom messages in the App settings", R.drawable.customize_your_messages, ContextCompat.getColor(getApplicationContext(), R.color.intro_third_slide)));
        addSlide(AppIntroFragment.newInstance("Choose your nearby friend", "Choose your nearby friend and communicate with him using Bluetooth", R.drawable.choose_your_friend, ContextCompat.getColor(getApplicationContext(), R.color.intro_fourth_slide)));


        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        //animation
        setDepthAnimation();

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        this.finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}