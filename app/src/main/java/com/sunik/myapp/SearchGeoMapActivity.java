package com.sunik.myapp;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;


public class SearchGeoMapActivity extends NMapActivity {
    //----------------------------------------------------------------------------------------------
    //- Member Variable
    private final String CLIENT_ID = "0xTCTiVL2YPK8JUdGQoR";
    public static final  NGeoPoint  NMAP_LOCATION_DEFAULT   = new NGeoPoint(126.978371, 37.5666091);
    public static final int         NMAP_ZOOMLEVEL_DEFAULT  = 6;
    //----------------------------------------------------------------------------------------------
    //- Debug 변수 ----------------------------------------------------------------------------------
    private final String                                        TAG = "SearchGeoMapActivity";

    //- Widget 객체 변수 -----------------------------------------------------------------------------
    private NMapView                                            mMapView;
    private EditText                                            mLatETXT, mLngETXT;

    private NMapController                                      mMapController;
    //- 오버레이 관련 --------------------------------------------------------------------------------
    private NMapOverlayManager                                  mOverlayManager;
    private NMapOverlayManager.OnCalloutOverlayListener         mCalloutOverlayListener;
    private NMapOverlayManager.OnCalloutOverlayViewListener     mCalloutOverlayViewListener;
    private NMapViewResourceProvider                            mMapViewerResourceProvider;
    private OnDataProviderListener                              mDataProviderLST;

    private NMapPOIdataOverlay.OnStateChangeListener            mPOIdataStateChangeListener;
    private NMapPOIdataOverlay                                  mFloatingPOIdataOverlay;
    private NMapPOIdataOverlay.OnFloatingItemChangeListener     mPOIdataFloatingItemChangeListener;
    private NMapPOIitem                                         mFloatingPOIitem;


    //--------------------------------------------------------------------------------------------------------
    //- Member Method :  Activity's Override Method
    //--------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //- Activity 화면 지정하기
        setContentView(R.layout.searchaddr);

        //- Widget 객체 가져오기
        mLatETXT = findViewById(R.id.latETXT);
        mLngETXT =  findViewById(R.id.lngETXT);

        mMapView = findViewById(R.id.map_view);
        findViewById(R.id.infoTXT).setVisibility(View.GONE);

        //- NMapView 설정
        mMapView.setClientId(CLIENT_ID);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();
        //- 지도 정보 설정 및 읽기 객체 가져오기
        mMapController = mMapView.getMapController();

        //- 오버레이 관리를 위한 리소스 프로바이더 생성
        mMapViewerResourceProvider = new NMapViewResourceProvider(this);
        setListener();
        super.setMapDataProviderListener(mDataProviderLST);

        //- Overlay관련 설정
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
        mOverlayManager.setOnCalloutOverlayListener(mCalloutOverlayListener);
        mOverlayManager.setOnCalloutOverlayViewListener(mCalloutOverlayViewListener);

