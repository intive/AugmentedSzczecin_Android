package com.blstream.as.data.rest.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rafal Soudani on 2015-06-10.
 */
@Table(name = Owner.TABLE_NAME, id = BaseColumns._ID)
public class Owner extends Model {
    public static final String TABLE_NAME = "Owners";
    public static final String OWNER_ID = "OwnerId";
    public static final String EMAIL = "Email";
    public static final String PASSWORD = "Password";

    @Column(name = OWNER_ID, unique = true)
    @SerializedName("id")
    private String ownerId;

    @Column(name = EMAIL)
    private String email;

    @Column(name = PASSWORD)
    private String password;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
