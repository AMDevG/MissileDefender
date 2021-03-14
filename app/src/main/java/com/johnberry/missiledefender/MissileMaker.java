package com.johnberry.missiledefender;

import android.animation.AnimatorSet;

import org.json.JSONException;

import java.sql.SQLException;
import java.util.ArrayList;

public class MissileMaker implements Runnable {
    private final MainActivity mainActivity;
    private boolean isRunning;
    private final ArrayList<Missile> activeMissiles = new ArrayList<>();
    private final int screenWidth;
    private final int screenHeight;
    private int planeCount = 0;
    private static int LEVEL_CHANGE_VALUE = 5;
    private static final int INTERCEPTOR_BLAST_RANGE = 150;
    private int level = 1;
    private long delay = 4000; // Pause between new missiles

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
            int resId = pickMissile();
            SoundPlayer.start("launch_missile");
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

                delay -= 200;

                if (delay < 400)
                    delay = 400;

                planeCount = 0;
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

        ArrayList<Base> bases = mainActivity.getBaseList();

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

        Base baseToRemove = null;
        boolean base_destroyed = false;

        if(!bases.isEmpty()) {
            for (Base b : bases) {
                float base_x = (float) b.getBaseX();
                float base_y = (float) b.getBaseY();
                float distanceBetween = (float) Math.sqrt((base_y - interceptorY) * (base_y - interceptorY) + (base_x - interceptorX) * (base_x - interceptorX));

                if (distanceBetween <= INTERCEPTOR_BLAST_RANGE) {
                    System.out.println("Interceptor Hit base!");
                    base_destroyed = true;
                    b.destroyBase();
                    baseToRemove = b;
                }
            }

            if(base_destroyed) {
                Base finalBaseToRemove = baseToRemove;
                SoundPlayer.start("base_blast");
                mainActivity.runOnUiThread(() -> {
                    mainActivity.getLayout().removeView(finalBaseToRemove.getBaseImg());
                });

                bases.remove(baseToRemove);

                if(bases.isEmpty()){
                    mainActivity.runOnUiThread(() -> {
                        try {
                            mainActivity.endGame();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    });
                }
                base_destroyed = false;
            }
        }

    }

    void applyGroundBlast(Missile missile) {

        ArrayList<Missile> nowGone = new ArrayList<>();
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
        SoundPlayer.start("base_blast");
        for (Missile m : temp) {
            float planeX = (int) (m.getX() + (0.5 * m.getWidth()));
            float planeY = (int) (m.getY() + (0.5 * m.getHeight()));

            m.groundBlast(planeX, planeY);
            nowGone.add(m);
        }

        for (Missile m : nowGone) {
            activeMissiles.remove(m);
        }
    }
    public int getLevel(){
        return level;
    }
}
