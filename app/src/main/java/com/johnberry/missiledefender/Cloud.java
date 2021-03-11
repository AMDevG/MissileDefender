package com.johnberry.missiledefender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

class Cloud {

    private final MainActivity mainActivity;
    private final ImageView imageView;
    private final AnimatorSet aSet = new AnimatorSet();
    private final int screenHeight;
    private final int screenWidth;
    private final long screenTime;
    private static final String TAG = "Plane";

    Cloud(int screenWidth, int screenHeight, long screenTime, final MainActivity mainActivity) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenTime = screenTime;
        this.mainActivity = mainActivity;


        imageView = new ImageView(mainActivity);
        imageView.setX(-500);

        mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(imageView));

    }

    AnimatorSet setData(int drawId) {
        imageView.setImageResource(drawId);
        int startY = (int) (Math.random() * screenHeight * 0.8);
        int endY = (startY + (Math.random() < 0.5 ? 150 : -150));


        ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x", -200, (screenWidth + 200));
        xAnim.setInterpolator(new LinearInterpolator());
        xAnim.setDuration((long) (screenTime * 0.75));
        xAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.runOnUiThread(() -> {
                    mainActivity.getLayout().removeView(imageView);
                    Log.d(TAG, "onAnimationEnd: NUM VIEWS " +
                            mainActivity.getLayout().getChildCount());
                });
            }
        });

        ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", startY, endY);
        yAnim.setInterpolator(new LinearInterpolator());
        yAnim.setDuration(screenTime);

        aSet.playTogether(xAnim, yAnim);
        return aSet;

    }

    void stop() {
        aSet.cancel();
    }


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



}
