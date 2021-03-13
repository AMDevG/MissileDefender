package com.johnberry.missiledefender;

import android.animation.AnimatorSet;

import java.util.ArrayList;

public class MissileMaker implements Runnable {
    private final MainActivity mainActivity;
    private boolean isRunning;
    private final ArrayList<Missile> activeMissiles = new ArrayList<>();
    private final int screenWidth;
    private final int screenHeight;
    private int planeCount = 0; // Current plane count for each level
    private static int LEVEL_CHANGE_VALUE = 5; // Change level after this many planes
    private static final int INTERCEPTOR_BLAST_RANGE = 150;
    private int level = 1;
    private long delay = 4000; // Pause between new planes

    MissileMaker(MainActivity mainActivity, int screenWidth, int screenHeight) {
        this.mainActivity = mainActivity;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    void setRunning(boolean running) {
        isRunning = running;
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
        for (Missile m : temp) {
            m.stop();
        }
    }

    @Override
    public void run() {
        setRunning(true);
        while (isRunning) {

            planeCount++;
//            System.out.println("PLane count is: " + planeCount);
            int resId = pickMissile();

            long missileTime = (long) ((delay * 0.5) + (Math.random() * delay));

            final Missile missile = new Missile(screenWidth, screenHeight, missileTime, mainActivity);
            activeMissiles.add(missile);
            final AnimatorSet as = missile.setData(resId);

            mainActivity.runOnUiThread(as::start);

            if (level == 1) {
                mainActivity.setLevel(level);
            }

            if (planeCount > LEVEL_CHANGE_VALUE) {

                LEVEL_CHANGE_VALUE *= 1.5;
                level++;
                mainActivity.setLevel(level);

                delay -= 200; // Reduce the delay between planes

                if (delay < 400) // But don't let the delay go down to 0
                    delay = 400;

                System.out.println("***** Delay is: " + delay + " ********* \n");
                planeCount = 0;
                System.out.println("*********** Missile CountReset: " + planeCount + " ********* \n");

            }

            try {
                Thread.sleep((long) (0.5 * delay));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int pickMissile() {
        return R.drawable.missile;
    }

    void removeMissile(Missile m) {
        activeMissiles.remove(m);
    }

    void applyInterceptorBlast(Interceptor interceptor) {

        float interceptorX = interceptor.getX();
        float interceptorY = interceptor.getY();

        ArrayList<Missile> nowGone = new ArrayList<>();
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);

        for (Missile m : temp) {
            float planeX = (int) (m.getX() + (0.5 * m.getWidth()));
            float planeY = (int) (m.getY() + (0.5 * m.getHeight()));
            float distanceBetween = (float) Math.sqrt((planeY - interceptorY) * (planeY - interceptorY) + (planeX - interceptorX) * (planeX - interceptorX));

            if (distanceBetween < INTERCEPTOR_BLAST_RANGE) {
                SoundPlayer.start("interceptor_hit_missile");
                mainActivity.incrementScore();
                m.interceptorBlast(planeX, planeY);
                nowGone.add(m);
            }
        }

        for (Missile m : nowGone) {
            activeMissiles.remove(m);
        }
    }

    void applyGroundBlast(Missile missile) {

        ArrayList<Missile> nowGone = new ArrayList<>();
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);

        for (Missile m : temp) {
            float planeX = (int) (m.getX() + (0.5 * m.getWidth()));
            float planeY = (int) (m.getY() + (0.5 * m.getHeight()));

            SoundPlayer.start("base_blast");
            m.groundBlast(planeX, planeY);
            nowGone.add(m);
        }

        for (Missile m : nowGone) {
            activeMissiles.remove(m);
        }

    }
}
