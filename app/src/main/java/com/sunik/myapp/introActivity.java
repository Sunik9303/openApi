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


public class IntroActivity extends Activity{
    //----------------------------------------------------------------------------------------------------------------
    //- Member Variable
    //----------------------------------------------------------------------------------------------------------------
    private final static String TAG = "IntroActivity";

    public  static final int    REQUEST_PEERMISSION_FINE 		= 1000;
    private static int          mPermissionCheck 				= -1;

    //- UI Widget 변수
    private ListView            mMapList, mLifeList;
    private ArrayAdapter        mMapAdapter, mLifeAdapter;

    //----------------------------------------------------------------------------------------------------------------
    //- Member Method : Activity's Override Method
    //----------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intro);

        initUI();

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PEERMISSION_FINE);
    }


    //- 권한 관련 요청 처리 결과 ------------------------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_PEERMISSION_FINE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.i(TAG, "접근 권한 허용");
                }else{
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    //- GPS 및 위치 검색을 위한 권한 검사 및 요청 ---------------------------------------------------------------------
    private void requestPermission(String szPermission, int reqCode){

        //- 위치 관련 접근 권한 사용 가능 여부 체크
        mPermissionCheck = ContextCompat.checkSelfPermission(this, szPermission);

        if(mPermissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            //- 사용자의 재요청
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, szPermission))
                ActivityCompat.requestPermissions(this,  new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, reqCode);
            else
                //- 최초 실행 요청
                ActivityCompat.requestPermissions(this,  new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, reqCode);
        }
    }

    //----------------------------------------------------------------------------------------------------------------
    //- Member Method : XML onClick Method
    //----------------------------------------------------------------------------------------------------------------
    public void clickFunc(View v){
        switch(v.getId())
        {
            case R.id.mapITEM:  mLifeList.setVisibility(View.GONE); mMapList.setVisibility(View.VISIBLE); break;

            case R.id.lifeITEM: mMapList.setVisibility(View.GONE); mLifeList.setVisibility(View.VISIBLE); break;
        }
    }

    //----------------------------------------------------------------------------------------------------------------
    //- Member Method : User Defined Method
    //----------------------------------------------------------------------------------------------------------------
    private void initUI(){

        mMapList = findViewById(R.id.mapList);
        mLifeList = findViewById(R.id.lifeList);

        //- ListView 설정
        makeListAdapter(R.array.items, mMapList, mMapAdapter);
        makeListAdapter(R.array.life_items, mLifeList, mLifeAdapter);
        /*
        final String[] mItems = getResources().getStringArray(R.array.items);

        ArrayAdapter listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mItems);
        ListView mapLST = findViewById(R.id.mapList);
        mapLST.setAdapter(listAdapter);*/

        //- ListView 항목 선택 시 기능 넣기
        mMapList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position)
                {
                    case 0: startActivity(new Intent(IntroActivity.this, BasicMapActivity.class));          break;
                    case 1: startActivity(new Intent(IntroActivity.this, ControllerMapActivity.class));     break;
                    case 2: startActivity(new Intent(IntroActivity.this, EventMapActivity.class));          break;
                    case 3: startActivity(new Intent(IntroActivity.this, OverlayMapActivity.class));        break;
                    case 4: startActivity(new Intent(IntroActivity.this, MyLocationMapActivity.class));        break;
                    case 5: startActivity(new Intent(IntroActivity.this, SearchGeoMapActivity.class));        break;
                }
            }
        });
        mLifeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position)
                {
                    case 0: startActivity(new Intent(IntroActivity.this, WeatherInfoActivity.class));       break;
                }
            }
        });
    }


    private void makeListAdapter(int arrId, ListView listView, ArrayAdapter<String> adapter){
        final String[] mItems = getResources().getStringArray(arrId);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mItems);
        listView.setAdapter(adapter);
    }
}