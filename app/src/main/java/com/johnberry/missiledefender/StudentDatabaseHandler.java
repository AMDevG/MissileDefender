package com.johnberry.missiledefender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;


// RUNS IN SPLASH ACTIVITY TO SET THE HIGH SCORE THRESHOLD TO ADD PLAYER TO TOP 10
public class StudentDatabaseHandler implements Runnable {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    private final MainActivity mainActivity;
    private static String dbURL;
    private static Connection conn;
    private static final String APP_SCORE_TABLE = "AppScores";
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());

    StudentDatabaseHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
    }

    public void run() {
        try {
            int scoreToBeat = getLowestScore();

            mainActivity.runOnUiThread(() -> {
                mainActivity.setScoreToBeat(scoreToBeat);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getLowestScore() throws SQLException, JSONException, ClassNotFoundException {

        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");

        Statement stmt = conn.createStatement();
        String sql = "SELECT * from " + APP_SCORE_TABLE + " ORDER BY SCORE DESC LIMIT 10";

        StringBuilder sb = new StringBuilder();
        ResultSet rs = stmt.executeQuery(sql);

        int lowestScore = 0;
        while (rs.next()) {
            int score = rs.getInt(3);

            if(lowestScore == 0){
                lowestScore = score;
            }
            else if(score < lowestScore){
                lowestScore = score;
            }
        }
        conn.close();
        stmt.close();

        return lowestScore;
    }
}