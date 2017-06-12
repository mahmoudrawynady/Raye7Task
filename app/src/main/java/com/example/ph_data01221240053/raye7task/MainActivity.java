package com.example.ph_data01221240053.raye7task;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
/*
* this activity create to make aSplash screen to the app.
* this splash screen is Raye7 Logo that will disapear after few seconds.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //intent to start the Maps Activity  that resonsible for every thing in tha Map after the splash activity had been hiden.
        Intent intent = new Intent(this, MapsActivity.class);
        //to start the activity.
        startActivity(intent);
        //to finish the activity so the Maps activity can appear to the user.
        finish();
    }
}
