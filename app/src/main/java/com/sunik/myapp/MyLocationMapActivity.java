package com.sunik.myapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;


public class MyLocationMapActivity extends NMapActivity {
    //----------------------------------------------------------------------------------------------
    //- Member Variable
    private final String CLIENT_ID = "0xTCTiVL2YPK8JUdGQoR";
    //----------------------------------------------------------------------------------------------
    //- Debug 변수 ----------------------------------------------------------------------------------
    private final String                                    TAG = "MyLocationMapActivity";

    //- 상수 ----------------------------------------------------------------------------------------
    //private static final NGeoPoint  NMAP_LOCATION_DEFAULT = new NGeoPoint(126.978371, 37.5666091);
    //private static final int        NMAP_ZOOMLEVEL_DEFAULT  = 11;

    //- UI Widget 객체 변수 --------------------------------------------------------------------------
    private NMapView                                        mMapView;

    private NMapController                                  mMapController;

    //- 오버레이 관련 --------------------------------------------------------------------------------
    private NMapOverlayManager                              mOverlayManager;
    private NMapOverlayManager.OnCalloutOverlayListener     mCalloutOverlayListener;
    private NMapOverlayManager.OnCalloutOverlayViewListener mCalloutOverlayViewListener;
    private NMapViewResourceProvider                        mMapViewerResourceProvider;
    private OnDataProviderListener                          mDataProviderLST;

    //-  현재 위치 오버레이
    private MapContainerView                                mMapContainerView;
    private NMapMyLocationOverlay                           mMyLocationOverlay;
    private NMapLocationManager                             mMapLocationManager;
    private NMapCompassManager                              mMapCompassManager;
    private NMapView.OnMapViewDelegate                      mMapViewTouchDelegate;

    private NMapLocationManager.OnLocationChangeListener    mMyLocationChangeListener;


    //--------------------------------------------------------------------------------------------------------
    //- Member Method :  Activity's Override Method
    //--------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //- NMapView 생성
        mMapView = new NMapView(this);
        mMapContainerView = new MapContainerView(this);
        mMapContainerView.addView(mMapView);

        //- Activity에 NMapView 설정
        setContentView(mMapContainerView);

        //- NMapView 설정
        mMapView.setClientId(CLIENT_ID);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        //- 지도 정보 설정 및 읽기 객체 가져오기
        mMapController = mMapView.getMapController();

        //- 리소스 프로바이더 생성 및 리스너 설정
        mMapViewerResourceProvider = new NMapViewResourceProvider(this);
        setListener();
        super.setMapDataProviderListener(mDataProviderLST);

        //- Overlay관련 설정
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
        mOverlayManager.setOnCalloutOverlayListener(mCalloutOverlayListener);
        mOverlayManager.setOnCalloutOverlayViewListener(mCalloutOverlayViewListener);

        //- 현재 위치 관련 설정
        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.setOnLocationChangeListener(mMyLocationChangeListener);
        mMapCompassManager = new NMapCompassManager(this);
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

        startMyLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopMyLocation();
    }

    //--------------------------------------------------------------------------------------------------------
    //- Member Method :  Custom
    //--------------------------------------------------------------------------------------------------------
     private void setListener(){
            mMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
                @Override
                public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
                    //- 현재 위치가 변경 이벤트 처리
                    if (mMapController != null) mMapController.animateTo(myLocation);
                    return true;
                }

                @Override
                public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
                    Toast.makeText(MyLocationMapActivity.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
                    //- 위치 탐색 불가능 지역 처리
                    Toast.makeText(MyLocationMapActivity.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();
                    stopMyLocation();
                }
            };
            mMapViewTouchDelegate = new NMapView.OnMapViewDelegate() {
                @Override
                public boolean isLocationTracking() {
                    if (mMapLocationManager != null) {
                        if (mMapLocationManager.isMyLocationEnabled()) {
                            return mMapLocationManager.isMyLocationFixed();
                        }
                    }
                    return false;
                }
            };
