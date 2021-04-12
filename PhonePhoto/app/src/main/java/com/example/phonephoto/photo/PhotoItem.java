package com.example.phonephoto.photo;

import android.telephony.PhoneNumberUtils;

import androidx.annotation.NonNull;

public class PhotoItem {
    private int id;
    private String path;
    private String name;
    private String addedDate;

    public PhotoItem(int id, String path, String name, String addedDate) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.addedDate = addedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }
}
