package com.blstream.as;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.blstream.as.fragment.LoggedInFragment;
import com.blstream.as.fragment.NotLoggedInFragment;


public class MainActivity extends ActionBarActivity {

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("Pref", Context.MODE_PRIVATE);
        LoggedInFragment loggedInFragment = new LoggedInFragment();
        NotLoggedInFragment notLoggedInFragment = new NotLoggedInFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (pref.getBoolean("LoggedIn",false)) {
            fragmentTransaction.replace(android.R.id.content, loggedInFragment);
        }
        else {
            fragmentTransaction.replace(android.R.id.content, notLoggedInFragment);
        }
        fragmentTransaction.commit();
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
    public void onRestart() {
        LoggedInFragment loggedInFragment = new LoggedInFragment();
        NotLoggedInFragment notLoggedInFragment = new NotLoggedInFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (pref.getBoolean("LoggedIn",false)) {
            fragmentTransaction.replace(android.R.id.content, loggedInFragment);
        }
        else {
            fragmentTransaction.replace(android.R.id.content, notLoggedInFragment);
        }
        fragmentTransaction.commit();
        super.onRestart();
    }
}
