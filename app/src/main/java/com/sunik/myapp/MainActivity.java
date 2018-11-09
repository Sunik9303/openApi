package com.sunik.myapp;

import android.os.Bundle;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapView;

public class MainActivity extends NMapActivity {

    //-member variable----------------------------------------
    private final String CLIENT_ID = "0xTCTiVL2YPK8JUdGQoR";

    //-member method------------------------------------------

    private NMapView nMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nMapView = (NMapView)findViewById(R.id.naverMap);

        nMapView.setClientId(CLIENT_ID);
        nMapView.setClickable(true);
        nMapView.setEnabled(true);
        nMapView.setFocusable(true);
        nMapView.setFocusableInTouchMode(true);
        nMapView.requestFocus();


    }
}
