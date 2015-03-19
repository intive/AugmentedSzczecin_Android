package com.blstream.as.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.blstream.as.R;


public class LoggedInFragment extends Fragment {

    private Button logoutButton;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View loggedInView = inflater.inflate(R.layout.logged_in_fragment, container, false);

        Context context = getActivity();
        pref = context.getSharedPreferences("Pref",Context.MODE_PRIVATE);

        logoutButton = (Button) loggedInView.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editor = pref.edit();
                editor.clear();
                editor.commit();
                getActivity().recreate();
            }
        });

        return loggedInView;
    }
}
