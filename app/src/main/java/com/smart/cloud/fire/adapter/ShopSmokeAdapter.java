package com.smart.cloud.fire.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.smart.cloud.fire.activity.AllSmoke.AllSmokePresenter;
import com.smart.cloud.fire.activity.GasDevice.OneGasInfoActivity;
import com.smart.cloud.fire.activity.NFCDev.NFCImageShowActivity;
import com.smart.cloud.fire.activity.THDevice.OneTHDevInfoActivity;
import com.smart.cloud.fire.base.presenter.BasePresenter;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.mvp.ChuangAn.ChuangAnActivity;
import com.smart.cloud.fire.mvp.LineChart.LineChartActivity;
import com.smart.cloud.fire.mvp.electric.ElectricActivity;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpError;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Smoke;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.Security.AirInfoActivity;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.Security.NewAirInfoActivity;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.ShopInfoFragmentPresenter;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.WiredDevFragment.WiredSmokeListActivity;
import com.smart.cloud.fire.retrofit.ApiStores;
import com.smart.cloud.fire.retrofit.AppClient;
import com.smart.cloud.fire.ui.CallManagerDialogActivity;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;
import retrofit2.Call;
import retrofit2.Callback;

public class ShopSmokeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnLongClickListener, View.OnClickListener{

    public static final int PULLUP_LOAD_MORE = 0;//??????????????????
    public static final int LOADING_MORE = 1;//???????????????
    public static final int NO_MORE_DATA = 2;//???????????????
    public static final int NO_DATA = 3;//?????????
    private static final int TYPE_ITEM = 0;  //??????Item View
    private static final int TYPE_FOOTER = 1;  //??????FootView
    private int load_more_status = 0;
    private LayoutInflater mInflater;
    private Context mContext;
    private List<Smoke> listNormalSmoke;


    public interface OnLongClickListener {
        void onLongClick(View view, int position);
    }
    public interface OnClickListener{
        void onClick(View view, int position);
    }

    private OnLongClickListener mOnLongClickListener = null;
    private OnClickListener mOnClickListener=null;

    public void setOnLongClickListener(OnLongClickListener listener) {
        mOnLongClickListener = listener;
    }

    public void setOnClickListener(OnClickListener listener){
        mOnClickListener=listener;
    }

