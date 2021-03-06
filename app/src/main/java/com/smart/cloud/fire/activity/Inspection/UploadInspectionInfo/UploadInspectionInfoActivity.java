package com.smart.cloud.fire.activity.Inspection.UploadInspectionInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.google.gson.JsonObject;
import com.smart.cloud.fire.activity.UploadNFCInfo.FileUtil;
import com.smart.cloud.fire.activity.UploadNFCInfo.FormFile;
import com.smart.cloud.fire.activity.UploadNFCInfo.UploadNFCInfoActivity;
import com.smart.cloud.fire.adapter.QuestionAdapter;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.Question;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class UploadInspectionInfoActivity extends Activity {

    @Bind(R.id.sign_cb)
    CheckBox sign_cb;
    @Bind(R.id.location_tv)
    TextView location_tv;//@@uid
    @Bind(R.id.uid_name)
    EditText uid_name;//@@uid
    @Bind(R.id.add_fire_name)
    EditText addFireName;//??????????????????
    @Bind(R.id.add_fire_address)
    EditText addFireAddress;//??????????????????
    @Bind(R.id.add_fire_dev_btn)
    RelativeLayout addFireDevBtn;//????????????????????????
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;//??????????????????
    @Bind(R.id.area_name)
    EditText area_name;//@@??????
    @Bind(R.id.device_type_name)
    EditText device_type_name;//@@??????
    @Bind(R.id.memo_name)
    EditText memo_name;//@@??????
    @Bind(R.id.take_photo_view)
    TakePhotosView take_photo_view;
    @Bind(R.id.memo_iv)
    ImageView memo_iv;
    @Bind(R.id.memo_tv)
    TextView memo_tv;
    @Bind(R.id.radio1)
    RadioButton radio1;
    @Bind(R.id.radio2)
    RadioButton radio2;
    @Bind(R.id.question_recyclerview)
    RecyclerView question_recyclerview;
    @Bind(R.id.add_sigh_view)
    AddSighView add_sigh_view;
    private Context mContext;
    private int privilege;
    private String userID;
    private String areaId;//@@9.27
    private String uploadTime;

    List<Photo> sighPhotos=new ArrayList<>();

    private boolean mWriteMode = false;
    NfcAdapter mNfcAdapter;
    PendingIntent mNfcPendingIntent;
    IntentFilter[] mWriteTagFilters;
    IntentFilter[] mNdefExchangeFilters;
    private Tag mDetectedTag;

    String questionJson;
    List<Question> listQ;
    QuestionAdapter questionAdapter;

    private String deviceState="1";
    String lon="";
    String lat="";
    private String imageFilePath;
    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/filename.jpg");//@@9.30

    String uid;
    String pid;
    String tid;
    String tuid;//????????????id

    String memo;
    String modify;
    boolean showMemoTv=false;

    String startdate;
    String enddate;
    String tasktype;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_inspection_info);

        ButterKnife.bind(this);
        mContext = this;
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        uid=getIntent().getStringExtra("uid");
        uid_name.setText(uid);
        pid=getIntent().getStringExtra("pid");
        tid=getIntent().getStringExtra("tid");
        memo=getIntent().getStringExtra("memo");
        modify=getIntent().getStringExtra("modify");
        startdate=getIntent().getStringExtra("startdate");
        enddate=getIntent().getStringExtra("enddate");
        tasktype=getIntent().getStringExtra("tasktype");
        tuid=getIntent().getStringExtra("tuid");
        if(modify!=null&&modify.length()>0){
            getRecentRecord();
        }
        if(uid!=null&&uid.length()>0){
            getNormalDevInfo(uid);
        }

        initView();
        if(f.exists()){
            f.delete();
        }//@@9.30
        if (mNfcAdapter==null) {
            T.showShort(mContext,"???????????????NFC??????");
            return;
        }
        initNFC();
    }

    private void getRecentRecord() {
        VolleyHelper helper=VolleyHelper.getInstance(mContext);
        RequestQueue mQueue = helper.getRequestQueue();
        String url="";

        url= ConstantValues.SERVER_IP_NEW+"getRecentRecord?uid="+uid;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(modify!=null&&modify.length()>0){
                                String qualified=response.getString("qualified");
                                String memo=response.getString("desc");
                                if(qualified.equals("1")){
                                    radio1.setChecked(true);
                                }else{
                                    radio2.setChecked(true);
                                }
                                memo_name.setText(memo);
                            }
                            tuid=response.getString("tuid");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                T.showShort(mContext,"????????????");
                dismissProgressBarOnUiThread();
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    private void getNormalDevInfo(String uid) {
        if(uid==null){
            return;
        }
        VolleyHelper helper=VolleyHelper.getInstance(mContext);
        RequestQueue mQueue = helper.getRequestQueue();
        String url="";

        url= ConstantValues.SERVER_IP_NEW+"getItemInfo?uid="+uid+"&userId="+userID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(uid!=null&&uid.length()==13){
                                uid_name.setText(uid);
                                addFireName.setText(response.getString("deviceName"));
                                addFireAddress.setText(response.getString("address"));
                                area_name.setText(response.getString("areaName"));
                                device_type_name.setText(response.getString("deviceTypeName"));
                            }
                            questionJson=response.getString("questionTypes");
                            dealwithQuestionJson(questionJson);

                            if(tid==null||tid.equals("")){
                                try{
                                    tid=response.getString("tid");
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            String pidtemp=response.getString("pid");
                            if(pidtemp!=null&&pid.length()>0){
                                pid=pidtemp;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                T.showShort(mContext,"????????????");
                showProgressBarOnUiThread();
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    String pathtemp;
    int nowIndex;
    int nowIndexSigh;
    String photonametemp;
    String pathParent;
    private void dealwithQuestionJson(String questionJson) {
        try {
            JSONArray jsonArray=new JSONArray(questionJson);
            if(jsonArray.length()!=0){
                listQ=new ArrayList<>();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject temp=jsonArray.getJSONObject(i);
                    String qtname=temp.getString("qtname");
                    JSONArray tempA=temp.getJSONArray("questions");
                    for(int j=0;j<tempA.length();j++){
                        Question question=new Question();
                        if(j==0){
                            question.setFirstItem(true);
                        }else{
                            question.setFirstItem(false);
                        }
                        question.setQjudge(tempA.getJSONObject(j).getString("qjudge"));
                        question.setQuestionType(qtname);
                        question.setQdetail(tempA.getJSONObject(j).getString("qdetail"));
                        question.setQid(tempA.getJSONObject(j).getInt("qid"));
                        listQ.add(question);
                    }
                }
            }
            questionAdapter=new QuestionAdapter(mContext,listQ,true);
            question_recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
            questionAdapter.setmOnClickListener(new QuestionAdapter.OnClickListener() {
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
            question_recyclerview.setAdapter(questionAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String signName="";
    Uri imageFileUri;
    List<Photo> photos = new ArrayList<>();

    private void initView() {
        take_photo_view.setmList(photos, true);
        take_photo_view.setmOnClickListener2(new TakePhotosView.OnClickListener2() {
            @Override
            public void onItemClick(int position) {
                if(photos.size()==3){
                    T.showShort(mContext,"????????????????????????");
                    return;
                }
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
                ((Activity) mContext).startActivityForResult(it, 102);
            }
        });
        take_photo_view.setmOnLongClickListener(new TakePhotosView.OnLongClickListener() {
            @Override
            public void onItemLongClick(Photo photo, int i) {
                sighPhotos.remove(i);
                add_sigh_view.setmList(sighPhotos, true);
            }
        });

        add_sigh_view.setmList(sighPhotos,true);
        add_sigh_view.setmOnClickListener(new AddSighView.OnClickListener() {
            @Override
            public void onItemClick(int position) {
                nowIndexSigh=position;
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
                imageFileUri= Uri.fromFile(file);//???????????????Uri
                Intent intent_sigh=new Intent(mContext, SignatureActivity.class);
                intent_sigh.putExtra("path",imageFileUri);
                startActivityForResult(intent_sigh, 166);
            }
        });
        add_sigh_view.setmOnLongClickListener(new AddSighView.OnLongClickListener() {
            @Override
            public void onItemLongClick(Photo photo, int i) {
                sighPhotos.remove(i);
                add_sigh_view.setmList(sighPhotos,true);
            }
        });

        sign_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mContext, SignatureActivity.class), 166);
            }
        });
        memo_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!showMemoTv){
                    if(memo!=null&&memo.length()>0){
                        memo_tv.setText("????????????:"+memo);
                        memo_tv.setVisibility(View.VISIBLE);
                        showMemoTv=true;
                    }else{
                        T.showShort(mContext,"???????????????");
                    }
                }else{
                    memo_tv.setVisibility(View.GONE);
                    showMemoTv=false;
                }
            }
        });
        addFireDevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBarOnUiThread();
                if(uid_name.getText()==null||uid_name.getText().toString().equals("")){
                    T.showShort(mContext,"??????????????????????????????");
                    dismissProgressBarOnUiThread();
                    return;
                }
                String answer="";
                if(questionAdapter!=null){
                    answer=questionAdapter.getAnwserJsonNew();
                }
                if(answer==null){
                    T.showShort(mContext,"?????????????????????");
                    dismissProgressBarOnUiThread();
                    return;
                }

                String finalAnswer = answer;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isSuccess=false;
                        boolean isHavePhoto=false;
                        if(sighPhotos.size()==0){
                            showToastOnUiThread("????????????????????????");
                            dismissProgressBarOnUiThread();
                            return;
                        }else{
                            for(int i=0;i<sighPhotos.size();i++){
                                File file = new File(sighPhotos.get(i).getPath());
                                if(file.exists()){
                                    signName=sighPhotos.get(i).getName();
                                    if(UploadUtil.uploadFile(file,userID,areaId,signName,"","registration")){
                                    }else{
                                        showToastOnUiThread("????????????????????????");
                                        dismissProgressBarOnUiThread();
                                        return;
                                    }
                                    showToastOnUiThread("????????????????????????");
                                }//@@11.07
                            }
                        }
                        String sighString="";
                        for(int i=0;i<sighPhotos.size();i++){
                            sighString+=sighPhotos.get(i).getName()+".jpg";
                            if(i!=(sighPhotos.size()-1)){
                                sighString+="#";
                            }
                        }

                        if(listQ!=null){
                            for(Question q:listQ){
                                for(Photo p:q.getPhotos()){
                                    File file = new File(p.getPath());
                                    if(!UploadUtil.uploadFile(file,userID,areaId,p.getName(),"","cheakImg")){
                                        showToastOnUiThread("??????????????????????????????");
                                    }
                                }
                            }
                        }

                        for (int i = 0; i < photos.size(); i++) {
                            File file = new File(photos.get(i).getPath());
                            if (file.exists()) {
                                signName = photos.get(i).getName();
                                if (UploadUtil.uploadFile(file, userID, areaId, signName, "", "cheakImg")) {
                                } else {
                                    showToastOnUiThread("????????????????????????");
                                    dismissProgressBarOnUiThread();
                                    return;
                                }
                                showToastOnUiThread("????????????????????????");
                                isSuccess=true;
                                isHavePhoto=true;
                            }//@@11.07
                        }

                        String photoString="";
                        for (int i = 0; i < photos.size(); i++) {
                            photoString += photos.get(i).getName() + ".jpg";
                            if (i != (photos.size()-1)) {
                                photoString += "#";
                            }
                        }

                        VolleyHelper helper=VolleyHelper.getInstance(mContext);
                        RequestQueue mQueue = helper.getRequestQueue();
//                        RequestQueue mQueue = Volley.newRequestQueue(mContext);
                        String url="";
                        if(isHavePhoto&&isSuccess){
                            if(modify!=null&&modify.length()>0){
                                //"cheakImg"????????????
                                url= ConstantValues.SERVER_IP_NEW+"updateResult?tuid="+tuid+"&uid="+uid_name.getText().toString()
                                        +"&tid="+tid+"&qualified="+deviceState+"&desc="+ URLEncoder.encode(memo_name.getText().toString())
                                        +"&photo1="+URLEncoder.encode(photoString);
                            }else{

                                url= ConstantValues.SERVER_IP_NEW+"postResult?workerId="+userID+"&uid="+uid_name.getText().toString()
                                        +"&tid="+tid+"&pid="+pid+"&qualified="+deviceState+"&desc="+ URLEncoder.encode(memo_name.getText().toString())
                                        +"&imgs="+URLEncoder.encode(photoString)
                                        +"&questionJson="+URLEncoder.encode(finalAnswer)+"&startdate="+startdate+"&enddate="+enddate+"&taskType="+tasktype
                                        +"&signature="+URLEncoder.encode(sighString);

                            }

                        }else{
                            if(isHavePhoto&&!isSuccess){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToastOnUiThread("??????????????????");
                                    }
                                });
                            }
                            if(modify!=null&&modify.length()>0){
                                url= ConstantValues.SERVER_IP_NEW+"updateResult?tuid="+tuid+"&uid="+uid_name.getText().toString()
                                        +"&tid="+tid+"&pid="+pid+"&qualified="+deviceState+"&desc="+ URLEncoder.encode(memo_name.getText().toString())
                                        +"&photo1=";
                            }else{
                                url= ConstantValues.SERVER_IP_NEW+"postResult?workerId="+userID+"&uid="+uid_name.getText().toString()
                                        +"&tid="+tid+"&pid="+pid+"&qualified="+deviceState+"&desc="+ URLEncoder.encode(memo_name.getText().toString())
                                        +"&imgs="+"&questionJson="+URLEncoder.encode(finalAnswer)+"&startdate="+startdate+"&enddate="+enddate+"&taskType="+tasktype
                                        +"&signature="+URLEncoder.encode(sighString);
                            }

                        }

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            int errorCode=response.getInt("errorCode");
                                            String error=response.getString("error");
                                            if(errorCode==0){
                                                showToastOnUiThread("??????????????????");
                                                clearView();
                                                if(pathParent!=null&&pathParent.length()>0){
                                                    File ftemp=new File(pathParent);
                                                    if(ftemp.exists()){
                                                        ftemp.delete();
                                                    }
                                                }
                                                if(f.exists()){
                                                    f.delete();
                                                }//@@9.30
                                                new AlertDialog.Builder(mContext).setMessage("??????,???????????????????????????")
                                                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                finish();
                                                            }
                                                        }).create().show();
                                            }else{
                                                T.showShort(mContext,error);
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
                                showToastOnUiThread("????????????");
                                dismissProgressBarOnUiThread();
                            }
                        });
                        mQueue.add(jsonObjectRequest);
                    }
                }).start();

            }
        });


        RadioGroup group = (RadioGroup)this.findViewById(R.id.radio_group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                int radioButtonId = arg0.getCheckedRadioButtonId();
                switch (radioButtonId){
                    case R.id.radio1:
                        deviceState="1";
                        break;
                    case R.id.radio2:
                        deviceState="0";
                        break;
                }
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

    private void clearView() {
        uid_name.setText("");
        addFireName.setText("");
        addFireAddress.setText("");
        area_name.setText("");
        device_type_name.setText("");
        memo_name.setText("");
        imageFilePath=null;
    }

    private void initNFC() {
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Intent filters for reading a note from a tag or exchanging over p2p.
        IntentFilter ndefDetected = new IntentFilter(
                NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
        }
        mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

        // Intent filters for writing to a tag
        IntentFilter tagDetected = new IntentFilter(
                NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] { tagDetected };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 166://??????
                if(resultCode==1){
                    sign_cb.setChecked(true);
                    sighPhotos.add(new Photo(photonametemp,pathtemp));
                    add_sigh_view.setmList(sighPhotos,true);
                }else{
                    sign_cb.setChecked(false);
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
                    listQ.get(nowIndex).getPhotos().add(new Photo(photonametemp,pathtemp));
//                    questionAdapter.notifyItemChanged(nowIndex);
                    questionAdapter.notifyDataSetChanged();
                }
                break;
            case 102:
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
        Bitmap textBitmap = ImageUtils.drawTextToRightBottom(this, bitmap, "??????:"+"\r\n"+address, 18, Color.RED, 25, 45);
        textBitmap= ImageUtils.drawTextToRightBottom(this, textBitmap, "??????:"+"\r\n"+TimeFormat.getNowTime(), 18, Color.RED, 25, 25);
        return textBitmap;
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

    @Override
    protected void onResume() {
        super.onResume();
//        mResumed = true;
        // Sticky notes received from Android
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = getNdefMessages(getIntent());
            byte[] payload = messages[0].getRecords()[0].getPayload();
            setNoteBody(new String(payload));
            setIntent(new Intent()); // Consume this intent.
        }
        if (mNfcAdapter!=null) {
            enableNdefExchangeMode();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
//        mResumed = false;
        if (mNfcAdapter!=null) {
            mNfcAdapter.disableForegroundNdefPush(this);
        }

        //????????????
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode
        if (!mWriteMode && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            mDetectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            //??????Ndef??????
            NdefMessage[] msgs = getNdefMessages(intent);
            String body = new String(msgs[0].getRecords()[0].getPayload());
            setNoteBody(body);
        }

        // Tag writing mode
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeTag(getNoteAsNdef(), detectedTag);
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

    private void setNoteBody(String body) {
        try {
            JSONObject jsonObject=new JSONObject(body);
            uid_name.setText(jsonObject.getString("uid"));
            addFireName.setText(jsonObject.getString("deviceName"));
            addFireAddress.setText(jsonObject.getString("address"));
            area_name.setText(jsonObject.getString("areaName"));
            device_type_name.setText(jsonObject.getString("deviceTypeName"));
            lon=jsonObject.getString("longitude");
            lat=jsonObject.getString("latitude");
            areaId=jsonObject.getString("areaId");//@@9.27

            getNormalDevInfo(jsonObject.getString("uid"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private NdefMessage getNoteAsNdef() {
        byte[] textBytes = "".getBytes();
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(),
                new byte[] {}, textBytes);
        return new NdefMessage(new NdefRecord[] {
                textRecord
        });
    }

    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                        record
                });
                msgs = new NdefMessage[] {
                        msg
                };
            }
        } else {
            finish();
        }
        return msgs;
    }

    private void enableNdefExchangeMode() {
        mNfcAdapter.enableForegroundNdefPush(UploadInspectionInfoActivity.this, getNoteAsNdef());
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
    }

    private void disableNdefExchangeMode() {
        mNfcAdapter.disableForegroundNdefPush(this);
        mNfcAdapter.disableForegroundDispatch(this);
    }

    private void enableTagWriteMode() {
        mWriteMode = true;
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] {
                tagDetected
        };
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }

    private void disableTagWriteMode() {
        mWriteMode = false;
        mNfcAdapter.disableForegroundDispatch(this);
    }

    boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    T.showShort(mContext,"Tag is read-only.");
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    T.showShort(mContext,"Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size
                            + " bytes.");
                    return false;
                }

                ndef.writeNdefMessage(message);
                T.showShort(mContext,"??????????????????.");
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        T.showShort(mContext,"Formatted tag and wrote message");
                        return true;
                    } catch (IOException e) {
                        T.showShort(mContext,"Failed to format tag.");
                        return false;
                    }
                } else {
                    T.showShort(mContext,"Tag doesn't support NDEF.");
                    return false;
                }
            }
        } catch (Exception e) {
            T.showShort(mContext,"??????????????????");
        }

        return false;
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

    private void showProgressBarOnUiThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void dismissProgressBarOnUiThread(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }


    private String address="";
    private LocationService locationService;
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                address=location.getAddrStr();
                location_tv.setText("????????????:"+address);
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
