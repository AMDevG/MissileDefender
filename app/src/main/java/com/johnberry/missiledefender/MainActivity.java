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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private ArrayList<Base> baseList = new ArrayList<>();
    public static int screenHeight;
    public static int screenWidth;
    private int scoreValue;
    private ImageView base1, base2, base3, launcher;
    private TextView scoreBox;
    private double interceptorBlast;

    private MissileMaker missileMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setupFullScreen();
        getScreenDimensions();
        setupSounds();

        base1 = findViewById(R.id.baseOneImg);
        base2 = findViewById(R.id.baseTwoImg);
        base3 = findViewById(R.id.baseThreeImg);
        scoreBox = findViewById(R.id.scoreBoxText);



        Base base1obj = new Base(base1);
        Base base2obj = new Base(base2);
        Base base3obj = new Base(base3);
        baseList.add(base1obj);
        baseList.add(base2obj);
        baseList.add(base3obj);

        layout = findViewById(R.id.layout);

        layout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                handleTouch(motionEvent.getX(), motionEvent.getY());
            }
            return false;
        });

        new ScrollingBackground(this,
                layout, R.drawable.clouds, 6000);

        missileMaker = new MissileMaker(this, screenWidth, screenHeight);
        new Thread(missileMaker).start();
    }

    public void setLevel(final int value) {
//        runOnUiThread(() -> level.setText(String.format(Locale.getDefault(), "Level: %d", value)));
    }

    public void handleTouch(float xLoc, float yLoc) {

        System.out.println("*********** \n TOUCH RECEIVED ******** \n");

//        launcher = base1;
        /// SET BASE TO LAUNCHER AFTER CALCING CLOSEST ONE

//        launcher.setRotation(angle-70); // Launcher starts 70 degrees off straight-up

        System.out.println("Touch is at X Coord: " + xLoc);
        System.out.println("-------------------------");
        System.out.println("Coordinates for bases are: ");

        Base closestBase = null;
        float shortestDistance = 0;
        float touchXloc = xLoc;

        for(Base b : baseList){
            float tmpDist = (float) Math.abs(b.getBaseX() - touchXloc);

            if (shortestDistance == 0){
                shortestDistance = tmpDist;
                closestBase = b;
            }
            else if (tmpDist < shortestDistance){
                    shortestDistance = tmpDist;
                    closestBase = b;
                }
            }

        System.out.println("Shortest distance is: " + shortestDistance);
        launcher = closestBase.getBaseImg();

        double startX = launcher.getX() + (0.5 * launcher.getWidth());
        double startY = launcher.getY() + (0.5 * launcher.getHeight());

        float angle = calculateAngle(startX, startY, xLoc, yLoc);

        Interceptor i = new Interceptor(this,  (float) (startX - 10), (float) (startY - 30), xLoc, yLoc);
        SoundPlayer.start("launch_interceptor");
        i.launch();
    }

    public static float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (190.0f - angle);
    }

    public void removeMissile(Missile m) {
        missileMaker.removeMissile(m);
    }


    public void applyInterceptorBlast(Interceptor interceptor) {
        missileMaker.applyInterceptorBlast(interceptor);
        interceptorBlast++;
        double acc = (double) scoreValue / interceptorBlast;
    }

    public void incrementScore() {
        scoreValue++;
        scoreBox.setText(String.format(Locale.getDefault(), "Score: %d", scoreValue));
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
        screenWidth = displayMetrics.widthPixels + getBarHeight();
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

    private int getBarHeight() {
        int resourceId = this.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return this.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}