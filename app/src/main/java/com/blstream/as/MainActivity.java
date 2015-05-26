package com.blstream.as;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.blstream.as.fragment.ActionBarConnector;
import com.blstream.as.fragment.SplashScreenFragment;
import com.blstream.as.fragment.StartScreenFragment;


public class MainActivity extends ActionBarActivity implements ActionBarConnector {

    private static final Integer SPLASH_TIME = 5;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager.beginTransaction()
                .replace(android.R.id.content, SplashScreenFragment.newInstance())
                .commit();

        handler.postDelayed(new Runnable() {
            public void run() {
                fragmentManager.beginTransaction()
                        .replace(android.R.id.content, StartScreenFragment.newInstance())
                        .commit();
                if (LoginUtils.isUserLogged(MainActivity.this)) {
                    //FIXME Quick fix for modules marge
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                }
            }
        }, SPLASH_TIME);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isBackAfterLogout()){
            goToStartScreen();
        }
    }

    public boolean isBackAfterLogout(){
        return (fragmentManager.getBackStackEntryCount()>1);
    }

    public void goToStartScreen(){
        fragmentManager.popBackStackImmediate(fragmentManager.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}
