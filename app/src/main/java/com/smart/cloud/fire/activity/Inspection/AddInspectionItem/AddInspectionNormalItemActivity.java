package com.smart.cloud.fire.activity.Inspection.AddInspectionItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.jakewharton.rxbinding.view.RxView;
import com.smart.cloud.fire.GetLocationActivity;
import com.smart.cloud.fire.activity.AddNFC.NFCDeviceType;
import com.smart.cloud.fire.activity.AddNFC.NFCInfo;
import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.Point;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Smoke;
import com.smart.cloud.fire.utils.NFCHelper;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.UploadUtil;
import com.smart.cloud.fire.utils.Utils;
import com.smart.cloud.fire.view.SelectPhotoView;
import com.smart.cloud.fire.view.XCDropDownListView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class AddInspectionNormalItemActivity extends MvpActivity<AddInspectionItemPresenter> implements AddInspectionItemView {

    @Bind(R.id.add_fire_mac)
    EditText addFireMac;//???????????????
    @Bind(R.id.add_fire_name)
    EditText addFireName;//??????????????????
    @Bind(R.id.add_fire_lat)
    EditText addFireLat;//????????????
    @Bind(R.id.add_fire_lon)
    EditText addFireLon;//????????????
    @Bind(R.id.add_fire_address)
    EditText addFireAddress;//??????????????????
    @Bind(R.id.makeTime_edit)
    EditText makeTime_text;//????????????@@11.16
    @Bind(R.id.makeAddress_edit)
    EditText makeAddress_edit;//????????????@@11.28
    @Bind(R.id.scan_er_wei_ma)
    ImageView scanErWeiMa;
    @Bind(R.id.location_image)
    ImageView locationImage;
    @Bind(R.id.add_fire_zjq)
    XCDropDownListView addFireZjq;//??????????????????
    @Bind(R.id.add_fire_point)
    XCDropDownListView add_fire_point;//?????????????????????
    @Bind(R.id.add_fire_type)
    XCDropDownListView addFireType;//??????????????????
    @Bind(R.id.add_fire_dev_btn)
    TextView addFireDevBtn;//????????????????????????
    @Bind(R.id.makeTime_rela)
    RelativeLayout makeTime_rela;//????????????
    @Bind(R.id.select_photo_view)
    SelectPhotoView select_photo_view;//@@????????????
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;//??????????????????
    @Bind(R.id.add_camera_name)
    EditText addCameraName;
    @Bind(R.id.producer_edit)
    EditText producer_edit;
    @Bind(R.id.memo_edit)
    EditText memo_edit;
    @Bind(R.id.info_line)
    LinearLayout info_line;//@@11.16

    private Context mContext;
    private int privilege;
    private String userID;
    private ShopType mShopType;
    private Point mPoint;
    private Area mArea;
    private NFCDeviceType nfcDeviceType;//@@8.16
    private String areaId = "";
    private String shopTypeId = "";


    private NFCInfo nfcInfo;

    private static final int DATE_DIALOG_ID = 1;
    private static final int SHOW_DATAPICK = 0;
    private int mYear;
    private int mMonth;
    private int mDay;

    String getDate;
    int fromOrto=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inspection_normal_item);

        ButterKnife.bind(this);
        mContext = this;
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        select_photo_view.setActivity(this);
        init();
    }


    private void init() {
        addFireZjq.setEditTextHint("??????");
        addFireType.setEditTextHint("??????");
        add_fire_point.setEditTextHint("?????????");
        makeTime_text.setOnClickListener(new AddInspectionNormalItemActivity.DateButtonOnClickListener());//@@11.16
        RxView.clicks(addFireDevBtn).throttleFirst(2, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                addFire();
            }
        });
        nfcInfo=new NFCInfo();


        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        setDateTime();
        String a=System.currentTimeMillis()+"";
        addFireMac.setText(a);
    }


    /**
     * ???????????????????????????????????????
     */
    private void addFire() {
        if (nfcDeviceType != null) {
            shopTypeId = nfcDeviceType.getPlaceTypeId();//@@8.16
        }
        if (mArea != null) {
            areaId = mArea.getAreaId();
        }
        String longitude = addFireLon.getText().toString().trim();
        String latitude = addFireLat.getText().toString().trim();
        String smokeName = addFireName.getText().toString().trim();
        String smokeMac = addFireMac.getText().toString().trim();
        String address = addFireAddress.getText().toString().trim();

        String producer=producer_edit.getText().toString().trim();
        String makeTime=makeTime_text.getText().toString().trim();
        String makeAddress=makeAddress_edit.getText().toString().trim();

        String memo=memo_edit.getText().toString().trim();

        if(longitude.length()==0||latitude.length()==0){
            toast("??????????????????");
            return;
        }
        if(smokeName.length()==0||smokeName.length()==0){
            toast("???????????????");
            return;
        }
        if(smokeMac.length()==0){
            toast("??????????????????MAC");
            return;
        }
        if(areaId==null||areaId.length()==0){
            toast("??????????????????");
            return;
        }
        if(shopTypeId==null||shopTypeId.length()==0){
            toast("??????????????????");
            return;
        }
        nfcInfo=new NFCInfo(smokeMac,longitude,latitude,areaId,mArea.getAreaName(),shopTypeId,nfcDeviceType.getPlaceTypeName(),smokeName,address,producer,makeTime,makeAddress,"");
//        mvpPresenter.addNFCInspectItem(userID, privilege + "", nfcInfo.getDeviceName(), nfcInfo.getUid(), nfcInfo.getAddress(),
//                nfcInfo.getLon(), nfcInfo.getLat(), nfcInfo.getDeviceTypeId(),nfcInfo.getAreaId(),nfcInfo.getProducer(),
//                nfcInfo.getMakeTime(),nfcInfo.getMakeAddress(),memo,mPoint.getPid(),"");

        final String photo=System.currentTimeMillis()+"";
        String photo1="";
        showLoading();
        if(select_photo_view.isPhotoExist()){
            photo1=photo;
            Observable.create(new Observable.OnSubscribe<String>() {
                @Override public void call(Subscriber<? super String> subscriber) {
                    boolean isSuccess= select_photo_view.upload(photo,"oriImgs");
                    if(isSuccess){
                        subscriber.onNext("Success");
                    }else{
                        subscriber.onNext("Fail");
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String>()
                    {
                        @Override public void onCompleted() {
                        }
                        @Override public void onError(Throwable e) {
                        }
                        @Override public void onNext(String s) {
                            if(s.equals("Success")){
                                T.showShort(mContext,"??????????????????");
                                mvpPresenter.addNFCInspectItem(userID, privilege + "", nfcInfo.getDeviceName(), nfcInfo.getUid(), nfcInfo.getAddress(),
                                        nfcInfo.getLon(), nfcInfo.getLat(), nfcInfo.getDeviceTypeId(),nfcInfo.getAreaId(),nfcInfo.getProducer(),
                                        nfcInfo.getMakeTime(),nfcInfo.getMakeAddress(),memo,mPoint.getPid(),photo);
                            }else{
                                T.showShort(mContext,"??????????????????");
                                mvpPresenter.addNFCInspectItem(userID, privilege + "", nfcInfo.getDeviceName(), nfcInfo.getUid(), nfcInfo.getAddress(),
                                        nfcInfo.getLon(), nfcInfo.getLat(), nfcInfo.getDeviceTypeId(),nfcInfo.getAreaId(),nfcInfo.getProducer(),
                                        nfcInfo.getMakeTime(),nfcInfo.getMakeAddress(),memo,mPoint.getPid(),"");
                            }
                        }
                    });
        }else{
            mvpPresenter.addNFCInspectItem(userID, privilege + "", nfcInfo.getDeviceName(), nfcInfo.getUid(), nfcInfo.getAddress(),
                    nfcInfo.getLon(), nfcInfo.getLat(), nfcInfo.getDeviceTypeId(),nfcInfo.getAreaId(),nfcInfo.getProducer(),
                    nfcInfo.getMakeTime(),nfcInfo.getMakeAddress(),memo,mPoint.getPid(),"");
        }


    }

    @Override
    protected AddInspectionItemPresenter createPresenter() {
        AddInspectionItemPresenter addNFCPresenter = new AddInspectionItemPresenter(this);
        return addNFCPresenter;
    }




    @Override
    public void onDestroy() {
        mvpPresenter.stopLocation();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        mvpPresenter.initLocation();
        super.onStart();
    }

    @OnClick({ R.id.location_image, R.id.add_fire_zjq, R.id.add_fire_type,R.id.add_fire_point})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.location_image:
                Intent intent=new Intent(mContext, GetLocationActivity.class);
                startActivityForResult(intent,1);//@@6.20
                break;
            case R.id.add_fire_zjq:
                if (addFireZjq.ifShow()) {
                    addFireZjq.closePopWindow();
                } else {
                    mvpPresenter.getPlaceTypeId(userID, privilege + "", 2);
                    addFireZjq.setClickable(false);
                    addFireZjq.showLoading();
                }
                break;
            case R.id.add_fire_type:
                if (addFireType.ifShow()) {
                    addFireType.closePopWindow();
                } else {
                    mvpPresenter.getPlaceTypeId(userID, privilege + "", 1);
                    addFireType.setClickable(false);
                    addFireType.showLoading();
                }
                break;
            case R.id.add_fire_point:
                if (add_fire_point.ifShow()) {
                    add_fire_point.closePopWindow();
                } else {
                    if(mArea==null){
                        T.showShort(mContext,"??????????????????");
                        return;
                    }
                    mvpPresenter.getPointsId(mArea.getAreaId());
                    add_fire_point.setClickable(false);
                    add_fire_point.showLoading();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void getLocationData(BDLocation location) {
        addFireLon.setText(location.getLongitude() + "");
        addFireAddress.setText(location.getAddrStr());
        addFireLat.setText(location.getLatitude() + "");
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
    public void getDataFail(String msg) {
        T.showShort(mContext, msg);
    }

    @Override
    public void getDataSuccess(Smoke smoke) {

    }

    @Override
    public void getPoints(ArrayList<Object> shopTypes) {
        add_fire_point.setItemsData(shopTypes,mvpPresenter);
        add_fire_point.showPopWindow();
        add_fire_point.setClickable(true);
        add_fire_point.closeLoading();
    }

    @Override
    public void getPointsFail(String msg) {
        T.showShort(mContext, msg);
        add_fire_point.setClickable(true);
        add_fire_point.closeLoading();
    }

    @Override
    public void getShopType(ArrayList<Object> shopTypes) {
        addFireType.setItemsData(shopTypes,mvpPresenter);
        addFireType.showPopWindow();
        addFireType.setClickable(true);
        addFireType.closeLoading();
    }

    @Override
    public void getShopTypeFail(String msg) {
        T.showShort(mContext, msg);
        addFireType.setClickable(true);
        addFireType.closeLoading();
    }

    @Override
    public void getNFCDeviceType(ArrayList<Object> deviceTypes) {
        addFireType.setItemsData(deviceTypes,mvpPresenter);
        addFireType.showPopWindow();
        addFireType.setClickable(true);
        addFireType.closeLoading();
    }

    @Override
    public void getNFCDeviceTypeFail(String msg) {
        T.showShort(mContext, msg);
        addFireType.setClickable(true);
        addFireType.closeLoading();
    }

    @Override
    public void getAreaType(ArrayList<Object> shopTypes) {
        addFireZjq.setItemsData(shopTypes,mvpPresenter);
        addFireZjq.showPopWindow();
        addFireZjq.setClickable(true);
        addFireZjq.closeLoading();
    }

    @Override
    public void getAreaTypeFail(String msg) {
        T.showShort(mContext, msg);
        addFireZjq.setClickable(true);
        addFireZjq.closeLoading();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1://@@6.20
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle=data.getBundleExtra("data");
                    addFireLat.setText(String.format("%.8f",bundle.getDouble("lat")));
                    addFireLon.setText(String.format("%.8f",bundle.getDouble("lon")));
                    addFireAddress.setText(bundle.getString("address"));
                }
                break;
            case 102:
//                if (resultCode == Activity.RESULT_OK) {
//                    Bitmap bmp = BitmapFactory.decodeFile(imageFilePath);
//                    try {
//                        saveFile(compressBySize(Environment.getExternalStorageDirectory().getAbsolutePath()+"/devimage.jpg",1500,2000),Environment.getExternalStorageDirectory().getAbsolutePath()+"/devimage.jpg");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    DisplayMetrics dm = new DisplayMetrics();
//                    getWindowManager().getDefaultDisplay().getMetrics(dm);
//                    int screenWidth=dm.widthPixels;
//                    if(bmp.getWidth()<=screenWidth){
//                        photo_image.setImageBitmap(bmp);
//                    }else{
//                        Bitmap mp=Bitmap.createScaledBitmap(bmp, screenWidth, bmp.getHeight()*screenWidth/bmp.getWidth(), true);
//                        photo_image.setImageBitmap(mp);
//                    }
//                }
                select_photo_view.onActivityResult(data);
                break;
        }

    }

    //@@10.12???????????????sd???
    public void saveFile(Bitmap bm, String fileName) throws Exception {
        File dirFile = new File(fileName);//????????????????????????
        if(dirFile.exists()){
            dirFile.delete();  //???????????????
        }
        File myCaptureFile = new File(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));//100????????????????????????70??????????????????30%
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
    }

    //@@10.12??????????????????
    public Bitmap compressBySize(String pathName, int targetWidth,
                                 int targetHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;// ?????????????????????????????????????????????????????????????????????????????????
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);// ?????????????????????????????????
        float imgWidth = opts.outWidth;
        float imgHeight = opts.outHeight;// ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        opts.inSampleSize = 1;
        if (widthRatio > 1 || widthRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }//???????????????????????????????????????????????????
        opts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(pathName, opts);
        return bitmap;
    }

    @Override
    public void addSmokeResult(String msg, int errorCode) {
        T.showShort(mContext, msg);
        if (errorCode == 0) {
            mShopType = null;
            mArea = null;
            clearText();
            shopTypeId = "";
            addFireMac.setText("");
            addFireZjq.addFinish();
            addFireType.addFinish();
            add_fire_point.addFinish();
        }
    }

    @Override
    public void getChoiceArea(Area area) {
        mArea = area;
    }

    @Override
    public void getChoiceShop(ShopType shopType) {
        mShopType = shopType;
    }

    @Override
    public void getChoicePoint(Point point) {
        mPoint=point;
    }

    @Override
    public void getChoiceNFCDeviceType(NFCDeviceType nfcDeviceType) {
        this.nfcDeviceType=nfcDeviceType;
    }




    /**
     * ?????????????????????????????????
     */
    private void clearText() {
        producer_edit.setText("");//@@11.16
        makeTime_text.setText("");//@@11.16
        addFireLon.setText("");
        addFireLat.setText("");
        addFireAddress.setText("");
        addFireName.setText("");
        addFireType.setEditTextData("");
        addFireZjq.setEditTextData("");//@@10.19
        add_fire_point.setEditTextData("");
        addCameraName.setText("");
        makeAddress_edit.setText("");//@@11.28
        select_photo_view.deleteTempPhoto();
    }


    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void setDateTime() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
//        updateDisplay(c);
    }

    private class DateButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Message msg = new Message();
            msg.what = SHOW_DATAPICK;
            saleHandler.sendMessage(msg);
        }
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
        }
        return null;
    }
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
        }
    }

    /**
     * ?????????????????????Handler
     */
    Handler saleHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_DATAPICK:
                    showDialog(DATE_DIALOG_ID);
                    break;
            }
        }
    };
    /**
     * ?????????????????????
     */

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDisplay(null);
        }
    };
    /**
     * ????????????
     */
    private void updateDisplay(Calendar c) {
        getDate=new StringBuilder().append(mYear).append(
                (mMonth + 1) < 10 ? "-0" + (mMonth + 1) : "-"+(mMonth + 1)).append(
                (mDay < 10) ? "-0" + mDay : "-"+mDay).toString();
        makeTime_text.setText(getDate);
    }
}
