package com.smart.cloud.fire.mvp.fragment.ConfireFireFragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.jakewharton.rxbinding.view.RxView;
import com.obsessive.zbar.CaptureActivity;
import com.smart.cloud.fire.GetLocationActivity;
import com.smart.cloud.fire.activity.UploadNFCInfo.FileUtil;
import com.smart.cloud.fire.activity.UploadNFCInfo.FormFile;
import com.smart.cloud.fire.base.ui.MvpFragment;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Camera;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Smoke;
import com.smart.cloud.fire.utils.IntegerTo16;
import com.smart.cloud.fire.utils.JsonUtils;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.Utils;
import com.smart.cloud.fire.view.XCDropDownListView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/9/21.
 */
public class ConfireFireFragment extends MvpFragment<ConfireFireFragmentPresenter> implements ConfireFireFragmentView {
    @Bind(R.id.add_repeater_mac)
    EditText addRepeaterMac;//???????????????
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
    @Bind(R.id.add_fire_man)
    EditText addFireMan;//?????????????????????
    @Bind(R.id.add_fire_man_phone)
    EditText addFireManPhone;//?????????????????????
    @Bind(R.id.add_fire_man_two)
    EditText addFireManTwo;//?????????2.???
    @Bind(R.id.add_fire_man_phone_two)
    EditText addFireManPhoneTwo;//???????????????2.???
    @Bind(R.id.scan_repeater_ma)
    ImageView scanRepeaterMa;
    @Bind(R.id.scan_er_wei_ma)
    ImageView scanErWeiMa;
    @Bind(R.id.location_image)
    ImageView locationImage;
    @Bind(R.id.add_fire_zjq)
    XCDropDownListView addFireZjq;//??????????????????
    @Bind(R.id.add_fire_type)
    XCDropDownListView addFireType;//??????????????????
    @Bind(R.id.add_fire_dev_btn)
    RelativeLayout addFireDevBtn;//????????????????????????
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;//??????????????????
    @Bind(R.id.add_camera_name)
    EditText addCameraName;
    @Bind(R.id.add_camera_relative)
    RelativeLayout addCameraRelative;
    @Bind(R.id.device_type_name)
    TextView device_type_name;
    @Bind(R.id.photo_image)
    ImageView photo_image;//@@????????????
    @Bind(R.id.tip_line)
    LinearLayout tip_line;
    @Bind(R.id.clean_all)
    TextView clean_all;
    @Bind(R.id.yc_mac)
    TextView yc_mac;

    private Context mContext;
    private int scanType = 0;//0????????????????????????1??????????????????
    private int privilege;
    private String userID;
    private ShopType mShopType;
    private Area mArea;
    private String areaId = "";
    private String shopTypeId = "";
    private String camera = "";

    String mac="";
    String devType="";

    private String uploadTime;
    private String imageFilePath;
    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/devimage.jpg");//@@9.30

