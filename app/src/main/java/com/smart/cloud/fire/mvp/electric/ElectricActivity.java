package com.smart.cloud.fire.mvp.electric;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.smart.cloud.fire.Volley.FastJsonRequest;
import com.smart.cloud.fire.adapter.ElectricActivityAdapterTest;
import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.ElectricEnergyEntity;
import com.smart.cloud.fire.global.ElectricValue;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.mvp.LineChart.LineChartActivity;
import com.smart.cloud.fire.mvp.electricChangeHistory.ElectricChangeHistoryActivity;
import com.smart.cloud.fire.utils.BingoDialog;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;

/**
 * Created by Administrator on 2016/11/2.
 */
public class ElectricActivity extends MvpActivity<ElectricPresenter> implements ElectricView {
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipe_fresh_layout)
    SwipeRefreshLayout swipeFreshLayout;
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.power_change_history_text)
    TextView power_change_history_text;//@@8.28??????????????????
    @Bind(R.id.more)
    TextView more;//@@??????
    private ElectricPresenter electricPresenter;
    private ElectricActivityAdapterTest electricActivityAdapter;
    private Context mContext;
    private LinearLayoutManager linearLayoutManager;
    private String electricMac;
    private int devType;//@@2018.05.15????????????
    private String repeatMac;
    private String userID;
    private int privilege;

    private String yuzhi43="";
    private String yuzhi44="";
    private String yuzhi45="";
    private String yuzhi46="";
    private String yuzhi47="0";

    private ElectricEnergyEntity energyEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electric);
        mContext=this;
        Intent intent=getIntent();
        electricMac = getIntent().getExtras().getString("ElectricMac");
        devType = getIntent().getExtras().getInt("devType");
        repeatMac = getIntent().getExtras().getString("repeatMac");
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege2();
//        power_change_history_text.setVisibility(View.VISIBLE);//@@8.28
        more.setVisibility(View.VISIBLE);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        registerForContextMenu(more);
        ButterKnife.bind(this);
        refreshListView();
        electricPresenter.getOneElectricInfo(userID,privilege+"",devType+"",electricMac,false);
        getYuzhi(electricMac);
        getFenli(electricMac);
    }

    private void showPopupMenu(View view) {
        // View??????PopupMenu???????????????View?????????
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu??????
        popupMenu.getMenuInflater().inflate(R.menu.menu_electr, popupMenu.getMenu());
        // menu???item????????????
        if(devType!=5){
            MenuItem item=popupMenu.getMenu().findItem(R.id.electr_yuzhi_set);
            item.setVisible(false);
        }
        if(devType!=52&&devType!=53&&devType!=75&&devType!=77){
            MenuItem item=popupMenu.getMenu().findItem(R.id.yuzhi_set);
            item.setVisible(false);
        }
        if(!(devType==80||devType==81)){
            MenuItem item=popupMenu.getMenu().findItem(R.id.fenli);
            item.setVisible(false);
            item=popupMenu.getMenu().findItem(R.id.utfenli);
            item.setVisible(false);
            item=popupMenu.getMenu().findItem(R.id.clear_voice);
            item.setVisible(false);
            item=popupMenu.getMenu().findItem(R.id.reset);
            item.setVisible(false);
            item=popupMenu.getMenu().findItem(R.id.race);
            item.setVisible(false);
        }else{
            MenuItem item=popupMenu.getMenu().findItem(R.id.change_history);
            item.setVisible(false);
        }
        if(devType!=81){
            MenuItem item=popupMenu.getMenu().findItem(R.id.restart);
            item.setVisible(false);
            item=popupMenu.getMenu().findItem(R.id.heartime_set);
            item.setVisible(false);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.electr_yuzhi_set:
                        gotoElectrYuzhiSet();
                        break;
                    case R.id.heartime_set:
                        gotoSethearttime();
                        break;
                    case R.id.restart:
                        gotorestart();
                        break;
                    case R.id.race:
                        gotoRace();
                        break;
                    case R.id.fenli:
                        gotoSetting();
                        break;
                    case R.id.utfenli:
                        gotoSetFenli();
                        break;
                    case R.id.clear_voice:
                        gotoClearvoice();
                        break;
                    case R.id.reset:
                        gotoResetAlarm();
                        break;
                    case R.id.change_history:
                        Intent intent=new Intent(mContext, ElectricChangeHistoryActivity.class);
                        intent.putExtra("mac",electricMac);
                        startActivity(intent);
                        break;
                    case R.id.yuzhi_set:
                        gotoYuzhiSet();
                        break;
                }
                return false;
            }
        });
        // PopupMenu????????????
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });

        popupMenu.show();
    }

    private void gotoElectrYuzhiSet() {
        LayoutInflater inflater = getLayoutInflater();
        View layout= inflater.inflate(R.layout.electr_yuzhi_setting,(ViewGroup) findViewById(R.id.rela));

        BingoDialog dialog=new BingoDialog(this,layout);
        final EditText high_value=(EditText)layout.findViewById(R.id.high_value);
        high_value.setText(yuzhi43);
        final EditText low_value=(EditText)layout.findViewById(R.id.low_value);
        low_value.setText(yuzhi44);
        final EditText overcurrentvalue=(EditText)layout.findViewById(R.id.overcurrentvalue);
        overcurrentvalue.setText(yuzhi45);
        final EditText Leakage_value=(EditText)layout.findViewById(R.id.Leakage_value);
        Leakage_value.setText(yuzhi46);


        Button commit=(Button)layout.findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="";
                try{
                    int high=(int)Float.parseFloat(high_value.getText().toString());
                    int low=(int)Float.parseFloat(low_value.getText().toString());
                    float value45=Float.parseFloat(overcurrentvalue.getText().toString());
                    int value46=(int)Float.parseFloat(Leakage_value.getText().toString());
                    int value47=0;

                    if(low<145||low>220){
                        T.showShort(mContext,"???????????????????????????145-220V");
                        return;
                    }
                    if(high<220||high>280){
                        T.showShort(mContext,"???????????????????????????220-280V");
                        return;
                    }
                    if(value45<1||value45>63){
                        T.showShort(mContext,"???????????????????????????1-63A");
                        return;
                    }
                    if(value46<10||value46>1000){
                        T.showShort(mContext,"??????????????????????????????10-1000mA");
                        return;
                    }
                    if(low>high){
                        T.showShort(mContext,"????????????????????????????????????");
                        return;
                    }

                    int b=0;

                    url= ConstantValues.SERVER_IP_NEW+"Telegraphy_Uool_control?repeaterMac="+repeatMac+"&Overvoltage="+high_value.getText().toString()
                            +"&Undervoltage="+low_value.getText().toString()
                            +"&Overcurrent="+value45
                            +"&Leakage="+value46
                            +"&deviceType="+81+"&devCmd=14&CurrentMAX=0&imei="+electricMac
                            +"&Temperature="+value47
                            +"&ShuntRelevance="+b;


                }catch(Exception e){
                    e.printStackTrace();
                    T.showShort(mContext,"??????????????????????????????");
                    return;
                }
                final ProgressDialog dialog1 = new ProgressDialog(mContext);
                dialog1.setTitle("??????");
                dialog1.setMessage("?????????????????????");
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
                VolleyHelper helper=VolleyHelper.getInstance(mContext);
                RequestQueue mQueue = helper.getRequestQueue();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int errorCode=response.getInt("errorCode");
                                    if(errorCode==0){
                                        T.showShort(mContext,"??????????????????????????????????????????");
                                        electricPresenter.getOneElectricInfo(userID,privilege+"",devType+"",electricMac,false);
                                    }else{
                                        T.showShort(mContext,"????????????");
                                    }
                                    getYuzhi(electricMac);
                                    getFenli(electricMac);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog1.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        T.showShort(mContext,"????????????");
                        dialog1.dismiss();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mQueue.add(jsonObjectRequest);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void refreshListView() {
        //?????????????????????????????????????????????4???
        swipeFreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipeFreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipeFreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        swipeFreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                electricPresenter.getOneElectricInfo(userID,privilege+"",devType+"",electricMac,true);
                getYuzhi(electricMac);
                getFenli(electricMac);
            }
        });
    }

    @Override
    protected ElectricPresenter createPresenter() {
        electricPresenter = new ElectricPresenter(this);
        return electricPresenter;
    }

    @Override
    public void getDataSuccess(List<ElectricValue.ElectricValueBean> smokeList) {
        if(smokeList.size()==0){
            Toast.makeText(mContext,"?????????",Toast.LENGTH_SHORT).show();
        }//@@7.7
        electricActivityAdapter = new ElectricActivityAdapterTest(mContext, smokeList, electricPresenter);
        recyclerView.setAdapter(electricActivityAdapter);
        swipeFreshLayout.setRefreshing(false);
        electricActivityAdapter.setOnItemClickListener(new ElectricActivityAdapterTest.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view, ElectricValue.ElectricValueBean data){
                Intent intent = new Intent(mContext, LineChartActivity.class);
                intent.putExtra("electricMac",electricMac);
                intent.putExtra("electricType",data.getElectricType());
                intent.putExtra("electricNum",data.getId());
                intent.putExtra("devType",devType);
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.power_change_history_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.power_change_history_text:
                Intent intent=new Intent(mContext, ElectricChangeHistoryActivity.class);
                intent.putExtra("mac",electricMac);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void getDataFail(String msg) {
        swipeFreshLayout.setRefreshing(false);
        T.showShort(mContext,msg);
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }


    public void getYuzhi(String mac){
        VolleyHelper helper=VolleyHelper.getInstance(mContext);
        String url=ConstantValues.SERVER_IP_NEW+"getElectrAlarmThreshold?mac="+mac;
        RequestQueue mQueue = helper.getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int errorCode=response.getInt("errorCode");
                            if(errorCode==0){
                                yuzhi43=response.getString("value43");
                                yuzhi44=response.getString("value44");
                                yuzhi45=response.getString("value45");
                                yuzhi46=response.getString("value46");
                                yuzhi47=response.getString("value47");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                T.showShort(mContext,"????????????");
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(jsonObjectRequest);
    }

    public void getFenli(String mac){
        VolleyHelper helper=VolleyHelper.getInstance(mContext);
        String url=ConstantValues.SERVER_IP_NEW+"getOneEnergyEntity?userId=13622215085&devType=80&privilege=4&smokeMac="+mac;
        RequestQueue mQueue = helper.getRequestQueue();

        FastJsonRequest<ElectricEnergyEntity> fRequest = new FastJsonRequest<ElectricEnergyEntity>(url, null,
                null, ElectricEnergyEntity.class, new Response.Listener<ElectricEnergyEntity>() {

            @Override
            public void onResponse(ElectricEnergyEntity response) {
                energyEntity=response;
                int a=1;
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(fRequest);
    }

    private void gotorestart() {
        final ProgressDialog dialog1 = new ProgressDialog(mContext);
        dialog1.setTitle("??????");
        dialog1.setMessage("?????????????????????");
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url= ConstantValues.SERVER_IP_NEW+"Telegraphy_Uool_control?repeaterMac="+repeatMac
                +"&deviceType="+devType+"&devCmd=16&imei="+electricMac;
        VolleyHelper helper=VolleyHelper.getInstance(mContext);
        RequestQueue mQueue = helper.getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int errorCode=response.getInt("errorCode");
                            if(errorCode==0){
                                T.showShort(mContext,"????????????");
                                electricPresenter.getOneElectricInfo(userID,privilege+"",devType+"",electricMac,false);
                            }else{
                                T.showShort(mContext,"????????????");
                            }
                            dialog1.dismiss();
                            getYuzhi(electricMac);
                            getFenli(electricMac);
                        } catch (JSONException e) {
                            dialog1.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                T.showShort(mContext,"????????????");
                dialog1.dismiss();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(jsonObjectRequest);
    }

    public void gotoSetting(){
        LayoutInflater inflater = getLayoutInflater();
        View layout= inflater.inflate(R.layout.ut_electr_threshold_setting,(ViewGroup) findViewById(R.id.rela));

        BingoDialog dialog=new BingoDialog(this,layout);
        final EditText high_value=(EditText)layout.findViewById(R.id.high_value);
        high_value.setText(yuzhi43);
        final EditText low_value=(EditText)layout.findViewById(R.id.low_value);
        low_value.setText(yuzhi44);
        final EditText overcurrentvalue=(EditText)layout.findViewById(R.id.overcurrentvalue);
        overcurrentvalue.setText(yuzhi45);
        final EditText Leakage_value=(EditText)layout.findViewById(R.id.Leakage_value);
        Leakage_value.setText(yuzhi46);
        final EditText temperature_value=(EditText)layout.findViewById(R.id.Temperature_value);
        temperature_value.setText(yuzhi47);
        final EditText currentMAX_value=(EditText)layout.findViewById(R.id.CurrentMAX_value);
        currentMAX_value.setText("0");
        final Switch fenli_switch=(Switch)layout.findViewById(R.id.fenli_switch);
        final LinearLayout fenliHoldTime_line=(LinearLayout) layout.findViewById(R.id.fenliHoldTime_line);
        final EditText fenliHoldTime_value=(EditText)layout.findViewById(R.id.fenliHoldTime_value);
        if(energyEntity!=null&&energyEntity.getShuntRelevanceTime()>0){
            fenli_switch.setChecked(true);
            fenliHoldTime_value.setText(energyEntity.getShuntRelevanceTime()+"");
            fenliHoldTime_line.setVisibility(View.VISIBLE);
        }else {
            fenli_switch.setChecked(false);
            fenliHoldTime_line.setVisibility(View.GONE);
        }

        fenli_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    final TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            -0.1f, Animation.RELATIVE_TO_SELF, 0.0f);
                    mShowAction.setDuration(500);
                    fenliHoldTime_line.setAnimation(mShowAction);
                    fenliHoldTime_line.setVisibility(View.VISIBLE);
                }else{
                    fenliHoldTime_line.setVisibility(View.GONE);
                }
            }
        });

        final Switch QI_switch=(Switch)layout.findViewById(R.id.QI_switch);
        if(energyEntity!=null&&energyEntity.getShuntCuPer()==1){
            QI_switch.setChecked(true);
        }else {
            QI_switch.setChecked(false);
        }
        final Switch TI_switch=(Switch)layout.findViewById(R.id.TI_switch);
        if(energyEntity!=null&&energyEntity.getShuntTemp()==1){
            TI_switch.setChecked(true);
        }else {
            TI_switch.setChecked(false);
        }
        final Switch fenli_liandong_switch=(Switch)layout.findViewById(R.id.fenli_liandong_switch);
        if(energyEntity!=null&&energyEntity.getShuntLink()==1){
            fenli_liandong_switch.setChecked(true);
        }else {
            fenli_liandong_switch.setChecked(false);
        }

        Button commit=(Button)layout.findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="";
                try{
                    int high=(int)Float.parseFloat(high_value.getText().toString());
                    int low=(int)Float.parseFloat(low_value.getText().toString());
                    float value45=Float.parseFloat(overcurrentvalue.getText().toString());
                    int value46=(int)Float.parseFloat(Leakage_value.getText().toString());
                    int value47=(int)Float.parseFloat(temperature_value.getText().toString());

                    if(low<145||low>220){
                        T.showShort(mContext,"???????????????????????????145-220V");
                        return;
                    }
                    if(high<220||high>280){
                        T.showShort(mContext,"???????????????????????????220-280V");
                        return;
                    }
                    if(value45<1||value45>63){
                        T.showShort(mContext,"???????????????????????????1-63A");
                        return;
                    }
                    if(value46<10||value46>1000){
                        T.showShort(mContext,"??????????????????????????????10-1000mA");
                        return;
                    }
                    if(value47<0||value47>100){
                        T.showShort(mContext,"??????????????????????????????0-100???");
                        return;
                    }
                    if(low>high){
                        T.showShort(mContext,"????????????????????????????????????");
                        return;
                    }

                    int b=0;
                    if(fenli_switch.isChecked()&&fenliHoldTime_value.getText().length()>0){
                        b= b+Integer.parseInt(fenliHoldTime_value.getText().toString());
                    }else if(fenli_switch.isChecked()&&fenliHoldTime_value.getText().length()==0){
                        b= b+31;
                    }

                    if(QI_switch.isChecked()){
                        b= b+32;
                    }

                    if(TI_switch.isChecked()){
                        b= b+64;
                    }

                    if(fenli_liandong_switch.isChecked()){
                        b= b+64;
                    }

                    url= ConstantValues.SERVER_IP_NEW+"Telegraphy_Uool_control?repeaterMac="+repeatMac+"&Overvoltage="+high_value.getText().toString()
                            +"&Undervoltage="+low_value.getText().toString()
                            +"&Overcurrent="+value45
                            +"&Leakage="+value46
                            +"&deviceType="+devType+"&devCmd=14&CurrentMAX=0&imei="+electricMac
                            +"&Temperature="+value47
                            +"&ShuntRelevance="+b;


                }catch(Exception e){
                    e.printStackTrace();
                    T.showShort(mContext,"??????????????????????????????");
                    return;
                }
                final ProgressDialog dialog1 = new ProgressDialog(mContext);
                dialog1.setTitle("??????");
                dialog1.setMessage("?????????????????????");
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
                VolleyHelper helper=VolleyHelper.getInstance(mContext);
                RequestQueue mQueue = helper.getRequestQueue();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int errorCode=response.getInt("errorCode");
                                    if(errorCode==0){
                                        T.showShort(mContext,"??????????????????????????????????????????");
                                        electricPresenter.getOneElectricInfo(userID,privilege+"",devType+"",electricMac,false);
                                    }else{
                                        T.showShort(mContext,"????????????");
                                    }
                                    getYuzhi(electricMac);
                                    getFenli(electricMac);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog1.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        T.showShort(mContext,"????????????");
                        dialog1.dismiss();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mQueue.add(jsonObjectRequest);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void gotoSetFenli(){
        LayoutInflater inflater = getLayoutInflater();
        View layout= inflater.inflate(R.layout.ut_fenli_setting,(ViewGroup) findViewById(R.id.rela));

        BingoDialog dialog=new BingoDialog(this,layout);

        final RadioGroup fenli_switch=(RadioGroup)layout.findViewById(R.id.radio_group);
        final RadioButton btn_on=(RadioButton)layout.findViewById(R.id.on);
        final RadioButton btn_off=(RadioButton)layout.findViewById(R.id.off);
        final LinearLayout fenliHoldTime_line=(LinearLayout) layout.findViewById(R.id.fenliHoldTime_line);
//        if(energyEntity.getShunt()==1){
//            fenli_switch.setChecked(true);
//            fenliHoldTime_line.setVisibility(View.VISIBLE);
//        }else {
//            fenli_switch.setChecked(false);
//            fenliHoldTime_line.setVisibility(View.GONE);
//        }

        fenli_switch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.on:
                        fenliHoldTime_line.setVisibility(View.VISIBLE);
                        break;
                    case R.id.off:
                        fenliHoldTime_line.setVisibility(View.GONE);
                        break;
                }
            }
        });
