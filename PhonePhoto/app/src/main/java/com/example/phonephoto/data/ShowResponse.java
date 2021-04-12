package com.example.phonephoto.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ShowResponse {
    @SerializedName("name")
    ArrayList<String> name;

    @SerializedName("numberOrPath")
    ArrayList<String> numberOrPath;

    public ArrayList<String> getName() { return name; }
    public ArrayList<String> getNumberOrPath() { return numberOrPath; }
}
