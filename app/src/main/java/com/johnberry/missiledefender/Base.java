package com.johnberry.missiledefender;

import android.widget.ImageView;

public class Base {

    private ImageView baseImg;
    private boolean isDestroyed;

    Base(ImageView baseImg){
        this.baseImg = baseImg;
        this.isDestroyed = false;
    }

    public void destroyBase(){
        isDestroyed = true;
    }
    public double getBaseX(){
        double x_factor = baseImg.getX() + (0.5 * baseImg.getWidth());
        return x_factor;
    }

    public double getBaseY(){
        double y_factor = baseImg.getY() + (0.5 * baseImg.getHeight());
        return y_factor;
    }

    public ImageView getBaseImg() {
        return baseImg;
    }

    public boolean getBaseStatus(){ return isDestroyed; }
}
