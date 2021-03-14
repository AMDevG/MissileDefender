package com.johnberry.missiledefender;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static com.johnberry.missiledefender.MainActivity.screenHeight;
import static com.johnberry.missiledefender.MainActivity.screenWidth;

class ScrollingBackground {

    private final Context context;
    private final ViewGroup layout;
    private ImageView backImageA;
    private ImageView backImageB;

    private final long duration;
    private final int resId;
    private final float alphaIncrementUp = 0.001f;
    private final float alphaIncrementDown = -0.001f;
    private boolean incrementUp = true;

    ScrollingBackground(Context context, ViewGroup layout, int resId, long duration) {
        this.context = context;
        this.layout = layout;
        this.resId = resId;
        this.duration = duration;
        setupBackground();
    }

    private void setupBackground() {
        backImageA = new ImageView(context);
        backImageB = new ImageView(context);

        backImageA.setAlpha(0.25f);
        backImageB.setAlpha(0.25f);

        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(screenWidth + getBarHeight(), screenHeight);

        backImageA.setLayoutParams(params);
        backImageB.setLayoutParams(params);

        layout.addView(backImageA);
        layout.addView(backImageB);

        Bitmap backBitmapA = BitmapFactory.decodeResource(context.getResources(), resId);
        Bitmap backBitmapB = BitmapFactory.decodeResource(context.getResources(), resId);

        backImageA.setImageBitmap(backBitmapA);
        backImageB.setImageBitmap(backBitmapB);

        backImageA.setScaleType(ImageView.ScaleType.FIT_XY);
        backImageB.setScaleType(ImageView.ScaleType.FIT_XY);

        backImageA.setZ(-1);
        backImageB.setZ(-1);

        animateBack();
    }

    private void animateBack() {

        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);


        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                float width = screenWidth + getBarHeight();

                float a_translationX = width * progress;
                float b_translationX = width * progress - width;

                float currAlpha = backImageA.getAlpha();
                float newAlpha = 0;

                if(currAlpha >= 0.95f){
                    incrementUp = false;
                    System.out.println("Alpha hit upper limit!");
                }
                else if(currAlpha <= 0.25f){
                    incrementUp = true;
                    System.out.println("Alpha hit lower limit!");
                }

                if(incrementUp){
                    newAlpha = currAlpha + alphaIncrementUp;
                    backImageA.setAlpha(newAlpha);
                    backImageB.setAlpha(newAlpha);
                }
                else{
                    newAlpha = currAlpha + alphaIncrementDown;
                    backImageA.setAlpha(newAlpha);
                    backImageB.setAlpha(newAlpha);
                }

                backImageA.setTranslationX(a_translationX);
                backImageB.setTranslationX(b_translationX);
            }
        });
        animator.start();
    }

    private int getBarHeight() {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
