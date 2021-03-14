package com.johnberry.missiledefender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DialogAPI.DialogListener  {

    private ConstraintLayout layout;
    public static ArrayList<Base> baseList = new ArrayList<>();
    public static int screenHeight;
    public static int screenWidth;
    private int scoreValue;
    private ImageView base1, base2, base3, launcher, gameOverImg;
    private TextView scoreBox,levelBox;
    private double interceptorBlast;
    private boolean hasHighScore = false;
    private int scoreToBeat;
    private String initials;
    int finalScore;

    private MissileMaker missileMaker;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new StudentDatabaseHandler(this )).start();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setupFullScreen();
        getScreenDimensions();
        setupSounds();

        layout = findViewById(R.id.layout);
        base1 = findViewById(R.id.baseOneImg);
        base2 = findViewById(R.id.baseTwoImg);
        base3 = findViewById(R.id.baseThreeImg);
        scoreBox = findViewById(R.id.scoreBoxText);
        levelBox = findViewById(R.id.levelTextView);
        gameOverImg = findViewById(R.id.gameOverImg);

        Base base1obj = new Base(base1);
        Base base2obj = new Base(base2);
        Base base3obj = new Base(base3);
        baseList.add(base1obj);
        baseList.add(base2obj);
        baseList.add(base3obj);

        setLevel(scoreValue);

        layout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                handleTouch(motionEvent.getX(), motionEvent.getY());
            }
            return false;
        });

        new ScrollingBackground(this,
                layout, R.drawable.clouds, 10000);

        missileMaker = new MissileMaker(this, screenWidth, screenHeight);
        new Thread(missileMaker).start();
    }

    public void setLevel(final int value) {
        runOnUiThread(() -> levelBox.setText(String.format(Locale.getDefault(), "Level: %d", value)));
    }

    public void handleTouch(float xLoc, float yLoc) {

        Base closestBase = null;
        float shortestDistance = 0;

        if(baseList.size() != 0) {
            for (Base b : baseList) {
                float tmpDist = (float) Math.abs(b.getBaseX() - xLoc);

                if (shortestDistance == 0) {
                    shortestDistance = tmpDist;
                    closestBase = b;
                } else if (tmpDist < shortestDistance) {
                    shortestDistance = tmpDist;
                    closestBase = b;
                }
            }

            launcher = closestBase.getBaseImg();

            double startX = launcher.getX() + (0.5 * launcher.getWidth());
            double startY = launcher.getY() + (0.5 * launcher.getHeight());

            Interceptor i = new Interceptor(this, (float) (startX - 10), (float) (startY - 30), xLoc, yLoc);
            SoundPlayer.start("launch_interceptor");
            i.launch();
        }
    }

    public void removeMissile(Missile m) {
        missileMaker.removeMissile(m);
    }


    public void applyInterceptorBlast(Interceptor interceptor) {
        missileMaker.applyInterceptorBlast(interceptor);
        interceptorBlast++;
    }

    public void applyGroundBlast(Missile missile) {
        missileMaker.applyGroundBlast(missile);
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
        SoundPlayer.setupSound(this, "background", R.raw.background);
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

    public static float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (190.0f - angle);
    }

    public ArrayList<Base> getBaseList(){
        return baseList;
    }

    public void endGame() throws SQLException, JSONException, ClassNotFoundException {
        missileMaker.setRunning(false);
        gameOverImg.setVisibility(View.VISIBLE);

        finalScore = scoreValue;
        int level = missileMaker.getLevel();

        if(finalScore > scoreToBeat){
            System.out.println("Made it to leaderboard enter initials here");
            openDialog();
        }
        else{
            showLeaderBoard();
        }
    }

    public void openDialog(){
        DialogAPI dialogAPI = new DialogAPI();
        dialogAPI.show(getSupportFragmentManager(),"Example Dialog");
    }

    @Override
    public void applyTexts(String userInitials) throws SQLException, JSONException, ClassNotFoundException {
        initials = userInitials;
        String level = String.valueOf(missileMaker.getLevel());
        String score = String.valueOf(finalScore);

        Intent intent = new Intent(this, LeaderBoardActivity.class);
        intent.putExtra("initials", initials);
        intent.putExtra("level", level);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }


    private void showLeaderBoard() throws SQLException, JSONException, ClassNotFoundException {
        System.out.println("No update; Show leaderboard here");
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        startActivity(intent);
        finish();
    }

    public void setScoreToBeat(int score){
        scoreToBeat = score;
        System.out.println("Retrieved lowest high score: " + scoreToBeat);
    }
}
