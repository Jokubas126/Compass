package com.example.mycompass;

import android.widget.TextView;

public class Texts {
    private TextView textView;

    public Texts(TextView textView){
        this.textView = textView;
    }

    public void showText(String text){
        textView.setText(text);
    }
}
