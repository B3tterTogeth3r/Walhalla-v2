package de.walhalla.app2;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Go to StartActivity
        Intent mainIntent = new Intent(this, StartActivity.class);
        startActivity(mainIntent);
        finish();
    }
}