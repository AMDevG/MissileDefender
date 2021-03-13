package com.johnberry.missiledefender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.Random;

public class Missile {

    private final MainActivity mainActivity;
    private final ImageView imageView;
    private final AnimatorSet aSet = new AnimatorSet();
    private final int screenHeight;
    private final int screenWidth;
    private final long screenTime;
    private final boolean hit = false;

    Missile(int screenWidth, int screenHeight, long screenTime, final MainActivity mainActivity){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenTime = screenTime;
        this.mainActivity = mainActivity;


        imageView = new ImageView(mainActivity);
        imageView.setX(-500);

        mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(imageView));
    }

    AnimatorSet setData(final int drawId) {
        mainActivity.runOnUiThread(() -> imageView.setImageResource(drawId));

        int startY = -100;
        int endY = screenHeight;

        int startX = (int) (Math.random() * screenWidth);
        int endX = (int) (Math.random() * screenWidth);

        ///CALCULATE ANGLES
//        int missileWidth = imageView.getDrawable().getIntrinsicWidth();


        ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x", startX, endX);
        xAnim.setInterpolator(new LinearInterpolator());
        xAnim.setDuration(screenTime);

        ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", startY, endY);
        yAnim.setInterpolator(new LinearInterpolator());
        yAnim.setDuration(screenTime);


        xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float yVal = getY() ;
//                System.out.println("yVal in Update is: " + yVal);

                if(yVal > (screenHeight * 0.85)){
                    makeGroundBlast(getX(), yVal);
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

    void makeGroundBlast(float x, float y){

        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.blast);
        iv.setTransitionName("Missile Ground Blast");

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
