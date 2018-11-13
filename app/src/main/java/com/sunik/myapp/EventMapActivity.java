package com.sunik.myapp;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapCircleData;
import com.nhn.android.maps.overlay.NMapCircleStyle;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;


public class EventMapActivity extends NMapActivity {
    //--------------------------------------------------------------------
    //- Member Variable
    private final String CLIENT_ID = "0xTCTiVL2YPK8JUdGQoR";
    //--------------------------------------------------------------------
    //- Debug 변수 -------------------------------------------------------
    private final String                            TAG             = "EventMapActivity";

    //- Widget 객체 변수---------------------------------------------------
    private TextView                                mInfoETXT;
    private NMapView                                mMapView;                     //- 지도 보여 주는 뷰
    private NMapController                          mMapController;             //- 지도에 대한 다양한 설정 및 읽어오는 객체

    private NMapView.OnMapViewTouchEventListener    mTouchEventListener;       //- 지도에 발생하는 터치 이벤트 감지 리스너

    //- 오버레이 관련 --------------------------------------------------------------------------------
    private NMapOverlayManager mOverlayManager;
    private NMapOverlayManager.OnCalloutOverlayListener     mCalloutOverlayListener;
    private NMapOverlayManager.OnCalloutOverlayViewListener mCalloutOverlayViewListener;
    private NMapViewResourceProvider                        mMapViewerResourceProvider;
    private OnDataProviderListener                          mDataProviderLST;

