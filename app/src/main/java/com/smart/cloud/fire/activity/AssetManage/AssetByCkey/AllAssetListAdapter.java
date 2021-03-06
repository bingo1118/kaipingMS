package com.smart.cloud.fire.activity.AssetManage.AssetByCkey;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smart.cloud.fire.activity.AssetManage.DealAsset.AssetInfoActivity;
import com.smart.cloud.fire.activity.AssetManage.DealAsset.DealAssetActivity;
import com.smart.cloud.fire.activity.AssetManage.DealAsset.UpdateAssetDataActivity;
import com.smart.cloud.fire.activity.AssetManage.DealAsset.UploadAssetProblemActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class AllAssetListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private LayoutInflater mInflater;
    private Context mContext;
    private List<AssetByCkeyEntity> itemsList;
    private AssetByCkeyAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setShowCheck(boolean showCheck) {
        this.showCheck = showCheck;
        notifyDataSetChanged();
        mList=new ArrayList<>();
    }

    private boolean showCheck=false;

    public List<String> getmList() {
        return mList;
    }

    private List<String> mList;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, AssetByCkeyEntity data);
    }

    public void setOnRecyclerViewItemClickListener(AssetByCkeyAdapter.OnRecyclerViewItemClickListener listener){
        this.mOnItemClickListener=listener;
    }

    public AllAssetListAdapter(Context mContext, List<AssetByCkeyEntity> electricList) {
        this.mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.itemsList = electricList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.asset_list_item2, parent, false);
        //????????????????????????????????????????????????????????????
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AssetByCkeyEntity mPoint = itemsList.get(position);

        if(showCheck){
            ((ItemViewHolder) holder).checkbox.setVisibility(View.VISIBLE);
            ((ItemViewHolder) holder).checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        mList.add(mPoint.getId());
                    }else{
                        mList.remove(mPoint.getId());
                    }
                }
            });
        }else{
            ((ItemViewHolder) holder).checkbox.setVisibility(View.GONE);
        }


        ((ItemViewHolder) holder).name_tv.setText(mPoint.getNamed());
        ((ItemViewHolder) holder).mac_tv.setText("????????????:"+mPoint.getAkey());
        ((ItemViewHolder) holder).area_tv.setText("????????????:"+mPoint.getAreaName());
        ((ItemViewHolder) holder).type_tv.setText("????????????:"+mPoint.getAtName());

        ((ItemViewHolder) holder).state_tv.setVisibility(View.GONE);
        ((ItemViewHolder) holder).state_tv.setText(mPoint.getIfFinishName());
        String state="";
        switch (mPoint.getState()){
            case 0:
                state="??????";
                break;
            case 1:
                state="??????";
                break;
            case 2:
                state="?????????";
                break;
        }
        ((ItemViewHolder) holder).state2_tv.setText("??????:"+state);
        ((ItemViewHolder) holder).commit_tv.setVisibility(View.GONE);
        ((ItemViewHolder) holder).commit_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext,DealAssetActivity.class);
                i.putExtra("order",mPoint);
                mContext.startActivity(i);
            }
        });
        ((ItemViewHolder) holder).rela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext,AssetInfoActivity.class);
                i.putExtra("info",mPoint);
                mContext.startActivity(i);
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
            mOnItemClickListener.onItemClick(v, (AssetByCkeyEntity) v.getTag());
        }
    }

    //????????????ViewHolder???????????????Item????????????????????????
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name_tv)
        TextView name_tv;
        @Bind(R.id.state_tv)
        TextView state_tv;
        @Bind(R.id.mac_tv)
        TextView mac_tv;
        @Bind(R.id.area_tv)
        TextView area_tv;
        @Bind(R.id.type_tv)
        TextView type_tv;
        @Bind(R.id.state2_tv)
        TextView state2_tv;
        @Bind(R.id.commit_tv)
        TextView commit_tv;
        @Bind(R.id.rela)
        RelativeLayout rela;
        @Bind(R.id.checkbox)
        CheckBox checkbox;


        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
