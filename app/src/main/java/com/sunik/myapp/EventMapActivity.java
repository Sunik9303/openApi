package com.sunik.myapp;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;


public class EventMapActivity extends NMapActivity {
    //--------------------------------------------------------------------
    //- Member Variable
    private final String CLIENT_ID = "0xTCTiVL2YPK8JUdGQoR";
    //--------------------------------------------------------------------
    //- Debug 변수 -------------------------------------------------------
    private final String  TAG   = "EventMapActivity";

    //- Widget 객체 변수---------------------------------------------------
    private TextView                                mInfoETXT;
    private NMapView                                mMapView;                     //- 지도 보여 주는 뷰
    private NMapController                          mMapController;             //- 지도에 대한 다양한 설정 및 읽어오는 객체

    private NMapView.OnMapViewTouchEventListener    mTouchEventListener;       //- 지도에 발생하는 터치 이벤트 감지 리스너

    //--------------------------------------------------------------------
    //- Member Method : Activity's Override Method
    //--------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.event_map);

        mMapView    = findViewById(R.id.map_view);
        mInfoETXT   = findViewById(R.id.infoTXT);

        //- NMapView 설정
        mMapView.setClientId(CLIENT_ID);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        //- 지도 정보 설정 및 읽기 객체 가져오기
        mMapController = mMapView.getMapController();

        //- 터치 이벤트 리스너 설정
        setListener();
        mMapView.setOnMapViewTouchEventListener(mTouchEventListener);
    }

    //--------------------------------------------------------------------
    //- Member Method : Custom Method
    //--------------------------------------------------------------------
    private void setListener() {

        mTouchEventListener = new NMapView.OnMapViewTouchEventListener() {
            @Override
            public void onLongPress(NMapView mapView, MotionEvent ev) {
            }

            @Override
            public void onLongPressCanceled(NMapView mapView) {
            }

            @Override
            public void onSingleTapUp(NMapView mapView, MotionEvent ev) {
            }

            @Override
            public void onTouchDown(NMapView mapView, MotionEvent ev) {
                Log.i(TAG, "TouchDown : " + ev.getAction());
            }

            @Override
            public void onScroll(NMapView mapView, MotionEvent e1, MotionEvent e2) {
            }

            @Override
            public void onTouchUp(NMapView mapView, MotionEvent ev) {

                String msg = "[ Current Map Info]";
                msg += "\nCENTER : " + mMapController.getMapCenter().getLatitude() + " , "
                                     + mMapController.getMapCenter().getLongitude();
                msg += "\nTYPE : " + getMapModeName(mMapController.getMapViewMode());
                msg += "\tZOOMLevel : " + mMapController.getZoomLevel();

                mInfoETXT.setText(msg);
                //changeMapMode();
                //NGeoPoint center = mMapController.getMapCenter();
                mMapController.setMapCenter(ev.getX(), ev.getY());
            }
        };

    }

    //- MapMode 이름 리턴 ---------------------------------------------------------------------
    private String getMapModeName(int type){

            switch(type)
            {
                case NMapView.VIEW_MODE_VECTOR:     return "VECTOR";
                case NMapView.VIEW_MODE_SATELLITE:  return "SATELLITE";
                case NMapView.VIEW_MODE_HYBRID:     return "HYBRID";
                case NMapView.VIEW_MODE_TRAFFIC:    return "TRAFFIC";
                case NMapView.VIEW_MODE_PANORAMA:   return "PANORAMA";
                case NMapView.VIEW_MODE_BICYCLE:    return "BICYCLE";
                default: return "UNKNOWN";
            }
    }

    //- MapMode 변경 ---------------------------------------------------------------------
    private void changeMapMode()
    {
        int currentMode = mMapController.getMapViewMode();

        currentMode = (currentMode != NMapView.VIEW_MODE_BICYCLE) ?  currentMode+1 : NMapView.VIEW_MODE_VECTOR;

        mMapController.setMapViewMode(currentMode);

    }

}











