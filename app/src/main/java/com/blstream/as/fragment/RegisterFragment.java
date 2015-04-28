package com.blstream.as.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blstream.as.HttpAsync;
import com.blstream.as.MainActivity;
import com.blstream.as.R;
import com.blstream.as.maps2d.PoiMapActivity;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {
    private EditText emailEditText;
    private EditText passEditText;
    private EditText repeatEditText;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String LOGIN_PREFERENCES = "LoginPreferences";
    private static final String USER_LOGIN_STATUS = "UserLoginStatus";
    private static final String USER_EMAIL = "UserEmail";
    private static final String USER_PASS = "UserPass";
    private static final String SERVER_URL = "http://private-f8d40-example81.apiary-mock.com/user";
    private static final String RESPONSE_FAIL = "status=500";
    private static final Integer RESPONSE_OK = 201;
    private static final String EXISTING_USER_EMAIL = "user@user.com";

    public RegisterFragment() {

    }

    public static RegisterFragment newInstance(){
        return new RegisterFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View registerView = inflater.inflate(R.layout.register_fragment, container, false);

        emailEditText = (EditText) registerView.findViewById(R.id.email);
        passEditText = (EditText) registerView.findViewById(R.id.password);
        repeatEditText = (EditText) registerView.findViewById(R.id.repeatPass);

        Button registerButton = (Button) registerView.findViewById(R.id.registerButton);

        Context context = getActivity();
        pref = context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);

        emailEditText.setOnFocusChangeListener(emailListener);
        passEditText.setOnFocusChangeListener(passListener);
        repeatEditText.setOnFocusChangeListener(repeatListener);

        registerButton.setOnClickListener(registerListener);

        if (!isInternetAvailable()){
            Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }

        return registerView;
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    View.OnFocusChangeListener emailListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus){
            if(!hasFocus){
                checkEmail();
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
                checkPassword();
            }
        }
    };

    View.OnFocusChangeListener repeatListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus){
            if(!hasFocus){
                checkRepeatPassword();
            }
        }
    };

    View.OnClickListener registerListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (isInternetAvailable()) {
                checkEmail();
                checkPassword();
                checkRepeatPassword();

                if (emailEditText.getError() == null && passEditText.getError() == null && repeatEditText.getError() == null) {
                    getResponse();
                }
            }
            else {
                Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
            }
        }
    };

    public void getResponse() {
        Integer response = null;
        try {
            if (!(emailEditText.getText().toString().equals(EXISTING_USER_EMAIL)))
                response = new HttpAsync().execute(SERVER_URL).get();
            else
                response = new HttpAsync().execute(SERVER_URL,RESPONSE_FAIL).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (response != null) {
            if (response.equals(RESPONSE_OK)) {
                register();
            } else {
                Toast.makeText(getActivity(), getString(R.string.register_fail), Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }
    }

    public void register() {
        editor = pref.edit();
        editor.putBoolean(USER_LOGIN_STATUS, true);
        editor.putString(USER_EMAIL, emailEditText.getText().toString());
        editor.putString(USER_PASS, passEditText.getText().toString());
        editor.apply();

        //FIXME Quick fix for modules marge
        startActivity(new Intent(getActivity(), PoiMapActivity.class));
    }

    private boolean emailValid() {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText()).matches();
    }

    private boolean passwordValid() {
        final String PASS_PATTERN = "((?=.*\\d)(?=.*[a-zA-Z]).{5,})";
        Pattern pattern = Pattern.compile(PASS_PATTERN);
        Matcher matcher = pattern.matcher(passEditText.getText());
        return matcher.matches();
    }

    private void checkEmail(){
        if (!emailValid()) {
            emailEditText.setError(getString(R.string.wrong_email));
        }
        if (TextUtils.isEmpty(emailEditText.getText())) {
            emailEditText.setError(getString(R.string.email_required));
        }
    }

    private void checkPassword(){
        if (!passwordValid()) {
            passEditText.setError(getString(R.string.wrong_password));
        }
        if (TextUtils.isEmpty(passEditText.getText())) {
            passEditText.setError(getString(R.string.password_required));
        }
    }

    private void checkRepeatPassword(){
        if (!passEditText.getText().toString().equals(repeatEditText.getText().toString())){
            repeatEditText.setError(getString(R.string.different_passwords));
        }
        if (TextUtils.isEmpty(repeatEditText.getText())) {
            repeatEditText.setError(getString(R.string.repeat_pass_required));
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        emailEditText.getEditableText().clear();
        passEditText.getEditableText().clear();
        repeatEditText.getEditableText().clear();

        if (getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        }
    }
}
