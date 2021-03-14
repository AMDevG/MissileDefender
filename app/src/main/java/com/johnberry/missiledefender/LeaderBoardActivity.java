package com.johnberry.missiledefender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;

public class LeaderBoardActivity extends AppCompatActivity {

    public static ArrayList<String> highScoreArr = new ArrayList();
    String initials;
    String level, score;
    private static TextView scorer1, scorer2, scorer3, scorer4, scorer5, scorer6, scorer7, scorer8, scorer9, scorer10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setupFullScreen();

        scorer1 = findViewById(R.id.scorer1);
        scorer2 = findViewById(R.id.scorer2);
        scorer3 = findViewById(R.id.scorer3);
        scorer4 = findViewById(R.id.scorer4);
        scorer5 = findViewById(R.id.scorer5);
        scorer6 = findViewById(R.id.scorer6);
        scorer7 = findViewById(R.id.scorer7);
        scorer8 = findViewById(R.id.scorer8);
        scorer9 = findViewById(R.id.scorer9);
        scorer10 = findViewById(R.id.scorer10);


        Intent i = getIntent();
        if(i.hasExtra("initials")) {
            initials = i.getStringExtra("initials");
            level = i.getStringExtra("level");
            score = i.getStringExtra("score");

            UpdateTableRunnable updateTableRunnable = null;
            try {
                updateTableRunnable = new UpdateTableRunnable(this, score, level, initials);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            new Thread(updateTableRunnable).start();
        }

        else{
            GetLeaderRunnable getLeaderRunnable = null;
            try {
                getLeaderRunnable = new GetLeaderRunnable(this);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            new Thread(getLeaderRunnable).start();
        }

    }

    public static void highScores(JSONArray highScoresIn) throws JSONException {
        for(int i = 0; i < highScoresIn.length(); i++){
            JSONArray record = highScoresIn.getJSONArray(i);

            JSONObject jsonMillis = record.getJSONObject(0);
            JSONObject jsonInits = record.getJSONObject(1);
            JSONObject jsonScore = record.getJSONObject(2);
            JSONObject jsonLevel = record.getJSONObject(3);

            String millisString = jsonMillis.getString("millis");
            String initsString = jsonInits.getString("initials");
            String scoreString = jsonScore.getString("score");
            String levelString = jsonLevel.getString("level");

            // Set initials with empty values
            if(initsString.length() < 1){
                initsString = "XXX";
            }

            String playerRecord = initsString + "       |       " + scoreString + "       |       " + levelString;
            System.out.println("Player Record #"+i +"  " + playerRecord);
            highScoreArr.add(playerRecord);
        }
        System.out.println("Retrieved HighScore Array. Update UI Here");
        updateLeaderBoard(highScoreArr);
    }

    private static void updateLeaderBoard(ArrayList<String> highScoreArrIn){
        for(int i = 0; i < highScoreArrIn.size(); i++){
            switch(i){
                case 0:
                    scorer1.setText(highScoreArrIn.get(i));
                    break;
                case 1:
                    scorer2.setText(highScoreArrIn.get(i));
                    break;
                case 2:
                    scorer3.setText(highScoreArrIn.get(i));
                    break;
                case 3:
                    scorer4.setText(highScoreArrIn.get(i));
                    break;
                case 4:
                    scorer5.setText(highScoreArrIn.get(i));
                    break;
                case 5:
                    scorer6.setText(highScoreArrIn.get(i));
                    break;
                case 6:
                    scorer7.setText(highScoreArrIn.get(i));
                    break;
                case 7:
                    scorer8.setText(highScoreArrIn.get(i));
                    break;
                case 8:
                    scorer9.setText(highScoreArrIn.get(i));
                    break;
                case 9:
                    scorer10.setText(highScoreArrIn.get(i));
                    break;
            }
        }
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