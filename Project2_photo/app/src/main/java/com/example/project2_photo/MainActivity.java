package com.example.project2_photo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    String TAG = "hamApp MainActivity";
    int MY_REQUEST_PERMISSIONS = 1234;                      // 내 임의로 정한 값
    public static final String[] PERMISSIONS = {            // 허용 필요한 권한 list
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    int NUMBER_OF_PERMISSION = PERMISSIONS.length;          // 권한 개수

    RecyclerView photoList;                                 // 갤러리 출력될 recyclerView
    List<String> filesList;                                 // 갤러리 파일 경로들 저장
    RecyclerView.LayoutManager layoutManager;
    Button button;

    Uri imgUri;                                             // 기기 이미지 파일 uri
    private Socket socket;
    private JSONObject data;                                // 서버로 전달할 데이터
    Cursor cursor;                                          // 갤러리 이미지 탐색

    EditText mEdityEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** 접근 권한 허용 요청하기 **/
        if(arePermissionsDenied())
            ActivityCompat.requestPermissions(this, PERMISSIONS, MY_REQUEST_PERMISSIONS);

        photoList = findViewById(R.id.photoList);
        mEdityEntry = findViewById(R.id.editText);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        /** 권한을 거절했다면 view 출력하지 않도록 하기 **/
        if(arePermissionsDenied()) return;

        photoList = findViewById(R.id.photoList);
        filesList = new ArrayList<>();  // 갤러리 파일 경로들

        /** 내부 이미지 파일 가져오기 **/

        /** 로컬 저장소의 Pictures 폴더 경로 가져오기 (1) **
         * 문제: getExternalStoragePublicDirectory()가 API level 29부터 deprecated */
        // File filePath;
        // filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        /** 로컬 저장소의 Pictures 폴더 경로 가져오기 (2) **
         * 출력: filePath: /storage/emulated/0/Android/data/com.example.project2_photo/files/Pictures
         * 문제: 다른 경로의 폴더도 따로 구해야 함
         */
        // File filePath;
        // filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        /** 로컬 저장소의 모든 이미지 파일 가져오기 **
         * MediaStore.Images: Collection of all media with MIME type of image/*
         * 출력: Uri: content://media/external/images/media
         *
         * ContentResolver.getType(): MIME 유형을 return
         * 출력: mimeType: vnd.android.cursor.dir/image
         */
        imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.d(TAG, "Uri: " + String.valueOf(imgUri));

        String mimeType = getContentResolver().getType(imgUri);
        //Log.d(TAG, "mimeType: " + mimeType);

        /** cursor 이용해 얻은 테이블 정보:
         *  | _id | _data | _size | _display_name | mime_type | title | date_added | date_modified | ...
         */
        cursor = getContentResolver().query(imgUri, null, null, null, null);
        cursor.moveToNext();
        final String urlString = "http://192.249.19.242:7080/root/";
        final String absolutePath = cursor.getString(cursor.getColumnIndex("_data"));
        Log.d(TAG, "절대경로: " + absolutePath);

        // socket 서버 접속
        try {
            socket = IO.socket("http://192.249.19.242:7080");
            socket.connect();
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on("serverMessage", onMessageReceived);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        /** 서버로 이미지 전달 **/
        //int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        //int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        /*cursor.moveToFirst();
        Log.d("ham MainActivity", "name: " + nameIndex + " / " + cursor.getString(nameIndex)); // 3
        Log.d("ham MainActivity", "size: " + sizeIndex + " / " + cursor.getString(sizeIndex)); // 2
        Log.d("ham MainActivity", "size: " + 0 + " / " + cursor.getColumnName(0) + " / " + cursor.getString(0));
        Log.d("ham MainActivity", "size: " + 1 + " / " + cursor.getColumnName(1) + " / " + cursor.getString(1));
        Log.d("ham MainActivity", "size: " + 10 + " / " + cursor.getColumnName(10) + " / " + cursor.getString(10));
        Log.d("ham MainActivity", "size: " + 11 + " / " + cursor.getColumnName(11) + " / " + cursor.getString(11));
        */
        layoutManager = new GridLayoutManager(this,3);
        photoList.setLayoutManager(layoutManager);

        GalleryAdapter galleryAdapter = new GalleryAdapter(cursor);
        photoList.setAdapter(galleryAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selPhotoUri = imgUri;

        //나중에 이미지뷰에 뿌려주기 위해 담아놓음.
        try {
            Bitmap selPhoto = MediaStore.Images.Media.getBitmap( getContentResolver(), selPhotoUri );
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("전송","시~~작 ~~~~~!");

        String urlString = "http://192.249.19.242:7080";

        //절대경로를 획득한다!!! 중요~
        Cursor c = getContentResolver().query(Uri.parse(selPhotoUri.toString()), null,null,null,null);
        c.moveToNext();
        String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));

        //파일 업로드 시작!
        DoFileUpload(urlString , absolutePath);
    }

    public void DoFileUpload(String apiUrl, String absolutePath) {
        HttpFileUpload(apiUrl, "", absolutePath);
    }

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    FileInputStream mFileInputStream;
    URL connectUrl;

    public void HttpFileUpload(String urlString, String params, String fileName) {
        try {

            mFileInputStream = new FileInputStream(fileName);
            connectUrl = new URL(urlString);
            Log.d("Test", "mFileInputStream  is " + mFileInputStream);

            // open connection
            HttpURLConnection conn = (HttpURLConnection)connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // write data
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName+"\"" + lineEnd);
            dos.writeBytes(lineEnd);

            int bytesAvailable = mFileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            byte[] buffer = new byte[bufferSize];
            int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

            Log.d("Test", "image byte is " + bytesRead);

            // read image
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = mFileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            Log.e("Test" , "File is written");
            mFileInputStream.close();
            dos.flush(); // finish upload...

            // get response
            int ch;
            InputStream is = conn.getInputStream();
            StringBuffer b =new StringBuffer();
            while( ( ch = is.read() ) != -1 ){
                b.append( (char)ch );
            }
            String s=b.toString();
            Log.e("Test", "result = " + s);
            mEdityEntry.setText(s);
            dos.close();

        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
            // TODO: handle exception
        }
    }

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

    /*** 서버 접근을 위한 함수들 ***/
    // socket 서버에 connect 되면 발생시킬 이벤트
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            socket.emit("clientMessage", "connect success! Yeh!");
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
}