package com.blstream.as.data.rest.model;

import com.activeandroid.annotation.Column;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rafal Soudani on 2015-03-23.
 */
public class User {

    public static final String USER_ID = "UserId";
    public static final String USER_NAME= "UserName";
    public static final String PASSWORD = "Password";

    @Column(name = USER_ID)
    @SerializedName("id")
    private int userId;

    @Column(name = USER_NAME)
    private String username;

    @Column(name = PASSWORD)
    private String password;

    /**
     * @return The user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId The user id
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return The password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password The password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format("User id:%d, name:%s", userId, username);
    }
}