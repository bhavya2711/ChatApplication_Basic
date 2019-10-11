package com.example.likhi.inclass06;

import java.io.Serializable;

public class User implements Serializable {
    String fname,lname,token,id;

    public User(String fname, String lname, String token, String id) {
        this.fname = fname;
        this.lname = lname;
        this.token = token;
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", token='" + token + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
