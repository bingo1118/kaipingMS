package com.smart.cloud.fire.activity.AssetManage.Tag;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.cloud.fire.activity.AddDev.AddDevActivity;
import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Smoke;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class TagListActivity extends MvpActivity<TagListPresenter> implements TagListView {


    @Bind(R.id.recycler_view)
    RecyclerView recycler_view;
    @Bind(R.id.swipere_fresh_layout)
    SwipeRefreshLayout swipereFreshLayout;
    @Bind(R.id.sum_tv)
    TextView sum_tv;
    @Bind(R.id.normal_tv)
    TextView normal_tv;
    @Bind(R.id.alarm_tv)
    TextView alarm_tv;
    @Bind(R.id.insp_tv)
    TextView insp_tv;
    @Bind(R.id.overtime_tv)
    TextView overtime_tv;


    @Bind(R.id.add)
    TextView add;


    private TagListPresenter mPresenter;
    private TagListAdapter mAdapter;
    Context mContext;
    int page = 1;
    List<Smoke> mList;

    private int loadMoreCount;
    private int lastVisibleItem;

    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list);
        ButterKnife.bind(this);
        mContext = this;

        mPresenter.getTagList(MyApp.getUserID(), MyApp.getPrivilege() + "","","","","","");

        refreshListView();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, AddDevActivity.class);
                startActivity(i);
            }
        });
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

        //??????????????????
        swipereFreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                mPresenter.getTagList(MyApp.getUserID(), MyApp.getPrivilege() + "","","","","","");
            }
        });


        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (mAdapter == null) {
                    return;
                }
                int count = mAdapter.getItemCount();
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == count) {
                    if (loadMoreCount >= 10) {
                        page += 1;
                        mPresenter.getTagList(MyApp.getUserID(), MyApp.getPrivilege() + "","","","","","");
                    } else {
                        T.showShort(mContext, "???????????????????????????");
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

    @Override
    protected TagListPresenter createPresenter() {
        if (mPresenter == null) {
            mPresenter = new TagListPresenter(this);
        }
        return mPresenter;
    }

    @Override
    public void getDataSuccess(List<Smoke> smokeList) {
        if (smokeList.size() == 0) {
            Toast.makeText(mContext, "?????????", Toast.LENGTH_SHORT).show();
        }//@@7.7
        mList=smokeList;
        loadMoreCount = smokeList.size();
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recycler_view.setLayoutManager(linearLayoutManager);
        mAdapter = new TagListAdapter(mContext, mList);
        recycler_view.setAdapter(mAdapter);
        swipereFreshLayout.setRefreshing(false);
        getSum();
    }

    private void getSum() {
        VolleyHelper helper = VolleyHelper.getInstance(mContext);
        RequestQueue mQueue = helper.getRequestQueue();
        String url = ConstantValues.SERVER_IP_NEW + "getTagStat?userId="+MyApp.getUserID()
                + "&privilege="+MyApp.getPrivilege();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int errorCode = response.getInt("errorcode");
                            String error = response.getString("error");
                            if (errorCode == 0) {
                                JSONObject jsonObject=response.getJSONObject("object");
                                sum_tv.setText("??????:"+jsonObject.getString("totalNum"));
                                normal_tv.setText("??????:"+jsonObject.getString("normalNum"));
                                alarm_tv.setText("??????:"+jsonObject.getString("alarmNum"));
                                insp_tv.setText("??????:"+jsonObject.getString("offLineNum"));
                                overtime_tv.setVisibility(View.GONE);
                            } else {
                                T.showShort(mContext, error);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                T.showShort(mContext, "????????????");
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void getDataFail(String msg) {
        T.showShort(mContext, msg);
    }

    @Override
    public void onLoadingMore(List<Smoke> smokeList) {
        loadMoreCount = smokeList.size();
        mList.addAll((List<Smoke>)smokeList);
        mAdapter.notifyDataSetChanged();
    }


}


