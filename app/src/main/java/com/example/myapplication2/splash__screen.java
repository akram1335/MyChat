package com.example.myapplication2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class splash__screen extends AppCompatActivity {
    private ImageView iv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        iv = (ImageView) findViewById(R.id.imageView);
        Animation ani = AnimationUtils.loadAnimation(this, R.anim.splash_show_anim);
        iv.startAnimation(ani);
        final Intent ii = new Intent(this, login.class);

        Thread timer = new Thread(){
            public void run () {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(ii);
                    finish();
                }
            }
        };timer.start();
    }
}