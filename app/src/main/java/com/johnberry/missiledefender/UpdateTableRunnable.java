package com.johnberry.missiledefender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class UpdateTableRunnable implements Runnable{

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    private final LeaderBoardActivity leaderBoardActivity;
    private static String dbURL;
    private static Connection conn;
    private static final String APP_SCORE_TABLE = "AppScores";
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());

    private final int score, level;
    private String initials;
    private final long time;
    private boolean isHighScore;

    private static JSONArray highScores = new JSONArray();

    UpdateTableRunnable(LeaderBoardActivity leaderBoardActivity, String score, String level, String initials) throws SQLException, JSONException, ClassNotFoundException {
        this.leaderBoardActivity = leaderBoardActivity;
        this.time = System.currentTimeMillis();
        this.score = Integer.parseInt(score);
        this.level = Integer.parseInt(level);
        this.initials = initials;
        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";

    }


    public void run() {
        try {
          updateHighScore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateHighScore() throws SQLException, JSONException, ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");

        System.out.println("In UPDATE TABLE RUNNER");

        Statement stmt = conn.createStatement();

        String sql = "insert into " + APP_SCORE_TABLE + " values (" +
                time + ", '" + initials + "', " + score + ", " +
                level +
                ")";

        stmt.executeUpdate(sql);
        stmt.close();
        conn.close();

        System.out.println("Updated DB!");

        createScoreList();

        leaderBoardActivity.runOnUiThread(() -> {
            try {
                leaderBoardActivity.highScores(highScores);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });


    }
    public void createScoreList() throws SQLException, JSONException, ClassNotFoundException {

        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");

        Statement stmt = conn.createStatement();
        String sql = "SELECT * from " + APP_SCORE_TABLE + " ORDER BY SCORE DESC LIMIT 10";

        StringBuilder sb = new StringBuilder();

        ResultSet rs = stmt.executeQuery(sql);


        while (rs.next()) {
            JSONArray recordArr = new JSONArray();

            long millis = rs.getLong(1);
            String initials = rs.getString(2);
            int score = rs.getInt(3);
            int level = rs.getInt(4);

            JSONObject jsonMillis = new JSONObject();
            JSONObject jsonInitials = new JSONObject();
            JSONObject jsonScore = new JSONObject();
            JSONObject jsonLevel = new JSONObject();

            jsonMillis.put("millis", millis);
            jsonInitials.put("initials", initials);
            jsonScore.put("score", score);
            jsonLevel.put("level", level);

            recordArr.put(jsonMillis);
            recordArr.put(jsonInitials);
            recordArr.put(jsonScore);
            recordArr.put(jsonLevel);

            highScores.put(recordArr);
        }
    }
}
