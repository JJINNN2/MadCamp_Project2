package com.example.phonephoto.phone;

import com.google.gson.annotations.SerializedName;

// 요청 시 보낼 데이터
public class PhoneUploadData {
    @SerializedName("phoneId")
    int phoneId;

    @SerializedName("phoneName")
    String phoneName;

    @SerializedName("phoneNumber")
    String phoneNumber;

    public PhoneUploadData(int phoneId, String phoneName, String phoneNumber) {
        this.phoneId = phoneId;
        this.phoneName = phoneName;
        this.phoneNumber = phoneNumber;
    }
}