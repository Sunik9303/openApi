package com.sunik.myapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class introActivity extends Activity {

    private final static  String TAG = "Intro Activity";

    //-gps 및 위치 관련 권한 설정용 전역 번수

    public static final int REQUEST_PERMISSIOM_FINE =1000;
    private static int mPermissionCheck = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,REQUEST_PERMISSIOM_FINE);
        initUI();
    }

    // 멤버 메소드 (사용자 정의 메소드)
    private void requestPermission(String szPermission, int reqCode){

        mPermissionCheck = ContextCompat.checkSelfPermission(this,szPermission);
        if(mPermissionCheck!=PackageManager.PERMISSION_GRANTED);
        {
            //-사용자 재요청
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,szPermission))
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},reqCode);
            else
                //-최초 실행 요청
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},reqCode);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case REQUEST_PERMISSIOM_FINE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.i(TAG,"접근 권한 허용");
                }else
                {
                    Toast.makeText(this,R.string.permission_denied,Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    private void initUI(){
       //-ListView 설정
        final String[] mItems = getResources().getStringArray(R.array.items);
        ArrayAdapter listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,mItems);

        final ListView listView = (ListView)findViewById(R.id.mapList);
        listView.setAdapter(listAdapter);

        //-ListView 항목 선택 시 기능 넣기
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(introActivity.this,""+mItems[position],Toast.LENGTH_LONG).show();

                switch (position)
                {
                    case 0:
                        startActivity(new Intent(introActivity.this , BasicMapActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(introActivity.this , ControllerMapActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(introActivity.this , EventMapActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(introActivity.this , OverlayMapActivity.class));
                        break;
                }

            }
        });
    }

}
