package com.blstream.as.fragment;

import android.content.Context;
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
import com.blstream.as.R;

import java.util.concurrent.ExecutionException;


public class LoginScreenFragment extends Fragment {

    private Button loginButton;
    private Button registerButton;
    private Button noRegisterButton;
    private Button aboutButton;
    private EditText emailEditText;
    private EditText passEditText;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String LOGIN_PREFERENCES = "LoginPreferences";
    private static final String USER_LOGIN_STATUS = "UserLoginStatus";
    private static final String USER_EMAIL = "UserEmail";
    private static final String USER_PASS = "UserPass";
    public static final String SERVER_URL = "http://private-f8d40-example81.apiary-mock.com/login";
    public static final String RESPONSE_CODE = "status=404";

    public LoginScreenFragment(){

    }

    public static final LoginScreenFragment newInstance(){
        LoginScreenFragment loginScreenFragment = new LoginScreenFragment();
        return loginScreenFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loginScreenView = inflater.inflate(R.layout.login_screen_fragment, container, false);

        Context context = getActivity();
        pref = context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);

        if (pref.getBoolean(USER_LOGIN_STATUS,false)){

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(android.R.id.content, LoggedInFragment.newInstance());
            fragmentTransaction.commit();
        }

        emailEditText = (EditText)loginScreenView.findViewById(R.id.email);
        passEditText = (EditText)loginScreenView.findViewById(R.id.password);
        emailEditText.setError(null);
        passEditText.setError(null);

        loginButton = (Button) loginScreenView.findViewById(R.id.loginButton);
        registerButton = (Button) loginScreenView.findViewById(R.id.registerButton);
        noRegisterButton = (Button) loginScreenView.findViewById(R.id.noRegisterButton);
        aboutButton = (Button) loginScreenView.findViewById(R.id.aboutButton);

        emailEditText.setOnFocusChangeListener(emailListener);
        passEditText.setOnFocusChangeListener(passListener);

        setLoginListener();
        setRegisterListener();
        setNoRegisterListener();
        setAboutListener();

        return loginScreenView;
    }

    View.OnFocusChangeListener emailListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus){
            if(!hasFocus) {
                if (TextUtils.isEmpty(emailEditText.getText())) {
                    emailEditText.setError(getString(R.string.field_required));
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

    View.OnFocusChangeListener passListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus){
            if(!hasFocus){
                if (TextUtils.isEmpty(passEditText.getText())) {
                    passEditText.setError(getString(R.string.field_required));
                }
            }
        }
    };

    public void setLoginListener(){
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (TextUtils.isEmpty(emailEditText.getText())) {
                    emailEditText.setError(getString(R.string.field_required));
                }
                if (TextUtils.isEmpty(passEditText.getText())) {
                    passEditText.setError(getString(R.string.field_required));
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
                response = new HttpAsync().execute(SERVER_URL,RESPONSE_CODE).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (response != null) {
            if (response == 200) {
                login();
            } else {
                emailEditText.setError(getString(R.string.login_fail));
            }
        }
    }

    private boolean emailValid() {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText()).matches();
    }

    public void login(){
        editor = pref.edit();
        editor.putBoolean(USER_LOGIN_STATUS, true);
        editor.putString(USER_EMAIL, emailEditText.getText().toString());
        editor.putString(USER_PASS, passEditText.getText().toString());
        editor.commit();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(android.R.id.content, LoggedInFragment.newInstance());
        fragmentTransaction.commit();
    }

    public void setRegisterListener(){
        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Intent i = new Intent(getActivity(), com.blstream.as.data.MainActivity.class);
                // tu chcial bym uruchomic activity z innego modulu
            }
        });
    }

    public void setNoRegisterListener(){
        noRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(android.R.id.content, NotLoggedInFragment.newInstance());
                fragmentTransaction.commit();
            }
        });
    }

    public void setAboutListener(){
        aboutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(android.R.id.content, AboutFragment.newInstance());
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        emailEditText.getEditableText().clear();
        passEditText.getEditableText().clear();
        emailEditText.setError(null);
        passEditText.setError(null);
    }
}
