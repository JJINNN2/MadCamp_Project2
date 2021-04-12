package com.example.phonephoto.data;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    // variable name should be same as in the json response from php
    @SerializedName("code")
    int code;

    @SerializedName("message")
    String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