    Handler handler = new Handler() {//@@9.29
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mProgressBar.setVisibility(View.GONE);
                    break;
                case 1:
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    T.showShort(mContext,"??????????????????");
                    break;
                case 3:
                    T.showShort(mContext,"??????????????????");
                    break;
                case 4:
                    T.showShort(mContext,"????????????");
                    break;
                case 5:
                    T.showShort(mContext,msg.obj.toString());
                    photo_image.setImageResource(R.drawable.add_photo);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_fire, null);
        ButterKnife.bind(this, view);
        if(f.exists()){
            f.delete();
        }//@@9.30
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege2();
        init();
    }

    private void init() {
        Intent intent=getActivity().getIntent();
        String mac=intent.getStringExtra("mac");
        devType=intent.getIntExtra("devType",0)+"";
        addFireMac.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(devType.equals("221")&&!hasFocus){
                    String temp=addFireMac.getText().toString();
                    if(temp.length()!=12){
                        T.showShort(mContext,"????????????MAC???????????????12?????????");
                        return;
                    }
                    if(!Utils.isNumeric(temp)){
                        T.showShort(mContext,"????????????MAC???????????????12?????????");
                        return;
                    }
                    yc_mac.setText("?????????????????????:"+temp);
                    yc_mac.setVisibility(View.VISIBLE);

                    temp=changeYongChuanMac(temp);
                    addFireMac.setText("A"+temp.toUpperCase());
                    T.showShort(mContext,"????????????MAC????????????");
                }
                if (!hasFocus&&addFireMac.getText().toString().length()>0) {
                    mvpPresenter.getOneSmoke(userID, privilege + "", addFireMac.getText().toString());//@@5.5???????????????????????????????????????????????????
                }
            }
        });//@@10.18
        addCameraRelative.setVisibility(View.VISIBLE);
        addFireZjq.setEditTextHint("??????");
        addFireType.setEditTextHint("??????");
        RxView.clicks(addFireDevBtn).throttleFirst(2, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                addFire();
            }
        });

        if (mac!=null){
            addFireMac.setText(mac);
            device_type_name.setVisibility(View.VISIBLE);
            device_type_name.setText("????????????:"+devType);
            mvpPresenter.getOneSmoke(userID, privilege + "", mac);
        }
        photo_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/devimage.jpg";
                File temp = new File(imageFilePath);
                if(!temp.exists()){
                    Uri imageFileUri = Uri.fromFile(temp);//???????????????Uri
                    Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//???????????????Activity
                    it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);//????????????????????????????????????????????????Uri
                    startActivityForResult(it, 102);
                }else{
                    //??????Intent
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(temp), "image/*");
                    startActivity(intent);
                }

            }
        });

        clean_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanAllView();
            }
        });
    }

    private String changeYongChuanMac(String temp) {
        String s="";
        String[] ss=new String[6];//????????????
        for(int i=0;i<temp.length();i+=2){
            ss[i/2]=temp.substring(i,i+2);
        }
        for (String each:ss) {
            String t=Integer.toHexString(Integer.parseInt(each));
            s=(t.length()==2?t:"0"+t)+s;
        }
        return s;
    }

    /**
     * ???????????????????????????????????????
     */
    private void addFire() {
        if (mShopType != null) {
            shopTypeId = mShopType.getPlaceTypeId();
        }
        if (mArea != null) {
            areaId = mArea.getAreaId();
        }
        final String longitude = addFireLon.getText().toString().trim();
        final String latitude = addFireLat.getText().toString().trim();
        final String smokeName = addFireName.getText().toString().trim();
        final String smokeMac = addFireMac.getText().toString().trim();
        final String address = addFireAddress.getText().toString().trim();
        final String placeAddress = "";
        final String principal1 = addFireMan.getText().toString().trim();
        final String principal2 = addFireManTwo.getText().toString().trim();
        final String principal1Phone = addFireManPhone.getText().toString().trim();
        final String principal2Phone = addFireManPhoneTwo.getText().toString().trim();
        final String repeater = addRepeaterMac.getText().toString().trim();
        camera = addCameraName.getText().toString().trim();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess=false;
                boolean isHavePhoto=false;
                uploadTime=System.currentTimeMillis()+"";
                if(imageFilePath!=null){
                    File file = new File(imageFilePath); //?????????path?????????????????????????????????
                    if(f.exists()){
                        isHavePhoto=true;
                    }else{
                        isHavePhoto=false;
                    }
                    if(isHavePhoto){
                        isSuccess=uploadFile(file,userID,areaId,uploadTime,getdevmac(smokeMac),"devimages");
                        if(isSuccess){
                            Message message = new Message();
                            message.what = 3;
                            handler.sendMessage(message);
                        }else{
                            Message message = new Message();
                            message.what =2 ;
                            handler.sendMessage(message);
                        }
                    }


                    if(f.exists()){
                        f.delete();
                    }//@@9.30
                }
                mvpPresenter.addSmoke(userID, privilege + "", smokeName, smokeMac, address, longitude,
                        latitude, placeAddress, shopTypeId, principal1, principal1Phone, principal2,
                        principal2Phone, areaId, repeater, camera,isSuccess);
            }
        }).start();

