package com.johnberry.missiledefender;

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
    private Connection conn;
    private static final String APP_SCORE_TABLE = "AppScores";
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());

    private final int score, level;
    private final String initials;;
    private final long time;

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
            sb.append(getAll());

            System.out.println("DB Returned with: " + sb.toString());
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAll() throws SQLException {
        Statement stmt = conn.createStatement();

        String sql = "SELECT * from " + APP_SCORE_TABLE + " ORDER BY SCORE DESC";

        StringBuilder sb = new StringBuilder();

        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            long millis = rs.getLong(1);
            String initials = rs.getString(2);
            int score = rs.getInt(3);
            int level = rs.getInt(4);

            sb.append(initials + " " + score + " " + millis + " " + level);
        }

        rs.close();
        stmt.close();

        return sb.toString();
    }

    public boolean isHighScore(int score){
        return false;
    }


}