package com.example.phonephoto.network;

import com.example.phonephoto.data.DeleteData;
import com.example.phonephoto.data.ShowResponse;
import com.example.phonephoto.data.JoinData;
import com.example.phonephoto.data.JoinResponse;
import com.example.phonephoto.data.LoginData;
import com.example.phonephoto.data.LoginResponse;
import com.example.phonephoto.data.UploadResponse;
import com.example.phonephoto.phone.PhoneUploadData;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ServiceApi {
    @Multipart
    @POST("/photo/upload")
    Call<UploadResponse> uploadPhoto(@Part MultipartBody.Part file, @Part("fileName") RequestBody name);

    // URL이 고정된 서버에서 파일 다운
    @POST("/photo/show")
    Call<ShowResponse> photoShow();

    // @Body 는 여러 파라미더를 전달하지 못함. 구조체로 만들어서 전달하자.
    @POST("/photo/delete")
    //Call<UploadResponse> deleteFile(@Body String fileName, @Body String filePath);
    Call<UploadResponse> deleteFile(@Body DeleteData data);

    @POST("/get_image")
    Call<UploadResponse> getOneFile(@Query("path") String filePath);

    @POST("/user/login")
    Call<LoginResponse> userLogin(@Body LoginData data);

    @POST("/user/join")
    Call<JoinResponse> userJoin(@Body JoinData data);

    @POST("/phone/upload")
    Call<UploadResponse> phoneUpload(@Body PhoneUploadData data);

    @POST("/phone/show")
    Call<ShowResponse> phoneShow();

    @POST("/delete_for_id")
    Call<UploadResponse> deleteForId(@Query("number") String number);
}
