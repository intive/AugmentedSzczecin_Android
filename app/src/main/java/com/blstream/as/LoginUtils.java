package com.blstream.as;


import android.content.Context;
import android.content.SharedPreferences;

public final class LoginUtils {
    public static boolean isUserLogged(Context context){
        SharedPreferences pref;
        final String LOGIN_PREFERENCES = "LoginPreferences";
        final String USER_LOGIN_STATUS = "UserLoginStatus";

        pref = context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        if (pref.getBoolean(USER_LOGIN_STATUS,false)) {
            return true;
        }
        return false;
    }
}
