package com.sunik.myapp;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;


public class ControllerMapActivity extends NMapActivity {
    //--------------------------------------------------------------------
    //- Member Variable
    private final String CLIENT_ID = "0xTCTiVL2YPK8JUdGQoR";
    //--------------------------------------------------------------------
    //- Debug 변수 -------------------------------------------------------
    private final String                            TAG = "ControllerMapActivity";

    //- 상수 --------------------------------------------------------------
    private static boolean                          XML_LAYOUT      = false;

    //- Widget 객체 변수---------------------------------------------------
    private NMapView                                mMapView;
    private NMapController                          mMapController;

    //private NMapView.OnMapStateChangeListener       mStateListener;
    private NMapView.OnMapViewTouchEventListener    mTouchEventListener;


    //--------------------------------------------------------------------
    //- Member Method : Activity's Override Method
    //--------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        //- NMapView 설정
        mMapView.setClientId(CLIENT_ID);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        //- 내장 컨드롤러 설정
        setController();

        mMapController = mMapView.getMapController();
        setListener();
        mMapView.setOnMapViewTouchEventListener(mTouchEventListener);

    }

    //--------------------------------------------------------------------
    //- Member Method : Custom Method
    //--------------------------------------------------------------------
    private void setListener() {

        /* MapView State Change Listener
        mStateListener = new NMapView.OnMapStateChangeListener() {

            @Override
            public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

                if (errorInfo == null) { // success
                    // restore map view state such as map center position and zoom level.
                } else { // fail
                    Log.e(TAG, "onFailedToInitializeWithError: " + errorInfo.toString());
                    Toast.makeText(ControllerMapActivity.this, errorInfo.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onAnimationStateChange(NMapView mapView, int animType, int animState) {
                Log.i(TAG, "onAnimationStateChange: animType=" + animType + ", animState=" + animState);
            }

            @Override
            public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
                Log.i(TAG, "onMapCenterChange: center=" + center.toString());
            }

            @Override
            public void onZoomLevelChange(NMapView mapView, int level) {
                Log.i(TAG, "onZoomLevelChange: level=" + level);
            }

            @Override
            public void onMapCenterChangeFine(NMapView mapView) {
            }
        };
        */

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
                Log.i(TAG, "onTouchUp : " + ev.getAction());
                String msg = "[ Current Map Info]";
                msg += "\nCENTER : " + mMapController.getMapCenter().getLatitude() + ", " + mMapController.getMapCenter().getLongitude();
                msg += "\nTYPE : " + mMapController.getMapViewMode();
                msg += "\nZOOMLevel : " + mMapController.getZoomLevel();

                Toast.makeText(ControllerMapActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void setController(){
        //- Builtin-ZoomController 설정
        NMapView.LayoutParams lp = new NMapView.LayoutParams(LayoutParams.WRAP_CONTENT,
                                                             LayoutParams.WRAP_CONTENT,
                                                             NMapView.LayoutParams.BOTTOM_RIGHT);
        mMapView.setBuiltInZoomControls(true, lp);
        mMapView.displayZoomControls(true);
    }
}