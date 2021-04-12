package com.example.mylogin;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

interface ApiConfig {
    @Multipart
    @POST("/upload")
//    Call<ServerResponse> uploadFile(@Part MultipartBody.Part file, @Part("file") RequestBody name);
    //Call<ServerResponse> uploadFile(@Part MultipartBody.Part file, @Part("file") String name);
    Call<ServerResponse> uploadFile(@Part MultipartBody.Part file, @Part("ham2") RequestBody name);

//    @Multipart
//    @POST("retrofit_example/upload_multiple_files.php")
//    Call<ServerResponse> uploadMulFile(@Part MultipartBody.Part file1,
//                                       @Part MultipartBody.Part file2);
}
