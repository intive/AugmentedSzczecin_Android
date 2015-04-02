package com.blstream.as;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.blstream.as.fragment.LoggedInFragment;
import com.blstream.as.fragment.LoginScreenFragment;
import com.blstream.as.fragment.RegisterFragment;
import com.blstream.as.fragment.SplashScreenFragment;


public class MainActivity extends ActionBarActivity {

    private static final Integer SPLASH_TIME = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, SplashScreenFragment.newInstance())
                    .commit();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(android.R.id.content, LoginScreenFragment.newInstance())
                            .commit();
                }
            }, SPLASH_TIME);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
     public void onResume() {
        super.onResume();

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            //if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().findFragmentByTag("login").getId())!=null)
            //if (getSupportFragmentManager().)
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("login");
            if (fragment != null)
                if (getSupportFragmentManager().getBackStackEntryAt(fragment.getId())!=null)
                    getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
}
