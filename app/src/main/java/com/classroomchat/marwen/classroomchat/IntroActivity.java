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


        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.intro_first_title), getResources().getString(R.string.intro_first_description), R.drawable.rotate_your_phone, ContextCompat.getColor(getApplicationContext(), R.color.intro_first_slide)));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.intro_second_title), getResources().getString(R.string.intro_second_description), R.drawable.turn_off_screen, ContextCompat.getColor(getApplicationContext(), R.color.intro_second_slide)));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.intro_third_title), getResources().getString(R.string.intro_third_description), R.drawable.customize_your_messages, ContextCompat.getColor(getApplicationContext(), R.color.intro_third_slide)));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.intro_fourth_title), getResources().getString(R.string.intro_fourth_description), R.drawable.choose_your_friend, ContextCompat.getColor(getApplicationContext(), R.color.intro_fourth_slide)));


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
        startActivity(new Intent(getApplicationContext(), SetUpProfile.class));
        this.finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}