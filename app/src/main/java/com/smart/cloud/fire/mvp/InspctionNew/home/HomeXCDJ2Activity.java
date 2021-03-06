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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
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
import com.smart.cloud.fire.adapter.XCDJInfoAdapter;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.XCDJInfo;
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
    @Bind(R.id.xcry_sigh_view)
    AddSighView xcry_sigh_view;
    @Bind(R.id.xcqk_et)
    EditText xcqk_et;
    @Bind(R.id.clyj_et)
    EditText clyj_et;
    @Bind(R.id.xcsj_et)
    EditText xcsj_et;
    @Bind(R.id.lxdh_name)
    EditText lxdh_name;
    @Bind(R.id.add_fire_dev_btn)
    RelativeLayout addFireDevBtn;//????????????????????????
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;//??????????????????
    @Bind(R.id.recycler_view)
    RecyclerView recycler_view;

    @Bind(R.id.take_photo_view)
    TakePhotosView take_photo_view;
    @Bind(R.id.add_sigh_view)
    AddSighView add_sigh_view;
    private Context mContext;
    private int privilege;
    private String userID;
    private String areaId;//@@9.27
    private String uploadTime;

    XCDJInfoAdapter mAdapter;

    List<Photo> sighPhotos = new ArrayList<>();
    List<Photo> xcrysighPhotos = new ArrayList<>();
    List<Photo> photos = new ArrayList<>();

    private List<XCDJInfo> listQ;
    String xcjlstring = "";
    String photoString = "";

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
    int nowIndexQ;
    String photonametemp;
    String pathParent;
    private String signName = "";
    Uri imageFileUri;

    private void initView() {
        listQ = new ArrayList<>();
        listQ.add(new XCDJInfo());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recycler_view.setLayoutManager(linearLayoutManager);
        mAdapter = new XCDJInfoAdapter(mContext, listQ);
        mAdapter.setmOnClickListener(new XCDJInfoAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position) {
                nowIndexQ = position;
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
                Uri imageFileUri = Uri.fromFile(file);//???????????????Uri
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//???????????????Activity
                it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);//????????????????????????????????????????????????Uri
                ((Activity) mContext).startActivityForResult(it, 148);
            }
        });
        recycler_view.setAdapter(mAdapter);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String t = format.format(new Date());
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

        xcry_sigh_view.setmList(sighPhotos, true);
        xcry_sigh_view.setmOnClickListener(new AddSighView.OnClickListener() {
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
                startActivityForResult(intent_sigh, 167);
            }
        });
        xcry_sigh_view.setmOnLongClickListener(new AddSighView.OnLongClickListener() {
            @Override
            public void onItemLongClick(Photo photo, int i) {
                sighPhotos.remove(i);
                add_sigh_view.setmList(sighPhotos, true);
            }
        });

        take_photo_view.setmList(photos, true);
        take_photo_view.setmOnClickListener2(new TakePhotosView.OnClickListener2() {
            @Override
            public void onItemClick(int position) {
                nowIndex = position;
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
                Uri imageFileUri = Uri.fromFile(file);//???????????????Uri
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//???????????????Activity
                it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);//????????????????????????????????????????????????Uri
                ((Activity) mContext).startActivityForResult(it, 147);
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
                for (int i = 0; i < listQ.size(); i++) {
                    View view = recycler_view.getChildAt(i);
                    EditText et1 = view.findViewById(R.id.title_et);
                    EditText et2 = view.findViewById(R.id.desc_et);
                    listQ.get(i).setTitle(et1.getText().toString());
                    listQ.get(i).setDesc(et2.getText().toString());
                }

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
                            showToastOnUiThread("????????????");
                            dismissProgressBarOnUiThread();
                            return;
                        } else {
                            for (int i = 0; i < sighPhotos.size(); i++) {
                                File file = new File(sighPhotos.get(i).getPath());
                                if (file.exists()) {
                                    signName = sighPhotos.get(i).getName();
                                    if (UploadUtil.uploadFile(file, userID, areaId, signName, "", "registration")) {
                                    } else {
                                        showToastOnUiThread("????????????????????????");
                                        dismissProgressBarOnUiThread();
                                        return;
                                    }
                                    showToastOnUiThread("????????????????????????");
                                }//@@11.07
                            }

                            for (int i = 0; i < photos.size(); i++) {
                                File file = new File(photos.get(i).getPath());
                                if (file.exists()) {
                                    signName = photos.get(i).getName();
                                    if (UploadUtil.uploadFile(file, userID, areaId, signName, "", "registration")) {
                                    } else {
                                        showToastOnUiThread("????????????????????????");
                                        dismissProgressBarOnUiThread();
                                        return;
                                    }
                                    showToastOnUiThread("????????????????????????");
                                }//@@11.07
                            }

                            for (int i = 0; i < xcrysighPhotos.size(); i++) {
                                File file = new File(xcrysighPhotos.get(i).getPath());
                                if (file.exists()) {
                                    signName = xcrysighPhotos.get(i).getName();
                                    if (UploadUtil.uploadFile(file, userID, areaId, signName, "", "registration")) {
                                    } else {
                                        showToastOnUiThread("??????????????????????????????");
                                        dismissProgressBarOnUiThread();
                                        return;
                                    }
                                    showToastOnUiThread("??????????????????????????????");
                                }//@@11.07
                            }



                            for (int i = 0; i < listQ.size(); i++) {
                                xcjlstring = xcjlstring+(i+1)+"."+listQ.get(i).getTitle()
                                        +"<br>&nbsp&nbsp"+listQ.get(i).getDesc()+"<br><br>";
                                for (int j = 0; j < listQ.get(i).getMlist().size(); j++) {
                                    photoString += listQ.get(i).getMlist().get(j).getName() + ".jpg";
                                    if (!(i == (photos.size() - 1)&&j==(listQ.get(i).getMlist().size()-1))) {
                                        photoString += "#";
                                    }
                                    File file = new File(listQ.get(i).getMlist().get(j).getPath());
                                    if (file.exists()) {
                                        signName = listQ.get(i).getMlist().get(j).getName();
                                        if (UploadUtil.uploadFile(file, userID, areaId, signName, "", "registration")) {
                                        } else {
                                            showToastOnUiThread("????????????????????????");
                                            dismissProgressBarOnUiThread();
                                            return;
                                        }
                                        showToastOnUiThread("????????????????????????");
                                    }//@@11.07
                                }
                            }
                        }

                        VolleyHelper helper = VolleyHelper.getInstance(mContext);
                        RequestQueue mQueue = helper.getRequestQueue();
                        String url = "";

                        String sighString = "";
                        for (int i = 0; i < sighPhotos.size(); i++) {
                            sighString += sighPhotos.get(i).getName() + ".jpg";
                            if (i != (sighPhotos.size() - 1)) {
                                sighString += "#";
                            }
                        }


//                        for (int i = 0; i < photos.size(); i++) {
//                            photoString += photos.get(i).getName() + ".jpg";
//                            if (i != (photos.size()-1)) {
//                                photoString += "#";
//                            }
//                        }

                        String xcryphotoString = "";
                        for (int i = 0; i < xcrysighPhotos.size(); i++) {
                            xcryphotoString += xcrysighPhotos.get(i).getName() + ".jpg";
                            if (i != (xcrysighPhotos.size() - 1)) {
                                xcryphotoString += "#";
                            }
                        }
                        url = ConstantValues.SERVER_IP_NEW + "saveInspectionInfo?importname=" + URLEncoder.encode(wwdwmc_et.getText().toString())
                                + "&inspectname=" + URLEncoder.encode(xcdwmc_et.getText().toString())
                                + "&inspectperson=" + URLEncoder.encode(xcryphotoString)
                                + "&inspecttime=" + xcsj_et.getText().toString()
                                + "&inspecttext=" + URLEncoder.encode(xcjlstring)
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
            case 166://??????
                if (resultCode == 1) {
                    sighPhotos.add(new Photo(photonametemp, pathtemp));
                    add_sigh_view.setmList(sighPhotos, true);
                }
                break;
            case 167://??????????????????
                if (resultCode == 1) {
                    xcrysighPhotos.add(new Photo(photonametemp, pathtemp));
                    xcry_sigh_view.setmList(xcrysighPhotos, true);
                }
                break;
            case 147://????????????
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bmp = BitmapFactory.decodeFile(pathtemp);
                    try {
                        saveFile(compressBySize(pathtemp, 1500, 2000), pathtemp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    photos.add(new Photo(photonametemp, pathtemp));
                    take_photo_view.setmList(photos, true);
                }
                break;
            case 148://??????????????????
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bmp = BitmapFactory.decodeFile(pathtemp);
                    try {
                        saveFile(compressBySize(pathtemp, 1500, 2000), pathtemp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    listQ.get(nowIndexQ).getMlist().add(new Photo(photonametemp, pathtemp));
//                    questionAdapter.notifyItemChanged(nowIndex);
                    mAdapter.notifyDataSetChanged();
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
        Bitmap textBitmap = ImageUtils.drawTextToRightBottom(this, bitmap, "??????:" + "\r\n" + TimeFormat.getNowTime(), 18, Color.RED, 25, 25);
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

}
