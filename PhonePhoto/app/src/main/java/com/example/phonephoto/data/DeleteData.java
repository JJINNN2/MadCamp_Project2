package com.example.phonephoto.data;

import com.google.gson.annotations.SerializedName;

public class DeleteData {
    @SerializedName("fileName")
    private String fileName;

    @SerializedName("filePath")
    private String filePath;

    public DeleteData(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }
}
