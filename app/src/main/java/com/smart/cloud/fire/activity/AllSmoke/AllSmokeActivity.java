package com.smart.cloud.fire.activity.AllSmoke;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.smart.cloud.fire.activity.Map.MapActivity;
import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.global.SmokeSummary;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.AllDevFragment.AllDevFragment;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.OffLineDevFragment.OffLineDevFragment;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.Utils;
import com.smart.cloud.fire.utils.VolleyHelper;
import com.smart.cloud.fire.view.AreaChooceListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;

public class AllSmokeActivity extends MvpActivity<AllSmokePresenter> implements AllSmokeView {
    RelativeLayout title_name_rela,title_lose_dev_rela;
    TextView title_name_tv,title_lose_dev_tv;
    Context mContext;
    private AllSmokePresenter mAllSmokePresenter;
    private String userID;
    private int privilege;

    private AllDevFragment allDevFragment;
    private OffLineDevFragment offLineDevFragment;
    private FragmentManager fragmentManager;
    public static final int FRAGMENT_ONE = 0;
    public static final int FRAGMENT_FIVE =4;
    private int position;
    private boolean visibility = false;
    private ShopType mShopType;
    private Area mArea;

    private String areaId = "";
    private String parentId="";//@@9.1
    private String shopTypeId = "";

    private String page="";//@@9.5

    private String areaId_1 = "";//@@9.6??????????????????
    private String parentId_1="";
    private String shopTypeId_1 = "";
    private String areaId_2 = "";//@@9.6??????????????????
    private String parentId_2="";
    private String shopTypeId_2 = "";

//    @Bind(R.id.add_fire)
//    ImageView addFire;//??????????????????????????????
//    @Bind(R.id.lin1)
//    LinearLayout lin1;//??????????????????
    @Bind(R.id.area_condition)
    AreaChooceListView areaCondition;//????????????????????????
//    @Bind(R.id.shop_type_condition)
//    XCDropDownListViewMapSearch shopTypeCondition;//??????????????????????????????
//    @Bind(R.id.smoke_total)
//    LinearLayout smokeTotal;
//    @Bind(R.id.total_num)
//    TextView totalNum;
    @Bind(R.id.online_num)
    TextView onlineNum;
    @Bind(R.id.offline_num)
    TextView offlineNum;
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
//    @Bind(R.id.search_fire)
//    ImageView searchFire;//??????????????????
    @Bind(R.id.turn_map_btn)
    RelativeLayout turn_map_btn;

