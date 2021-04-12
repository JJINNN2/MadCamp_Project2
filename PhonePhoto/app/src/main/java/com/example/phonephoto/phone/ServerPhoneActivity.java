package com.example.phonephoto.phone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.phonephoto.R;
import com.example.phonephoto.data.DeleteData;
import com.example.phonephoto.data.ShowResponse;
import com.example.phonephoto.data.UploadResponse;
import com.example.phonephoto.network.RetrofitClient;
import com.example.phonephoto.network.ServiceApi;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerPhoneActivity extends AppCompatActivity {

    String TAG = "PJ2 ServerPhoneActivity";

    ConstraintLayout constraintLayout;
    RecyclerView recyclerView;

    LinearLayout selectedImage;

    TextView phoneName;
    TextView phoneNumber;

    LinearLayoutManager layoutManager;
    ServerPhoneAdapter serverPhoneAdapter;
    ArrayList<String> idArray, nameArray, numberArray;

    Handler handler = new Handler();  // 외부쓰레드 에서 메인 UI화면을 그릴때 사용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_phone);

        constraintLayout = findViewById(R.id.constraintLayout);
        recyclerView = findViewById(R.id.recyclerView);
        selectedImage = findViewById(R.id.selevtedImage);   //폰에서 쓸까?
//        phoneName = findViewById(R.id.phoneName);
//        phoneNumber = findViewById(R.id.phoneNumber);
        // recyclerView 에 linear layout 매니저 연결
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        showFile();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        // 이름과 번호
        serverPhoneAdapter = new ServerPhoneAdapter(this, nameArray, numberArray);
        recyclerView.setAdapter(serverPhoneAdapter);
        Log.d(TAG, "onResume2");
    }

    private void showFile() {
        Log.d(TAG, "showFile");
        ServiceApi getResponse = RetrofitClient.getRetrofit().create(ServiceApi.class);
        Call<ShowResponse> call = getResponse.phoneShow();

        Log.d(TAG, String.valueOf(call));

        call.enqueue(new Callback<ShowResponse>() {
            @Override
            public void onResponse(Call<ShowResponse> call, Response<ShowResponse> response) {
                Log.d(TAG, "onResponse");

                ShowResponse downloadResponse = response.body();
                Log.d(TAG, String.valueOf(downloadResponse));

                if (downloadResponse != null) {
                    Log.d(TAG, "downloadResponse 받았다!");

                    nameArray = downloadResponse.getName();
                    numberArray = downloadResponse.getNumberOrPath();
                    //Log.d(TAG, nameArray[4]);
                    onResume();
                    Log.d(TAG, "진짜 잘받았나");

                } else {
                    // Call은 제대로 보냈으나 response 값이 제대로 오지 않음
                    Log.d(TAG, "serverResponse 못받았어ㅠ_ㅠ");
                }
            }

            @Override
            public void onFailure(Call<ShowResponse> call, Throwable t) {
                Log.d(TAG, "onFailure");
            }
        });
    }

    public void downloadPhone(int position) {
        Log.d(TAG, "downloadImage");
        final String name = nameArray.get(position);
        final String number = numberArray.get(position);

        Log.d(TAG, "selected name: "+name);
        Log.d(TAG, "selected number: "+number);

        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);

        startActivity(intent);
    }

    public void deletePhone(final int position) {
        Log.d(TAG, "deletePhone");
        final String number = numberArray.get(position);

        //Log.d(TAG, name + ", " + path);

        ServiceApi getResponse = RetrofitClient.getRetrofit().create(ServiceApi.class);
        Call<UploadResponse> call = getResponse.deleteForId(number);
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                nameArray.remove(position);
                numberArray.remove(position);
                onResume();

                Log.d(TAG, "onResponse");
            }
            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Log.d(TAG, "onFailure");
            }
        });

    }

    public void backClick(View view) {
        setResult(RESULT_OK);
        finish();
    }
}