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


public class RegisterActivity extends Activity {

    private Button registerButton;
    private EditText emailEditText;
    private EditText passEditText;
    private EditText repeatEditText;
    String email;
    String pass;
    String repeat;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    boolean Error = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = getApplicationContext().getSharedPreferences("Pref", Context.MODE_PRIVATE);
        if (pref.getBoolean("LoggedIn",false)) {
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = (EditText)findViewById(R.id.email);
        passEditText = (EditText)findViewById(R.id.password);
        repeatEditText = (EditText)findViewById(R.id.repeatPass);
        registerListener();
    }

    public void registerListener() {
        registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        public void onClick(View v) {
            emailEditText.setError(null);
            passEditText.setError(null);
            repeatEditText.setError(null);
            Error = false;

            email = emailEditText.getText().toString();
            pass = passEditText.getText().toString();
            repeat = repeatEditText.getText().toString();

            if (!EmailValid()) {
                emailEditText.setError("Nieprawidłowy email!");
                Error = true;
            }
            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Pole wymagane!");
                Error = true;
            }
            if (!PasswordValid()) {
                passEditText.setError("Nieprawidłowe hasło!");
                Error = true;
            }
            if (TextUtils.isEmpty(pass)) {
                passEditText.setError("Pole wymagane!");
                Error = true;
            }
            if (!pass.equals(repeat)){
                repeatEditText.setError("Hasła muszą być jednakowe!");
                Error = true;
            }
            if (TextUtils.isEmpty(repeat)) {
                repeatEditText.setError("Pole wymagane!");
                Error = true;
            }
            if (!Error) {
                Register();
            }
        }
    };

    public void Register() {
        editor = pref.edit();
        editor.putBoolean("LoggedIn", true);
        editor.putString("Email", email);
        editor.putString("Haslo", pass);
        editor.commit();
        finish();
    }

    private boolean EmailValid() {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean PasswordValid() {
        final String PASS_PATTERN = "((?=.*\\d)(?=.*[a-zA-Z]).{5,})";
        Pattern pattern = Pattern.compile(PASS_PATTERN);
        Matcher matcher = pattern.matcher(pass);
        return matcher.matches();
    }

    @Override
    public void onRestart() {
        if(pref.getBoolean("LoggedIn",false))
            finish();
        emailEditText.setText("");
        passEditText.setText("");
        super.onRestart();
    }
}