    @Override
    public boolean onLongClick(View view) {
        if (null != mOnLongClickListener) {
            mOnLongClickListener.onLongClick(view, (int) view.getTag());
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if(null!=mOnClickListener){
            mOnClickListener.onClick(v,(int)v.getTag());
        }
    }

    public ShopSmokeAdapter(Context mContext, List<Smoke> listNormalSmoke) {
        this.mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.listNormalSmoke = listNormalSmoke;
        this.mContext = mContext;
    }
    /**
     * item????????????
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //???????????????????????????????????????????????????View
        if (viewType == TYPE_ITEM) {
            final View view = mInflater.inflate(R.layout.shop_info_adapter, parent, false);
            //????????????????????????????????????????????????????????????
            ItemViewHolder viewHolder = new ItemViewHolder(view);
            view.setOnLongClickListener(this);//@@
            return viewHolder;
        } else if (viewType == TYPE_FOOTER) {
            View foot_view = mInflater.inflate(R.layout.recycler_load_more_layout, parent, false);
            //????????????????????????????????????????????????????????????
            FootViewHolder footViewHolder = new FootViewHolder(foot_view);
            return footViewHolder;
        }
        return null;
    }

    /**
     * ?????????????????????
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            final Smoke normalSmoke = listNormalSmoke.get(position);
            final int devType = normalSmoke.getDeviceType();
            int netStates = normalSmoke.getNetState();
            ((ItemViewHolder) holder).right_into_image.setVisibility(View.VISIBLE);//@@9.14
            if(normalSmoke.getRssivalue()==null||normalSmoke.getRssivalue().equals("0")){
                ((ItemViewHolder) holder).rssi_value.setVisibility(View.GONE);
                ((ItemViewHolder) holder).rssi_image.setVisibility(View.GONE);
            }else{
                ((ItemViewHolder) holder).rssi_value.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).rssi_image.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).rssi_value.setText(normalSmoke.getRssivalue());
            }
            ((ItemViewHolder) holder).voltage_image.setVisibility(View.VISIBLE);
            int voltage=normalSmoke.getLowVoltage();
            if(voltage==0){
                ((ItemViewHolder) holder).voltage_image.setVisibility(View.GONE);
            }else if(voltage>0&&voltage<10){
                ((ItemViewHolder) holder).voltage_image.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).voltage_image.setImageResource(R.drawable.p0);
            }else if(voltage>=10&&voltage<30){
                ((ItemViewHolder) holder).voltage_image.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).voltage_image.setImageResource(R.drawable.p1);
            }else if(voltage>=30&&voltage<60){
                ((ItemViewHolder) holder).voltage_image.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).voltage_image.setImageResource(R.drawable.p2);
            }else if(voltage>=60&&voltage<80){
                ((ItemViewHolder) holder).voltage_image.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).voltage_image.setImageResource(R.drawable.p3);
            }else if(voltage>=80){
                ((ItemViewHolder) holder).voltage_image.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).voltage_image.setImageResource(R.drawable.p4);
            }
            ((ItemViewHolder) holder).voltage_image.setVisibility(View.GONE);
            ((ItemViewHolder) holder).dev_hearttime_set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder customizeDialog =
                            new AlertDialog.Builder(mContext);
                    final View dialogView = LayoutInflater.from(mContext)
                            .inflate(R.layout.dev_heart_time_setting,null);
                    customizeDialog.setView(dialogView);
                    final EditText heartTime_edit=(EditText) dialogView.findViewById(R.id.hearttime_value);
                    Button commit_btn=(Button)dialogView.findViewById(R.id.commit);
                    commit_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(heartTime_edit.getText().length()>0){
                                T.showShort(mContext,"????????????");
                            }else{
                                T.showShort(mContext,"???????????????");
                            }
                        }
                    });
                    customizeDialog.show();
                }
            });
            if(devType==18){
                ((ItemViewHolder) holder).state_name_tv.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).state_tv.setVisibility(View.VISIBLE);
                if(normalSmoke.getElectrState()==1){
                    ((ItemViewHolder) holder).state_tv.setText("???");
                }else{
                    ((ItemViewHolder) holder).state_tv.setText("???");
                }
            }else{
                ((ItemViewHolder) holder).state_name_tv.setVisibility(View.GONE);
                ((ItemViewHolder) holder).state_tv.setVisibility(View.GONE);
            }//@@11.01
            ((ItemViewHolder) holder).power_button.setVisibility(View.GONE);
            ((ItemViewHolder) holder).category_group_lin.setOnClickListener(null);
            ((ItemViewHolder) holder).dev_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path=ConstantValues.NFC_IMAGES+"devimages/"+normalSmoke.getMac()+".jpg";
                    Intent intent=new Intent(mContext, NFCImageShowActivity.class);
                    intent.putExtra("path",path);
                    mContext.startActivity(intent);
                }
            });
            final TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -0.1f, Animation.RELATIVE_TO_SELF, 0.0f);
            mShowAction.setDuration(500);
            ((ItemViewHolder) holder).dev_info_rela.setVisibility(View.GONE);
            ((ItemViewHolder) holder).show_info_text.setText("??????");
            ((ItemViewHolder) holder).show_info_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s= (String) ((ItemViewHolder) holder).show_info_text.getText();
                        if(s.equals("??????")){
                            ((ItemViewHolder) holder).dev_info_rela.setVisibility(View.VISIBLE);
                            ((ItemViewHolder) holder).dev_info_rela.startAnimation(mShowAction);
                            ((ItemViewHolder) holder).show_info_text.setText("??????");
                        }else{
                            ((ItemViewHolder) holder).dev_info_rela.setVisibility(View.GONE);
                            ((ItemViewHolder) holder).show_info_text.setText("??????");
                        }
                    }
            });
            switch (devType){
                case 150:
                case 89:
                case 87:
                case 86:
                case 61://@@??????????????????
                case 58://@@??????????????????
                case 41://@@NB??????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("?????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("?????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.GONE);
                    ((ItemViewHolder) holder).power_button.setVisibility(View.VISIBLE);
                    if(devType==89||devType==86){
                        ((ItemViewHolder) holder).power_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle("?????????????????????");
                                final String[] types = {"????????????", "????????????"};
                                //    ?????????????????????????????????
                                /**
                                 * ???????????????????????????????????????????????????????????????????????????
                                 * ???????????????????????????????????????????????????????????????????????????1????????????'???' ???????????????
                                 * ?????????????????????????????????????????????????????????
                                 */
                                builder.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        cencelSound(normalSmoke,which+1+"");
//                                        Toast.makeText(mContext, "????????????" + types[which], Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton("??????",null);
                                builder.show();
                            }
                        });
                    }else{
                        ((ItemViewHolder) holder).power_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cencelSound(normalSmoke,"1");
                            }
                        });
                    }
                    if(normalSmoke.getElectrState()==1){
                        ((ItemViewHolder) holder).power_button.setText("?????????");
                        ((ItemViewHolder) holder).power_button.setEnabled(false);
                    }else{
                        ((ItemViewHolder) holder).power_button.setText("??????");
                    }
                    break;
                case 56://@@NBIot??????
                    ((ItemViewHolder) holder).power_button.setVisibility(View.VISIBLE);
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("?????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                        ((ItemViewHolder) holder).power_button.setBackgroundColor(Color.GRAY);
                        ((ItemViewHolder) holder).power_button.setClickable(false);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("?????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                        ((ItemViewHolder) holder).power_button.setBackgroundColor(Color.RED);
                        ((ItemViewHolder) holder).power_button.setClickable(true);
                        ((ItemViewHolder) holder).power_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ProgressDialog dialog1 = new ProgressDialog(mContext);
                                dialog1.setTitle("??????");
                                dialog1.setMessage("?????????????????????");
                                dialog1.setCanceledOnTouchOutside(false);
                                dialog1.show();
                                String userid= SharedPreferencesManager.getInstance().getData(mContext,
                                        SharedPreferencesManager.SP_FILE_GWELL,
                                        SharedPreferencesManager.KEY_RECENTNAME);
                                ApiStores apiStores1 = AppClient.retrofit(ConstantValues.SERVER_IP_NEW).create(ApiStores.class);
                                Call<HttpError> call=apiStores1.EasyIot_erasure_control(userid,normalSmoke.getMac(),"1");
                                if (call != null) {
                                    call.enqueue(new Callback<HttpError>() {
                                        @Override
                                        public void onResponse(Call<HttpError> call, retrofit2.Response<HttpError> response) {
                                            T.showShort(mContext,response.body().getError()+"");
                                            dialog1.dismiss();
                                        }

                                        @Override
                                        public void onFailure(Call<HttpError> call, Throwable t) {
                                            T.showShort(mContext,"??????");
                                            dialog1.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.GONE);
                    break;
                case 92://@@?????????????????????
                case 57://@@
                case 55://@@????????????
                case 31://@@12.26 ??????iot??????
                case 21://@@12.01 Lora??????
                case 1://????????????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("?????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("?????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.GONE);
                    break;
                case 96://??????????????????
                case 73://??????7020??????
                case 72://????????????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) holder).category_group_lin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, OneGasInfoActivity.class);
                        intent.putExtra("Mac",normalSmoke.getMac());
                        intent.putExtra("devType",normalSmoke.getDeviceType());
                        intent.putExtra("devName",normalSmoke.getName());
                        mContext.startActivity(intent);
                    }
                });
                    break;
                case 93://?????????????????????
                case 16://@@9.29
                case 2://????????????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.GONE);
                    break;
                case 22:
                case 23:
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.GONE);
                    break;
                case 45://????????????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("HM?????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("HM?????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.GONE);
                    break;
                case 91:
                case 83://??????????????????
                case 81://lora????????????
                case 80://??????????????????
                case 77://??????????????????
                case 76://NB??????????????????
                case 75://????????????
                case 59:
                case 53:
                case 52:
                case 5://??????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("???????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("???????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).category_group_lin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(normalSmoke.getDeviceType()!=35){
                                Intent intent = new Intent(mContext, ElectricActivity.class);
                                intent.putExtra("ElectricMac",normalSmoke.getMac());
                                intent.putExtra("devType",normalSmoke.getDeviceType());
                                intent.putExtra("repeatMac",normalSmoke.getRepeater());
                                mContext.startActivity(intent);
                            }
                        }
                    });
                    break;
                case 36:
                case 35://NB??????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("NB?????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("NB?????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    break;
                case 99://???????????????
                case 79://???????????????
                case 26://???????????????
                case 25://??????????????????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).category_group_lin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, OneTHDevInfoActivity.class);
                            intent.putExtra("Mac",normalSmoke.getMac());
                            intent.putExtra("Position",normalSmoke.getName());
                            intent.putExtra("devType",normalSmoke.getDeviceType()+"");
                            mContext.startActivity(intent);
                        }
                    });
                    break;
                case 102:
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.GONE);
                    ((ItemViewHolder) holder).power_button.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) holder).power_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final ProgressDialog dialog1 = new ProgressDialog(mContext);
                            dialog1.setTitle("??????");
                            dialog1.setMessage("?????????????????????");
                            dialog1.setCanceledOnTouchOutside(false);
                            dialog1.show();
                            String userid= SharedPreferencesManager.getInstance().getData(mContext,
                                    SharedPreferencesManager.SP_FILE_GWELL,
                                    SharedPreferencesManager.KEY_RECENTNAME);
                            ApiStores apiStores1 = AppClient.retrofit(ConstantValues.SERVER_IP_NEW).create(ApiStores.class);
                            Call<HttpError> call=apiStores1.nanjing_jiade_cancel(normalSmoke.getMac(),normalSmoke.getDeviceType()+"","2");
                            if (call != null) {
                                call.enqueue(new Callback<HttpError>() {
                                    @Override
                                    public void onResponse(Call<HttpError> call, retrofit2.Response<HttpError> response) {
                                        T.showShort(mContext,response.body().getError()+"");
                                        dialog1.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<HttpError> call, Throwable t) {
                                        T.showShort(mContext,"??????");
                                        dialog1.dismiss();
                                    }
                                });
                            }
                        }
                    });
                    break;
                case 7://????????????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.GONE);
                    break;
                case 20://@@????????????????????????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("???????????????????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("???????????????????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.GONE);
                    break;
                case 84:
                case 8://????????????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("???????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("???????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.GONE);
                    break;
                case 9://????????????@@5.11??????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("???????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("???????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    break;
                case 12://??????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("?????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("?????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.GONE);
                    break;
                case 11://??????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.GONE);
                    break;
                case 13://??????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).category_group_lin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(normalSmoke.getNetState()==0){
                                Toast.makeText(mContext,"???????????????",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent(mContext, NewAirInfoActivity.class);
                            intent.putExtra("Mac",normalSmoke.getMac());
                            intent.putExtra("Position",normalSmoke.getName());
                            mContext.startActivity(intent);
                        }
                    });
                    break;
                case 116:
                case 51://??????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("CA?????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("CA?????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) holder).category_group_lin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ChuangAnActivity.class);
                            intent.putExtra("Mac",normalSmoke.getMac());
                            intent.putExtra("Position",normalSmoke.getName());
                            mContext.startActivity(intent);
                        }
                    });
                    break;
                case 200:
                case 125:
                case 100://??????????????????
                case 97://??????????????????
                case 94://?????????????????????
                case 78:
                case 70:
                case 68:
                case 47:
                case 42:
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) holder).category_group_lin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, LineChartActivity.class);
                            intent.putExtra("electricMac",normalSmoke.getMac());
                            intent.putExtra("isWater","1");//@@???????????????
                            intent.putExtra("devType",devType);
                            mContext.startActivity(intent);
                        }
                    });
                    break;
                case 10://????????????@@5.11??????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) holder).category_group_lin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, LineChartActivity.class);
                            intent.putExtra("electricMac",normalSmoke.getMac());
                            intent.putExtra("isWater",normalSmoke.getDeviceType()+"");//@@???????????????
                            mContext.startActivity(intent);
                        }
                    });
                    break;
                case 43://@@lora??????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) holder).category_group_lin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, LineChartActivity.class);
                            intent.putExtra("electricMac",normalSmoke.getMac());
                            intent.putExtra("isWater","3");//@@???????????????
                            mContext.startActivity(intent);
                        }
                    });
                    break;
                case 124:
                case 101://??????????????????
                case 98://??????????????????
                case 95://?????????????????????
                case 69:
                case 85:
                case 48:
                case 46:
                case 44://????????????
                case 19://????????????@@2018.01.02
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("??????????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) holder).category_group_lin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, LineChartActivity.class);
                            intent.putExtra("electricMac",normalSmoke.getMac());
                            intent.putExtra("isWater",normalSmoke.getDeviceType()+"");//@@???????????????
                            mContext.startActivity(intent);
                        }
                    });
                    break;
                case 27://????????????
                case 15://????????????@@8.3??????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("?????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("?????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.GONE);
                    break;
                case 90://????????????
                case 82://NB????????????
                case 18://??????@@10.31
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("?????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("?????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).right_into_image.setVisibility(View.GONE);
                    break;
                case 14://GPS??????@@8.8
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("GPS???"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("GPS???"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    break;
                case 131://lora??????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("LoRa?????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("LoRa?????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    break;
                case 126:
                case 119://????????????????????????
                    if (netStates == 0) {//?????????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("???????????????"+normalSmoke.getName()+"????????????)");
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.RED);
                    } else {//??????????????????
                        ((ItemViewHolder) holder).smoke_name_text.setText("???????????????"+normalSmoke.getName());
                        ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
                    }
                    ((ItemViewHolder) holder).category_group_lin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, WiredSmokeListActivity.class);
                            intent.putExtra("Mac",normalSmoke.getMac());
                            intent.putExtra("Position",normalSmoke.getName());
                            mContext.startActivity(intent);
                        }
                    });
                    ((ItemViewHolder) holder).power_button.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) holder).power_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final ProgressDialog dialog1 = new ProgressDialog(mContext);
                            dialog1.setTitle("??????");
                            dialog1.setMessage("?????????????????????");
                            dialog1.setCanceledOnTouchOutside(false);
                            dialog1.show();
                            ApiStores apiStores1 = AppClient.retrofit(ConstantValues.SERVER_IP_NEW).create(ApiStores.class);
                            Call<HttpError> call=apiStores1.cancelSound(normalSmoke.getMac());
                            if (call != null) {
                                call.enqueue(new Callback<HttpError>() {
                                    @Override
                                    public void onResponse(Call<HttpError> call, retrofit2.Response<HttpError> response) {
                                        T.showShort(mContext,response.body().getError()+"");
                                        dialog1.dismiss();
                                    }
                                    @Override
                                    public void onFailure(Call<HttpError> call, Throwable t) {
                                        T.showShort(mContext,"??????");
                                        dialog1.dismiss();
                                    }
                                });
                            }
                        }
                    });
                    break;
            }

            if (netStates == 0) {//?????????????????????
                ((ItemViewHolder) holder).online_state_image.setImageResource(R.drawable.sblb_lixian);
            } else {//??????????????????
                ((ItemViewHolder) holder).online_state_image.setImageResource(R.drawable.sblb_zaixian);
                ((ItemViewHolder) holder).smoke_name_text.setTextColor(Color.BLACK);
            }

            ((ItemViewHolder) holder).address_tv.setText(normalSmoke.getAddress());
            ((ItemViewHolder) holder).mac_tv.setText(normalSmoke.getMac());//@@
            ((ItemViewHolder) holder).repeater_tv.setText(normalSmoke.getRepeater());
            ((ItemViewHolder) holder).type_tv.setText(normalSmoke.getPlaceType());
            ((ItemViewHolder) holder).area_tv.setText(normalSmoke.getAreaName());



            ((ItemViewHolder) holder).manager_img.setOnClickListener(new View.OnClickListener() {//???????????????????????????
                @Override
                public void onClick(View v) {
                    if(normalSmoke.getPrincipal1()!=null&&normalSmoke.getPrincipal1().length()>0){
                        Intent intent=new Intent(mContext, CallManagerDialogActivity.class);
                        intent.putExtra("people1",normalSmoke.getPrincipal1());
                        intent.putExtra("people2",normalSmoke.getPrincipal2());
                        intent.putExtra("phone1",normalSmoke.getPrincipal1Phone());
                        intent.putExtra("phone2",normalSmoke.getPrincipal2Phone());
                        mContext.startActivity(intent);
                    }else{
                        T.showShort(mContext,"??????????????????");
                    }
                }
            });
            ((ItemViewHolder) holder).category_group_lin.setOnLongClickListener(this);
            ((ItemViewHolder) holder).category_group_lin.setTag(position);
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    footViewHolder.footViewItemTv.setText("??????????????????...");
                    break;
                case LOADING_MORE:
                    footViewHolder.footViewItemTv.setText("????????????????????????...");
                    break;
                case NO_MORE_DATA:
                    T.showShort(mContext, "??????????????????");
                    footViewHolder.footer.setVisibility(View.GONE);
                    break;
                case NO_DATA:
                    footViewHolder.footer.setVisibility(View.GONE);
                    break;
            }
        }

    }

    /**
     * ?????????????????????Item????????????FootView??????
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        // ????????????item?????????footerView
        if (position == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return listNormalSmoke.size();
    }

    //????????????ViewHolder???????????????Item????????????????????????
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.category_group_lin)
        LinearLayout category_group_lin;
        @Bind(R.id.smoke_name_text)
        TextView smoke_name_text;
        @Bind(R.id.mac_tv)
        TextView mac_tv;
        @Bind(R.id.repeater_tv)
        TextView repeater_tv;
        @Bind(R.id.area_tv)
        TextView area_tv;
        @Bind(R.id.type_tv)
        TextView type_tv;
        @Bind(R.id.address_tv)
        TextView address_tv;
        @Bind(R.id.manager_img)
        TextView manager_img;
        @Bind(R.id.right_into_image)
        ImageView right_into_image;
        @Bind(R.id.item_lin)
        LinearLayout item_lin;//@@8.8
        @Bind(R.id.state_name_tv)
        TextView state_name_tv;//@@11.01
        @Bind(R.id.state_tv)
        TextView state_tv;//@@11.01
        @Bind(R.id.rssi_value)
        TextView rssi_value;//@@2018.03.07
        @Bind(R.id.xy_button)
        TextView power_button;//@@2018.03.07
        @Bind(R.id.dev_image)
        TextView dev_image;//@@2018.03.07
        @Bind(R.id.dev_hearttime_set)
        TextView dev_hearttime_set;//@@2018.03.07
        @Bind(R.id.voltage_image)
        ImageView voltage_image;
        @Bind(R.id.rssi_image)
        ImageView rssi_image;
        @Bind(R.id.show_info_text)
        TextView show_info_text;
        @Bind(R.id.dev_info_rela)
        RelativeLayout dev_info_rela;
        @Bind(R.id.online_state_image)
        ImageView online_state_image;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * ??????FootView??????
     */
    public static class FootViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.foot_view_item_tv)
        TextView footViewItemTv;
        @Bind(R.id.footer)
        LinearLayout footer;
        public FootViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    //????????????
    public void addItem(List<Smoke> smokeList) {
        smokeList.addAll(listNormalSmoke);
        listNormalSmoke.removeAll(listNormalSmoke);
        listNormalSmoke.addAll(smokeList);
        notifyDataSetChanged();
    }

    public void addMoreItem(List<Smoke> smokeList) {
        listNormalSmoke.addAll(smokeList);
        notifyDataSetChanged();
    }

    /**
     * //??????????????????
     * PULLUP_LOAD_MORE=0;
     * //???????????????
     * LOADING_MORE=1;
     * //???????????????????????????????????????
     * NO_MORE_DATA=2;
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        load_more_status = status;
        notifyDataSetChanged();
    }

    //??????????????????
    private void cencelSound(Smoke normalSmoke,String state) {
        final ProgressDialog dialog1 = new ProgressDialog(mContext);
        dialog1.setTitle("??????");
        dialog1.setMessage("?????????????????????");
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String userid= SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        ApiStores apiStores1 = AppClient.retrofit(ConstantValues.SERVER_IP_NEW).create(ApiStores.class);
        Call<HttpError> call=null;
        switch (normalSmoke.getDeviceType()){
            case 41:
                call=apiStores1.NB_IOT_Control(userid,normalSmoke.getMac(),"1");
                break;
            case 58:
                call=apiStores1.nanjing_jiade_cancel(normalSmoke.getMac(),"58",state);
                break;
            case 61:
                call=apiStores1.nanjing_jiade_cancel(normalSmoke.getMac(),"61",state);
                break;
            case 86:
                call=apiStores1.nanjing_jiade_cancel(normalSmoke.getMac(),"86",state);
                break;
            case 89:
                call=apiStores1.nanjing_jiade_cancel(normalSmoke.getMac(),"89",state);
                break;
        }
        if (call != null) {
            call.enqueue(new Callback<HttpError>() {
                @Override
                public void onResponse(Call<HttpError> call, retrofit2.Response<HttpError> response) {
                    T.showShort(mContext,response.body().getError()+"");
                    dialog1.dismiss();
                }

                @Override
                public void onFailure(Call<HttpError> call, Throwable t) {
                    T.showShort(mContext,"??????");
                    dialog1.dismiss();
                }
            });
        }
    }
}
