package com.blstream.as.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.blstream.as.HomeActivity;
import com.blstream.as.HttpAsync;
import com.blstream.as.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;


public class LoginScreenFragment extends Fragment {

    private Button loginButton;
    private Button backButton;
    private EditText emailEditText;
    private EditText passEditText;
    private SharedPreferences pref;
    private static final String LOGIN_PREFERENCES = "LoginPreferences";
    private static final String USER_LOGIN_STATUS = "UserLoginStatus";
    private static final String USER_EMAIL = "UserEmail";
    private static final String USER_PASS = "UserPass";

    private static final String SERVER_URL = "http://78.133.154.62:1080/users/whoami";
    private static final Integer RESPONSE_FAIL = 401;

    private ProgressDialog loginProgressDialog;

    public LoginScreenFragment() {

    }

    public static LoginScreenFragment newInstance() {
        return new LoginScreenFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loginScreenView = inflater.inflate(R.layout.login_screen_fragment, container, false);

        Context context = getActivity();
        pref = context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);

        emailEditText = (EditText) loginScreenView.findViewById(R.id.email);
        passEditText = (EditText) loginScreenView.findViewById(R.id.password);
        emailEditText.setError(null);
        passEditText.setError(null);

        loginButton = (Button) loginScreenView.findViewById(R.id.loginButton);
        backButton = (Button) loginScreenView.findViewById(R.id.backButton);

        emailEditText.setOnFocusChangeListener(emailListener);
        passEditText.setOnFocusChangeListener(passListener);

        setLoginListener();
        setBackListener();

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

                    if (formIsEmpty()){
                        showEmptyFormDialog();
                    }

                    if (formIsCorrect()) {
                    loginProgressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.login_progress_dialog), true);
                    try {
                        getResponse();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void showEmptyFormDialog(){
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.empty_login_form)
                .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .show();
    }

    public boolean formIsEmpty(){
        return (emailEditText.getError()!=null && passEditText.getError()!=null);
    }

    public boolean formIsCorrect(){
        return (emailEditText.getError()==null && passEditText.getError()==null);
    }

    public void getResponse() throws IOException, JSONException {
        HttpAsync http = new HttpAsync();
        http.post(SERVER_URL, emailEditText.getText().toString(), passEditText.getText().toString(), new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                loginProgressDialog.dismiss();
                showConnectionErrorDialog();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    login();
                } else {
                    loginProgressDialog.dismiss();
                    if (response.code() == RESPONSE_FAIL) {
                        showLoginFailDialog();
                    } else {
                        showConnectionErrorDialog();
                    }
                }
            }
        });
    }

    public void login() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(USER_LOGIN_STATUS, true);
        editor.putString(USER_EMAIL, emailEditText.getText().toString());
        editor.putString(USER_PASS, passEditText.getText().toString());
        editor.apply();
        loginProgressDialog.dismiss();

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                emailEditText.getEditableText().clear();
                passEditText.getEditableText().clear();
                emailEditText.setError(null);
                passEditText.setError(null);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //FIXME Quick fix for modules marge
        startActivity(new Intent(getActivity(), HomeActivity.class));
    }

    public void showLoginFailDialog() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.login_fail)
                        .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }

    public void showConnectionErrorDialog() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.connection_fail)
                        .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }

    public void setBackListener(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToStartScreen();
            }
        });
    }

    public void goToStartScreen(){
        if (getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        }
    }
}