/*
            mDataProviderLST = new OnDataProviderListener() {
                @Override
                public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {

                    Log.i(TAG, "onReverseGeocoderResponse: placeMark=" + ((placeMark != null) ? placeMark.toString() : null));

                    if (errInfo != null) {
                        Log.e(TAG, "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());
                        Toast.makeText(MyLocationMapActivity.this, errInfo.toString(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (mFloatingPOIitem != null && mFloatingPOIdataOverlay != null) {
                        mFloatingPOIdataOverlay.deselectFocusedPOIitem();

                        if (placeMark != null) mFloatingPOIitem.setTitle(placeMark.toString());

                        mFloatingPOIdataOverlay.selectPOIitemBy(mFloatingPOIitem.getId(), false);
                    }
                }
            };
*/

            mCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener() {
                @Override
                public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {

                    if (itemOverlay instanceof NMapPOIdataOverlay) {
                        NMapPOIdataOverlay poiDataOverlay = (NMapPOIdataOverlay) itemOverlay;

                        if (!poiDataOverlay.isFocusedBySelectItem()) {
                            int countOfOverlappedItems = 1;

                            NMapPOIdata poiData = poiDataOverlay.getPOIdata();
                            for (int i = 0; i < poiData.count(); i++) {
                                NMapPOIitem poiItem = poiData.getPOIitem(i);

                                if (poiItem == overlayItem) continue;

                                if (Rect.intersects(poiItem.getBoundsInScreen(), overlayItem.getBoundsInScreen())) {
                                    countOfOverlappedItems++;
                                }
                            }

                            if (countOfOverlappedItems > 1) {
                                String text = countOfOverlappedItems + " overlapped items for " + overlayItem.getTitle();
                                Toast.makeText(MyLocationMapActivity.this, text, Toast.LENGTH_LONG).show();
                                return null;
                            }
                        }
                    }

                    // use custom old callout overlay
                    if (overlayItem instanceof NMapPOIitem) {
                        NMapPOIitem poiItem = (NMapPOIitem) overlayItem;

                        if (poiItem.showRightButton()) {
                            return new NMapCalloutCustomOldOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);
                        }
                    }
                    return new NMapCalloutCustomOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);
                }
            };

            mCalloutOverlayViewListener = new NMapOverlayManager.OnCalloutOverlayViewListener() {
                @Override
                public View onCreateCalloutOverlayView(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {

                    if (overlayItem != null) {
                        // [TEST] 말풍선 오버레이를 뷰로 설정함
                        String title = overlayItem.getTitle();
                        if (title != null && title.length() > 5) {
                            return new NMapCalloutCustomOverlayView(MyLocationMapActivity.this, itemOverlay, overlayItem, itemBounds);
                        }
                    }
                    // null을 반환하면 말풍선 오버레이를 표시하지 않음
                    return null;
                }
            };
        }

    //- GPS 및 네트워크 활용 현재 위치 지정 -------------------------------------------------------------
    private void startMyLocation() {

        if (mMyLocationOverlay != null) {
            if (!mOverlayManager.hasOverlay(mMyLocationOverlay))  mOverlayManager.addOverlay(mMyLocationOverlay);

            if (mMapLocationManager.isMyLocationEnabled()) {

                if (!mMapView.isAutoRotateEnabled()) {
                    mMyLocationOverlay.setCompassHeadingVisible(true);
                    mMapCompassManager.enableCompass();
                    mMapView.setAutoRotateEnabled(true, false);
                } else {
                    stopMyLocation();
                }

                mMapView.postInvalidate();
            } else {
                boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
                if (!isMyLocationEnabled) {
                    Toast.makeText(MyLocationMapActivity.this, "Please enable a My Location source in system settings", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    return;
                }
            }
        }
    }

    private void stopMyLocation() {
        if (mMyLocationOverlay != null) {
            mMapLocationManager.disableMyLocation();
            if (mMapView.isAutoRotateEnabled()) {
                mMyLocationOverlay.setCompassHeadingVisible(false);
                mMapCompassManager.disableCompass();
                mMapView.setAutoRotateEnabled(false, false);
            }
        }
    }

    //--------------------------------------------------------------------------------------------------------
    //- INNER CLASS
    //--------------------------------------------------------------------------------------------------------
    private class MapContainerView extends ViewGroup {

        public MapContainerView(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            final int width = getWidth();
            final int height = getHeight();
            final int count = getChildCount();

            for (int i = 0; i < count; i++)
            {
                final View view = getChildAt(i);
                final int childWidth = view.getMeasuredWidth();
                final int childHeight = view.getMeasuredHeight();
                final int childLeft = (width - childWidth) / 2;
                final int childTop = (height - childHeight) / 2;
                view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            }

            if (changed)
                mOverlayManager.onSizeChanged(width, height);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            int sizeSpecWidth = widthMeasureSpec;
            int sizeSpecHeight = heightMeasureSpec;

            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View view = getChildAt(i);

                if (view instanceof NMapView) {
                    if (mMapView.isAutoRotateEnabled()) {
                        int diag = (((int)(Math.sqrt(w * w + h * h)) + 1) / 2 * 2);
                        sizeSpecWidth = MeasureSpec.makeMeasureSpec(diag, MeasureSpec.EXACTLY);
                        sizeSpecHeight = sizeSpecWidth;
                    }
                }
                view.measure(sizeSpecWidth, sizeSpecHeight);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }  //--- Inner Class

}