        overlaytFloatingPOIdata();
    }


    private void overlaytFloatingPOIdata() {
        // Markers for POI item
        int marker = NMapPOIflagType.SPOT;

        // set POI data
        NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
        poiData.beginPOIdata(1);
        NMapPOIitem item = poiData.addPOIitem(null, "HEAR", marker, 0);
        if (item != null) {
            item.setPoint(mMapController.getMapCenter());
            item.setFloatingMode(NMapPOIitem.FLOATING_FIXED);
            item.setRightButton(true);
            mFloatingPOIitem = item;
        }
        poiData.endPOIdata();

        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
        if (poiDataOverlay != null) {
            poiDataOverlay.setOnFloatingItemChangeListener(mPOIdataFloatingItemChangeListener);
            poiDataOverlay.setOnStateChangeListener(mPOIdataStateChangeListener);
            poiDataOverlay.selectPOIitem(0, false);
            mFloatingPOIdataOverlay = poiDataOverlay;
        }
    }

    //--------------------------------------------------------------------------------------------------------
    //- Member Method :  User Defined Method
    //--------------------------------------------------------------------------------------------------------
    private void setListener() {

        //- PIO의 위치 변경 감지 및 처리 리스너
        mDataProviderLST = new OnDataProviderListener() {
            @Override
            public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {

                Log.i(TAG, "onReverseGeocoderResponse: placeMark=" + ((placeMark != null) ? placeMark.toString() : null));

                if (errInfo != null) {
                    Log.e(TAG, "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());
                    Toast.makeText(SearchGeoMapActivity.this, errInfo.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (mFloatingPOIitem != null && mFloatingPOIdataOverlay != null) {
                    mFloatingPOIdataOverlay.deselectFocusedPOIitem();

                    if(placeMark != null) mFloatingPOIitem.setTitle(placeMark.toString());

                    mFloatingPOIdataOverlay.selectPOIitemBy(mFloatingPOIitem.getId(), false);
                }
            }
        };


        //- 말풍선 관련 리스너
        mCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener() {
            @Override
            public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {

                if (itemOverlay instanceof NMapPOIdataOverlay) {
                    Log.i(TAG, "mCalloutOverlayListener()");
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
                            Toast.makeText(SearchGeoMapActivity.this, text, Toast.LENGTH_LONG).show();
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

                //set basic callout overlay
                return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds);
            }
        };

        mCalloutOverlayViewListener = new NMapOverlayManager.OnCalloutOverlayViewListener() {
            @Override
            public View onCreateCalloutOverlayView(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {

                if (overlayItem != null) {
                    Log.i(TAG, "mCalloutOverlayViewListener()");
                    // [TEST] 말풍선 오버레이를 뷰로 설정함
                    String title = overlayItem.getTitle();
                    if (title != null && title.length() > 5) {
                        return new NMapCalloutCustomOverlayView(SearchGeoMapActivity.this, itemOverlay, overlayItem, itemBounds);
                    }
                }
                // null을 반환하면 말풍선 오버레이를 표시하지 않음
                return null;
            }
        };

        //- 상태가 변경된 경우 감지 후 처리
        mPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {
            @Override
            public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {

                Log.i(TAG, "mPOIdataStateChangeListener() - onCalloutClick: title=" + item.getTitle());
                Toast.makeText(SearchGeoMapActivity.this, "onCalloutClick: " + item.getTitle(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {

                if (item != null) {
                    Log.i(TAG, "onFocusChanged: " + item.toString());
                    mMapController.setMapCenter(item.getPoint());
                } else {
                    Log.i(TAG, "onFocusChanged: ");
                }
            }
        };

        //- 좌표가 변경된 경우 감지 후 처리
        mPOIdataFloatingItemChangeListener = new NMapPOIdataOverlay.OnFloatingItemChangeListener() {
            @Override
            public void onPointChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {
                NGeoPoint point = nMapPOIitem.getPoint();

                Log.i(TAG, "mPOIdataFloatingItemChangeListener() - onPointChanged: point=" + point.toString());
            }
        };
    }

    //--------------------------------------------------------------------------------------------------------
    //- Member Method :  XML onclick attribute Method
    //--------------------------------------------------------------------------------------------------------
    public void clickFunc(View v) {

        switch (v.getId())
        {
            case R.id.searchBTN:
                 Log.i(TAG, "searchBTN()");

                 mOverlayManager.clearOverlays();
                 if(mLatETXT.getText().length() > 0 && mLngETXT.getText().length()>0)
                 {
                     NGeoPoint point = new NGeoPoint(new Double(mLngETXT.getText().toString()).doubleValue(),
                             new Double(mLatETXT.getText().toString()).doubleValue());

                     point.set( new Double(mLngETXT.getText().toString()).doubleValue(), new Double(mLatETXT.getText().toString()).doubleValue());

                     //findPlacemarkAtLocation(point.latitude, point.latitude);
                     findPlacemarkAtLocation(NMAP_LOCATION_DEFAULT.longitude, NMAP_LOCATION_DEFAULT.latitude);

                     Toast.makeText(this,"clickFunc || lat : " + point.getLatitude() + ", lng : " + point.getLongitude(), Toast.LENGTH_LONG).show();

                 }else{
                        Toast.makeText(this, "위도 및 경도 값을 모두 입력해야 합니다.", Toast.LENGTH_SHORT).show();
                 }
                 break;
        }
    }
}
