package com.example.thewordbooks;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            Intent intent=new Intent();
            intent.setClass(MainActivity.this, port_activity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
        else{
            Intent intent =new Intent();
            intent.setClass(MainActivity.this, land_activity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    }
}