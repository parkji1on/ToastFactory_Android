package com.example.tykoon.retrofit.model;


import com.google.gson.annotations.SerializedName;

public class PostMemberReq {
    @SerializedName("name") private String name;
    @SerializedName("password") private String password;

    public PostMemberReq(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
