package com.example.phonephoto;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phonephoto.data.JoinData;
import com.example.phonephoto.data.JoinResponse;
import com.example.phonephoto.network.RetrofitClient;
import com.example.phonephoto.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinActivity extends AppCompatActivity {

    String TAG = "PJ2 JoinActivity";

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mNameView;
    private Button mJoinButton;
    private Button mCancelButton;
    private ProgressBar mProgressView;
    private ServiceApi service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Log.d(TAG, "onCreate");

        mEmailView = (AutoCompleteTextView) findViewById(R.id.join_email);
        mPasswordView = (EditText) findViewById(R.id.join_password);
        mNameView = (EditText) findViewById(R.id.join_name);
        mJoinButton = (Button) findViewById(R.id.join_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mProgressView = (ProgressBar) findViewById(R.id.join_progress);

        service = RetrofitClient.getRetrofit().create(ServiceApi.class);

        mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptJoin();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void attemptJoin() {
        Log.d(TAG, "attemptJoin");
        mNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // ??????????????? ????????? ??????
        if (password.isEmpty()) {
            mEmailView.setError("??????????????? ??????????????????.");
            focusView = mEmailView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError("6??? ????????? ??????????????? ??????????????????.");
            focusView = mPasswordView;
            cancel = true;
        }

        // ???????????? ????????? ??????
        if (email.isEmpty()) {
            mEmailView.setError("???????????? ??????????????????.");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("@??? ????????? ????????? ???????????? ??????????????????.");
            focusView = mEmailView;
            cancel = true;
        }

        // ????????? ????????? ??????
        if (name.isEmpty()) {
            mNameView.setError("????????? ??????????????????.");
            focusView = mNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            startJoin(new JoinData(name, email, password));
            showProgress(true);
        }
    }

    private void startJoin(JoinData data) {
        service.userJoin(data).enqueue(new Callback<JoinResponse>() {
            @Override
            public void onResponse(Call<JoinResponse> call, Response<JoinResponse> response) {
                Log.d(TAG, "onResponse");
                JoinResponse result = response.body();
                Toast.makeText(JoinActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                showProgress(false);

                if (result.getCode() == 200) {
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JoinResponse> call, Throwable t) {
                Log.d(TAG, "onFailure");
                Toast.makeText(JoinActivity.this, "???????????? ?????? ??????", Toast.LENGTH_SHORT).show();
                Log.e("???????????? ?????? ??????", t.getMessage());
                showProgress(false);
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}