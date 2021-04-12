package com.example.mylogin;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {

    String TAG = "hamApp MainActivity";
    int MY_REQUEST_PERMISSIONS = 1234;                      // 내 임의로 정한 값
    public static final String[] PERMISSIONS = {            // 허용 필요한 권한 list
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    int NUMBER_OF_PERMISSION = PERMISSIONS.length;          // 권한 개수

    Button btnUpload, btnMulUpload, btnPickImage, btnPickVideo;
    String mediaPath, mediaPath1;
    ImageView imgView;
    String[] mediaColumns = {MediaStore.Video.Media._ID};
    ProgressDialog progressDialog;
    TextView str1, str2;

    Socket socket;

    Uri imgUri;

    ApiConfig apiConfig;

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** 권한을 거절했다면 view 출력하지 않도록 하기 **/
        if(arePermissionsDenied()) return;

        imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.d(TAG, "Uri: " + String.valueOf(imgUri));


        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Uploading...");

        btnUpload = (Button) findViewById(R.id.upload);
        btnMulUpload = (Button) findViewById(R.id.uploadMultiple);
        btnPickImage = (Button) findViewById(R.id.pick_img);
        btnPickVideo = (Button) findViewById(R.id.pick_vdo);
        imgView = (ImageView) findViewById(R.id.preview);
        str1 = (TextView) findViewById(R.id.filename1);
        str2 = (TextView) findViewById(R.id.filename2);

        //apiConfig = AppConfig.getRetrofit().create(ApiConfig.class);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();

            }
        });

        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        try {
//            socket = IO.socket("http://192.249.19.242:7080");
//            socket.connect();
//            socket.on(io.socket.client.Socket.EVENT_CONNECT, onConnect);
//            socket.on("serverMessage", onMessageReceived);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
    }

    // socket 서버에 connect 되면 발생하는 이벤트
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String[] myArgs = {"Ham1", "chang1", "su1"};
            socket.emit("image", imgUri);
        }
    };

    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 전달받은 데이터 추출
            try {
                JSONObject receivedData = (JSONObject) args[0];
                Log.d(TAG, receivedData.getString("msg"));
                Log.d(TAG, receivedData.getString("data"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Log.d("hamApp", "onActivityResult");
            // When an Image is picked
            if (requestCode == 0 && resultCode == RESULT_OK && null != data) {

                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediaPath = cursor.getString(columnIndex);
                str1.setText(mediaPath);
                // Set the Image in ImageView for Previewing the Media
                imgView.setImageBitmap(BitmapFactory.decodeFile(mediaPath));
                cursor.close();

            } // When an Video is picked
            else if (requestCode == 1 && resultCode == RESULT_OK && null != data) {

                // Get the Video from data
                Uri selectedVideo = data.getData();
                String[] filePathColumn = {MediaStore.Video.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedVideo, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                mediaPath1 = cursor.getString(columnIndex);
                str2.setText(mediaPath1);
                // Set the Video Thumb in ImageView Previewing the Media
                imgView.setImageBitmap(getThumbnailPathForLocalFile(MainActivity.this, selectedVideo));
                cursor.close();

            } else {
                Toast.makeText(this, "You haven't picked Image/Video", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

    }

    // Providing Thumbnail For Selected Image
    public Bitmap getThumbnailPathForLocalFile(Activity context, Uri fileUri) {
        long fileId = getFileId(context, fileUri);
        return MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),
                fileId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
    }

    // Getting Selected File ID
    public long getFileId(Activity context, Uri fileUri) {
        Cursor cursor = context.managedQuery(fileUri, mediaColumns, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            return cursor.getInt(columnIndex);
        }
        return 0;
    }

    // Uploading Image/Video
    private void uploadFile() {
        Log.d("hamApp", "uploadFile");
        //progressDialog.show();

        // Map is used to multipart the file using okhttp3.RequestBody
        File file = new File(mediaPath);

        Log.d("hamApp", String.valueOf(mediaPath));

        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("ham1", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);

        Log.d("hamApp", "requestBody: " + String.valueOf(requestBody));
        //Log.d("hamApp", "fileToUpload: " + String.valueOf(fileToUpload));
        //Log.d("hamApp", "filename: " + String.valueOf(filename));

        //Call call = getResponse.uploadFile(fileToUpload, filename);
        //Call call = getResponse.uploadFile(fileToUpload, file.getName());
        Call call = getResponse.uploadFile(fileToUpload, filename);

        Log.d("hamApp", "call: " + call.toString());
        Log.d("hamApp", "file name: " + file.getName());

        // enqueue: [네이버국어사전] (데이터의 아이템)을 큐(대기 행렬)에 더하다.
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                Log.d("hamApp", "onResponse");
                ServerResponse serverResponse = response.body();
                Log.d("hamApp", String.valueOf(serverResponse));
                if (serverResponse != null) {
                    Log.d("hamApp", "serverResponse 받았다!");
                    if (serverResponse.getSuccess()) {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("hamApp","server msg: "+serverResponse.getMessage());
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Call은 제대로 보냈으나 서버에서 이거뭐냐? 하고 reponse를 보낸 경우 (????)
                    Log.d("hamApp", "serverResponse 못받았어ㅠ_ㅠ");
                    assert serverResponse != null;
                    Log.v("hamApp", serverResponse.toString());
                }
                progressDialog.dismiss();
            }

            // Call을 서버쪽으로 아예 보내지 못한 경우
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("hamApp", "onFailure");
                Log.d("hamApp", t.toString());
            }
        });
    }


    // Uploading Image/Video
//    private void uploadMultipleFiles() {
//        Log.d("hamApp", "uploadMultipleFiles");
//        progressDialog.show();
//
//        // Map is used to multipart the file using okhttp3.RequestBody
//        File file = new File(mediaPath);
//        File file1 = new File(mediaPath1);
//
//        // Parsing any Media type file
//        RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file);
//        RequestBody requestBody2 = RequestBody.create(MediaType.parse("*/*"), file1);
//
//        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file1", file.getName(), requestBody1);
//        MultipartBody.Part fileToUpload2 = MultipartBody.Part.createFormData("file2", file1.getName(), requestBody2);
//
//        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
//        Call<ServerResponse> call = getResponse.uploadMulFile(fileToUpload1, fileToUpload2);
//        //Call<ServerResponse> call = getResponse.uploadFile(fileToUpload1, requestBody1);
//
//        call.enqueue(new Callback<ServerResponse>() {
//            @Override
//            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
//                ServerResponse serverResponse = response.body();
//                if (serverResponse != null) {
//                    if (serverResponse.getSuccess()) {
//                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    assert serverResponse != null;
//                    Log.v("Response", serverResponse.toString());
//                }
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onFailure(Call<ServerResponse> call, Throwable t) {
//
//            }
//        });
//    }


    /** arePermissionsDenied()
     *
     * 필요한 권한들이 모두 허용이 된 상태인지 체크
     * @return 모두 허용된 경우 false
     */
    public boolean arePermissionsDenied() {
        Log.d(TAG, "arePermissionsDenied");
        for (int i = 0; i < NUMBER_OF_PERMISSION; i++) {
            if (ContextCompat.checkSelfPermission(this, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED)
                return true;
        }
        return false;
    }

    /** onRequestPermissionsResult()
     *
     * requestPermissions 으로 사용자가 앱 권한 요청에 응답하면
     * 시스템은 앱의 onRequestPermissionsResult() 메서드를 호출하여 사용자 응답을 전달한다.
     * @param requestCode ?
     * @param permissions ?
     * @param grantResults ?
     *
     * 권한 거절했다면 finish()
     * 모두 허용했다면 onResume()
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_REQUEST_PERMISSIONS && grantResults.length > 0) {
            if(arePermissionsDenied()) finish();
            onResume();
        }
    }
}