    private NMapPOIdataOverlay.OnStateChangeListener        mPOIdataStateChangeListener;
    private NMapPOIdataOverlay                              mFloatingPOIdataOverlay;
    private NMapPOIdataOverlay.OnFloatingItemChangeListener mPOIdataFloatingItemChangeListener;
    private NMapPOIitem mFloatingPOIitem;


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
        mMapController.setZoomLevel(2);
    }

    //--------------------------------------------------------------------
    //- Member Method : Custom Method
    //--------------------------------------------------------------------

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
                //mMapController.setMapCenter(ev.getX(), ev.getY());
                //mOverlayManager.clearOverlays();

                Toast.makeText(EventMapActivity.this,""+mapView.getMapProjection().fromPixels((int)ev.getX(),(int)ev.getY()).getLongitude()+", "
                        +mapView.getMapProjection().fromPixels((int)ev.getX(),(int)ev.getY()).getLatitude()+"",Toast.LENGTH_LONG).show();
                //overlayPOIData(mapView.getMapProjection().fromPixels((int)ev.getX(),(int)ev.getY()));
            }
        };

        //- PIO의 위치 변경 감지 및 처리 리스너
        mDataProviderLST = new OnDataProviderListener() {
            @Override
            public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {

                Log.i(TAG, "onReverseGeocoderResponse: placeMark=" + ((placeMark != null) ? placeMark.toString() : null));

                if (errInfo != null) {
                    Log.e(TAG, "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());
                    Toast.makeText(EventMapActivity.this, errInfo.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (mFloatingPOIitem != null && mFloatingPOIdataOverlay != null) {
                    mFloatingPOIdataOverlay.deselectFocusedPOIitem();

                    if (placeMark != null) mFloatingPOIitem.setTitle(placeMark.toString());

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

                    if (!poiDataOverlay.isFocusedBySelectItem()){
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
                            Toast.makeText(EventMapActivity.this, text, Toast.LENGTH_LONG).show();
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

                // use custom callout overlay
                //return new NMapCalloutCustomOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);

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
                        return new NMapCalloutCustomOverlayView(EventMapActivity.this, itemOverlay, overlayItem, itemBounds);
                    }
                }
                // null을 반환하면 말풍선 오버레이를 표시하지 않음
                return null;
            }
        };

        mPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {
            @Override
            public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {

                Log.i(TAG, "mPOIdataStateChangeListener() - onCalloutClick: title=" + item.getTitle());

                // [[TEMP]] handle a click event of the callout
                Toast.makeText(EventMapActivity.this, "onCalloutClick: " + item.getTitle(), Toast.LENGTH_LONG).show();
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

        mPOIdataFloatingItemChangeListener = new NMapPOIdataOverlay.OnFloatingItemChangeListener() {
            @Override
            public void onPointChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {
                NGeoPoint point = nMapPOIitem.getPoint();

                Log.i(TAG, "mPOIdataFloatingItemChangeListener() - onPointChanged: point=" + point.toString());

                findPlacemarkAtLocation(point.longitude, point.latitude);
                nMapPOIitem.setTitle(null);
            }
        };
    }

    //--------------------------------------------------------------------------------------------------------
    //- Member Method :  XML onclick attribute Method
    //--------------------------------------------------------------------------------------------------------
    /*public void clickFunc(View v){
        switch(v.getId())
        {
            case    R.id.poiBTN:
                mOverlayManager.clearOverlays();
                overlayPOIData();
                break;

            case    R.id.pathBTN:
                mOverlayManager.clearOverlays();
                overlayPathData();
                break;

            case    R.id.piopathBTN:
                mOverlayManager.clearOverlays();
                overlayPathPOIdata();
                break;

            case    R.id.floatBTN:
                mOverlayManager.clearOverlays();
                overlaytFloatingPOIdata();
                break;
        }
    }*/

    //- PIO 객체 생성 ------------------------------------------------------------------------------------
    private void overlayPOIData(NGeoPoint ngeoPoint) {

        // Markers for POI item
        int markerId = NMapPOIflagType.PIN;

        //-  POI data 설정
        NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
        poiData.beginPOIdata(2);

        NMapPOIitem item = poiData.addPOIitem(ngeoPoint.getLatitude(), ngeoPoint.getLongitude(), "Pizza 777-111", markerId, 0);
        item.setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW);

        poiData.addPOIitem(127.061, 37.51, "Pizza 123-456", markerId, 0);
        poiData.endPOIdata();

        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

        //- PIO Item 변화 감지 및 처리
        poiDataOverlay.setOnStateChangeListener(mPOIdataStateChangeListener);

        // select an item
        poiDataOverlay.selectPOIitem(0, true);

        // show all POI data
        poiDataOverlay.showAllPOIdata(0);

    }

    //- PIO 객체 생성 ------------------------------------------------------------------------------------
    private void overlayPathData() {

        NMapPathData pathData = new NMapPathData(9);

        pathData.initPathData();
        pathData.addPathPoint(127.108099, 37.366034, NMapPathLineStyle.TYPE_SOLID);
        pathData.addPathPoint(127.108088, 37.366043, 0);
        pathData.addPathPoint(127.108079, 37.365619, 0);
        pathData.addPathPoint(127.107458, 37.365608, 0);
        pathData.addPathPoint(127.107232, 37.365608, 0);
        pathData.addPathPoint(127.106904, 37.365624, 0);
        pathData.addPathPoint(127.105933, 37.365621, NMapPathLineStyle.TYPE_DASH);
        pathData.addPathPoint(127.105929, 37.366378, 0);
        pathData.addPathPoint(127.106279, 37.366380, 0);
        pathData.endPathData();

        NMapPathDataOverlay pathDataOverlay = mOverlayManager.createPathDataOverlay(pathData);
        if (pathDataOverlay != null)
        {
            // add path data with polygon type
            NMapPathData pathData2 = new NMapPathData(4);
            pathData2.initPathData();
            pathData2.addPathPoint(127.106, 37.367, NMapPathLineStyle.TYPE_SOLID);
            pathData2.addPathPoint(127.107, 37.367, 0);
            pathData2.addPathPoint(127.107, 37.368, 0);
            pathData2.addPathPoint(127.106, 37.368, 0);
            pathData2.endPathData();
            pathDataOverlay.addPathData(pathData2);

            // set path line style
            NMapPathLineStyle pathLineStyle = new NMapPathLineStyle(mMapView.getContext());
            pathLineStyle.setPataDataType(NMapPathLineStyle.DATA_TYPE_POLYGON);
            pathLineStyle.setLineColor(0xA04DD2, 0xff);
            pathLineStyle.setFillColor(0xFFFFFF, 0x00);
            pathData2.setPathLineStyle(pathLineStyle);

            // add circle data
            NMapCircleData circleData = new NMapCircleData(1);
            circleData.initCircleData();
            circleData.addCirclePoint(127.1075, 37.3675, 50.0F);
            circleData.endCircleData();
            pathDataOverlay.addCircleData(circleData);
            // set circle style
            NMapCircleStyle circleStyle = new NMapCircleStyle(mMapView.getContext());
            circleStyle.setLineType(NMapPathLineStyle.TYPE_DASH);
            circleStyle.setFillColor(0x000000, 0x00);
            circleData.setCircleStyle(circleStyle);

            pathDataOverlay.showAllPathData(0);
        }
    }

    //- 경로 시작 & 종료 지점에 PIO 표시 ----------------------------------------------------------------------
    private void overlayPathPOIdata() {

        // set POI data
        NMapPOIdata poiData = new NMapPOIdata(4, mMapViewerResourceProvider, true);
        poiData.beginPOIdata(4);
        poiData.addPOIitem(349652983, 149297368, "Pizza 124-456", NMapPOIflagType.FROM, null);
        poiData.addPOIitem(349652966, 149296906, null, NMapPOIflagType.NUMBER_BASE + 1, null);
        poiData.addPOIitem(349651062, 149296913, null, NMapPOIflagType.NUMBER_BASE + 999, null);
        poiData.addPOIitem(349651376, 149297750, "Pizza 000-999", NMapPOIflagType.TO, null);
        poiData.endPOIdata();

        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

        // set event listener to the overlay
        poiDataOverlay.setOnStateChangeListener(mPOIdataStateChangeListener);

    }

    private void overlaytFloatingPOIdata() {
        // Markers for POI item
        int marker1 = NMapPOIflagType.PIN;

        // set POI data
        NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
        poiData.beginPOIdata(1);
        NMapPOIitem item = poiData.addPOIitem(null, "Touch & Drag to Move", marker1, 0);
        if (item != null) {
            // initialize location to the center of the map view.
            item.setPoint(mMapController.getMapCenter());
            // set floating mode
            item.setFloatingMode(NMapPOIitem.FLOATING_TOUCH | NMapPOIitem.FLOATING_DRAG);
            // show right button on callout
            item.setRightButton(true);

            mFloatingPOIitem = item;
        }
        poiData.endPOIdata();

        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
        if (poiDataOverlay != null) {
            poiDataOverlay.setOnFloatingItemChangeListener(mPOIdataFloatingItemChangeListener);

            // set event listener to the overlay
            poiDataOverlay.setOnStateChangeListener(mPOIdataStateChangeListener);

            poiDataOverlay.selectPOIitem(0, false);

            mFloatingPOIdataOverlay = poiDataOverlay;
        }
    }



}











