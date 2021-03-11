package com.johnberry.missiledefender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    public static int screenHeight;
    public static int screenWidth;
    private int scoreValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        layout = findViewById(R.id.layout);
        layout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                handleTouch(motionEvent.getX(), motionEvent.getY());
            }
            return false;
        });

        new ScrollingBackground(this,
                layout, R.drawable.clouds, 6000);

        setupFullScreen();
        getScreenDimensions();
        setupSounds();

    }

    public void handleTouch(float xLoc, float yLoc) {

//        double startX = launcher.getX() + (0.5 * launcher.getWidth());
//        double startY = launcher.getY() + (0.5 * launcher.getHeight());
//
//        float angle = calculateAngle(startX, startY, xLoc, yLoc);
//        launcher.setRotation(angle-70); // Launcher starts 70 degrees off straight-up
//
//        Interceptor i = new Interceptor(this,  (float) (startX - 10), (float) (startY - 30), xLoc, yLoc);
//        SoundPlayer.start("launch_interceptor");
//        i.launch();
    }





    private void setupSounds(){
        SoundPlayer.setupSound(this, "base_blast", R.raw.base_blast);
        SoundPlayer.setupSound(this, "interceptor_blast", R.raw.interceptor_blast);
        SoundPlayer.setupSound(this, "interceptor_hit_missile", R.raw.interceptor_hit_missile);
        SoundPlayer.setupSound(this, "launch_interceptor", R.raw.launch_interceptor);
        SoundPlayer.setupSound(this, "launch_missile", R.raw.launch_missile);
        SoundPlayer.setupSound(this, "missile_miss", R.raw.missile_miss);
    }

    public ConstraintLayout getLayout() {
        return layout;
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