package com.blstream.as.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.blstream.as.R;


public class LoggedInFragment extends Fragment {

    private Button logoutButton;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String LOGIN_PREFERENCES = "LoginPreferences";

    public LoggedInFragment(){

    }

    public static final LoggedInFragment newInstance(){
        LoggedInFragment loggedInFragment = new LoggedInFragment();
        return loggedInFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View loggedInView = inflater.inflate(R.layout.logged_in_fragment, container, false);

        Context context = getActivity();
        pref = context.getSharedPreferences(LOGIN_PREFERENCES,Context.MODE_PRIVATE);

        logoutButton = (Button) loggedInView.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editor = pref.edit();
                editor.clear();
                editor.commit();

                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack(getFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });

        return loggedInView;
    }
}
