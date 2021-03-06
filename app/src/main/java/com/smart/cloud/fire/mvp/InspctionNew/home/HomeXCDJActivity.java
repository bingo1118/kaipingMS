package com.smart.cloud.fire.mvp.InspctionNew.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.smart.cloud.fire.activity.UploadNFCInfo.FileUtil;
import com.smart.cloud.fire.activity.UploadNFCInfo.FormFile;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.service.LocationService;
import com.smart.cloud.fire.utils.ImageUtils;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.SignView.SignatureActivity;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.TimeFormat;
import com.smart.cloud.fire.utils.UploadUtil;
import com.smart.cloud.fire.utils.VolleyHelper;
import com.smart.cloud.fire.view.SighList.AddSighView;
import com.smart.cloud.fire.view.TakePhoto.Photo;
import com.smart.cloud.fire.view.TakePhoto.TakePhotosView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class HomeXCDJActivity extends Activity {

    @Bind(R.id.time_tv)
    TextView time_tv;
    @Bind(R.id.wwdwmc_et)
    EditText wwdwmc_et;
    @Bind(R.id.xcdwmc_et)
    EditText xcdwmc_et;
    @Bind(R.id.xcry_et)
    EditText xcry_et;
    @Bind(R.id.xcqk_et)
    EditText xcqk_et;
    @Bind(R.id.clyj_et)
    EditText clyj_et;
    @Bind(R.id.lxdh_name)
    EditText lxdh_name;
    @Bind(R.id.add_fire_dev_btn)
    RelativeLayout addFireDevBtn;//????????????????????????
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;//??????????????????

    @Bind(R.id.take_photo_view)
    TakePhotosView take_photo_view;
    @Bind(R.id.add_sigh_view)
    AddSighView add_sigh_view;
    private Context mContext;
    private int privilege;
    private String userID;
    private String areaId;//@@9.27
    private String uploadTime;

    List<Photo> sighPhotos = new ArrayList<>();
    List<Photo> photos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_xcdj);

        ButterKnife.bind(this);
        mContext = this;
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();

        initView();

    }


    String pathtemp;
    int nowIndex;
    int nowIndexSigh;
    String photonametemp;
    String pathParent;
    private String signName = "";
    Uri imageFileUri;

    private void initView() {
        add_sigh_view.setmList(sighPhotos, true);
        add_sigh_view.setmOnClickListener(new AddSighView.OnClickListener() {
            @Override
            public void onItemClick(int position) {
                nowIndexSigh = position;
                photonametemp = System.currentTimeMillis() + "";
                pathParent = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                        + "uploadtemp" + File.separator;
                File fileParent = new File(pathParent);
                if (!fileParent.exists()) {
                    fileParent.mkdirs();
                }
                pathtemp = pathParent + photonametemp + ".jpg";
                File file = new File(pathtemp);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                imageFileUri = Uri.fromFile(file);//???????????????Uri
                Intent intent_sigh = new Intent(mContext, SignatureActivity.class);
                intent_sigh.putExtra("path", imageFileUri);
                startActivityForResult(intent_sigh, 166);
            }
        });
        add_sigh_view.setmOnLongClickListener(new AddSighView.OnLongClickListener() {
            @Override
            public void onItemLongClick(Photo photo, int i) {
                sighPhotos.remove(i);
                add_sigh_view.setmList(sighPhotos, true);
            }
        });

        take_photo_view.setmList(sighPhotos, true);
        take_photo_view.setmOnClickListener2(new TakePhotosView.OnClickListener2() {
            @Override
            public void onItemClick(int position) {
                nowIndex=position;
                photonametemp = System.currentTimeMillis()+"";
                pathParent= Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator
                        +"uploadtemp"+File.separator;
                File fileParent=new File(pathParent);
                if(!fileParent.exists()){
                    fileParent.mkdirs();
                }
                pathtemp= pathParent+photonametemp+".jpg";
                File file=new File(pathtemp);
                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Uri imageFileUri = Uri.fromFile(file);//???????????????Uri
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//???????????????Activity
                it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);//????????????????????????????????????????????????Uri
                ((Activity)mContext).startActivityForResult(it, 147);
            }
        });
        take_photo_view.setmOnLongClickListener(new TakePhotosView.OnLongClickListener() {
            @Override
            public void onItemLongClick(Photo photo, int i) {
                sighPhotos.remove(i);
                add_sigh_view.setmList(sighPhotos, true);
            }
        });


        addFireDevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBarOnUiThread();
                if (wwdwmc_et.getText() == null || xcdwmc_et.getText().toString().equals("")) {
                    T.showShort(mContext, "??????????????????");
                    dismissProgressBarOnUiThread();
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (sighPhotos.size() == 0) {
                            T.showShort(mContext, "????????????????????????");
                            dismissProgressBarOnUiThread();
                            return;
                        } else {
                            for (int i = 0; i < sighPhotos.size(); i++) {
                                File file = new File(sighPhotos.get(i).getPath());
                                if (file.exists()) {
                                    signName = System.currentTimeMillis() + "";
                                    if (UploadUtil.uploadFile(file, userID, areaId, signName, "", "registration")) {
                                    } else {
//                                        T.showShort(mContext, "????????????????????????");
                                        dismissProgressBarOnUiThread();
                                        return;
                                    }
//                                    T.showShort(mContext, "????????????????????????");
                                }//@@11.07
                            }

                            for (int i = 0; i < photos.size(); i++) {
                                File file = new File(photos.get(i).getPath());
                                if (file.exists()) {
                                    signName = System.currentTimeMillis() + "";
                                    if (UploadUtil.uploadFile(file, userID, areaId, signName, "", "registration")) {
                                    } else {
//                                        T.showShort(mContext, "????????????????????????");
                                        dismissProgressBarOnUiThread();
                                        return;
                                    }
                                    T.showShort(mContext, "????????????????????????");
                                }//@@11.07
                            }
                        }

                        VolleyHelper helper = VolleyHelper.getInstance(mContext);
                        RequestQueue mQueue = helper.getRequestQueue();
                        String url = "";

                        String sighString = "";
                        for (int i = 0; i < sighPhotos.size(); i++) {
                            sighString += sighPhotos.get(i).getName() + ".jpg";
                            if (i != (sighPhotos.size()-1)) {
                                sighString += "#";
                            }
                        }

                        String photoString = "";
                        for (int i = 0; i < photos.size(); i++) {
                            photoString += photos.get(i).getName() + ".jpg";
                            if (i != (photos.size()-1)) {
                                photoString += "#";
                            }
                        }
                        url = ConstantValues.SERVER_IP_NEW + "saveInspectionInfo?importname=" + URLEncoder.encode(wwdwmc_et.getText().toString())
                                + "&inspectname=" + URLEncoder.encode(xcdwmc_et.getText().toString())
                                + "&inspectperson=" + URLEncoder.encode(xcry_et.getText().toString())
                                + "&inspecttime=" + "2021"
                                + "&inspecttext=" + URLEncoder.encode(xcqk_et.getText().toString())
                                + "&chargeperson=" + URLEncoder.encode(sighString)
                                + "&telphone=" + URLEncoder.encode(lxdh_name.getText().toString())
                                + "&dealtime=" + "2021"
                                + "&remarks=" + URLEncoder.encode(photoString);

                        System.out.print(url);


                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            int errorCode = response.getInt("errorcode");
                                            String error = response.getString("error");
                                            if (errorCode == 0) {
                                                T.showShort(mContext, "??????????????????");

                                                if (pathParent != null && pathParent.length() > 0) {
                                                    File ftemp = new File(pathParent);
                                                    if (ftemp.exists()) {
                                                        ftemp.delete();
                                                    }
                                                }

                                                new AlertDialog.Builder(mContext).setMessage("??????,???????????????????????????")
                                                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                finish();
                                                            }
                                                        }).create().show();
                                            } else {
                                                T.showShort(mContext, error);
                                            }
                                            dismissProgressBarOnUiThread();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            dismissProgressBarOnUiThread();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                T.showShort(mContext, "????????????");
                                error.printStackTrace();
                                dismissProgressBarOnUiThread();
                            }
                        });
                        mQueue.add(jsonObjectRequest);
                    }
                }).start();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 166://??????
                if (resultCode == 1) {
                    sighPhotos.add(new Photo(photonametemp, pathtemp));
                    add_sigh_view.setmList(sighPhotos, true);
                }
                break;
            case 147://????????????
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bmp = BitmapFactory.decodeFile(pathtemp);
                    try {
                        saveFile(compressBySize(pathtemp,1500,2000),pathtemp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    photos.add(new Photo(photonametemp,pathtemp));
                    take_photo_view.setmList(photos, true);
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
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
        Bitmap textBitmap = ImageUtils.drawTextToRightBottom(this, bitmap, "??????:" + "\r\n" + address, 18, Color.RED, 25, 45);
        textBitmap = ImageUtils.drawTextToRightBottom(this, textBitmap, "??????:" + "\r\n" + TimeFormat.getNowTime(), 18, Color.RED, 25, 25);
        return textBitmap;
    }

    //@@10.12???????????????sd???
    public void saveFile(Bitmap bm, String fileName) throws Exception {
        File dirFile = new File(fileName);//????????????????????????
        if (dirFile.exists()) {
            dirFile.delete();  //???????????????
        }
        File myCaptureFile = new File(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));//100????????????????????????70??????????????????30%
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
    }



    @Override
    protected void onPause() {
        super.onPause();
//        mResumed = false;

        //????????????
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void afterTextChanged(Editable arg0) {
//            if (mResumed) {
//                mNfcAdapter.enableForegroundNdefPush(MainActivity.this, getNoteAsNdef());
//            }
        }
    };

    public static boolean uploadFile(File imageFile, String userId, String areaId, String uploadtime) {
        try {
            String requestUrl = ConstantValues.SERVER_IP_NEW + "UploadFileAction";
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

    private void showProgressBarOnUiThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void dismissProgressBarOnUiThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }


    private String address = "";
    private LocationService locationService;
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                address = location.getAddrStr();
                locationService.stop();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        locationService = MyApp.app.locationService;
        locationService.registerListener(mListener);
        locationService.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationService.unregisterListener(mListener); //???????????????
        locationService.stop(); //??????????????????
    }


}

