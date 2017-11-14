package com.ilma.testing.ilmaapp.Models;

/**
 * Created by fazal on 11/14/2017.
 */

public class User {
    String fullname;
    String email;
    String gender;
    String bday;
    String UserID;

    public User(String fullname, String email, String gender, String bday, String userID) {
        this.fullname = fullname;
        this.email = email;
        this.gender = gender;
        this.bday = bday;
        UserID = userID;
    }

    public User(String fullname, String email, String gender) {
        this.fullname = fullname;
        this.email = email;
        this.gender = gender;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getBday() {
        return bday;
    }

    public String getUserID() {
        return UserID;
    }
}
