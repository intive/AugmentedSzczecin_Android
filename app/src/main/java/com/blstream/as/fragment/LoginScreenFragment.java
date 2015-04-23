package com.blstream.as.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.blstream.as.HttpAsync;
import com.blstream.as.MainActivity;
import com.blstream.as.R;
import com.blstream.as.maps2d.PoiMapActivity;

import java.util.concurrent.ExecutionException;


public class LoginScreenFragment extends Fragment {

    private Button loginButton;
    private EditText emailEditText;
    private EditText passEditText;
    private SharedPreferences pref;
    private static final String LOGIN_PREFERENCES = "LoginPreferences";
    private static final String USER_LOGIN_STATUS = "UserLoginStatus";
    private static final String USER_EMAIL = "UserEmail";
    private static final String USER_PASS = "UserPass";

    private static final String SERVER_URL = "http://private-f8d40-example81.apiary-mock.com/login";
    private static final String RESPONSE_FAIL = "status=404";
    private static final Integer RESPONSE_OK = 200;

    public LoginScreenFragment() {

    }

    public static LoginScreenFragment newInstance() {
        return new LoginScreenFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).getSupportActionBar().hide();
        View loginScreenView = inflater.inflate(R.layout.login_screen_fragment, container, false);

        Context context = getActivity();
        pref = context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);

        emailEditText = (EditText) loginScreenView.findViewById(R.id.email);
        passEditText = (EditText) loginScreenView.findViewById(R.id.password);
        emailEditText.setError(null);
        passEditText.setError(null);

        loginButton = (Button) loginScreenView.findViewById(R.id.loginButton);

        emailEditText.setOnFocusChangeListener(emailListener);
        passEditText.setOnFocusChangeListener(passListener);

        setLoginListener();

        return loginScreenView;
    }

    View.OnFocusChangeListener emailListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                if (TextUtils.isEmpty(emailEditText.getText())) {
                    emailEditText.setError(getString(R.string.email_required));
                }
            }
            emailEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    emailEditText.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    };

    View.OnFocusChangeListener passListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                if (TextUtils.isEmpty(passEditText.getText())) {
                    passEditText.setError(getString(R.string.password_required));
                }
            }
        }
    };

    public void setLoginListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(emailEditText.getText())) {
                    emailEditText.setError(getString(R.string.email_required));
                }
                if (TextUtils.isEmpty(passEditText.getText())) {
                    passEditText.setError(getString(R.string.password_required));
                }

                if (emailEditText.getError() == null && passEditText.getError() == null) {
                    getResponse();
                }
            }
        });
    }

    public void getResponse() {
        Integer response = null;
        try {
            if (emailValid())
                response = new HttpAsync().execute(SERVER_URL).get();
            else
                response = new HttpAsync().execute(SERVER_URL, RESPONSE_FAIL).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (response != null) {
            if (response.equals(RESPONSE_OK)) {
                login();
            } else {
                emailEditText.setError(getString(R.string.login_fail));
            }
        }
    }

    private boolean emailValid() {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText()).matches();
    }

    public void login() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(USER_LOGIN_STATUS, true);
        editor.putString(USER_EMAIL, emailEditText.getText().toString());
        editor.putString(USER_PASS, passEditText.getText().toString());
        editor.apply();

        //FIXME Quick fix for modules marge
        startActivity(new Intent(getActivity(), PoiMapActivity.class));
    }

    @Override
    public void onPause() {
        super.onPause();
        emailEditText.getEditableText().clear();
        passEditText.getEditableText().clear();
        emailEditText.setError(null);
        passEditText.setError(null);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        }
    }
}