    List<Area> parent = null;//@@8.31
    Map<String, List<Area>> map = null;//@@8.31

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_smoke);
        ButterKnife.bind(this);

        //???????????????????????????????????
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // ???????????????????????????????????
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        mContext=this;
        init();
    }

    @OnClick({ R.id.area_condition,R.id.turn_map_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_fire://???????????????????????????
                if (visibility) {
                    visibility = false;
//                    lin1.setVisibility(View.GONE);
                    if (areaCondition.ifShow()) {
                        areaCondition.closePopWindow();
                    }
//                    if (shopTypeCondition.ifShow()) {
//                        shopTypeCondition.closePopWindow();
//                    }
                } else {
                    visibility = true;
                    areaCondition.setEditText("");
//                    shopTypeCondition.setEditText("");
                    areaCondition.setEditTextHint("??????");
//                    shopTypeCondition.setEditTextHint("??????");
                    areaCondition.setHintTextColor("#fff");
//                    lin1.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.area_condition://??????????????????????????????
                if (areaCondition.ifShow()) {
                    areaCondition.closePopWindow();
                } else {
                    VolleyHelper helper=VolleyHelper.getInstance(mContext);
                    RequestQueue mQueue = helper.getRequestQueue();
//                    RequestQueue mQueue = Volley.newRequestQueue(mContext);
                    String url= ConstantValues.SERVER_IP_NEW+"getAreaInfo?userId="+userID+"&privilege="+privilege;
                    StringRequest stringRequest = new StringRequest(url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject=new JSONObject(response);
                                        if(jsonObject.getInt("errorCode")==0){
                                            parent = new ArrayList<>();
                                            map = new HashMap<>();
                                            JSONArray jsonArrayParent=jsonObject.getJSONArray("areas");
                                            for(int i=0;i<jsonArrayParent.length();i++){
                                                JSONObject tempParent= jsonArrayParent.getJSONObject(i);
                                                Area tempArea=new Area();
                                                tempArea.setAreaId(tempParent.getString("areaId"));
                                                tempArea.setAreaName(tempParent.getString("areaName"));
                                                tempArea.setIsParent(1);
                                                parent.add(tempArea);
                                                List<Area> child = new ArrayList<>();
                                                JSONArray jsonArrayChild=tempParent.getJSONArray("areas");
                                                for(int j=0;j<jsonArrayChild.length();j++){
                                                    JSONObject tempChild= jsonArrayChild.getJSONObject(j);
                                                    Area tempAreaChild=new Area();
                                                    tempAreaChild.setAreaId(tempChild.getString("areaId"));
                                                    tempAreaChild.setAreaName(tempChild.getString("areaName"));
                                                    tempAreaChild.setIsParent(0);
                                                    child.add(tempAreaChild);
                                                }
                                                map.put(tempParent.getString("areaName"),child);
                                            }
                                        }
                                        areaCondition.setItemsData2(parent,map, mAllSmokePresenter);
                                        areaCondition.showPopWindow();
                                        areaCondition.setClickable(true);
                                        areaCondition.closeLoading();
//                                        mvpPresenter.getPlaceTypeId(userID, privilege + "", 2);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error","error");
                        }
                    });
                    mQueue.add(stringRequest);
                    areaCondition.setClickable(false);
                    areaCondition.showLoading();
                }
                break;
//            case R.id.shop_type_condition://??????????????????????????????
//                if (shopTypeCondition.ifShow()) {
//                    shopTypeCondition.closePopWindow();
//                } else {
//                    mvpPresenter.getPlaceTypeId(userID, privilege + "", 1);
//                    shopTypeCondition.setClickable(false);
//                    shopTypeCondition.showLoading();
//                }
//                break;
            case R.id.search_fire://????????????
                if (!Utils.isNetworkAvailable(this)) {
                    return;
                }
//                if (shopTypeCondition.ifShow()) {
//                    shopTypeCondition.closePopWindow();
//                }
                if (areaCondition.ifShow()) {
                    areaCondition.closePopWindow();
                }
                if ((mShopType != null && mShopType.getPlaceTypeId() != null) || (mArea != null && mArea.getAreaId() != null)) {
//                    lin1.setVisibility(View.GONE);
//                    searchFire.setVisibility(View.GONE);
//                    if(MyApp.app.getPrivilege()!=1){//@@9.29 1???
//                        addFire.setVisibility(View.VISIBLE);
//                    }
                    areaCondition.searchClose();
//                    shopTypeCondition.searchClose();
                    visibility = false;
                    if (mArea != null && mArea.getAreaId() != null) {
                        if(mArea.getIsParent()==1){
                            parentId= mArea.getAreaId();//@@9.1
                            areaId="";
                        }else{
                            areaId = mArea.getAreaId();
                            parentId="";
                        }
                    } else {
                        parentId="";//@@11.07
                        areaId = "";
                    }
                    if (mShopType != null && mShopType.getPlaceTypeId() != null) {
                        shopTypeId = mShopType.getPlaceTypeId();
                    } else {
                        shopTypeId = "";
                    }
                    //????????????????????????fragment??????
//                    switch (position) {
//                        case FRAGMENT_ONE:
                            page="1";
                            mvpPresenter.getNeedSmoke(userID, privilege + "", "1",parentId,areaId, shopTypeId,"1");//??????????????????
                            mvpPresenter.getSmokeSummary(userID,privilege+"",parentId,areaId,shopTypeId,"1");//??????????????????
                            areaId_1=areaId;//@@9.6
                            parentId_1=parentId;
                            shopTypeId_1=shopTypeId;
//                            break;
//                        case FRAGMENT_FIVE://@@6.29
                            mvpPresenter.getNeedLossSmoke(userID, privilege + "", parentId,areaId, shopTypeId, "1","1",false,0,null,offLineDevFragment);
                            mvpPresenter.getSmokeSummary(userID,privilege+"",parentId,areaId,shopTypeId,"1");
                            areaId_2=areaId;//@@9.6
                            parentId_2=parentId;
                            shopTypeId_2=shopTypeId;
//                            break;
//                        default:
//                            break;
//                    }
                    mShopType = null;
                    mArea = null;
                } else {
//                    lin1.setVisibility(View.GONE);
                    return;
                }
                break;
            case R.id.turn_map_btn:
                Intent intent=new Intent(AllSmokeActivity.this, MapActivity.class);
                intent.putExtra("devType","1");
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    @Override
    protected AllSmokePresenter createPresenter() {
        mAllSmokePresenter=new AllSmokePresenter(this);
        return mAllSmokePresenter;
    }

    private void init() {
        fragmentManager = getFragmentManager();
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        showFragment(FRAGMENT_ONE);
        title_name_tv=(TextView )findViewById(R.id.title_name_text);
        title_lose_dev_tv=(TextView)findViewById(R.id.title_lose_dev_text) ;
        title_name_rela=(RelativeLayout)findViewById(R.id.title_name) ;
        title_name_rela.setEnabled(false);
        title_name_rela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title_lose_dev_rela.setEnabled(true);
                title_name_rela.setEnabled(false);
                mvpPresenter.unSubscribe("allSmoke");
                position=FRAGMENT_ONE;//@@????????????
            }
        });
        title_lose_dev_rela=(RelativeLayout)findViewById(R.id.title_lose_dev) ;
        title_lose_dev_rela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title_name_rela.setEnabled(true);
                title_lose_dev_rela.setEnabled(false);
                mvpPresenter.unSubscribe("lostSmoke");
                position=FRAGMENT_FIVE;//@@????????????
            }
        });
        title_name_tv.setText("????????????");
        title_lose_dev_tv.setText("????????????");
        areaCondition.setActivity(this);//@@12.21
        areaCondition.setHintTextColor("#ffffffff");
        areaCondition.setEditTextHint("#ffffffff");
        areaCondition.setEditTextHint("????????????");
    }

    public void showFragment(int index) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        hideFragment(ft);
        //????????????????????????
        position = index;
        if (areaCondition.ifShow()) {
            areaCondition.closePopWindow();
        }//@@5.5??????????????????
