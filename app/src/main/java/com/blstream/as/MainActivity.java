package com.blstream.as;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import com.blstream.as.fragment.LoginScreenFragment;
import com.blstream.as.fragment.SplashScreenFragment;


public class MainActivity extends ActionBarActivity {

    private static final String LOGIN_PREFERENCES = "LoginPreferences";
    private static final String USER_LOGIN_STATUS = "UserLoginStatus";
    private static final Integer SPLASH_TIME = 5000;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, SplashScreenFragment.newInstance())
                .commit();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                pref = getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);

                if (pref.getBoolean(USER_LOGIN_STATUS, false)) {
                    //FIXME Quick fix for modules marge
                    MainActivity.this.finish();
                    startActivity(new Intent(MainActivity.this, PoiMapActivity.class));
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(android.R.id.content, LoginScreenFragment.newInstance())
                            .commit();
                }
            }
        }, SPLASH_TIME);

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
