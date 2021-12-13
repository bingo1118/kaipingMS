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
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class HomeXCDJ2Activity extends Activity {

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
    @Bind(R.id.xcsj_et)
    EditText xcsj_et;
    @Bind(R.id.lxdh_name)
    EditText lxdh_name;
    @Bind(R.id.add_fire_dev_btn)
    RelativeLayout addFireDevBtn;//添加设备按钮。。
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;//加载进度。。

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
        setContentView(R.layout.activity_home_xcdj2);

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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String t=format.format(new Date());
        xcsj_et.setText(t);

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
                imageFileUri = Uri.fromFile(file);//获取文件的Uri
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
                Uri imageFileUri = Uri.fromFile(file);//获取文件的Uri
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//跳转到相机Activity
                it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);//告诉相机拍摄完毕输出图片到指定的Uri
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
                    T.showShort(mContext, "请先录入信息");
                    dismissProgressBarOnUiThread();
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (sighPhotos.size() == 0) {
                            showToastOnUiThread("请先签名");
                            dismissProgressBarOnUiThread();
                            return;
                        } else {
                            for (int i = 0; i < sighPhotos.size(); i++) {
                                File file = new File(sighPhotos.get(i).getPath());
                                if (file.exists()) {
                                    signName = sighPhotos.get(i).getName();
                                    if (UploadUtil.uploadFile(file, userID, areaId, signName, "", "registration")) {
                                    } else {
                                        showToastOnUiThread("数字签名上传失败");
                                        dismissProgressBarOnUiThread();
                                        return;
                                    }
                                    showToastOnUiThread("数字签名上传完成");
                                }//@@11.07
                            }

                            for (int i = 0; i < photos.size(); i++) {
                                File file = new File(photos.get(i).getPath());
                                if (file.exists()) {
                                    signName = photos.get(i).getName();
                                    if (UploadUtil.uploadFile(file, userID, areaId, signName, "", "registration")) {
                                    } else {
                                        showToastOnUiThread("巡查图片上传失败");
                                        dismissProgressBarOnUiThread();
                                        return;
                                    }
                                    showToastOnUiThread( "巡查图片上传完成");
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
                                + "&inspecttime=" + xcsj_et.getText().toString()
                                + "&inspecttext=" + URLEncoder.encode(xcqk_et.getText().toString())
                                + "&dealidea=" + URLEncoder.encode(clyj_et.getText().toString())
                                + "&chargeperson=" + URLEncoder.encode(sighString)
                                + "&telphone=" + URLEncoder.encode(lxdh_name.getText().toString())
                                + "&dealtime=" + xcsj_et.getText().toString()
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
                                                T.showShort(mContext, "记录上传成功");

                                                if (pathParent != null && pathParent.length() > 0) {
                                                    File ftemp = new File(pathParent);
                                                    if (ftemp.exists()) {
                                                        ftemp.delete();
                                                    }
                                                }

                                                new AlertDialog.Builder(mContext).setMessage("成功,点击确定返回上一页")
                                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
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
                                T.showShort(mContext, "网络错误");
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

    private void showToastOnUiThread(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T.showShort(mContext, s);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 166://签名
                if (resultCode == 1) {
                    sighPhotos.add(new Photo(photonametemp, pathtemp));
                    add_sigh_view.setmList(sighPhotos, true);
                }
                break;
            case 147://细则图片
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


    //@@10.12压缩图片尺寸
    public Bitmap compressBySize(String pathName, int targetWidth,
                                 int targetHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);// 得到图片的宽度、高度；
        float imgWidth = opts.outWidth;
        float imgHeight = opts.outHeight;// 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        opts.inSampleSize = 1;
        if (widthRatio > 1 || widthRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }//设置好缩放比例后，加载图片进内容；
        opts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(pathName, opts);
        Bitmap textBitmap = ImageUtils.drawTextToRightBottom(this, bitmap, "时间:" + "\r\n" + TimeFormat.getNowTime(), 18, Color.RED, 25, 25);
        return textBitmap;
    }

    //@@10.12存储文件到sd卡
    public void saveFile(Bitmap bm, String fileName) throws Exception {
        File dirFile = new File(fileName);//检测图片是否存在
        if (dirFile.exists()) {
            dirFile.delete();  //删除原图片
        }
        File myCaptureFile = new File(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));//100表示不进行压缩，70表示压缩率为30%
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
    }

}
