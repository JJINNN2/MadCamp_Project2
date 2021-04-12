package com.example.phonephoto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phonephoto.data.LoginData;
import com.example.phonephoto.data.LoginResponse;
import com.example.phonephoto.network.RetrofitClient;
import com.example.phonephoto.network.ServiceApi;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    String TAG = "PJ2 LoginActivity";

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private TextView mJoinButton;
    private Button mLoginButton;
    private ProgressBar mProgressView;
    private ServiceApi service;

    private SessionCallback sessionCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate");

        mEmailView = (AutoCompleteTextView) findViewById(R.id.login_email);
        mPasswordView = (EditText) findViewById(R.id.login_password);
        mJoinButton = (TextView) findViewById(R.id.join_button);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mProgressView = (ProgressBar) findViewById(R.id.login_progress);
        service = RetrofitClient.getRetrofit().create(ServiceApi.class);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });

        // 카카오톡 로그인
        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();

        //커스텀 카카오 로그인 버튼
//        Button btnLoginKakao = findViewById(R.id.kakaoLoginButton);
//        btnLoginKakao.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
//            }
//        });
//        getAppKeyHash();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // 카카오톡 로그인
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int result = errorResult.getErrorCode();

                    if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),"로그인 도중 오류가 발생했습니다: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(),"세션이 닫혔습니다. 다시 시도해 주세요: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("name", result.getNickname());
                    intent.putExtra("profile", result.getProfileImagePath());
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) {
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    //해쉬키 받는 코드
    /*private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }*/
    // 카카오톡 코드 끝


    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean isProblem = false;
        View focusView = null;

        // 패스워드의 유효성 검사
        if (password.isEmpty()) {
            mEmailView.setError("비밀번호를 입력해주세요.");
            focusView = mEmailView;
            isProblem = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError("6자 이상의 비밀번호를 입력해주세요.");
            focusView = mPasswordView;
            isProblem = true;
        }

        // 이메일의 유효성 검사
        if (email.isEmpty()) {
            mEmailView.setError("이메일을 입력해주세요.");
            focusView = mEmailView;
            isProblem = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("@를 포함한 유효한 이메일을 입력해주세요.");
            focusView = mEmailView;
            isProblem = true;
        }

        if (isProblem) {
            focusView.requestFocus();
            Log.d(TAG, "attemptLogin: there is problem.");
        } else {
            startLogin(new LoginData(email, password));
            showProgress(true);
            Log.d(TAG, "attemptLogin: 입력한 값 형식에는 문제 없음");
        }
    }

    private void startLogin(LoginData data) {
        Log.d(TAG, "startLogin");

        service.userLogin(data).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.body().getCode() == 200) {
                    Log.d(TAG, "startLogin/onResponse: Login complete!");

                    // MainActivity 이동
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    // response.body().getCode() == 204
                    Log.d(TAG, "startLogin/onResponse: " + response.body().getMessage());
                }

                Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                showProgress(false);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "로그인 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "startLogin/onFailure: " + t.getMessage());
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