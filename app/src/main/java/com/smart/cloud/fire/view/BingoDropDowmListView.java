package com.smart.cloud.fire.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smart.cloud.fire.base.presenter.BasePresenter;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpAreaResult;
import com.smart.cloud.fire.rxjava.ApiCallback;
import com.smart.cloud.fire.rxjava.SubscriberCallBack;

import java.util.ArrayList;
import java.util.List;

import fire.cloud.smart.com.smartcloudfire.R;
import rx.Observable;
import rx.functions.Func1;

public class BingoDropDowmListView extends LinearLayout {

    private RelativeLayout relativeLayout;
    private TextView editText;
    private ImageView imageView;
    private PopupWindow popupWindow = null;
    private List<BingoViewModel> dataList =  new ArrayList<>();
    private Context mContext;
    ProgressBar loading_prg_monitor;
    private RelativeLayout clear_choice;

    public String getSelecedName() {
        return selecedName;
    }

    public String getSelecedId() {
        return selecedId;
    }

    String selecedName;
    String selecedId;

    private OnSelectedItem mOnSelectedItem;

    public void setmOnClick(OnClick mOnClickListener) {
        this.mOnClick = mOnClickListener;
    }

    private OnClick mOnClick;

    public void setmOnSelectedItem(OnSelectedItem mOnSelectedItem) {
        this.mOnSelectedItem = mOnSelectedItem;
    }

    public interface OnSelectedItem{
        public void onSelectedItem(String selectrdItemId);
    }

    public interface OnClick{
        public void onClick();
    }


    public BingoDropDowmListView(Context context) {
        this(context,null);
        // TODO Auto-generated constructor stub
    }
    public BingoDropDowmListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }
    public BingoDropDowmListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        initView();
    }

    public void initView(){
        mContext = getContext();
        String infServie = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater;
        layoutInflater =  (LayoutInflater) getContext().getSystemService(infServie);
        View view  = layoutInflater.inflate(R.layout.dropdownlist_view, this,true);
        loading_prg_monitor = (ProgressBar) findViewById(R.id.loading_prg_monitor);
        editText= (TextView)findViewById(R.id.text);
        imageView = (ImageView)findViewById(R.id.btn);
        clear_choice = (RelativeLayout) findViewById(R.id.clear_choice);
        relativeLayout= (RelativeLayout) findViewById(R.id.compound);
        relativeLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(mOnClick!=null){
                    mOnClick.onClick();
                }
                if (popupWindow == null) {
                    showPopWindow();
                } else {
                    closePopWindow();
                }
            }
        });
        clear_choice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataList.size() > 0) {
                    selecedName=null;
                    selecedId=null;

                    imageView.setVisibility(View.VISIBLE);
                    clear_choice.setVisibility(View.GONE);
                    editText.setText("");
                }
            }
        });
    }

    public void showLoading(){
        imageView.setVisibility(View.GONE);
        loading_prg_monitor.setVisibility(View.VISIBLE);
    }

    public void closeLoading(){
        loading_prg_monitor.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
    }
    /**
     * ????????????????????????
     */
    public void showPopWindow() {
        if(dataList==null||dataList.size()==0){
            return;
        }
        // ??????popupWindow???????????????
        String infServie = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater;
        layoutInflater =  (LayoutInflater) getContext().getSystemService(infServie);
        View contentView  = layoutInflater.inflate(R.layout.dropdownlist_popupwindow, null,false);
        ListView listView = (ListView)contentView.findViewById(R.id.listView);

        listView.setAdapter(new BingoDropDowmListView.XCDropDownListAdapter(getContext(), dataList));
        popupWindow = new PopupWindow(contentView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(this);
    }
    /**
     * ????????????????????????
     */
    public void closePopWindow(){
        if(popupWindow!=null){
            popupWindow.dismiss();
        }
        popupWindow = null;
    }

    public boolean ifShow(){
        if (popupWindow == null) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * ????????????
     * @param list
     */
    public void setItemsData(List<BingoViewModel> list){
        dataList = list;
        editText.setText("");
    }

    public void setEditTextData(String editTextData){
        editText.setText(editTextData);
    }
    /**
     * ???????????????
     * @author caizhiming
     *
     */
    class XCDropDownListAdapter extends BaseAdapter {

        Context mContext;
        List<BingoViewModel> mData;
        LayoutInflater inflater;
        public XCDropDownListAdapter(Context ctx,List<BingoViewModel> data){
            mContext  = ctx;
            mData = data;
            inflater = LayoutInflater.from(mContext);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            // ???????????????
            BingoDropDowmListView.ListItemView listItemView = null;
            if (convertView == null) {
                // ??????list_item?????????????????????
                convertView = inflater.inflate(R.layout.dropdown_list_item, null);

                listItemView = new BingoDropDowmListView.ListItemView();
                // ??????????????????
                listItemView.tv = (TextView) convertView
                        .findViewById(R.id.tv);
                listItemView.layout = (LinearLayout) convertView.findViewById(R.id.layout_container);
                // ??????????????????convertView
                convertView.setTag(listItemView);
            } else {
                listItemView = (BingoDropDowmListView.ListItemView) convertView.getTag();
            }
            BingoViewModel model = mData.get(position);

            // ????????????
            listItemView.tv.setText(model.getModelName());
            final String text = model.getModelName();
            listItemView.layout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    editText.setText(text);
                    imageView.setVisibility(View.GONE);
                    clear_choice.setVisibility(View.VISIBLE);
                    selecedId=model.getModelId();
                    selecedName=model.getModelName();
                    if(mOnSelectedItem!=null){
                        mOnSelectedItem.onSelectedItem(selecedId);
                    }
                    closePopWindow();
                }
            });

            return convertView;
        }

    }
    private static class ListItemView{
        TextView tv;
        LinearLayout layout;
    }

    public void addFinish(){
        imageView.setVisibility(View.VISIBLE);
        clear_choice.setVisibility(View.GONE);
    }

    public void setEditTextHint(String textStr){
        editText.setHint(textStr);
    }

    public void initData(String Type){
        Observable mObservable ;
        mObservable= BasePresenter.apiStores1.getAreaId(MyApp.getUserID(), MyApp.getPrivilege()+"","").map(new Func1<HttpAreaResult,ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call(HttpAreaResult o) {
                return o.getSmoke();
            }
        });

        BasePresenter.addSubscription(mObservable,new SubscriberCallBack<>(new ApiCallback<ArrayList<BingoViewModel>>() {
            @Override
            public void onSuccess(ArrayList<BingoViewModel> model) {
                if(model!=null&&model.size()>0){
                    dataList=model;
                }
            }
            @Override
            public void onFailure(int code, String msg) {

            }
            @Override
            public void onCompleted() {
            }
        }));
    }

}