//        mvpPresenter.addSmoke(userID, privilege + "", smokeName, smokeMac, address, longitude,
//                latitude, placeAddress, shopTypeId, principal1, principal1Phone, principal2,
//                principal2Phone, areaId, repeater, camera);
    }

    @Override
    protected ConfireFireFragmentPresenter createPresenter() {
        ConfireFireFragmentPresenter mConfireFireFragmentPresenter = new ConfireFireFragmentPresenter(ConfireFireFragment.this);
        return mConfireFireFragmentPresenter;
    }

    @Override
    public String getFragmentName() {
        return "ConfireFireFragment";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (addFireZjq.ifShow()) {
            addFireZjq.closePopWindow();
        }
        if (addFireType.ifShow()) {
            addFireType.closePopWindow();
        }
        ButterKnife.unbind(this);
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

    @OnClick({R.id.scan_repeater_ma, R.id.scan_er_wei_ma, R.id.location_image, R.id.add_fire_zjq, R.id.add_fire_type})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_repeater_ma:
                scanType = 0;
                Intent scanRepeater = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(scanRepeater, 0);
                break;
            case R.id.scan_er_wei_ma:
                scanType = 1;
                Intent openCameraIntent = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
                break;
            case R.id.location_image:
                mvpPresenter.startLocation();
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
//        mProgressBar.setVisibility(View.VISIBLE);
        Message message = new Message();
        message.what = 1;
        handler.sendMessage(message);
    }

    @Override
    public void hideLoading() {
//        mProgressBar.setVisibility(View.GONE);
        Message message = new Message();
        message.what = 0;
        handler.sendMessage(message);
    }

    @Override
    public void getDataFail(String msg) {
        T.showShort(mContext, msg);
    }

    @Override
    public void getDataSuccess(Smoke smoke) {
        tip_line.setVisibility(View.VISIBLE);
        addFireLon.setText(smoke.getLongitude() + "");
        addFireLat.setText(smoke.getLatitude() + "");
        addFireAddress.setText(smoke.getAddress());
        addFireName.setText(smoke.getName());
        addFireMan.setText(smoke.getPrincipal1());
        addFireManPhone.setText(smoke.getPrincipal1Phone());
        addFireManTwo.setText(smoke.getPrincipal2());
        addFireManPhoneTwo.setText(smoke.getPrincipal2Phone());
        addFireZjq.setEditTextData(smoke.getAreaName());
        addFireType.setEditTextData(smoke.getPlaceType());//@@10.18
        areaId=smoke.getAreaId()+"";
        shopTypeId=smoke.getPlaceTypeId();//@@10.18
        Camera mCamera = smoke.getCamera();
        if (mCamera != null) {
            addCameraName.setText(mCamera.getCameraId());
        }
        addRepeaterMac.setText(smoke.getRepeater().trim());
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
    public void addSmokeResult(String msg, int errorCode) {
//        T.showShort(mContext, msg);
        if (errorCode == 0) {
            cleanAllView();
            Message message = new Message();
            message.what = 4;
            handler.sendMessage(message);
        }else{
            imageFilePath=null;
            Message message = new Message();
            message.what = 5;
            message.obj=msg;
            handler.sendMessage(message);
        }
        tip_line.setVisibility(View.GONE);
    }

    private void cleanAllView() {
        mShopType = null;
        mArea = null;
        clearText();
        areaId = "";
        shopTypeId = "";
        camera = "";
        addFireMac.setText("");
        addFireZjq.addFinish();
        addFireType.addFinish();
        tip_line.setVisibility(View.GONE);
        yc_mac.setVisibility(View.GONE);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if (resultCode == getActivity().RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString("result");
                    if (scanType == 0) {
                        addRepeaterMac.setText(scanResult);
                    } else {
                        if(scanResult.contains("-")){
                            scanResult=scanResult.substring(scanResult.lastIndexOf("=")+1);
                        }//@@12.26??????nb-iot??????
                        String temp= JsonUtils.isJson(scanResult);
                        if(temp!=null){
                            scanResult=temp;
                        }
                        addFireMac.setText(scanResult);
                        clearText();
                        mvpPresenter.getOneSmoke(userID, privilege + "", scanResult);//@@5.5???????????????????????????????????????????????????
                    }
                }
                break;
            case 1://@@6.20
                if (resultCode == getActivity().RESULT_OK) {
                    Bundle bundle=data.getBundleExtra("data");
                    try{
                        addFireLat.setText(String.format("%.8f",bundle.getDouble("lat")));
                        addFireLon.setText(String.format("%.8f",bundle.getDouble("lon")));
                        addFireAddress.setText(bundle.getString("address"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case 102:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bmp = BitmapFactory.decodeFile(imageFilePath);
                    try {
                        saveFile(compressBySize(Environment.getExternalStorageDirectory().getAbsolutePath()+"/devimage.jpg",1500,2000),Environment.getExternalStorageDirectory().getAbsolutePath()+"/devimage.jpg");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DisplayMetrics dm = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int screenWidth=dm.widthPixels;
                    if(bmp.getWidth()<=screenWidth){
                        photo_image.setImageBitmap(bmp);
                    }else{
                        Bitmap mp=Bitmap.createScaledBitmap(bmp, screenWidth, bmp.getHeight()*screenWidth/bmp.getWidth(), true);
                        photo_image.setImageBitmap(mp);
                    }
//                    photo_image.setImageBitmap(bmp);
                }
                break;
            case 103:
                Bitmap bm = null;
                // ?????????????????????ContentProvider??????????????? ????????????ContentResolver??????
                ContentResolver resolver = getActivity().getContentResolver();

                try {
                    Uri originalUri = data.getData(); // ???????????????uri

                    bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); // ?????????bitmap??????

                    // ??????????????????????????????????????????????????????

                    String[] proj = {MediaStore.Images.Media.DATA};

                    // ?????????android????????????????????????????????????????????????Android??????
                    @SuppressWarnings("deprecation")
                    Cursor cursor = getActivity().managedQuery(originalUri, proj, null, null, null);
                    // ?????????????????? ????????????????????????????????????????????????
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    // ????????????????????? ???????????????????????????????????????????????????
                    cursor.moveToFirst();
                    // ???????????????????????????????????????
                    String path = cursor.getString(column_index);
                    photo_image.setImageURI(originalUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    /**
     * ?????????????????????????????????
     */
    private void clearText() {
        addFireLon.setText("");
        addFireLat.setText("");
        addFireAddress.setText("");
        addFireName.setText("");
        addFireMan.setText("");
        addFireManPhone.setText("");
        addFireManTwo.setText("");
        addFireManPhoneTwo.setText("");
        addFireZjq.setEditTextData("");
        addFireType.setEditTextData("");
        addCameraName.setText("");
        photo_image.setImageResource(R.drawable.add_photo);
        imageFilePath=null;
    }

    public static boolean uploadFile(File imageFile,String userId,String areaId,String uploadtime) {
        try {
            String requestUrl = ConstantValues.SERVER_IP_NEW+"UploadFileAction";
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", userId);
            params.put("areaId", areaId);
            params.put("time", uploadtime);
            FormFile formfile = new FormFile(imageFile.getName(), imageFile, "image", "application/octet-stream");
            FileUtil.post(requestUrl, params, formfile);
            System.out.println("Success");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Fail");
            return false;
        }
    }

    public static boolean uploadFile(File imageFile,String userId,String areaId,String uploadtime,String mac,String location) {
        try {
            String requestUrl = ConstantValues.SERVER_IP_NEW+"UploadFileAction";
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", userId);
            params.put("areaId", areaId);
            params.put("time", uploadtime);
            params.put("mac", mac);
            params.put("location", location);
            FormFile formfile = new FormFile(imageFile.getName(), imageFile, "image", "application/octet-stream");
            FileUtil.post(requestUrl, params, formfile);
            System.out.println("Success");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Fail");
            return false;
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

    public String getdevmac(String smokeMac){
        String deviceType="";
        String macStr = (String) smokeMac.subSequence(0, 1);
        if(smokeMac.length()==15){
//            deviceType="14";//GPS
            deviceType="41";//??????NB
        }else if (smokeMac.length()==6){
            deviceType="70";//????????????
        }else if (smokeMac.length()==7){
            if (macStr.equals("W")){//@@9.29 ??????NB
                deviceType="69";//@@????????????
            }
            smokeMac = smokeMac.substring(1, smokeMac.length());
        }else if (smokeMac.length()==12){
            deviceType="51";//??????
        }else if(smokeMac.length()==16||smokeMac.length()==18){
            switch(macStr){
                case "A":
                    smokeMac = smokeMac.substring(1, smokeMac.length());//????????????????????????
                    deviceType="119";
                    break;
                case "W":
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("W")){
                        deviceType="19";//@@??????2018.01.02
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("A")){
                        deviceType="124";//@@????????????2018.01.30
                        smokeMac =smokeMac.substring(0,smokeMac.length()-1);
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("B")){
                        deviceType="125";//@@????????????2018.01.30
                        smokeMac =smokeMac.substring(0,smokeMac.length()-1);
                    }else{
                        deviceType="10";//@@??????
                    }
                    smokeMac = smokeMac.replace("W","");//????????????
                    break;
                case "Z":
                    smokeMac = smokeMac.substring(1, smokeMac.length());//????????????
                    deviceType="55";
                    break;
                default:
                    deviceType="21";//loraOne??????
                    break;
            }
        }else{
            switch (macStr){
                case "R":
                    if ((smokeMac.charAt(smokeMac.length()-1)+"").equals("R")){//@@9.29 ??????NB
                        deviceType="16";//@@NB??????
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("N")){
                        deviceType="22";
                    }else{
                        deviceType="2";//@@??????
                    }
                    smokeMac = smokeMac.replace("R","");//??????
                    smokeMac = smokeMac.replace("N","");//??????
                    break;
                case "Q":
                    deviceType="5";
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("Q")){
                    }//@@8.26
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("S")){
                    }//@@2018.01.18 ????????????
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("L")){
                        deviceType="52";
                    }//@@2018.05.15 Lara????????????
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("N")){
                        deviceType="53";
                    }//@@2018.05.15 Lara????????????
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("G")){
                        deviceType="59";
                    }//@@NB??????????????????
                    smokeMac = smokeMac.replace("Q","");//????????????
                    smokeMac = smokeMac.replace("S","");//????????????
                    smokeMac = smokeMac.replace("L","");//????????????
                    smokeMac = smokeMac.replace("N","");//????????????
                    smokeMac = smokeMac.replace("G","");//????????????

                    break;
                case "T":
                    smokeMac = smokeMac.replace("T","");//???????????????
                    deviceType="25";
                    break;
                case "A":
                    smokeMac = smokeMac.substring(1, smokeMac.length());
                    deviceType="119";
                    break;
                case "G":
                    smokeMac = smokeMac.replace("G","");//??????????????? 6
                    deviceType="7";
                    break;
                case "K":
                    smokeMac = smokeMac.replace("K","");//@@????????????????????????2018.01.24
                    deviceType="20";
                    break;
                case "S":
                    smokeMac = smokeMac.replace("S","");//????????????????????? 7
                    deviceType="8";
                    break;
                case "J":
                    smokeMac = smokeMac.replace("J","");//????????????
                    deviceType="9";
                    break;
                case "W":
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("W")){
                        deviceType="19";//@@??????2018.01.02
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("C")){
                        deviceType="42";//@@NB??????
                        smokeMac =smokeMac.substring(0,smokeMac.length()-1);
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("L")){
                        deviceType="43";//@@Lara??????
                        smokeMac =smokeMac.substring(0,smokeMac.length()-1);
                    }else{
                        deviceType="10";//@@??????
                    }
                    smokeMac = smokeMac.replace("W","");//????????????
                    smokeMac = smokeMac.replace("L","");//????????????
                    break;
                case "L":
                    smokeMac = smokeMac.replace("L","");//????????????
                    deviceType="11";
                    break;
                case "M":
                    smokeMac = smokeMac.replace("M","");//????????????
                    deviceType="12";
                    break;
                case "N":
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("N")){
                        deviceType="56";//@@NB-iot??????
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("O")){
                        deviceType="57";//@@onet??????
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("R")){
                        deviceType="45";//@@????????????
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("Z")){
                        deviceType="58";//@@??????????????????
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("H")){
                        deviceType="35";//@@?????? ??????
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("I")){
                        deviceType="36";//@@??????????????????
                    }else{
                        deviceType="41";
                    }
                    smokeMac = smokeMac.replace("N","");//NB????????????
                    smokeMac = smokeMac.replace("O","");
                    smokeMac = smokeMac.replace("R","");
                    smokeMac = smokeMac.replace("Z","");
                    smokeMac = smokeMac.replace("H","");
                    smokeMac = smokeMac.replace("I","");
                    break;
                case "H":
                    smokeMac = smokeMac.replace("H","");//???????????????
                    deviceType="13";
                    break;
                case "Y":
                    smokeMac = smokeMac.replace("Y","");//??????
                    deviceType="15";
                    break;
                case "P":
                    smokeMac = smokeMac.replace("P","");//10.31??????
                    deviceType="18";
                    break;
            }
        }
        return smokeMac;
    }


}
