package com.baidu.mapapi.overlayutil;

/**
 * Created by Administrator on 2016/7/28.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.clusterutil.clustering.view.DeviceItem;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.smart.cloud.fire.activity.Inspection.InspectionMap.InspectionMapPresenter;
import com.smart.cloud.fire.activity.NFCDev.NFCRecordBean;
import com.smart.cloud.fire.global.NFCInfoEntity;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Camera;
import com.smart.cloud.fire.mvp.fragment.MapFragment.MapFragmentPresenter;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Smoke;
import com.smart.cloud.fire.view.ShowInspDialog;
import com.smart.cloud.fire.view.ShowSmokeDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fire.cloud.smart.com.smartcloudfire.R;

public class MyOverlayManager extends OverlayManager {
    private Context mContext;
    private BaiduMap mBaiduMap;
    private static List<Smoke> mapNormalSmoke;
    private MapFragmentPresenter mMapFragmentPresenter;
    private List<BitmapDescriptor> viewList;

    private static List<NFCRecordBean> mapNormalNFC;//@@8.18
    private static List<NFCInfoEntity> mInspectionList;




    public  MyOverlayManager(){
    }

    public void init(Context context,BaiduMap baiduMap,List<Smoke> mapNormalSmoke, MapFragmentPresenter mMapFragmentPresenter,List<BitmapDescriptor> viewList){
        initBaiduMap(baiduMap);
        this.mapNormalSmoke = mapNormalSmoke;
        this.mMapFragmentPresenter = mMapFragmentPresenter;
        this.viewList = viewList;
        mContext=context;
        mBaiduMap=baiduMap;
    }

    public void initInspection(Context context, BaiduMap baiduMap, List<NFCInfoEntity> mapNormalSmoke, List<BitmapDescriptor> viewList){
        initBaiduMap(baiduMap);
        this.mInspectionList = mapNormalSmoke;
        this.viewList = viewList;
        mContext=context;
        mBaiduMap=baiduMap;
    }

    //@@8.18
    public void initNFC(BaiduMap baiduMap,List<NFCRecordBean> mapNormalSmoke, MapFragmentPresenter mMapFragmentPresenter,List<BitmapDescriptor> viewList){
        initBaiduMap(baiduMap);
        this.mapNormalNFC = mapNormalSmoke;
        this.mMapFragmentPresenter = mMapFragmentPresenter;
        this.viewList = viewList;
        this.mapNormalSmoke=null;//@@11.29
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Bundle bundle = marker.getExtraInfo();
        if(mMapFragmentPresenter!=null){
            mMapFragmentPresenter.getClickDev(bundle);
        }else{
            getClickDev(bundle);
        }
        return true;
    }

    public void getClickDev(Bundle bundle){
        Serializable object = bundle.getSerializable("mNormalSmoke");
        boolean result = object instanceof NFCInfoEntity;

        if (result) {
            NFCInfoEntity normalSmoke = (NFCInfoEntity) object;
            new ShowInspDialog((Activity) mContext,normalSmoke);
        }


    }

    @Override
    public boolean onPolylineClick(Polyline arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<OverlayOptions> getOverlayOptions() {
        // TODO Auto-generated method stub
        List<OverlayOptions> overlayOptionses = new ArrayList<>();
        if(mapNormalSmoke!=null&&mapNormalSmoke.size()>0){
            ArrayList<BitmapDescriptor> giflist = new ArrayList<>();
            giflist.add(viewList.get(0));
            giflist.add(viewList.get(1));
            ArrayList<BitmapDescriptor> giflistRQ = new ArrayList<>();
            giflistRQ.add(viewList.get(2));
            giflistRQ.add(viewList.get(1));
            ArrayList<BitmapDescriptor> giflist2 = new ArrayList<>();
            giflist2.add(viewList.get(3));
            giflist2.add(viewList.get(4));
            ArrayList<BitmapDescriptor> giflistDq = new ArrayList<>();
            giflistDq.add(viewList.get(5));
            giflistDq.add(viewList.get(1));
            ArrayList<BitmapDescriptor> giflistSG = new ArrayList<>();
            giflistSG.add(viewList.get(6));
            giflistSG.add(viewList.get(1));
            ArrayList<BitmapDescriptor> giflistSB = new ArrayList<>();
            giflistSB.add(viewList.get(7));
            giflistSB.add(viewList.get(1));
            ArrayList<BitmapDescriptor> giflistSY = new ArrayList<>();//@@??????5.4
            giflistSY.add(viewList.get(8));
            giflistSY.add(viewList.get(9));
            ArrayList<BitmapDescriptor> giflistSW = new ArrayList<>();//@@??????5.4
            giflistSY.add(viewList.get(11));
            giflistSY.add(viewList.get(9));
            ArrayList<BitmapDescriptor> giflistSJSB = new ArrayList<>();//@@????????????5.4
            giflistSJSB.add(viewList.get(10));
            giflistSJSB.add(viewList.get(1));
            ArrayList<BitmapDescriptor> giflistMC = new ArrayList<>();//@@??????8.10
            giflistMC.add(viewList.get(11));
            giflistMC.add(viewList.get(1));
            ArrayList<BitmapDescriptor> giflistHW = new ArrayList<>();//@@??????8.10
            giflistHW.add(viewList.get(12));
            giflistHW.add(viewList.get(1));
            ArrayList<BitmapDescriptor> giflistHJTCQ = new ArrayList<>();//@@???????????????8.10
            giflistHJTCQ.add(viewList.get(13));
            giflistHJTCQ.add(viewList.get(1));
            ArrayList<BitmapDescriptor> giflistZJ = new ArrayList<>();//@@??????8.10
            giflistZJ.add(viewList.get(14));
            giflistZJ.add(viewList.get(1));
            ArrayList<BitmapDescriptor> giflistSJ = new ArrayList<>();//@@??????8.10
            giflistSJ.add(viewList.get(15));
            giflistSJ.add(viewList.get(1));
            ArrayList<BitmapDescriptor> giflistPL = new ArrayList<>();//@@??????
            giflistPL.add(viewList.get(16));
            giflistPL.add(viewList.get(1));
            for (Smoke smoke : mapNormalSmoke) {
                Camera mCamera = smoke.getCamera();
                int alarmState = smoke.getIfDealAlarm();
                Bundle bundle = new Bundle();
//                if(mCamera!=null&&mCamera.getLatitude()!=null&&mCamera.getLatitude().length()>0){
//                    double latitude = Double.parseDouble(mCamera.getLatitude());
//                    double longitude = Double.parseDouble(mCamera.getLongitude());
//                    LatLng latLng = new LatLng(latitude, longitude);
//                    bundle.putSerializable("mNormalSmoke",mCamera);
//                    markMap(latLng,overlayOptionses,alarmState,giflist2,viewList.get(3),bundle);
//                }else{//@@8.14 ?????????????????????
                    if(smoke.getLatitude().length()==0||smoke.getLongitude().length()==0){
                       continue;
                    }//@@
                    double latitude = Double.parseDouble(smoke.getLatitude());
                    double longitude = Double.parseDouble(smoke.getLongitude());

                    LatLng latLng = new LatLng(latitude, longitude);
                    bundle.putSerializable("mNormalSmoke",smoke);
                    int devType = smoke.getDeviceType();
                    switch (devType){
                        case 57://@@
                        case 55://@@????????????
                        case 31://@@12.26 ??????iot??????
                        case 21://@@12.01 Lora??????
                        case 61://@@??????????????????
                        case 58://@@??????????????????
                        case 56://@@NBIot??????
                        case 41://@@NB??????
                        case 1:
                            markMap(latLng,overlayOptionses,alarmState,giflist,viewList.get(0),bundle);
                            break;
                        case 73://??????7020??????
                        case 72://????????????
                        case 22:
                        case 23:
                        case 16://@@9.29
                        case 2:
                            markMap(latLng,overlayOptionses,alarmState,giflistRQ,viewList.get(2),bundle);
                            break;
                        case 81:
                        case 80:
                        case 77:
                        case 76:
                        case 75:
                        case 59:
                        case 53://NB??????
                        case 52://@@Lara????????????
                        case 5:
                            markMap(latLng,overlayOptionses,alarmState,giflistDq,viewList.get(5),bundle);
                            break;
                        case 7:
                            markMap(latLng,overlayOptionses,alarmState,giflistSG,viewList.get(6),bundle);
                            break;
                        case 8:
                            markMap(latLng,overlayOptionses,alarmState,giflistSB,viewList.get(7),bundle);
                            break;
                        case 9:
                            markMap(latLng,overlayOptionses,alarmState,giflistSJSB,viewList.get(10),bundle);
                            break;
                        case 125:
                        case 78:
                        case 70:
                        case 68:
                        case 47:
                        case 43://@@lora??????
                        case 42:
                        case 10:
                            markMap(latLng,overlayOptionses,alarmState,giflistSY,viewList.get(8),bundle);
                            break;
                        case 11:
                            markMap(latLng,overlayOptionses,alarmState,giflistHW,viewList.get(12),bundle);
                            break;
                        case 12:
                            markMap(latLng,overlayOptionses,alarmState,giflistMC,viewList.get(11),bundle);
                            break;
                        case 79://???????????????
                        case 26://???????????????
                        case 25://??????????????????
                        case 13:
                            markMap(latLng,overlayOptionses,alarmState,giflistHJTCQ,viewList.get(13),bundle);
                            break;
                        case 126:
                            markMap(latLng,overlayOptionses,alarmState,giflistZJ,viewList.get(14),bundle);
                            break;
                        case 15:
                            markMap(latLng,overlayOptionses,alarmState,giflistSJ,viewList.get(15),bundle);
                            break;
                        case 18:
                            markMap(latLng,overlayOptionses,alarmState,giflistPL,viewList.get(16),bundle);
                            break;
                        case 124:
                        case 69:
                        case 48:
                        case 46:
                        case 44://????????????
                        case 19:
                            markMap(latLng,overlayOptionses,alarmState,giflistSW,viewList.get(11),bundle);
                            break;
                    }
//                }
            }
        }else if(mapNormalNFC!=null&&mapNormalNFC.size()>0){//@@8.18 NFC????????????
            for (NFCRecordBean smoke : mapNormalNFC) {
                Bundle bundle = new Bundle();
                if(smoke.getLatitude().length()==0||smoke.getLongitude().length()==0){
                    continue;
                }//@@
                double latitude = Double.parseDouble(smoke.getLatitude());
                double longitude = Double.parseDouble(smoke.getLongitude());

                LatLng latLng = new LatLng(latitude, longitude);
                bundle.putSerializable("mNormalSmoke",smoke);
                String stateType = smoke.getDevicestate();
                switch (stateType) {
                    case "0":
                        markMap(latLng, overlayOptionses, 1, null, viewList.get(7), bundle);//?????? Yellow
                        break;
                    case "1":
                        markMap(latLng, overlayOptionses, 1, null, viewList.get(2), bundle);//?????? Green
                        break;
                    case "2":
                        markMap(latLng, overlayOptionses, 1, null, viewList.get(1), bundle);//????????? Red
                        break;
                    default:
                        markMap(latLng, overlayOptionses, 1, null, viewList.get(7), bundle);//?????? Yellow
                        break;
                    }
                }
        }else if(mInspectionList!=null&&mInspectionList.size()>0){//@@8.18 NFC????????????
            for (NFCInfoEntity smoke : mInspectionList) {
                Bundle bundle = new Bundle();
                if(smoke.getLatitude()==null||smoke.getLongitude()==null||smoke.getLatitude().length()==0||smoke.getLongitude().length()==0){
                    continue;
                }//@@
                double latitude = Double.parseDouble(smoke.getLatitude());
                double longitude = Double.parseDouble(smoke.getLongitude());

                LatLng latLng = new LatLng(latitude, longitude);
                bundle.putSerializable("mNormalSmoke",smoke);
//                String stateType = smoke.getIscheck();
//                switch (stateType) {
//                    case "0":
//                        markMap(latLng, overlayOptionses, 1, null, viewList.get(7), bundle);//?????? Yellow
//                        break;
//                    case "1":
//                        markMap(latLng, overlayOptionses, 1, null, viewList.get(2), bundle);//?????? Green
//                        break;
//                    case "2":
                        markMap(latLng, overlayOptionses, 1, null, viewList.get(0), bundle);//????????? Red
//                        break;
//                    default:
//                        markMap(latLng, overlayOptionses, 1, null, viewList.get(7), bundle);//?????? Yellow
//                        break;
//                }
            }
        }
        return overlayOptionses;
    }


    private void markMap(LatLng latLng,List<OverlayOptions> overlayOptions,int alarmState,
                         ArrayList<BitmapDescriptor> bitmapDescriptors,BitmapDescriptor bitmapDescriptor, Bundle bundle){
        if(alarmState==0){
            overlayOptions.add(new MarkerOptions().position(latLng).icons(bitmapDescriptors).extraInfo(bundle)
                    .zIndex(0).period(10));
//                    .animateType(MarkerOptions.MarkerAnimateType.drop));//????????????????????????
        }else{
            overlayOptions.add(new MarkerOptions().position(latLng).icon(bitmapDescriptor).extraInfo(bundle)
                    .zIndex(0).draggable(false)////????????????????????????@@5.18
                    .perspective(true));
//                    .animateType(MarkerOptions.MarkerAnimateType.drop));//????????????????????????
        }

    }
}

