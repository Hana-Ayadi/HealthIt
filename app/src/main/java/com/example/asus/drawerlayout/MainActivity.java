package com.example.asus.drawerlayout;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
protected DrawerLayout mDrawerLayout;
private ActionBarDrawerToggle mToogle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer);
        mToogle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
    mDrawerLayout.addDrawerListener(mToogle);
    mToogle.syncState();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToogle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * invoked when clicked on Home
     * @param item
     */
    public void Home(MenuItem item) {
        Log.d("statistics","hello from Home");
        Intent settingsIntent =new Intent(this,FirstPage.class);
        startActivity(settingsIntent);

    }

    /**
     * invoked when clicked on settings
     * @param item
     */
    public void settings(MenuItem item) {
        Log.d("settings","hello from settings");
        Intent settingsIntent =new Intent(this,SettingsActivity.class);
        startActivity(settingsIntent);
    }
    /**
     * invoked when clicked on Mesure
     * @param item
     */
    public void mesure(MenuItem item) {
        Intent mesureIntent =new Intent(this,MesureActivity.class);
        startActivity(mesureIntent);
    }
    /**
     * invoked when clicked on Statistics
     * @param item
     */
    public void statistics(MenuItem item) {
        Log.d("statistics","hello from statistics");
        Intent StatIntent =new Intent(this,StatisticsActivity.class);
        startActivity(StatIntent);
    }
}