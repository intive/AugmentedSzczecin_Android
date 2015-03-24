package com.blstream.as.rest.model;

/**
 * Created by Rafal Soudani on 2015-03-23.
 */
public class User {

    private int id;
    private String username;
    private String password;

    /**
     * @return The id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(int id) {
        this.id = id;
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
        return String.format("User id:%d, name:%s");
    }
}