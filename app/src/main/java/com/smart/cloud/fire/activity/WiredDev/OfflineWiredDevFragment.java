package com.smart.cloud.fire.activity.WiredDev;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smart.cloud.fire.adapter.ShopCameraAdapter;
import com.smart.cloud.fire.adapter.ShopSmokeAdapter;
import com.smart.cloud.fire.adapter.WiredDevAdapter;
import com.smart.cloud.fire.base.ui.MvpFragment;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.global.SmokeSummary;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Smoke;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.ShopInfoFragmentView;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.WiredDevFragment.WiredDevFragment;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

/**
 * Created by Rain on 2017/7/19.
 */
public class OfflineWiredDevFragment extends MvpFragment<WiredDevPresenter> implements WiredDevView {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipere_fresh_layout)
    SwipeRefreshLayout swipereFreshLayout;
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
//    @Bind(R.id.smoke_total)
//    LinearLayout smokeTotal;
//    @Bind(R.id.total_num)
//    TextView totalNum;
//    @Bind(R.id.online_num)
//    TextView onlineNum;
//    @Bind(R.id.offline_num)
//    TextView offlineNum;
    private LinearLayoutManager linearLayoutManager;
    private WiredDevAdapter shopSmokeAdapter;
    private int lastVisibleItem;
    private Context mContext;
    private List<Smoke> list;
    private boolean research = false;
    private int page;
    private String userID;
    private int privilege;
    private int loadMoreCount;
    private int preseterTpye=1;
    private WiredDevPresenter mShopInfoFragmentPresenter;//@@7.17

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_dev, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext=getActivity();
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        page = 1;
//        smokeTotal.setVisibility(View.VISIBLE);
        list = new ArrayList<>();
        refreshListView();
        mvpPresenter.getNeedLossSmoke(userID, privilege + "","", "", "", page+"","2",false,1,list,OfflineWiredDevFragment.this);
        mvpPresenter.getSmokeSummary(userID,privilege+"","","","","2",OfflineWiredDevFragment.this);
    }

    private void refreshListView() {
        //?????????????????????????????????????????????4???
        swipereFreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipereFreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipereFreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        linearLayoutManager=new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        swipereFreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((WiredDevActivity)getActivity()).refreshView();
//               refreshView();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (research) {
                    if(shopSmokeAdapter!=null){
                        shopSmokeAdapter.changeMoreStatus(ShopCameraAdapter.NO_DATA);
                    }
                    return;
                }
                if(shopSmokeAdapter==null){
                    return;
                }
                int count = shopSmokeAdapter.getItemCount();
                int itemCount = lastVisibleItem+1;
                if (newState == RecyclerView.SCROLL_STATE_IDLE && itemCount == count) {
                    if(loadMoreCount>=20){
                        page = page + 1 ;
                        mvpPresenter.getNeedLossSmoke(userID, privilege + "","", "", "", page+"","2",false,1,list,OfflineWiredDevFragment.this);
                    }else{
                        T.showShort(mContext,"???????????????????????????");
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected WiredDevPresenter createPresenter() {
//        mShopInfoFragmentPresenter = new ShopInfoFragmentPresenter(this,(ShopInfoFragment)getParentFragment());
        mShopInfoFragmentPresenter = new WiredDevPresenter((WiredDevActivity)getActivity());
        return mShopInfoFragmentPresenter;
    }

    @Override
    public String getFragmentName() {
        return "OffLineDevFragment";
    }

    @Override
    public void getDataSuccess(List<?> smokeList,boolean search) {
        loadMoreCount = smokeList.size();
        research = search;
        list.clear();
        list.addAll((List<Smoke>)smokeList);
        shopSmokeAdapter = new WiredDevAdapter(mContext, list);
        recyclerView.setAdapter(shopSmokeAdapter);
        swipereFreshLayout.setRefreshing(false);
        shopSmokeAdapter.changeMoreStatus(ShopSmokeAdapter.NO_DATA);
    }

    @Override
    public void getDataFail(String msg) {
        swipereFreshLayout.setRefreshing(false);
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

    @Override
    public void onLoadingMore(List<?> smokeList) {
        loadMoreCount = smokeList.size();
        list.addAll((List<Smoke>)smokeList);
        shopSmokeAdapter.changeMoreStatus(ShopSmokeAdapter.LOADING_MORE);
    }

    @Override
    public void getAreaType(ArrayList<?> shopTypes, int type) {
    }

    @Override
    public void getAreaTypeFail(String msg, int type) {
    }

    @Override
    public void unSubscribe(String type) {
    }

    @Override
    public void getLostCount(String count) {
    }

    @Override
    public void refreshView() {
        page = 1;
        list.clear();
        mvpPresenter.getNeedLossSmoke(userID, privilege + "", "","", "", page+"","2",true,1,list,OfflineWiredDevFragment.this);
        mvpPresenter.getSmokeSummary(userID,privilege+"","","","","2",OfflineWiredDevFragment.this);
    }

    @Override
    public void getChoiceArea(Area area) {

    }

    @Override
    public void getChoiceShop(ShopType shopType) {

    }

    @Override
    public void getSmokeSummary(SmokeSummary smokeSummary) {
//        totalNum.setText(smokeSummary.getAllSmokeNumber()+"");
//        onlineNum.setText(smokeSummary.getOnlineSmokeNumber()+"");
//        offlineNum.setText(smokeSummary.getLossSmokeNumber()+"");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}


