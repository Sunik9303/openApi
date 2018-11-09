package com.sunik.myapp;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;

public class TouchEventActivity extends NMapActivity {

    //-member variable----------------------------------------
    private final String CLIENT_ID = "0xTCTiVL2YPK8JUdGQoR";

    //-member method------------------------------------------

    private TextView textView; // 지도 정보
    private NMapView nMapView; //지도 보여주는 view
    private NMapController nMapController; //지도 컨트롤러 지도에 대한 다양한 설정하는 객체

    private NMapView.OnMapViewTouchEventListener mTouchEventListener; //터치 이벤트 감지 리스너

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        nMapView = (NMapView)findViewById(R.id.naverMap);

        nMapView.setClientId(CLIENT_ID);
        nMapView.setClickable(true);
        nMapView.setEnabled(true);
        nMapView.setFocusable(true);
        nMapView.setFocusableInTouchMode(true);
        nMapView.requestFocus();

        //지도에 대한 정보, 설정 및 읽기 객체 가져오기
        nMapController = nMapView.getMapController();

        //지도에 대한 터치 이벤트 리스너 등록
        setListener();
        nMapView.setOnMapViewTouchEventListener(mTouchEventListener);

    }

    //touch event에 대한 메소드들을 정의(필요한 것만 사용하면 됨)
    private void setListener(){

        mTouchEventListener = new NMapView.OnMapViewTouchEventListener() {
            @Override
            public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {

            }

            @Override
            public void onLongPressCanceled(NMapView nMapView) {

            }

            @Override
            public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {

            }

            @Override
            public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {
                String msg = "[Current Map Info]";
                msg += "\nCENTER : " + nMapController.getMapCenter().getLatitude() + ","
                        +nMapController.getMapCenter().getLongitude();
                msg += "\nTYPE : " + getMapModeName(nMapController.getMapViewMode());

                msg += "\nZOOMLevel : " + nMapController.getZoomLevel();

                changeMapMode();
                textView.setText(msg);
            }

            @Override
            public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {

            }

            @Override
            public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {

            }
        };
    }


    private String getMapModeName(int type){

        switch(type){
            case NMapView.VIEW_MODE_VECTOR: return "VECTOR";
            case NMapView.VIEW_MODE_SATELLITE: return "SATELLITE";
            case NMapView.VIEW_MODE_HYBRID: return "HYBRID";
            case NMapView.VIEW_MODE_TRAFFIC: return "TRAFFIC";
            case NMapView.VIEW_MODE_PANORAMA: return "PANORAMA";
            case NMapView.VIEW_MODE_BICYCLE: return "BICYCLE";
            default: return  "UNKNOWN";

        }
    }

    private void changeMapMode(){

        int currentMode = nMapController.getMapViewMode();

        currentMode =(currentMode != NMapView.VIEW_MODE_BICYCLE) ? currentMode+1 : NMapView.VIEW_MODE_VECTOR;

        nMapController.setMapViewMode(currentMode);

    }





}