//        fenli_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    fenliHoldTime_line.setVisibility(View.VISIBLE);
//                }else{
//                    fenliHoldTime_line.setVisibility(View.GONE);
//                }
//            }
//        });

        final EditText fenliHoldTime_value=(EditText)layout.findViewById(R.id.fenliHoldTime_value);

        Button commit=(Button)layout.findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="";
                try{

                    int b=0;
                    if(btn_on.isChecked()&&fenliHoldTime_value.getText().length()>0){
                        b= b+Integer.parseInt(fenliHoldTime_value.getText().toString());
                    }else if(btn_off.isChecked()&&fenliHoldTime_value.getText().length()==0){
                        b= b+31;
                    }

                    url= ConstantValues.SERVER_IP_NEW+"Telegraphy_Uool_control?repeaterMac="+repeatMac
                            +"&deviceType="+devType+"&devCmd=31&imei="+electricMac
                            +"&ShuntRelevance="+b;


                }catch(Exception e){
                    e.printStackTrace();
                    T.showShort(mContext,"??????????????????????????????");
                    return;
                }
                final ProgressDialog dialog1 = new ProgressDialog(mContext);
                dialog1.setTitle("??????");
                dialog1.setMessage("?????????????????????");
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
                VolleyHelper helper=VolleyHelper.getInstance(mContext);
                RequestQueue mQueue = helper.getRequestQueue();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int errorCode=response.getInt("errorCode");
                                    if(errorCode==0){
                                        T.showShort(mContext,"????????????");
                                        electricPresenter.getOneElectricInfo(userID,privilege+"",devType+"",electricMac,false);
                                    }else{
                                        T.showShort(mContext,"????????????");
                                    }
                                    getYuzhi(electricMac);
                                    getFenli(electricMac);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog1.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        T.showShort(mContext,"????????????");
                        dialog1.dismiss();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mQueue.add(jsonObjectRequest);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void gotoSethearttime(){
        LayoutInflater inflater = getLayoutInflater();
        View layout= inflater.inflate(R.layout.ut_hearttime_settting,(ViewGroup) findViewById(R.id.rela));

        BingoDialog dialog=new BingoDialog(this,layout);

        final EditText fenliHoldTime_value=(EditText)layout.findViewById(R.id.heartime_set);


        Button commit=(Button)layout.findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="";
                try{
                    int hearttime=Integer.parseInt(fenliHoldTime_value.getText().toString());
                    url= ConstantValues.SERVER_IP_NEW+"Telegraphy_Uool_control?repeaterMac="+repeatMac
                            +"&deviceType="+devType+"&devCmd=31&imei="+electricMac
                            +"&hearTime="+hearttime;
                }catch(Exception e){
                    e.printStackTrace();
                    T.showShort(mContext,"??????????????????????????????");
                    return;
                }
                final ProgressDialog dialog1 = new ProgressDialog(mContext);
                dialog1.setTitle("??????");
                dialog1.setMessage("?????????????????????");
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
                VolleyHelper helper=VolleyHelper.getInstance(mContext);
                RequestQueue mQueue = helper.getRequestQueue();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int errorCode=response.getInt("errorCode");
                                    if(errorCode==0){
                                        T.showShort(mContext,"????????????");
                                        electricPresenter.getOneElectricInfo(userID,privilege+"",devType+"",electricMac,false);
                                    }else{
                                        T.showShort(mContext,"????????????");
                                    }
                                    getYuzhi(electricMac);
                                    getFenli(electricMac);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog1.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        T.showShort(mContext,"????????????");
                        dialog1.dismiss();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mQueue.add(jsonObjectRequest);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void gotoClearvoice() {
        final ProgressDialog dialog1 = new ProgressDialog(mContext);
        dialog1.setTitle("??????");
        dialog1.setMessage("?????????????????????");
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url= ConstantValues.SERVER_IP_NEW+"Telegraphy_Uool_control?repeaterMac="+repeatMac
                +"&deviceType="+devType+"&devCmd=32&imei="+electricMac;
        VolleyHelper helper=VolleyHelper.getInstance(mContext);
        RequestQueue mQueue = helper.getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int errorCode=response.getInt("errorCode");
                            if(errorCode==0){
                                T.showShort(mContext,"????????????");
                                electricPresenter.getOneElectricInfo(userID,privilege+"",devType+"",electricMac,false);
                            }else{
                                T.showShort(mContext,"????????????");
                            }
                            dialog1.dismiss();
                            getYuzhi(electricMac);
                            getFenli(electricMac);
                        } catch (JSONException e) {
                            dialog1.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                T.showShort(mContext,"????????????");
                dialog1.dismiss();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(jsonObjectRequest);
    }

    private void gotoResetAlarm() {
        final ProgressDialog dialog1 = new ProgressDialog(mContext);
        dialog1.setTitle("??????");
        dialog1.setMessage("?????????????????????");
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url= ConstantValues.SERVER_IP_NEW+"Telegraphy_Uool_control?repeaterMac="+repeatMac
                +"&deviceType="+devType+"&devCmd=33&imei="+electricMac;
        VolleyHelper helper=VolleyHelper.getInstance(mContext);
        RequestQueue mQueue = helper.getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int errorCode=response.getInt("errorCode");
                            if(errorCode==0){
                                T.showShort(mContext,"????????????");
                                electricPresenter.getOneElectricInfo(userID,privilege+"",devType+"",electricMac,false);
                            }else{
                                T.showShort(mContext,"????????????");
                            }
                            dialog1.dismiss();
                            getYuzhi(electricMac);
                            getFenli(electricMac);
                        } catch (JSONException e) {
                            dialog1.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                T.showShort(mContext,"????????????");
                dialog1.dismiss();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(jsonObjectRequest);
    }

    private void gotoYuzhiSet() {
        LayoutInflater inflater;
        View layout;
        inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.electr_threshold_setting,(ViewGroup) findViewById(R.id.rela));
        BingoDialog dialog=new BingoDialog(this,layout);
        final EditText high_value=(EditText)layout.findViewById(R.id.high_value);
        high_value.setText(yuzhi43);
        final EditText low_value=(EditText)layout.findViewById(R.id.low_value);
        low_value.setText(yuzhi44);
        final EditText overcurrentvalue=(EditText)layout.findViewById(R.id.overcurrentvalue);
        overcurrentvalue.setText(yuzhi45);
        final EditText Leakage_value=(EditText)layout.findViewById(R.id.Leakage_value);
        Leakage_value.setText(yuzhi46);
        TextView high_value_yuzhi_text=(TextView)layout.findViewById(R.id.high_value_yuzhi);
        TextView low_value_yuzhi_text=(TextView)layout.findViewById(R.id.low_value_yuzhi);
        TextView overcurrentvalue_yuzhi_text=(TextView)layout.findViewById(R.id.overcurrentvalue_yuzhi);
        TextView Leakage_value_yuzhi_text=(TextView)layout.findViewById(R.id.Leakage_value_yuzhi);
        if(devType==75||devType==77){
            high_value_yuzhi_text.setText("???230-320V???");
            low_value_yuzhi_text.setText("???100-200V???");
            overcurrentvalue_yuzhi_text.setText("???4-250A???");
            Leakage_value_yuzhi_text.setText("???30-1000mA???");
        }
        Button commit=(Button)(Button)layout.findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="";
                try{
                    int high=(int)Float.parseFloat(high_value.getText().toString());
                    int low=(int)Float.parseFloat(low_value.getText().toString());
                    float value45=Float.parseFloat(overcurrentvalue.getText().toString());
                    int value46=(int)Float.parseFloat(Leakage_value.getText().toString());
                    if(devType==75||devType==77){
                        if(low<100||low>200){
                            T.showShort(mContext,"???????????????????????????100-200V");
                            return;
                        }
                        if(high<230||high>320){
                            T.showShort(mContext,"???????????????????????????230-320V");
                            return;
                        }
                        if(value45<4||value45>250){
                            T.showShort(mContext,"???????????????????????????4-250A");
                            return;
                        }
                        if(value46<30||value46>1000){
                            T.showShort(mContext,"??????????????????????????????30-1000mA");
                            return;
                        }
                        if(low>high){
                            T.showShort(mContext,"????????????????????????????????????");
                            return;
                        }
                    }else{
                        if(low<145||low>220){
                            T.showShort(mContext,"???????????????????????????145-220V");
                            return;
                        }
                        if(high<220||high>280){
                            T.showShort(mContext,"???????????????????????????220-280V");
                            return;
                        }
                        if(value45<1||value45>63){
                            T.showShort(mContext,"???????????????????????????1-63A");
                            return;
                        }
                        if(value46<10||value46>90){
                            T.showShort(mContext,"??????????????????????????????10-90mA");
                            return;
                        }
                        if(low>high){
                            T.showShort(mContext,"????????????????????????????????????");
                            return;
                        }
                    }

                    if(devType==52){
                        url= ConstantValues.SERVER_IP_NEW+"ackControlCvls?Overvoltage="+high_value.getText().toString()
                                +"&Undervoltage="+low_value.getText().toString()
                                +"&Overcurrent="+value45
                                +"&Leakage="+value46
                                +"&repeaterMac="+repeatMac+"&smokeMac="+electricMac+"&userId="+userID;
                    }else if(devType==53){
                        url= ConstantValues.SERVER_IP_NEW+"EasyIot_Uool_control?Overvoltage="+high_value.getText().toString()
                                +"&Undervoltage="+low_value.getText().toString()
                                +"&Overcurrent="+value45
                                +"&Leakage="+value46
                                +"&appId=1&devSerial="+electricMac+"&userId="+userID;
                    }else if(devType==75||devType==77){
                        url= ConstantValues.SERVER_IP_NEW+"Telegraphy_Uool_control?Overvoltage="+high_value.getText().toString()
                                +"&Undervoltage="+low_value.getText().toString()
                                +"&Overcurrent="+value45
                                +"&Leakage="+value46
                                +"&deviceType="+devType+"&devCmd=14&imei="+electricMac;
                    }else{
                        Toast.makeText(getApplicationContext(),"??????????????????????????????", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                                            Toast.makeText(getApplicationContext(),"?????????????????????", Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    e.printStackTrace();
                    T.showShort(mContext,"??????????????????????????????");
                    return;
                }
                final ProgressDialog dialog1 = new ProgressDialog(mContext);
                dialog1.setTitle("??????");
                dialog1.setMessage("?????????????????????");
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
                VolleyHelper helper=VolleyHelper.getInstance(mContext);
                RequestQueue mQueue = helper.getRequestQueue();
//                            RequestQueue mQueue = Volley.newRequestQueue(context);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int errorCode=response.getInt("errorCode");
                                    if(errorCode==0){
                                        T.showShort(mContext,"????????????");
                                        electricPresenter.getOneElectricInfo(userID,privilege+"",devType+"",electricMac,false);
                                    }else{
                                        T.showShort(mContext,"????????????");
                                    }
                                    getYuzhi(electricMac);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog1.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        T.showShort(mContext,"????????????");
                        dialog1.dismiss();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mQueue.add(jsonObjectRequest);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void gotoRace() {
        LayoutInflater inflater = getLayoutInflater();
        View layout= inflater.inflate(R.layout.electr_fenli_race,(ViewGroup) findViewById(R.id.rela));

        BingoDialog dialog=new BingoDialog(this,layout);

        final TextView ActivePowerA=(TextView)layout.findViewById(R.id.ActivePowerA);
        final TextView ActivePowerB=(TextView)layout.findViewById(R.id.ActivePowerB);
        final TextView ActivePowerC=(TextView)layout.findViewById(R.id.ActivePowerC);

        ActivePowerA.setText(energyEntity.getActivePowerA());
        ActivePowerB.setText(energyEntity.getActivePowerB());
        ActivePowerC.setText(energyEntity.getActivePowerC());

        final TextView ReactivePowerA=(TextView)layout.findViewById(R.id.ReactivePowerA);
        final TextView ReactivePowerB=(TextView)layout.findViewById(R.id.ReactivePowerB);
        final TextView ReactivePowerC=(TextView)layout.findViewById(R.id.ReactivePowerC);

        ReactivePowerA.setText(energyEntity.getReactivePowerA());
        ReactivePowerB.setText(energyEntity.getReactivePowerB());
        ReactivePowerC.setText(energyEntity.getReactivePowerC());

        final TextView ApparentPowerA=(TextView)layout.findViewById(R.id.ApparentPowerA);
        final TextView ApparentPowerB=(TextView)layout.findViewById(R.id.ApparentPowerB);
        final TextView ApparentPowerC=(TextView)layout.findViewById(R.id.ApparentPowerC);

        ApparentPowerA.setText(energyEntity.getApparentPowerA());
        ApparentPowerB.setText(energyEntity.getApparentPowerB());
        ApparentPowerC.setText(energyEntity.getApparentPowerC());

        final TextView PowerFactorA=(TextView)layout.findViewById(R.id.PowerFactorA);
        final TextView PowerFactorB=(TextView)layout.findViewById(R.id.PowerFactorB);
        final TextView PowerFactorC=(TextView)layout.findViewById(R.id.PowerFactorC);

        PowerFactorA.setText(energyEntity.getPowerFactorA());
        PowerFactorB.setText(energyEntity.getPowerFactorB());
        PowerFactorC.setText(energyEntity.getPowerFactorC());

        final TextView ActiveEnergyA=(TextView)layout.findViewById(R.id.ActiveEnergyA);
        final TextView ActiveEnergyB=(TextView)layout.findViewById(R.id.ActiveEnergyB);
        final TextView ActiveEnergyC=(TextView)layout.findViewById(R.id.ActiveEnergyC);

        ActiveEnergyA.setText(energyEntity.getActiveEnergyA());
        ActiveEnergyB.setText(energyEntity.getActiveEnergyB());
        ActiveEnergyC.setText(energyEntity.getActiveEnergyC());

        final TextView ReactiveEnergyA=(TextView)layout.findViewById(R.id.ReactiveEnergyA);
        final TextView ReactiveEnergyB=(TextView)layout.findViewById(R.id.ReactiveEnergyB);
        final TextView ReactiveEnergyC=(TextView)layout.findViewById(R.id.ReactiveEnergyC);

        ReactiveEnergyA.setText(energyEntity.getReactiveEnergyA());
        ReactiveEnergyB.setText(energyEntity.getReactiveEnergyB());
        ReactiveEnergyC.setText(energyEntity.getReactiveEnergyC());

        final TextView ApparentEnergyA=(TextView)layout.findViewById(R.id.ApparentEnergyA);
        final TextView ApparentEnergyB=(TextView)layout.findViewById(R.id.ApparentEnergyB);
        final TextView ApparentEnergyC=(TextView)layout.findViewById(R.id.ApparentEnergyC);

        ApparentEnergyA.setText(energyEntity.getApparentEnergyA());
        ApparentEnergyB.setText(energyEntity.getApparentEnergyB());
        ApparentEnergyC.setText(energyEntity.getApparentEnergyC());

        dialog.show();
    }

}



