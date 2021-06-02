package de.walhalla.app2;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity is just to remove the white loading screen and wait until all the necessary
 * values and methods are loaded before going to {@link StartActivity}
 *
 * @author B3tterTogeth3r
 * @version 1.0
 * @see AppCompatActivity
 * @since 1.6
 */
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