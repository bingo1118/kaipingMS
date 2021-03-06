package com.smart.cloud.fire.mvp.LineChart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ProofGasEntity;
import com.smart.cloud.fire.global.TemperatureTime;
import com.smart.cloud.fire.mvp.electricChangeHistory.ElectricChangeHistoryActivity;
import com.smart.cloud.fire.utils.BingoDialog;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.Utils;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;

/**
 * Created by Administrator on 2016/11/1.
 */
public class LineChartActivity extends MvpActivity<LineChartPresenter> implements LineChartView {
    /*=========== ???????????? ==========*/
    @Bind(R.id.lvc_main)
    lecho.lib.hellocharts.view.LineChartView mLineChartView;//??????????????????
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.btn_next)
    ImageView btnNext;
    @Bind(R.id.btn_before)
    ImageView btnBefore;
    @Bind(R.id.title_tv)
    TextView titleTv;
    @Bind(R.id.water_threshold)
    TextView water_threshold;//@@2018.01.03??????????????????
    @Bind(R.id.btn_new)
    ImageView btnNew;
    @Bind(R.id.yuzhi_line)
    LinearLayout yuzhi_line;
    @Bind(R.id.high_value)
    TextView high_value;
    @Bind(R.id.low_value)
    TextView low_value;
    @Bind(R.id.more)
    TextView more;//@@??????
    private LineChartPresenter lineChartPresenter;

    /*=========== ???????????? ==========*/
    private LineChartData mLineData;                    //????????????
    private int numberOfLines = 1;                      //????????????/?????????????????????
    private int maxNumberOfLines = 4;                   //????????????/?????????????????????
    private int numberOfPoints = 8;                    //??????????????????

    /*=========== ???????????? ==========*/
    private boolean isHasAxes = true;                   //?????????????????????
    private boolean isHasAxesNames = true;              //???????????????????????????
    private boolean isHasLines = true;                  //??????????????????/??????
    private boolean isHasPoints = true;                 //???????????????????????????
    private boolean isFilled = true;                   //???????????????????????????
    private boolean isHasPointsLabels = false;          //????????????????????????????????????
    private boolean isCubic = false;                    //??????????????????
    private boolean isPointsHasSelected = false;        //???????????????????????????(??????/????????????)
    private boolean isPointsHaveDifferentColor;         //??????????????????????????????

    /*=========== ???????????? ==========*/
    private ValueShape pointsShape = ValueShape.CIRCLE; //????????????(???/???/??????)
    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints]; //????????????????????????????????????
    private Context context;
    private String userID;
    private int privilege;
    private String electricMac;
    private String electricType;
    private String electricNum;
    private int page = 1;
    private List<TemperatureTime.ElectricBean> electricBeen;
    private boolean haveDataed = true;
    private Map<Integer, String> data = new HashMap<>();

    private String isWater=null;//@@12.15
    private int devType;

    String threshold_h;
    String threshold_l;
    String getdatatime;
    String uploaddatatime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        ButterKnife.bind(this);
        context = this;
        userID = SharedPreferencesManager.getInstance().getData(context,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        electricMac = getIntent().getExtras().getString("electricMac");
        electricType = getIntent().getExtras().getInt("electricType") + "";
        electricNum = getIntent().getExtras().getInt("electricNum") + "";
        isWater=getIntent().getExtras().getString("isWater");//@@12.15
        devType=getIntent().getExtras().getInt("devType");
        electricBeen = new ArrayList<>();
        if(isWater==null){
            mvpPresenter.getElectricTypeInfo(userID, privilege + "", electricMac, electricType, electricNum, page + "", false,devType);
        }else if(isWater.equals("chuangan")){
            mvpPresenter.getChuanganInfo(userID, privilege + "", electricMac, electricNum, page + "", false);
        }else if(isWater.equals("tem")){
            mvpPresenter.getTHDevInfoHistoryInfo(electricMac,page+"", "1",false);
        }else if(isWater.equals("hum")){
            mvpPresenter.getTHDevInfoHistoryInfo(electricMac,page+"", "2",false);
        }else if(isWater.equals("gas")){
            mvpPresenter.getGasHistoryInfo(userID, privilege + "",electricMac,page+"",false);
        }else{
            if(isWater.equals("19")||isWater.equals("124")||electricMac.length()>10){
                water_threshold.setVisibility(View.VISIBLE);//@@2018.01.03
                yuzhi_line.setVisibility(View.VISIBLE);
                getYuzhi();
            }else if(isWater.equals("10")){
                water_threshold.setVisibility(View.VISIBLE);//@@2018.01.03
                yuzhi_line.setVisibility(View.VISIBLE);
                getYuzhi();
            }else if(isWater.equals("3")){
                more.setVisibility(View.VISIBLE);
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopupMenu(v);
                    }
                });
                yuzhi_line.setVisibility(View.VISIBLE);
                getYuzhi();
            }
            mvpPresenter.getWaterHistoryInfo(userID, privilege + "", electricMac, page + "", false);
        }
        initView();
        initListener();
    }

    private void showPopupMenu(View view) {
        // View??????PopupMenu???????????????View?????????
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu??????
        popupMenu.getMenuInflater().inflate(R.menu.menu_water, popupMenu.getMenu());
        // menu???item????????????
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.yuzhi_set:
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.water_threshold_setting,(ViewGroup) findViewById(R.id.rela));
                        BingoDialog dialog=new BingoDialog(mActivity,layout);
                        final EditText high_value=(EditText)layout.findViewById(R.id.high_value);
                        final EditText low_value=(EditText)layout.findViewById(R.id.low_value);
                        Button commit=(Button)layout.findViewById(R.id.commit);
                        commit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url="";
                                try{
                                    float high=Float.parseFloat(high_value.getText().toString());
                                    float low=Float.parseFloat(low_value.getText().toString());
                                    if(low>high){
                                        T.showShort(context,"??????????????????????????????");
                                        return;
                                    }
                                    url= ConstantValues.SERVER_IP_NEW+"reSetAlarmNum?mac="+electricMac+"&threshold207="+low+"&threshold208="+high;
                                }catch(Exception e){
                                    e.printStackTrace();
                                    T.showShort(context,"??????????????????????????????");
                                    return;
                                }
                                VolleyHelper helper=VolleyHelper.getInstance(context);
                                RequestQueue mQueue = helper.getRequestQueue();
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    int errorCode=response.getInt("errorCode");
//                                                            if(errorCode==0){
//                                                                T.showShort(context,"????????????");
//                                                                getYuzhi();
//                                                            }else{
//                                                                T.showShort(context,"????????????");
//                                                            }
                                                    T.showShort(context,response.getString("error"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        T.showShort(context,"????????????");
                                    }
                                });
                                mQueue.add(jsonObjectRequest);
                                dialog.dismiss();
                            }
                        });
                        TextView title=(TextView)layout.findViewById(R.id.title_text);
                        TextView high_value_name=(TextView)layout.findViewById(R.id.high_value_name);
                        TextView low_value_name=(TextView)layout.findViewById(R.id.low_value_name);
                        if(isWater.equals("1")||isWater.equals("3")||isWater.equals("10")){
                            title.setText("??????????????????");
                            high_value_name.setText("??????????????????kpa???:");
                            low_value_name.setText("??????????????????kpa???:");
                        }else{
                            title.setText("??????????????????");
                            high_value_name.setText("??????????????????m???:");
                            low_value_name.setText("??????????????????m???:");
                        }
                        dialog.show();
                        break;
                    case R.id.bodongyuzhi_set:
                        LayoutInflater inflater1 = getLayoutInflater();
                        View layout1 = inflater1.inflate(R.layout.water_threshold_setting,(ViewGroup) findViewById(R.id.rela));
                        AlertDialog.Builder builder1=new AlertDialog.Builder(context).setView(layout1);
                        final AlertDialog dialog1=builder1.create();
                        final EditText high_value1=(EditText)layout1.findViewById(R.id.high_value);
                        final EditText low_value1=(EditText)layout1.findViewById(R.id.low_value);
                        TextView title1=(TextView)layout1.findViewById(R.id.title_text);
                        TextView high_value_name1=(TextView)layout1.findViewById(R.id.high_value_name);
                        TextView low_value_name1=(TextView)layout1.findViewById(R.id.low_value_name);
                        Button commit1=(Button)layout1.findViewById(R.id.commit);
                        commit1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url="";
                                try{
                                    int high=(int)Float.parseFloat(high_value1.getText().toString());
                                    int low=(int)Float.parseFloat(low_value1.getText().toString());
                                    url= ConstantValues.SERVER_IP_NEW+"set_water_wave_Control?smokeMac="+electricMac+"&waveValue="+high+"&waveTime="+low;
                                }catch(Exception e){
                                    e.printStackTrace();
                                    T.showShort(context,"??????????????????????????????");
                                    return;
                                }
                                VolleyHelper helper=VolleyHelper.getInstance(context);
                                RequestQueue mQueue = helper.getRequestQueue();
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    T.showShort(context,response.getString("error"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        T.showShort(context,"????????????");
                                    }
                                });
                                mQueue.add(jsonObjectRequest);
                                dialog1.dismiss();
                            }
                        });
                        title1.setText("??????????????????");
                        high_value_name1.setText("???????????????kpa???:");
                        low_value_name1.setText("?????????????????????min???:");
                        dialog1.show();
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

    private void initListener() {
        //????????????????????????
        mLineChartView.setOnValueTouchListener(new ValueTouchListener());
    }

    private void initView() {
        /**
         * ???????????????????????? ???????????????????????????????????????????????????????????????
         * ?????????ListView???????????????????????????notifyDataSetChanged()??????????????????setAdapter()
         */
        mLineChartView.setViewportCalculationEnabled(false);
        mLineChartView.setZoomEnabled(false);
    }

    private void getYuzhi() {
        VolleyHelper helper=VolleyHelper.getInstance(context);
        RequestQueue mQueue = helper.getRequestQueue();
//        RequestQueue mQueue = Volley.newRequestQueue(context);
        String url= ConstantValues.SERVER_IP_NEW+"getWaterAlarmThreshold?mac="+electricMac+"&deviceType="+devType;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int errorCode=response.getInt("errorCode");
                            if(errorCode==0){
                                threshold_h=response.has("value208")?response.getString("value208"):response.getString("threshold1");
                                threshold_l=response.has("value207")?response.getString("value207"):response.getString("threshold2");
                                try {
                                    getdatatime=response.getString("ackTimes");
                                    uploaddatatime=response.getString("waveValue");
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if(isWater.equals("1")||isWater.equals("3")||isWater.equals("10")){
                                    low_value.setText("??????????????????"+threshold_l+"kpa");
                                    high_value.setText("??????????????????"+threshold_h+"kpa");
                                }else{
                                    low_value.setText("??????????????????"+threshold_l+"m");
                                    high_value.setText("??????????????????"+threshold_h+"m");
                                }
                            }else{
                                if(isWater.equals("1")||isWater.equals("3")){
                                    low_value.setText("???????????????:?????????");
                                    high_value.setText("???????????????:?????????");
                                }else{
                                    low_value.setText("???????????????:?????????");
                                    high_value.setText("???????????????:?????????");
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                T.showShort(context,"????????????");
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    /**
     * ????????????????????????????????????????????????
     */
    private void setPointsValues(List<TemperatureTime.ElectricBean> list) {
        data.clear();
        for (int i = 0; i < maxNumberOfLines; ++i) {
            for (int j = 0; j < (list.size()+1); ++j) {
                if (j > 0 && j < 7) {
                    String str = list.get(j - 1).getElectricValue();
                    if (electricType.equals("7")) {
                        data.put(j, str);
                    }
                    float f = new BigDecimal(str).floatValue();
                    randomNumbersTab[i][j] = (f);
                }
            }
        }
    }

    /**
     * ????????????????????????
     */
    private void setLinesDatas(List<TemperatureTime.ElectricBean> list) {
        List<Line> lines = new ArrayList<>();
        ArrayList<AxisValue> axisValuesX = new ArrayList<>();
        //?????????????????????????????????????????????
        for (int i = 0; i < numberOfLines; ++i) {
            //????????????
            List<PointValue> values = new ArrayList<>();
            for (int j = 0; j < (list.size()+1); ++j) {
                if (j > 0 && j < 7) {
                    values.add(new PointValue(j, randomNumbersTab[i][j]));
                    axisValuesX.add(new AxisValue(j).setLabel(getTime(list.get(j-1).getElectricTime())));
                }
            }

            Line line = new Line(values);               //???????????????????????????
            line.setColor(ChartUtils.COLORS[i]);        //??????????????????
            line.setShape(pointsShape);                 //??????????????????
            line.setHasLines(isHasLines);               //?????????????????????
            line.setHasPoints(isHasPoints);             //????????????????????????
            line.setCubic(isCubic);                     //????????????????????????????????????
            line.setFilled(isFilled);                   //?????????????????????????????????
            line.setHasLabels(isHasPointsLabels);       //??????????????????????????????
            //???????????????????????????
            line.setHasLabelsOnlyForSelected(isPointsHasSelected);
            //????????????????????????????????? ?????????????????????
            if (isPointsHaveDifferentColor) {
                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);
        }

        Axis axisX = new Axis().setHasLines(true);                    //X???
        Axis axisY = new Axis().setHasLines(true);  //Y???          //????????????
        switch (electricType) {
            case "6":
                axisY.setName("?????????(V)");
                titleTv.setText("???????????????");
                break;
            case "7":
                axisY.setName("?????????(A)");
                titleTv.setText("???????????????");
                break;
            case "8":
                axisY.setName("?????????(mA)");
                titleTv.setText("???????????????");
                break;
            case "9":
                axisY.setName("?????????(???)");
                titleTv.setText("???????????????");
                break;
            default:
                if(isWater!=null){
                    if(isWater.equals("1")||isWater.equals("3")||isWater.equals("10")){
                        axisY.setName("?????????(kPa)");
                        titleTv.setText("????????????????????????");
                    }else if(isWater.equals("chuangan")){
                        axisY.setName("?????????");
                        titleTv.setText("????????????????????????");
                    }else if(isWater.equals("tem")){
                        axisY.setName("?????????(???)");
                        titleTv.setText("?????????????????????");
                    }else if(isWater.equals("hum")){
                        axisY.setName("????????????%???");
                        titleTv.setText("?????????????????????");
                    }else if(isWater.equals("gas")){
                        axisY.setName("?????????");
                        titleTv.setText("????????????????????????");
                    }else{
                        axisY.setName("?????????(m)");
                        titleTv.setText("????????????????????????");
                    }
                }
                break;
        }
        axisX.setTextColor(Color.GRAY);//X?????????
        axisX.setMaxLabelChars(3);
        axisX.setValues(axisValuesX);
        axisX.setHasTiltedLabels(true);//X?????????????????????????????????????????????true???????????????
        axisX.setTextSize(10);
        axisX.setInside(true);
        axisY.setTextColor(Color.GRAY);

        mLineData = new LineChartData(lines);                      //????????????????????????????????????
        mLineData.setBaseValue(Float.NaN);
        mLineData.setAxisXBottom(axisX);            //??????X????????? ??????
        mLineData.setAxisYLeft(axisY);
        mLineData.setValueLabelBackgroundColor(Color.BLUE);     //????????????????????????
        mLineData.setValueLabelTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        //???????????????(?????????????????????)
        /* ??????????????????????????? ?????????????????????
         * mLineData.setValueLabelBackgroundAuto(true);            //??????????????????????????????????????????
         * mLineData.setValueLabelBackgroundColor(Color.BLUE);     //????????????????????????
         * mLineData.setValueLabelBackgroundEnabled(true);         //???????????????????????????
         * mLineData.setValueLabelsTextColor(Color.RED);           //????????????????????????
         * mLineData.setValueLabelTextSize(15);                    //????????????????????????
         * mLineData.setValueLabelTypeface(Typeface.MONOSPACE);    //????????????????????????
        */

        mLineChartView.setLineChartData(mLineData);    //??????????????????
    }

//    private String getTime(String str) {
//        String[] strings = str.split(" ");
//        return strings[1];
//    }

    private String getTime(String str) {
        if(str.length()<5){
            return "";
        }else{
            String strings = str.substring(5, str.length());
            return strings;
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void resetViewport(List<TemperatureTime.ElectricBean> tem) {
        //????????????????????????,??????????????????????????????
        float value=0;
        if(tem!=null&&tem.size()>0){
            value = Utils.getMaxFloat(tem)*1.5f;
        }
        final Viewport v = new Viewport(mLineChartView.getMaximumViewport());
        v.left = 0;                             //?????????????????????
        v.bottom = 0;
        v.top = value;
        if(value==0){
            switch (electricType) {
                case "6":
                    v.top = 400;
                    break;
                case "7":
                    v.top = 50;
                    break;
                case "8":
                    v.top = 700;
                    break;
                case "9":
                    v.top = 80;
                    break;
                default:
                    if(isWater!=null){
                        if(isWater.equals("1")){
                            v.top=10;
                        }else{
                            v.top=10;
                        }
                    }else{
                        v.top=100;
                    }
                    break;
            }
        }

        //????????????100
        v.right = numberOfPoints - 1;           //???????????? ?????????0?????? ?????????1 ?????? -1
        mLineChartView.setMaximumViewport(v);   //???????????????????????? ???????????????
        mLineChartView.setCurrentViewport(v);   //???????????????????????? ???????????????????????????
    }

    @Override
    public void getDataSuccess(List<TemperatureTime.ElectricBean> temperatureTimes) {
        int len = temperatureTimes.size();
        if (len == 6) {
            btnNext.setClickable(true);
            btnNext.setBackgroundResource(R.drawable.next_selector);
            electricBeen.clear();
            electricBeen.addAll(temperatureTimes);
        } else if (len < 6&&electricBeen.size()>0) {
            btnNext.setClickable(false);
            btnNext.setBackgroundResource(R.mipmap.next_an);
            for (int i = 0; i < len; i++) {
                electricBeen.remove(0);
                TemperatureTime.ElectricBean tElectricBean = temperatureTimes.get(i);
                electricBeen.add(tElectricBean);
            }
        }else if(len>0&&len < 6&&electricBeen.size()==0){
            btnNext.setClickable(false);
            btnNext.setBackgroundResource(R.mipmap.next_an);
            electricBeen.clear();
            electricBeen.addAll(temperatureTimes);
        }else{
            T.showShort(mActivity,"?????????");
        }
        setPointsValues(electricBeen);
        setLinesDatas(electricBeen);
        resetViewport(electricBeen);
    }

    @Override
    public void getDataFail(String msg) {
//        page= page-1;
        btnNext.setClickable(false);
        btnNext.setBackgroundResource(R.mipmap.next_an);
        T.showShort(context, msg);
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }


    /**
     * ??????????????????
     */
    private class ValueTouchListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            switch (electricType) {
                case "6":
                    Toast.makeText(LineChartActivity.this, "????????????: " + value.getY() + "V", Toast.LENGTH_SHORT).show();
                    break;
                case "7":
                    int i = (int) value.getX();
                    String str = data.get(i);
                    Toast.makeText(LineChartActivity.this, "????????????: " + str + "A", Toast.LENGTH_SHORT).show();
                    break;
                case "8":
                    Toast.makeText(LineChartActivity.this, "???????????????: " + value.getY() + "mA", Toast.LENGTH_SHORT).show();
                    break;
                case "9":
                    Toast.makeText(LineChartActivity.this, "????????????: " + value.getY() + "???", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    if(isWater!=null){
                        if(isWater.equals("1")||isWater.equals("3")||isWater.equals("10")){
                            Toast.makeText(LineChartActivity.this, "????????????: " + value.getY() + "kPa", Toast.LENGTH_SHORT).show();
                        }else if(isWater.equals("tem")){
                            Toast.makeText(LineChartActivity.this, "????????????: " + value.getY() + "???", Toast.LENGTH_SHORT).show();
                        }else if(isWater.equals("hum")){
                            Toast.makeText(LineChartActivity.this, "????????????: " + value.getY() + "%", Toast.LENGTH_SHORT).show();
                        }else if(isWater.equals("chuangan")){
                            Toast.makeText(LineChartActivity.this, "????????????: " + value.getY() + "%", Toast.LENGTH_SHORT).show();
                        }else if(isWater.equals("gas")){
                            Toast.makeText(LineChartActivity.this, "????????????: " + value.getY() , Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LineChartActivity.this, "????????????: " + value.getY() + "m", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }

        @Override
        public void onValueDeselected() {
        }
    }

    @Override
    protected LineChartPresenter createPresenter() {
        lineChartPresenter = new LineChartPresenter(this);
        return lineChartPresenter;
    }

    @OnClick({R.id.btn_next, R.id.btn_before,R.id.btn_new,R.id.water_threshold})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                page = page + 1;
                if (page == 2) {
                    btnBefore.setClickable(true);
                    btnBefore.setBackgroundResource(R.drawable.before_selector);
                }
                if(isWater==null){
                    mvpPresenter.getElectricTypeInfo(userID, privilege + "", electricMac, electricType, electricNum, page + "", false,devType);
                }else if(isWater.equals("chuangan")){
                    mvpPresenter.getChuanganInfo(userID, privilege + "", electricMac, electricNum, page + "", false);
                }else if(isWater.equals("tem")){
                    mvpPresenter.getTHDevInfoHistoryInfo(electricMac,page+"", "1",false);
                }else if(isWater.equals("hum")){
                    mvpPresenter.getTHDevInfoHistoryInfo(electricMac,page+"", "2",false);
                }else if(isWater.equals("gas")){
                    mvpPresenter.getGasHistoryInfo(userID, privilege + "",electricMac,page+"",false);
                }else{
                    mvpPresenter.getWaterHistoryInfo(userID, privilege + "", electricMac, page + "", false);
                }
                break;
            case R.id.btn_before:
                if (page > 1) {
                    page = page - 1;
                    if (page == 1) {
                        btnBefore.setClickable(false);
                        btnBefore.setBackgroundResource(R.mipmap.prve_an);
                    }
                    if(isWater==null){
                        mvpPresenter.getElectricTypeInfo(userID, privilege + "", electricMac, electricType, electricNum, page + "", false,devType);
                    }else if(isWater.equals("chuangan")){
                        mvpPresenter.getChuanganInfo(userID, privilege + "", electricMac, electricNum, page + "", false);
                    }else if(isWater.equals("tem")){
                        mvpPresenter.getTHDevInfoHistoryInfo(electricMac,page+"", "1",false);
                    }else if(isWater.equals("hum")){
                        mvpPresenter.getTHDevInfoHistoryInfo(electricMac,page+"", "2",false);
                    }else if(isWater.equals("gas")){
                        mvpPresenter.getGasHistoryInfo(userID, privilege + "",electricMac,page+"",false);
                    }else{
                        mvpPresenter.getWaterHistoryInfo(userID, privilege + "", electricMac, page + "", false);
                    }
                }
                break;
            case R.id.btn_new:
                page = 1;
                btnBefore.setClickable(false);
                btnBefore.setBackgroundResource(R.mipmap.prve_an);
                btnNext.setClickable(true);
                btnNext.setBackgroundResource(R.drawable.next_selector);
                if(isWater==null){
                    mvpPresenter.getElectricTypeInfo(userID, privilege + "", electricMac, electricType, electricNum, page + "", false,devType);
                }else if(isWater.equals("chuangan")){
                    mvpPresenter.getChuanganInfo(userID, privilege + "", electricMac, electricNum, page + "", false);
                }else if(isWater.equals("tem")){
                    mvpPresenter.getTHDevInfoHistoryInfo(electricMac,page+"", "1",false);
                }else if(isWater.equals("hum")){
                    mvpPresenter.getTHDevInfoHistoryInfo(electricMac,page+"", "2",false);
                }else if(isWater.equals("gas")){
                    mvpPresenter.getGasHistoryInfo(userID, privilege + "",electricMac,page+"",false);
                }else{
                    mvpPresenter.getWaterHistoryInfo(userID, privilege + "", electricMac, page + "", false);
                }
                break;
            case R.id.water_threshold:
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.water_threshold_setting,(ViewGroup) findViewById(R.id.rela));
                final EditText high_value=(EditText)layout.findViewById(R.id.high_value);
                final EditText low_value=(EditText)layout.findViewById(R.id.low_value);
                TextView title=(TextView)layout.findViewById(R.id.title_text);
                TextView high_value_name=(TextView)layout.findViewById(R.id.high_value_name);
                TextView low_value_name=(TextView)layout.findViewById(R.id.low_value_name);
                final EditText uploadtime_value=(EditText)layout.findViewById(R.id.uploadtime_value);
                final EditText getdatatime_value=(EditText)layout.findViewById(R.id.getdatatime_value);
                LinearLayout uploadtime_lin=(LinearLayout)layout.findViewById(R.id.uploadtime_lin);
                LinearLayout getdatatime_lin=(LinearLayout)layout.findViewById(R.id.getdatatime_lin);
                if(isWater.equals("1")||isWater.equals("3")||isWater.equals("10")){
                    if(devType==78||devType==47||devType==100){
                        uploadtime_lin.setVisibility(View.VISIBLE);
//                        getdatatime_lin.setVisibility(View.VISIBLE);
                        high_value.setText(threshold_h);
                        low_value.setText(threshold_l);
                        getdatatime_value.setText(getdatatime);
                        uploadtime_value.setText(uploaddatatime);
                    }
                    title.setText("??????????????????");
                    high_value_name.setText("??????????????????kpa???:");
                    low_value_name.setText("??????????????????kpa???:");
                }else{
                    if(devType==48){
                        uploadtime_lin.setVisibility(View.VISIBLE);
                        getdatatime_lin.setVisibility(View.VISIBLE);
                        high_value.setText(threshold_h);
                        low_value.setText(threshold_l);
                        getdatatime_value.setText(getdatatime);
                        uploadtime_value.setText(uploaddatatime);
                    }
                    title.setText("??????????????????");
                    high_value_name.setText("??????????????????m???:");
                    low_value_name.setText("??????????????????m???:");
                }
                BingoDialog dialog =new BingoDialog(this,layout);
                Button commit=(Button)layout.findViewById(R.id.commit);
                commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url="";
                        try{
                            float high=Float.parseFloat(high_value.getText().toString());
                            float low=Float.parseFloat(low_value.getText().toString());
                            float uploadtime=Float.parseFloat(uploadtime_value.getText().length()>0?uploadtime_value.getText().toString():"0");
                            float getdatatime=Float.parseFloat(getdatatime_value.getText().length()>0?getdatatime_value.getText().toString():"0");
                            if(low>high){
                                T.showShort(context,"??????????????????????????????????????????");
                                return;
                            }
                            if(devType==78||devType==85||devType==97||devType==98){
                                url= ConstantValues.SERVER_IP_NEW+"nanjing_set_water_data?imeiValue="+electricMac+"&deviceType="+devType
                                        +"&hight_set="+high+"&low_set="+low+"&send_time="+uploadtime+"&collect_time="+getdatatime;
                            }else if(devType==47||devType==48){
                                url= ConstantValues.SERVER_IP_NEW+"set_water_level_Control?smokeMac="+electricMac+"&deviceType="+devType
                                        +"&hvalue="+high+"&lvalue="+low+"&waveValue="+uploadtime+"&waveTime="+getdatatime;
                            }else if(devType==100||devType==101){
                                url= ConstantValues.SERVER_IP_NEW+"nanjing_set_water_data?imeiValue="+electricMac+"&deviceType="+devType
                                        +"&hight_set="+high+"&low_set="+low+"&send_time="+uploadtime+"&collect_time="+getdatatime+"&lowpow_set=0";
                            }else{
                                url= ConstantValues.SERVER_IP_NEW+"reSetAlarmNum?mac="+electricMac+"&threshold207="+low+"&threshold208="+high;
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            T.showShort(context,"??????????????????????????????");
                            return;
                        }
                        VolleyHelper helper=VolleyHelper.getInstance(context);
                        RequestQueue mQueue = helper.getRequestQueue();
//                            RequestQueue mQueue = Volley.newRequestQueue(context);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            int errorCode=response.getInt("errorCode");
                                            if(errorCode==0){
                                                getYuzhi();
                                            }
                                            T.showShort(context,response.getString("error"));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                T.showShort(context,"????????????");
                            }
                        });
                        mQueue.add(jsonObjectRequest);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
    }
}
