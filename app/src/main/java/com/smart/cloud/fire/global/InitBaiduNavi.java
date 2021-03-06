package com.smart.cloud.fire.global;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.smart.cloud.fire.mvp.fragment.MapFragment.MapItem;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Smoke;
import com.smart.cloud.fire.ui.BNDemoGuideActivity;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.TestAuthorityUtil;
import com.smart.cloud.fire.utils.TurnToMapUtil;
import com.smart.cloud.fire.view.NormalDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

/**
 * Created by Administrator on 2016/8/8.
 */
public class InitBaiduNavi {
    private Activity mActivity;
    private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";
    private String mSDCardPath = null;
    String authinfo = null;
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private double lati;
    private double lon;
    private double toLat;
    private double toLon;
    private MapItem normalSmoke;
    private NormalDialog mNormalDialog;

    public InitBaiduNavi(Activity mActivity, MapItem normalSmoke){
        this.mActivity = mActivity;
        if(!TestAuthorityUtil.testLocation(mActivity.getApplicationContext())){
            return;
        }
        this.normalSmoke = normalSmoke;
        if(mNormalDialog==null){
            mNormalDialog = new NormalDialog(mActivity);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LatLng slatLng=new LatLng(0,0);
            double latitude = Double.parseDouble(normalSmoke.getLat());
            double longitude = Double.parseDouble(normalSmoke.getLon());
            LatLng elatLng=new LatLng(latitude,longitude);
            showListDialog(slatLng,elatLng);
        }else{
            mNormalDialog.showLoadingDialog("???????????????...");
            mLocationClient = new LocationClient(mActivity.getApplicationContext());     //??????LocationClient???
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// ??????????????????
            option.setCoorType("bd09ll");// ???????????????????????????????????????,?????????gcj02
            option.setScanSpan(3000);// ??????????????????????????????????????????5000ms
            option.setIsNeedAddress(true);// ???????????????????????????????????????
            option.setNeedDeviceDirect(true);// ????????????????????????????????????????????????
            option.setOpenGps(true);// ??????GPS
            mLocationClient.setLocOption(option);
            mLocationClient.registerLocationListener(myListener);    //??????????????????
            BNOuterLogUtil.setLogSwitcher(true);
            if (initDirs()) {
                initNavi();
            }
        }
    }

