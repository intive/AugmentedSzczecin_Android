package com.blstream.as;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.blstream.as.fragment.LoginScreenFragment;
import com.blstream.as.fragment.SplashScreenFragment;


public class MainActivity extends ActionBarActivity {

    private static final Integer SPLASH_TIME = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_activity);

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
     public void onResume() {
        super.onResume();

        /*if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            //if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().findFragmentByTag("login").getId())!=null)
            //if (getSupportFragmentManager().)
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("login");
            if (fragment != null)
                if (getSupportFragmentManager().getBackStackEntryAt(fragment.getId())!=null)
                    getSupportFragmentManager().popBackStackImmediate();
            getSupportFragmentManager().pop
        }*/
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate(getFragmentManager().getBackStackEntryAt(0).getId(), android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
}
