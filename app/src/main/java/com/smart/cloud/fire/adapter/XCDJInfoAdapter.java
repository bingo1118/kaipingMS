package com.smart.cloud.fire.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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

import com.smart.cloud.fire.activity.GasDevice.OneGasInfoActivity;
import com.smart.cloud.fire.activity.NFCDev.NFCImageShowActivity;
import com.smart.cloud.fire.activity.THDevice.OneTHDevInfoActivity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.XCDJInfo;
import com.smart.cloud.fire.mvp.ChuangAn.ChuangAnActivity;
import com.smart.cloud.fire.mvp.LineChart.LineChartActivity;
import com.smart.cloud.fire.mvp.electric.ElectricActivity;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpError;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Smoke;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.Security.NewAirInfoActivity;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.WiredDevFragment.WiredSmokeListActivity;
import com.smart.cloud.fire.retrofit.ApiStores;
import com.smart.cloud.fire.retrofit.AppClient;
import com.smart.cloud.fire.ui.CallManagerDialogActivity;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.view.TakePhoto.Photo;
import com.smart.cloud.fire.view.TakePhoto.TakePhotosView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;
import retrofit2.Call;
import retrofit2.Callback;

public class XCDJInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView
    private LayoutInflater mInflater;
    private Context mContext;
    private List<XCDJInfo> mList;


    public void setmOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public OnClickListener mOnClickListener;
    public interface OnClickListener{
        public void onItemClick(int position);
    };



    public XCDJInfoAdapter(Context mContext,List<XCDJInfo> list) {
        this.mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        mList=list;
    }
    /**
     * item显示类型
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //进行判断显示类型，来创建返回不同的View
        if (viewType == TYPE_ITEM) {
            final View view = mInflater.inflate(R.layout.scdj_info_item, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            ItemViewHolder viewHolder = new ItemViewHolder(view);
            return viewHolder;
        } else if (viewType == TYPE_FOOTER) {
            View foot_view = mInflater.inflate(R.layout.load_more_layout, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            FootViewHolder footViewHolder = new FootViewHolder(foot_view);
            return footViewHolder;
        }
        return null;
    }

    /**
     * 数据的绑定显示
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            if(position==0){
                ((ItemViewHolder) holder).delete_tv.setVisibility(View.GONE);
            }else{
                ((ItemViewHolder) holder).delete_tv.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).delete_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mList.remove(position);
                        notifyDataSetChanged();
                    }
                });
            }
            XCDJInfo info=mList.get(position);
            ((ItemViewHolder) holder).take_photo_view.setmList(info.getMlist(),true);
            ((ItemViewHolder) holder).take_photo_view.setmOnClickListener(new TakePhotosView.OnClickListener() {
                @Override
                public void onItemClick() {
                    if(mOnClickListener!=null){
                        mOnClickListener.onItemClick(position);
                    }
                }
            });
            ((ItemViewHolder) holder).take_photo_view.setmOnLongClickListener(new TakePhotosView.OnLongClickListener() {
                @Override
                public void onItemLongClick(Photo photo, int i) {
                    info.getMlist().remove(i);
                    ((ItemViewHolder) holder).take_photo_view.setmList(info.getMlist(),true);
                }
            });
        } else if (holder instanceof FootViewHolder) {
            ((FootViewHolder) holder).add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mList.add(new XCDJInfo());
                    notifyDataSetChanged();
                }
            });

        }

    }

    /**
     * 进行判断是普通Item视图还是FootView视图
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        int a=getItemCount();
        if (position == getItemCount()-1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size()+1;
    }


    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.delete_tv)
        TextView delete_tv;
        @Bind(R.id.title_et)
        EditText title_et;
        @Bind(R.id.desc_et)
        EditText desc_et;
        @Bind(R.id.take_photo_view)
        TakePhotosView take_photo_view;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 底部FootView布局
     */
    public static class FootViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.add_btn)
        Button add_btn;
        public FootViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

