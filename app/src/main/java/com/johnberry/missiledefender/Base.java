package com.johnberry.missiledefender;

import android.widget.ImageView;

public class Base {

    private ImageView baseImg;

    Base(ImageView baseImg){
        this.baseImg = baseImg;
    }
    public double getBaseX(){
        double x_factor = baseImg.getX() + (0.5 * baseImg.getWidth());
        return x_factor;
    }

    public double getBaseY(){
        double y_factor = baseImg.getY() + (0.5 * baseImg.getHeight());
        return y_factor;
    }


}
