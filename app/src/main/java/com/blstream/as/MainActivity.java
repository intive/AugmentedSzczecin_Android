package com.blstream.as;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;

import com.blstream.as.fragment.ActionBarConnector;
import com.blstream.as.fragment.SplashScreenFragment;
import com.blstream.as.fragment.StartScreenFragment;


public class MainActivity extends ActionBarActivity implements ActionBarConnector{

    private static final Integer SPLASH_TIME = 5;
    private static final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, SplashScreenFragment.newInstance())
                .commit();

        handler.postDelayed(new Runnable() {
            public void run() {
                if (LoginUtils.isUserLogged(MainActivity.this)) {
                    //FIXME Quick fix for modules marge
                    MainActivity.this.finish();
                    startActivity(new Intent(MainActivity.this, HomeScreenActivity.class));
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(android.R.id.content, StartScreenFragment.newInstance())
                            .commit();
                }
            }
        }, SPLASH_TIME);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate(getFragmentManager().getBackStackEntryAt(0).getId(), android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void hideActionBar(){
        if (getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }
    }
}
