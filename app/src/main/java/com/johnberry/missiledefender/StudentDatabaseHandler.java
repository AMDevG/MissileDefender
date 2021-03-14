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

public class StudentDatabaseHandler implements Runnable {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    private final MainActivity context;
    private static String dbURL;
    private static Connection conn;
    private static final String APP_SCORE_TABLE = "AppScores";
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());

    private final int score, level;
    private final String initials;;
    private final long time;
    private boolean isHighScore = false;

    private static JSONArray highScores = new JSONArray();

    StudentDatabaseHandler(MainActivity ctx, String initials, int score, int level) {
        context = ctx;
        this.time = System.currentTimeMillis();

        this.initials = initials;
        this.score = score;
        this.level = level;

        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
    }

    public void run() {

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");


            if(checkScore()) {

                System.out.println("High Score; Inserting into DB");
                StringBuilder sb = new StringBuilder();

                Statement stmt = conn.createStatement();

                String sql = "insert into " + APP_SCORE_TABLE + " values (" +
                        time + ", '" + initials + "', " + score + ", " +
                        level +
                        ")";

                int result = stmt.executeUpdate(sql);

                stmt.close();

                String response = "Score for " + initials + " added (" + result + " record)\n\n";

                sb.append(response);

            }
            conn.close();

            createScoreList();
            MainActivity.highScores(highScores);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkScore() throws SQLException {


        Statement stmt = conn.createStatement();

        String sql = "SELECT * from " + APP_SCORE_TABLE + " ORDER BY SCORE DESC";

        StringBuilder sb = new StringBuilder();

        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
//            long millis = rs.getLong(1);
//            String initials = rs.getString(2);
            int score = rs.getInt(3);

            if(this.score > score){
//                System.out.println("New High Score!!");
                isHighScore = true;
            }

        }

        rs.close();
        stmt.close();

        return isHighScore;
    }

    public void createScoreList() throws SQLException, JSONException, ClassNotFoundException {

        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");

        Statement stmt = conn.createStatement();
        String sql = "SELECT * from " + APP_SCORE_TABLE + " ORDER BY SCORE DESC";

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

    public static JSONArray getScoreList(){
        return highScores;
    }


}