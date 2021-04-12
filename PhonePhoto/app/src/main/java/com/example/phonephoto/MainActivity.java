package com.example.phonephoto;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.phonephoto.phone.PhoneFragment;
import com.example.phonephoto.photo.PhotoFragment;
import com.example.phonephoto.photo.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    String TAG = "PJ2 MainActivity";

    /*** 권한 허용 ***/
    int MY_REQUEST_PERMISSIONS = 1234;                      // 내 임의로 정한 값
    public static final String[] PERMISSIONS = {            // 허용 필요한 권한 list
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    int NUMBER_OF_PERMISSION = PERMISSIONS.length;          // 권한 개수

    /*** 레이아웃 ***/
//    ViewPager viewPager;
//    PhoneFragment phoneFragment;
//    PhotoFragment photoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        /** 접근 권한 허용 요청하기 **/
        if(arePermissionsDenied())
            ActivityCompat.requestPermissions(this, PERMISSIONS, MY_REQUEST_PERMISSIONS);

        /** tab 설정 **/
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        PhoneFragment phoneFragment = new PhoneFragment();
        PhotoFragment photoFragment = new PhotoFragment();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(phoneFragment, getString(R.string.tab1));
        viewPagerAdapter.addFragment(photoFragment, getString(R.string.tab2));
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);    // 클릭할 때 바뀌도록.
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
     * 권한 거절했다면 finish()   실행
     * 모두 허용했다면 onResume() 실행
     * @param requestCode ?
     * @param permissions ?
     * @param grantResults ?
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult");

        if(requestCode == MY_REQUEST_PERMISSIONS && grantResults.length > 0) {
            if(arePermissionsDenied()) finish();
            onResume();
        }
    }
}