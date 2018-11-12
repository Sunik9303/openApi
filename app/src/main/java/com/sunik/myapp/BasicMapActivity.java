package com.sunik.myapp;

import android.os.Bundle;
import android.widget.TextView;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapView;


public class BasicMapActivity extends NMapActivity {

    //- Widget 객체 변수---------------------------------------------------
    private NMapView              mMapView;         // 지도 화면 View

    private final String CLIENT_ID = "0xTCTiVL2YPK8JUdGQoR";

    //- Method ------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMapView = new NMapView(this);
        setContentView(new TextView(this));

        setContentView(R.layout.basic_map);
        mMapView = findViewById(R.id.map_view);

        //- NMapView 설정
        mMapView.setClientId(CLIENT_ID);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();
    }
}

        /*
        if(!XML_LAYOUT)
        {
            Log.d(TAG, "onCreate() OK  - new NMapView ");
            mMapView = new NMapView(this);
            setContentView(mMapView);
        }else {
            Log.d(TAG, "onCreate() OK  - Layout ");
            setContentView(R.layout.basic_map);
            mMapView = findViewById(R.id.map_view);
        }
        */