//        if (shopTypeCondition.ifShow()) {
//            shopTypeCondition.closePopWindow();
//        }//@@5.5??????????????????
        switch (index) {
            case FRAGMENT_ONE:
//                if(MyApp.app.getPrivilege()!=1){//@@9.29 1???
//                    addFire.setVisibility(View.VISIBLE);
//                }
                if (allDevFragment == null) {
                    offLineDevFragment=new OffLineDevFragment();
                    ft.add(R.id.fragment_content, offLineDevFragment);
                    allDevFragment = new AllDevFragment();
                    ft.add(R.id.fragment_content, allDevFragment);
                } else {
                    ft.show(allDevFragment);
                }
                break;
            case FRAGMENT_FIVE:
//                if(MyApp.app.getPrivilege()!=1){//@@9.29 1???
//                    addFire.setVisibility(View.VISIBLE);
//                }
                if (offLineDevFragment == null) {
                    offLineDevFragment = new OffLineDevFragment();
                    ft.add(R.id.fragment_content, offLineDevFragment);
                } else {
                    ft.show(offLineDevFragment);
                }
                break;
        }
        ft.commit();
    }

    public void hideFragment(FragmentTransaction ft) {
        //????????????????????????????????????
        if (allDevFragment != null) {
            ft.hide(allDevFragment);
        }
        if (offLineDevFragment != null) {
            ft.hide(offLineDevFragment);
        }
    }

    @Override
    public void getDataSuccess(List<?> smokeList, boolean research) {
        allDevFragment.getDataSuccess(smokeList,research);
    }

    @Override
    public void getSmokeSummary(SmokeSummary smokeSummary) {
//        totalNum.setText(smokeSummary.getAllSmokeNumber()+"");
        onlineNum.setText(smokeSummary.getOnlineSmokeNumber()+"");
        offlineNum.setText(smokeSummary.getLossSmokeNumber()+"");
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void unSubscribe(String type) {
        switch (type) {
            case "allSmoke":
//                mAllSmokePresenter.getSmokeSummary(userID,privilege+"","","","","1", allDevFragment);//@@9.5
//                lin1.setVisibility(View.GONE);
//                searchFire.setVisibility(View.GONE);
//                if(MyApp.app.getPrivilege()!=1){//@@9.29 1???
//                    addFire.setVisibility(View.VISIBLE);
//                }
                showFragment(FRAGMENT_ONE);
                break;
            case "lostSmoke":
//                mAllSmokePresenter.getSmokeSummary(userID,privilege+"","","","","1", offLineDevFragment);//@@9.5
//                lin1.setVisibility(View.GONE);
//                searchFire.setVisibility(View.GONE);
//                if(MyApp.app.getPrivilege()!=1){//@@9.29 1???
//                    addFire.setVisibility(View.VISIBLE);
//                }
                showFragment(FRAGMENT_FIVE);
                break;
            default:
                break;
        }
    }

    @Override
    public void getAreaType(ArrayList<?> shopTypes, int type) {
        if (type == 1) {
//            shopTypeCondition.setItemsData((ArrayList<Object>) shopTypes, mAllSmokePresenter);
//            shopTypeCondition.showPopWindow();
//            shopTypeCondition.setClickable(true);
//            shopTypeCondition.closeLoading();
        } else {
            areaCondition.setItemsData((ArrayList<Object>) shopTypes, mAllSmokePresenter);
            areaCondition.showPopWindow();
            areaCondition.setClickable(true);
            areaCondition.closeLoading();
        }

    }

    @Override
    public void getAreaTypeFail(String msg, int type) {
        T.showShort(mContext, msg);
        if (type == 1) {
//            shopTypeCondition.setClickable(true);
//            shopTypeCondition.closeLoading();
        } else {
            areaCondition.setClickable(true);
            areaCondition.closeLoading();
        }
    }

    @Override
    public void getDataFail(String msg) {
        T.show(mContext,msg, Toast.LENGTH_SHORT);//@@4.27
    }

    @Override
    public void getChoiceArea(Area area) {
        mArea = area;
        if (mArea != null && mArea.getAreaId() != null) {
//            if(MyApp.app.getPrivilege()!=1){//@@9.29 1???
//                addFire.setVisibility(View.GONE);
//            }
//            searchFire.setVisibility(View.VISIBLE);
        }
        if (mArea.getAreaId() == null && mShopType == null) {
//            if(MyApp.app.getPrivilege()!=1){//@@9.29 1???
//                addFire.setVisibility(View.VISIBLE);
//            }
//            searchFire.setVisibility(View.GONE);
        } else if (mArea.getAreaId() == null && mShopType != null && mShopType.getPlaceTypeId() == null) {
//            if(MyApp.app.getPrivilege()!=1){//@@9.29 1???
//                addFire.setVisibility(View.VISIBLE);
//            }
//            searchFire.setVisibility(View.GONE);
        }
        if (mArea != null && mArea.getAreaId() != null) {
            if(mArea.getIsParent()==1){
                parentId= mArea.getAreaId();//@@9.1
                areaId="";
            }else{
                areaId = mArea.getAreaId();
                parentId="";
            }
        } else {
            parentId="";//@@11.07
            areaId = "";
        }
        page="1";
        mvpPresenter.getNeedSmoke(userID, privilege + "", "1",parentId,areaId, shopTypeId,"1");//??????????????????
        mvpPresenter.getSmokeSummary(userID,privilege+"",parentId,areaId,shopTypeId,"1");//??????????????????
        areaId_1=areaId;//@@9.6
        parentId_1=parentId;
        shopTypeId_1=shopTypeId;
//                            break;
//                        case FRAGMENT_FIVE://@@6.29
        mvpPresenter.getNeedLossSmoke(userID, privilege + "", parentId,areaId, shopTypeId, "1","1",false,0,null,offLineDevFragment);
        mvpPresenter.getSmokeSummary(userID,privilege+"",parentId,areaId,shopTypeId,"1");
        areaId_2=areaId;//@@9.6
        parentId_2=parentId;
        shopTypeId_2=shopTypeId;

        areaCondition.searchClose();
    }

    @Override
    public void getChoiceShop(ShopType shopType) {
        mShopType = shopType;
        if (mShopType != null && mShopType.getPlaceTypeId() != null) {
//            if(MyApp.app.getPrivilege()!=1){//@@9.29 1???
//                addFire.setVisibility(View.GONE);
//            }
//            searchFire.setVisibility(View.VISIBLE);
        }
        if (mShopType.getPlaceTypeId() == null && mArea == null) {
//            if(MyApp.app.getPrivilege()!=1){//@@9.29 1???
//                addFire.setVisibility(View.VISIBLE);
//            }
//            searchFire.setVisibility(View.GONE);
        } else if (mShopType.getPlaceTypeId() == null && mArea != null && mArea.getAreaId() == null) {
//            if(MyApp.app.getPrivilege()!=1){//@@9.29 1???
//                addFire.setVisibility(View.VISIBLE);
//            }
//            searchFire.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadingMore(List<?> smokeList) {
    }

    public String getParentId1() {
        return parentId_1;
    }

    public String getAreaId1() {
        return areaId_1;
    }

    public String getShopTypeId1() {
        return shopTypeId_1;
    }

    public String getParentId2() {
        return parentId_2;
    }

    public String getAreaId2() {
        return areaId_2;
    }

    public String getShopTypeId2() {
        return shopTypeId_2;
    }

    public String getPage() {
        return page;
    }

    public void refreshFragment(){
        allDevFragment.refreshView();
        offLineDevFragment.refreshView();
        areaCondition.setEditText("");
    }

}
