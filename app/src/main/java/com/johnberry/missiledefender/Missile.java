package com.johnberry.missiledefender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import org.json.JSONException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class Missile {

    private final MainActivity mainActivity;
    private  ImageView imageView;
    private final AnimatorSet aSet = new AnimatorSet();
    private final int screenHeight;
    private final int screenWidth;
    private final long screenTime;
    private final boolean hit = false;
    private AnimatorSet animatorSet;
    private ObjectAnimator xAnim, yAnim;

    Missile(int screenWidth, int screenHeight, long screenTime, final MainActivity mainActivity){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenTime = screenTime;
        this.mainActivity = mainActivity;
        imageView = new ImageView(mainActivity);

        mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(imageView));

        SoundPlayer.start("launch_missile");
        int startY = -100;
        int endY = screenHeight;

        int startX = (int) (Math.random() * screenWidth);
        int endX = (int) (Math.random() * screenWidth);

        int imageWidth = (int) getWidth();
        startX -= imageWidth;
        startY -= imageWidth;

        float angle = MainActivity.calculateAngle(
                startX, startY, endX, endY);

        imageView.setRotation(angle);

        imageView.setX(startX);
        imageView.setY(startY);
        imageView.setZ(-10);


        xAnim = ObjectAnimator.ofFloat(imageView, "x", startX, endX);
        xAnim.setInterpolator(new LinearInterpolator());
        xAnim.setDuration(screenTime);

        yAnim = ObjectAnimator.ofFloat(imageView, "y", startY, endY);
        yAnim.setInterpolator(new LinearInterpolator());
        yAnim.setDuration(screenTime);

    }

    AnimatorSet setData(final int drawId) {

        mainActivity.runOnUiThread(() -> imageView.setImageResource(drawId));

        xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                if(getY() > (screenHeight * 0.9)){
                    groundBlast(getX(), getY());
                    xAnim.cancel();
                    yAnim.cancel();

                    mainActivity.runOnUiThread(() -> {
                        if (!hit) {
                            mainActivity.getLayout().removeView(imageView);
                            mainActivity.removeMissile(Missile.this);
                        }
                    });
                }
            }
        });

        xAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                mainActivity.runOnUiThread(() -> {
                    if (!hit) {
                        mainActivity.getLayout().removeView(imageView);
                        mainActivity.removeMissile(Missile.this);
                    }
                });
            }
        });

        aSet.playTogether(xAnim, yAnim);
        return aSet;
    }

    void groundBlast(float x, float y){
        final ImageView iv = new ImageView(mainActivity);

        ArrayList<Base> bases = mainActivity.getBaseList();
        int LETHAL_PROXIMITY_RANGE = 200;
        Base baseToRemove = null;
        boolean base_destroyed = false;

        if(!bases.isEmpty()) {
            for (Base b : bases) {
                float x_coord = (float) b.getBaseX();
                int explosionRange = (int) Math.abs(x_coord - x);

                if (explosionRange <= LETHAL_PROXIMITY_RANGE) {
                    base_destroyed = true;
                    b.destroyBase();
                    baseToRemove = b;
                }
            }

            if(base_destroyed) {
                iv.setImageResource(R.drawable.blast);

                Base finalBaseToRemove = baseToRemove;
                SoundPlayer.start("base_blast");
                mainActivity.runOnUiThread(() -> {
                    mainActivity.getLayout().removeView(finalBaseToRemove.getBaseImg());
                });

                bases.remove(baseToRemove);


                if(bases.isEmpty()){
                    mainActivity.runOnUiThread(() -> {
                        mainActivity.endGame();
                    });
                }
                base_destroyed = false;
            }
            else{
                iv.setImageResource(R.drawable.explode);
            }
        }

        iv.setTransitionName("Missile Ground Blast");

        int w = iv.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        iv.setX(x - offset);
        iv.setY(y - offset);
        iv.setZ(-15);

        aSet.cancel();
        mainActivity.getLayout().addView(iv);


        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);

        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageView);
            }
        });
        alpha.start();
    }

    void stop(){ aSet.cancel();}

    float getX() {
        return imageView.getX();
    }

    float getY() {
        return imageView.getY();
    }

    float getWidth() {
        return imageView.getWidth();
    }

    float getHeight() {
        return imageView.getHeight();
    }

    void interceptorBlast(float x, float y) {

        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.explode);
        iv.setTransitionName("Missile Intercepted Blast");

        int w = imageView.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        iv.setX(x - offset);
        iv.setY(y - offset);
        iv.setRotation((float) (360.0 * Math.random()));

        aSet.cancel();

        mainActivity.getLayout().removeView(imageView);
        mainActivity.getLayout().addView(iv);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageView);
            }
        });
        alpha.start();
    }
}
