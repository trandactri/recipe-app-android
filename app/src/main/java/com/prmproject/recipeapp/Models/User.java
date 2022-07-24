package com.prmproject.recipeapp.Models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class User implements Parcelable {
    protected String id;
    protected String email;
    protected String role;

    public User(String id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public User() {
    }


    protected User(Parcel in) {
        id = in.readString();
        email = in.readString();
        role = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(role);
    }

}
