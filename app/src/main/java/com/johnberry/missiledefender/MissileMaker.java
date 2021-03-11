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

    MissileMaker(MainActivity mainActivity, int screenWidth, int screenHeight){
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
            int resId = pickMissile();

            long missileTime = (long) ((delay * 0.5) + (Math.random() * delay));
            final Missile missile = new Missile(screenWidth, screenHeight, missileTime, mainActivity);
            activeMissiles.add(missile);
            final AnimatorSet as = missile.setData(resId);

            mainActivity.runOnUiThread(as::start);


            if (planeCount > LEVEL_CHANGE_VALUE) {
                LEVEL_CHANGE_VALUE *= 1.5;
                level++;
                mainActivity.setLevel(level);

                delay -= 200; // Reduce the delay between planes

                if (delay < 200) // But don't let the delay go down to 0
                    delay = 200;

                planeCount = 0;
            }

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int pickMissile(){
        return -1;
    }

    void removeMissile(Missile m) {
        activeMissiles.remove(m);
    }




}
