package com.blstream.as.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.blstream.as.LoginActivity;
import com.blstream.as.R;
import com.blstream.as.RegisterActivity;


public class NotLoggedInFragment extends Fragment {

    private Button loginButton;
    private Button registerButton;
    Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View notLoggedInView = inflater.inflate(R.layout.not_logged_in_fragment, container, false);

        loginButton = (Button) notLoggedInView.findViewById(R.id.loginButton);
        registerButton = (Button) notLoggedInView.findViewById(R.id.registerButton);

        setLoginListener();
        setRegisterListener();

        return notLoggedInView;
    }

    public void setLoginListener(){
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setRegisterListener(){
        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
