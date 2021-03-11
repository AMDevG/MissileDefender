package com.johnberry.missiledefender;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    public static int screenHeight;
    public static int screenWidth;
    private static final float yCoord = 50;
    private static final float xCoord = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFullScreen();
        getScreenDimensions();

        ImageView cloudImage = findViewById(R.id.cloudImage);
        ViewGroup layout = findViewById(R.id.layout);

        SoundPlayer.setupSound(this, "base_blast", R.raw.base_blast);
        SoundPlayer.setupSound(this, "interceptor_blast", R.raw.interceptor_blast);
        SoundPlayer.setupSound(this, "interceptor_hit_missile", R.raw.interceptor_hit_missile);
        SoundPlayer.setupSound(this, "launch_interceptor", R.raw.launch_interceptor);
        SoundPlayer.setupSound(this, "launch_missile", R.raw.launch_missile);
        SoundPlayer.setupSound(this, "missile_miss", R.raw.missile_miss);

    }

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    private void setupFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}