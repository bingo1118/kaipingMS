package com.smart.cloud.fire.activity.Inspection.ItemsList;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.smart.cloud.fire.activity.Inspection.AddInspectionItem.UpdateItemInfoActivity;
import com.smart.cloud.fire.activity.Inspection.InspHistory.InspHistoryActivity;
import com.smart.cloud.fire.activity.Inspection.UploadInspectionInfo.UploadInspectionInfoActivity;
import com.smart.cloud.fire.activity.UploadNFCInfo.UploadNFCInfoActivity;
import com.smart.cloud.fire.global.NFCInfoEntity;
import com.smart.cloud.fire.global.Point;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class ItemsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private LayoutInflater mInflater;
    private Context mContext;
    private List<NFCInfoEntity> itemsList;
    private ItemsListPresenter mPresenter;
    private ItemsListAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private String tid;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Point data);
    }

    public void setOnRecyclerViewItemClickListener(ItemsListAdapter.OnRecyclerViewItemClickListener listener){
        this.mOnItemClickListener=listener;
    }

    public ItemsListAdapter(Context mContext, List<NFCInfoEntity> electricList,ItemsListPresenter presenter,String tid) {
        this.mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.itemsList = electricList;
        this.mPresenter = presenter;
        this.tid=tid;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.inspection_itemlist_item, parent, false);
        //????????????????????????????????????????????????????????????
        ItemsListAdapter.ItemViewHolder viewHolder = new ItemsListAdapter.ItemViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NFCInfoEntity mPoint = itemsList.get(position);
        ((ItemsListAdapter.ItemViewHolder) holder).name_tv.setText(mPoint.getDeviceName());
        String state="";
        ((ItemViewHolder) holder).state_tv.setVisibility(View.VISIBLE);
        switch (mPoint.getStatus()){
            case "0":
                ((ItemViewHolder) holder).turn_to_insp.setVisibility(View.GONE);
                switch (mPoint.getDevicestate()){
                    case 0:
                        state="?????????";
                        break;
                    case 1:
                        state="??????";
                        break;
                    case 2:
                        state="?????????";
                        break;
                    case 3:
                        state="??????";
                        break;
                }
                break;
            case "1":
                state="??????";
                ((ItemViewHolder) holder).turn_to_insp.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).modify.setVisibility(View.GONE);
                ((ItemViewHolder) holder).turn_to_update.setVisibility(View.GONE);
                break;
            case "2":
                state="?????????";
                ((ItemViewHolder) holder).turn_to_insp.setVisibility(View.GONE);
                ((ItemViewHolder) holder).modify.setVisibility(View.GONE);
//                ((ItemViewHolder) holder).turn_to_update.setVisibility(View.VISIBLE);
                break;
            case "3":
                state="??????";
                ((ItemViewHolder) holder).turn_to_insp.setVisibility(View.GONE);
                ((ItemViewHolder) holder).modify.setVisibility(View.GONE);
//                ((ItemViewHolder) holder).turn_to_update.setVisibility(View.VISIBLE);
                break;
        }


        ((ItemViewHolder) holder).worker_tv.setText("??????:"+mPoint.getAddress());
        ((ItemViewHolder) holder).state_tv.setText("??????:"+state);
        ((ItemViewHolder) holder).id_tv.setText("??????:"+mPoint.getUid());

        ((ItemViewHolder) holder).turn_to_insp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, UploadInspectionInfoActivity.class);
                intent.putExtra("uid",mPoint.getUid());
                intent.putExtra("pid",mPoint.getPid());
                intent.putExtra("tid",tid);
                intent.putExtra("memo",mPoint.getMemo());
                intent.putExtra("tuid",mPoint.getTuid());
                intent.putExtra("startdate",mPoint.getStartdate());
                intent.putExtra("enddate",mPoint.getEnddate());
                intent.putExtra("tasktype",mPoint.getTasktype());
                mContext.startActivity(intent);
            }
        });
        ((ItemViewHolder) holder).modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, UploadInspectionInfoActivity.class);
                intent.putExtra("uid",mPoint.getUid());
                intent.putExtra("tid",tid);
                intent.putExtra("memo",mPoint.getMemo());
                intent.putExtra("modify","1");
                mContext.startActivity(intent);
            }
        });
        ((ItemViewHolder) holder).turn_to_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, UpdateItemInfoActivity.class);
                intent.putExtra("uid",mPoint.getUid());
                mContext.startActivity(intent);
            }
        });
        ((ItemViewHolder) holder).rela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, InspHistoryActivity.class);
                intent.putExtra("uid",mPoint.getUid());
                mContext.startActivity(intent);
            }
        });
        holder.itemView.setTag(mPoint);
    }

    @Override
    public int getItemCount() {

        return itemsList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //??????????????????getTag??????????????????
            mOnItemClickListener.onItemClick(v, (Point) v.getTag());
        }
    }

    //????????????ViewHolder???????????????Item????????????????????????
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name_tv)
        TextView name_tv;
        @Bind(R.id.state_tv)
        TextView state_tv;
        @Bind(R.id.id_tv)
        TextView id_tv;
        @Bind(R.id.worker_tv)
        TextView worker_tv;
        @Bind(R.id.turn_to_insp)
        Button turn_to_insp;
        @Bind(R.id.turn_to_update)
        Button turn_to_update;
        @Bind(R.id.modify)
        Button modify;
        @Bind(R.id.rela)
        RelativeLayout rela;


        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
