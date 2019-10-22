package com.example.mycompass;

import android.widget.ImageView;

public class Arrow{
    private ImageView imageView;

    private int imageId;

    public Arrow(int imageId, ImageView imageViewId){
        this.imageId = imageId;
        this.imageView = imageViewId;
    }

    //shows the image in the layout
    public void showImage(){
        imageView.setImageResource(imageId);
    }

    //shows the image of arrow after taking its rotation angle
    public void rotate(int angle){
        imageView.setRotation(angle);
        showImage();
    }
}
