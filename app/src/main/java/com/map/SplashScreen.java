package com.guardianmap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ketan on 6/2/2016.
 */
public class SplashScreen extends AppCompatActivity
{
    Handler handler;
    Runnable runnable;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
setContentView(R.layout.splashscreen);
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this,MapsActivity.class));
                finish();
            }
        },3000);
    }
}
