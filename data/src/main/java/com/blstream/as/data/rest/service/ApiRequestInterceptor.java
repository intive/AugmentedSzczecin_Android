package com.blstream.as.data.rest.service;
import android.util.Base64;

import com.blstream.as.data.rest.model.User;

import retrofit.RequestInterceptor;
/**
 * Created by Rafał Soudani on 2015-04-25.
 */
public class ApiRequestInterceptor implements RequestInterceptor {

    private User user;

    @Override
    public void intercept(RequestFacade requestFacade) {

        if (user != null) {
            final String authorizationValue = encodeCredentialsForBasicAuthorization();
            requestFacade.addHeader("Authorization", authorizationValue);
        }
    }

    private String encodeCredentialsForBasicAuthorization() {
        final String userAndPassword = user.getUsername() + ":" + user.getPassword();
        return "Basic " + Base64.encodeToString(userAndPassword.getBytes(), Base64.NO_WRAP);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
