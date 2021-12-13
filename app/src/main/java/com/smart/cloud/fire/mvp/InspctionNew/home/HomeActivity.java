package com.smart.cloud.fire.mvp.InspctionNew.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.igexin.sdk.PushManager;
import com.smart.cloud.fire.activity.AlarmHistory.AlarmHistoryActivity;
import com.smart.cloud.fire.activity.Inspection.PointList.PointListActivity;
import com.smart.cloud.fire.activity.Inspection.UploadInspectionInfo.UploadProblemActivity;
import com.smart.cloud.fire.activity.Inspection.UploadMsg.UploadMsgActivity;
import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.SafeScore;
import com.smart.cloud.fire.global.SmokeSummary;
import com.smart.cloud.fire.mvp.Inspction.InspMainActivity;
import com.smart.cloud.fire.mvp.login.LoginActivity;
import com.smart.cloud.fire.mvp.login.SplashActivity;
import com.smart.cloud.fire.mvp.main.presenter.MainPresenter;
import com.smart.cloud.fire.mvp.main.view.MainView;
import com.smart.cloud.fire.order.OrderList.OrderListActivity;
import com.smart.cloud.fire.service.RemoteService;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;

public class HomeActivity extends MvpActivity<MainPresenter> implements MainView {





    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mContext=this;




        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        regFilter();
        startService(new Intent(HomeActivity.this, RemoteService.class));
        //启动个推接收推送信息。。
        PushManager.getInstance().initialize(this.getApplicationContext(), com.smart.cloud.fire.geTuiPush.DemoPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), com.smart.cloud.fire.geTuiPush.DemoIntentService.class);
        init();
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    public void init(){

    }

    @OnClick({R.id.home_zhxj_line,R.id.home_bjxx_line,R.id.home_afgl_line,
                R.id.home_sbbz_line,R.id.home_dtwz_line,R.id.home_xjxm_line,
                    R.id.home_xfxt_line,R.id.home_gdrl_line,R.id.home_xcdj_line
                        ,R.id.home_tcdl_line})
    public void onClick(View v){
        Intent i = null;
        switch (v.getId()){
            case R.id.home_zhxj_line:
                i=new Intent(mContext,HomeZHXJActivity.class);
                break;
            case R.id.home_bjxx_line:
                i=new Intent(mContext, AlarmHistoryActivity.class);
                break;
            case R.id.home_afgl_line:
                i=new Intent(mContext, HomeAFGLActivity.class);
                break;
            case R.id.home_sbbz_line:
//                i = new Intent(mContext, UploadMsgActivity.class);
//                i = new Intent(mContext, UploadProblemActivity.class);
                i = new Intent(mContext, HomeSBBZActivity.class);
                break;
            case R.id.home_dtwz_line:
                i = new Intent(mContext, HomeDTWZActivity.class);
                break;
            case R.id.home_xjxm_line:
                i=new Intent(mContext, PointListActivity.class);
                break;
            case R.id.home_xfxt_line:
                i = new Intent(mContext, HomeXFXTActivity.class);
                break;
            case R.id.home_gdrl_line:
                i=new Intent(mContext, OrderListActivity.class);
                break;
            case R.id.home_xcdj_line:
                if(MyApp.app.getPrivilege()==31){
                    i=new Intent(mContext, HomeXCDJ2Activity.class);
                }else{
                    T.showShort(mContext,"您没有该功能权限");
                }

                break;
            case R.id.home_tcdl_line:
//                i=new Intent(mContext,InspMainActivity.class);
                SharedPreferencesManager.getInstance().putData(mContext,
                        SharedPreferencesManager.SP_FILE_GWELL,
                        SharedPreferencesManager.KEY_RECENTPASS,
                        "");
                SharedPreferencesManager.getInstance().removeData(mContext,
                        "LASTAREANAME");//@@11.13
                SharedPreferencesManager.getInstance().removeData(mContext,
                        "LASTAREAID");//@@11.13
                SharedPreferencesManager.getInstance().removeData(mContext,
                        "LASTAREAISPARENT");//@@11.13
                PushManager.getInstance().stopService(getApplicationContext());
                unbindAlias();
                i = new Intent(mContext, LoginActivity.class);
                finish();
                break;

        }
        if(i!=null){
            startActivity(i);
        }
    }


    /**
     * 添加广播接收器件。。
     */
    private void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("APP_EXIT");
        mContext.registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, Intent intent) {
            //退出。。
            if (intent.getAction().equals("APP_EXIT")) {
                SharedPreferencesManager.getInstance().putData(mContext,
                        SharedPreferencesManager.SP_FILE_GWELL,
                        SharedPreferencesManager.KEY_RECENTPASS,
                        "");
                SharedPreferencesManager.getInstance().removeData(mContext,
                        "LASTAREANAME");//@@11.13
                SharedPreferencesManager.getInstance().removeData(mContext,
                        "LASTAREAID");//@@11.13
                SharedPreferencesManager.getInstance().removeData(mContext,
                        "LASTAREAISPARENT");//@@11.13
                PushManager.getInstance().stopService(getApplicationContext());
                unbindAlias();
                Intent in = new Intent(mContext, SplashActivity.class);
                startActivity(in);
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
        VolleyHelper.getInstance(mContext).stopRequestQueue();
    }

    /**
     * 个推解绑@@5.16
     */
    private void unbindAlias() {
        String userCID = SharedPreferencesManager.getInstance().getData(this,SharedPreferencesManager.SP_FILE_GWELL,"CID");//@@
        String username = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        String url= ConstantValues.SERVER_IP_NEW+"loginOut?userId="+username+"&alias="+username+"&cid="+userCID+"&appId=1";//@@5.27添加app编号
//        RequestQueue mQueue = Volley.newRequestQueue(this);
        VolleyHelper helper=VolleyHelper.getInstance(mContext);
        RequestQueue mQueue = helper.getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mvpPresenter.exitBy2Click(mContext);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void exitBy2Click(boolean isExit) {
        if (isExit) {
            //moveTaskToBack(false);
            moveTaskToBack(true);//@@5.31
        }
    }

    @Override
    public void getOnlineSummary(SmokeSummary model) {

    }

    @Override
    public void getSafeScore(SafeScore model) {

    }

}