    class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            int result = bdLocation.getLocType();
            switch (result){
                case 61:
                    lati = bdLocation.getLatitude();
                    lon =bdLocation.getLongitude();
                    routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL);
                    break;
                case 62:
                    T.showShort(mActivity,"????????????,??????????????????????????????wifi????????????????????????");
                    break;
                case 63:
                    T.showShort(mActivity,"????????????????????????????????????????????????????????????");
                    if(mNormalDialog!=null){
                        mNormalDialog.dismiss();
                        mNormalDialog=null;
                    }
                    break;
                case 65:
                    T.showShort(mActivity,"?????????????????????");
                    if(mNormalDialog!=null){
                        mNormalDialog.dismiss();
                        mNormalDialog=null;
                    }
                    break;
                case 66:
                    T.showShort(mActivity,"??????????????????");
                    if(mNormalDialog!=null){
                        mNormalDialog.dismiss();
                        mNormalDialog=null;
                    }
                    break;
                case 67:
                    T.showShort(mActivity,"??????????????????");
                    if(mNormalDialog!=null){
                        mNormalDialog.dismiss();
                        mNormalDialog=null;
                    }
                    break;
                case 68:
                    T.showShort(mActivity," ????????????????????????????????????????????????????????????????????????");
                    if(mNormalDialog!=null){
                        mNormalDialog.dismiss();
                        mNormalDialog=null;
                    }
                    break;
                case 161:
                    lati = bdLocation.getLatitude();
                    lon =bdLocation.getLongitude();
                    routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL);
                    break;
                case 162:
                    T.showShort(mActivity,"????????????,??????????????????????????????wifi????????????????????????");
                    if(mNormalDialog!=null){
                        mNormalDialog.dismiss();
                        mNormalDialog=null;
                    }
                    break;
                case 167:
                    T.showShort(mActivity,"????????????????????????????????????????????????????????????????????????");
                    if(mNormalDialog!=null){
                        mNormalDialog.dismiss();
                        mNormalDialog=null;
                    }
                    break;
                case 502:
                    T.showShort(mActivity,"????????????,??????????????????????????????wifi????????????????????????");
                    if(mNormalDialog!=null){
                        mNormalDialog.dismiss();
                        mNormalDialog=null;
                    }
                    break;
                case 505:
                    T.showShort(mActivity,"????????????,??????????????????????????????wifi????????????????????????");
                    if(mNormalDialog!=null){
                        mNormalDialog.dismiss();
                        mNormalDialog=null;
                    }
                    break;
                case 601:
                    T.showShort(mActivity,"????????????,??????????????????????????????wifi????????????????????????");
                    if(mNormalDialog!=null){
                        mNormalDialog.dismiss();
                        mNormalDialog=null;
                    }
                    break;
                case 602:
                    T.showShort(mActivity,"????????????,??????????????????????????????wifi????????????????????????");
                    if(mNormalDialog!=null){
                        mNormalDialog.dismiss();
                        mNormalDialog=null;
                    }
                    break;
                default:
                    T.showShort(mActivity,"????????????,??????????????????????????????wifi????????????????????????");
                    if(mNormalDialog!=null){
                        mNormalDialog.dismiss();
                        mNormalDialog=null;
                    }
                    break;
            }
        }
    }

    private void initNavi() {
        BaiduNaviManager.getInstance().init(mActivity, mSDCardPath, APP_FOLDER_NAME, mNaviInitListener, null, ttsHandler, null);
    }


    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }


    private BaiduNaviManager.NaviInitListener mNaviInitListener = new BaiduNaviManager.NaviInitListener(){
        @Override
        public void onAuthResult(int status, String msg) {
            if (0 == status) {
                authinfo = "key????????????!";
            } else {
                authinfo = "key????????????, " + msg;
            }

        }

        public void initSuccess() {
            //Toast.makeText(mActivity, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
            initSetting();
        }

        public void initStart() {
            //Toast.makeText(mActivity, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
        }

        public void initFailed() {
            //Toast.makeText(mActivity, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
        }
    };

    private void initSetting(){
        BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        double latitude = Double.parseDouble(normalSmoke.getLat());
        double longitude = Double.parseDouble(normalSmoke.getLon());
        GetLoad(latitude,longitude);
    }

    /**
     * ??????TTS??????????????????handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    //T.showShort(mActivity, "Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    //T.showShort(mActivity, "Handler : TTS play end");
                    break;
                }
                default :
                    break;
            }
        }
    };

    private void GetLoad(double fromLat,double formLon){
        toLat = fromLat;
        toLon = formLon;

        if (BaiduNaviManager.isNaviInited()) {
            mLocationClient.start();
        }
    }


    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType) {
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        switch (coType) {
            case BD09LL: {
                sNode = new BNRoutePlanNode(lon,lati, "????????????", null, coType);
                eNode = new BNRoutePlanNode(toLon, toLat, "???????????????", null, coType);
                break;
            }
            default:
                break;
        }
        if (sNode != null && eNode != null) {
            stopLead();
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(mActivity, list, 1, true, new DemoRoutePlanListener(sNode));

        }
    }

    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
			/*
			 * ?????????????????????resetEndNode??????????????????
			 */
            Intent intent = new Intent(mActivity, BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
            if(mNormalDialog!=null){
                mNormalDialog.dismiss();
                mNormalDialog=null;
            }
            Intent i = new Intent();
            i.setAction("CLOSE_ALARM_ACTIVITY");
            mActivity.sendBroadcast(i);
        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(mActivity, "????????????", Toast.LENGTH_SHORT).show();
            if(mNormalDialog!=null){
                mNormalDialog.dismiss();
                mNormalDialog=null;
            }
        }
    }

    private void stopLead(){
        if(mLocationClient!=null){
            mLocationClient.unRegisterLocationListener(myListener);
            mLocationClient.stop();
            mLocationClient=null;
        }
    }

    private void showListDialog(LatLng slatLng, LatLng elatLng) {
        final String[] items = { "??????","??????"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(mActivity);
        listDialog.setTitle("???????????????????????????");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) { // ????????????1??????
                    if (TurnToMapUtil.isGdMapInstalled()) {
                        LatLng slatLngTemp=TurnToMapUtil.BD09ToGCJ02(slatLng);
                        LatLng elatLngTemp=TurnToMapUtil.BD09ToGCJ02(elatLng);
                        TurnToMapUtil.openGaoDeNavi(mActivity,slatLngTemp.latitude,slatLngTemp.longitude,null,elatLngTemp.latitude,elatLngTemp.longitude,"????????????");
                    }else {
                        T.showShort(mActivity,"??????????????????????????????");
                        new AlertDialog.Builder(mActivity)
                                .setMessage("?????????????????????")
                                .setPositiveButton("??????", (dialog1, which1) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(TurnToMapUtil.DOWNLOAD_GAODE_MAP))))
                                .setNegativeButton("??????", null)
                                .show();
                    }
                } else if (which == 1) { // ????????????2??????
                    if (TurnToMapUtil.isBaiduMapInstalled()){
                        TurnToMapUtil.openBaiDuNavi(mActivity,slatLng.latitude,slatLng.longitude,null,elatLng.latitude,elatLng.longitude,"?????????");
                    }else {
                        T.showShort(mActivity,"??????????????????????????????");
                        new AlertDialog.Builder(mActivity)
                                .setMessage("?????????????????????")
                                .setNegativeButton("??????", null)
                                .setPositiveButton("??????", (dialog12, which12) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(TurnToMapUtil.DOWNLOAD_BAIDU_MAP))))
                                .show();
                    }
                }
            }
        });
        listDialog.show();
    }

}
