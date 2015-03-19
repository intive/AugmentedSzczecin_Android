package com.blstream.as;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends Activity {

    private Button loginButton;
    private Button registerButton;
    private EditText emailEditText;
    private EditText passEditText;
    String email;
    String pass;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    boolean Error = false;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = getApplicationContext().getSharedPreferences("Pref", Context.MODE_PRIVATE);
        if (pref.getBoolean("LoggedIn",false)) {
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = (EditText)findViewById(R.id.email);
        passEditText = (EditText)findViewById(R.id.password);
        setLoginListener();
        setRegisterListener();
    }

    public void setLoginListener() {
        loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(loginListener);
    }

    public void setRegisterListener() {
        registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(registerListener);
    }

    View.OnClickListener registerListener = new View.OnClickListener() {
        public void onClick(View v) {
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener loginListener = new View.OnClickListener() {
        public void onClick(View v) {
            emailEditText.setError(null);
            passEditText.setError(null);
            Error = false;

            email = emailEditText.getText().toString();
            pass = passEditText.getText().toString();

            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Pole wymagane!");
                Error = true;
            }
            if (TextUtils.isEmpty(pass)) {
                passEditText.setError("Pole wymagane!");
                Error = true;
            }

            if (!Error) {
                Login();
            }
        }
    };

    public void Login() {
        editor = pref.edit();
        editor.putBoolean("LoggedIn", true);
        editor.putString("Email", email);
        editor.putString("Haslo", pass);
        editor.commit();
        this.finish();
    }

    @Override
    public void onRestart() {
        pref = getApplicationContext().getSharedPreferences("Pref", Context.MODE_PRIVATE);
        if(pref.getBoolean("LoggedIn",false))
            finish();
        emailEditText.setText("");
        passEditText.setText("");
        super.onRestart();
    }
}