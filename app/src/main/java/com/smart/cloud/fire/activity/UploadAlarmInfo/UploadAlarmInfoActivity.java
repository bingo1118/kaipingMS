package com.smart.cloud.fire.activity.UploadAlarmInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.smart.cloud.fire.SQLEntity.UploadAlarmMsgTemp;
import com.smart.cloud.fire.activity.UploadNFCInfo.FileUtil;
import com.smart.cloud.fire.activity.UploadNFCInfo.FormFile;
import com.smart.cloud.fire.activity.Video.RecordVideoActivity;
import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.NpcCommon;
import com.smart.cloud.fire.pushmessage.PushAlarmMsg;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class UploadAlarmInfoActivity extends MvpActivity<UploadAlarmInfoPresenter> implements UploadAlarmInfoView{

    @Bind(R.id.msg_text)
    TextView msg_text;
    @Bind(R.id.address_text)
    TextView address_text;
    @Bind(R.id.time_text)
    TextView time_text;
    @Bind(R.id.add_fire_dev_btn)
    RelativeLayout addFireDevBtn;//????????????????????????
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;//??????????????????
    @Bind(R.id.memo_name)
    EditText memo_name;//@@??????
    @Bind(R.id.photo_image)
    ImageView photo_image;//@@????????????
    @Bind(R.id.video_upload)
    ImageView video_upload;//@@????????????
    private Context mContext;
    private int privilege;
    private String userID;
    private String areaId;//@@9.27
    private String uploadTime;

    private UploadAlarmInfoPresenter presenter;

    private boolean mWriteMode = false;
    NfcAdapter mNfcAdapter;
    AlertDialog alertDialog;
    PendingIntent mNfcPendingIntent;
    IntentFilter[] mWriteTagFilters;
    IntentFilter[] mNdefExchangeFilters;
    private Tag mDetectedTag;

    private String deviceState="4";//@@3 ?????? 4 ??????
    private PushAlarmMsg mPushAlarmMsg;
    private String imageFilePath;
    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/devalarm.jpg");//@@9.30
    String video_path=Environment.getExternalStorageDirectory() + File.separator + "SmartCloudFire/video/temp.mp4";

    Intent intent_result;

    String image_name="";
    String video_name="";

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
                    toast("??????????????????");
                    break;
                case 3:
                    toast("??????????????????");
                    break;
                case 9:
                    toast("??????????????????");
                    break;
                case 8:
                    toast("??????????????????");
                    break;
                case 4:
                    toast("????????????");
                    break;
                case 5:
                    toast("????????????");
                    break;
                case 66:
                    clearView();
                    toast("??????????????????????????????????????????????????????");
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    String mac="";
    String alarm="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_alarm_info);

        ButterKnife.bind(this);
        mContext = this;
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        mPushAlarmMsg = (PushAlarmMsg) getIntent().getExtras().getSerializable("mPushAlarmMsg");
        mac=mPushAlarmMsg.getMac();

        if(f.exists()){
            f.delete();
        }//@@9.30
        File v=new File(video_path);
        if(v.exists()){
            v.delete();
        }
        initView();
        initNFC();
    }

    @Override
    protected UploadAlarmInfoPresenter createPresenter() {
        presenter=new UploadAlarmInfoPresenter(this);
        return presenter;
    }

    private void initView() {
        msg_text.setText(Html.fromHtml("<font color=\'#000\'>"+mPushAlarmMsg.getAddress()+"-"+mPushAlarmMsg.getName()
                +"??????"+"</font><font color=\'#bc1c07\'>"+"???"+mPushAlarmMsg.getAlarmTypeName()+"???"+"</font>"
                +"<font color=\'#000\'>?????????????????????</font>"));
        address_text.setText("??????:"+mPushAlarmMsg.getAddress());
        time_text.setText("??????:"+mPushAlarmMsg.getAlarmTime());
        addFireDevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(!NpcCommon.verifyNetwork(MyApp.app)){
                            SQLiteDatabase db = LitePal.getDatabase();

                            if(f.exists()){
                                image_name=System.currentTimeMillis()+"";
                                File imagetemp=new File(Environment.getExternalStorageDirectory() + File.separator + "SmartCloudFire/image/");
                                imagetemp.mkdirs();
                                copy(f,imagetemp,image_name+".jpg");
                            }
                            File video_file = new File(video_path);
                            if(video_file!=null){
                                video_name=System.currentTimeMillis()+"";
                                File videotemp=new File(Environment.getExternalStorageDirectory() + File.separator + "SmartCloudFire/videotemp/");
                                videotemp.mkdirs();
                                copy(video_file,videotemp,video_name+".mp4");
                                video_file.delete();
                            }
                            UploadAlarmMsgTemp temp=new UploadAlarmMsgTemp();
                            temp.setUserID(userID);
                            temp.setAlarmTruth(deviceState);
                            temp.setDealDetail(memo_name.getText().toString());
                            temp.setImage_path(image_name);
                            temp.setVideo_path(video_name);
                            temp.setMac(mac);
                            temp.save();
                            Message message = new Message();
                            message.what = 66;
                            handler.sendMessage(message);
                            return;
                        }
                        boolean isSuccess=false;
                        boolean isSuccess_video=false;
                        boolean isHavePhoto=false;
                        if(imageFilePath!=null){
                            File file = new File(imageFilePath); //?????????path?????????????????????????????????
                            uploadTime=System.currentTimeMillis()+"";
                            if(f.exists()){
                                isHavePhoto=true;
                            }//@@11.07
                            image_name=System.currentTimeMillis()+"";
                            isSuccess=uploadFile(file,userID,areaId,uploadTime,mac,"devalarm",image_name);
//                            isSuccess=presenter.uploadImage(file,userID,"","",mac,"devalarm");
                            if(isSuccess){
                                Message message = new Message();
                                message.what = 3;
                                handler.sendMessage(message);
                            }else{
                                Message message = new Message();
                                message.what = 2;
                                handler.sendMessage(message);
                            }
                        }

                        File video_file = new File(video_path);
                        if(video_file.exists()){
                            if(video_file.isFile()){
                                video_name=System.currentTimeMillis()+"";
                                isSuccess_video=uploadFile(video_file,userID,areaId,uploadTime,mac,"devalarm_video",video_name);
                                if(isSuccess_video){
                                    Message message = new Message();
                                    message.what = 8;
                                    handler.sendMessage(message);
                                }else{
                                    Message message = new Message();
                                    message.what = 9;
                                    handler.sendMessage(message);
                                }
                            }
                        }

                        String username = SharedPreferencesManager.getInstance().getData(mContext,
                                SharedPreferencesManager.SP_FILE_GWELL,
                                SharedPreferencesManager.KEY_RECENTNAME);
//                        if(deviceState.equals("4")){
//                            presenter.uploadAlarm(username,mac,alarm);//???????????????6???7????????????
//                        }else{
//                            presenter.erasure(username,mac,"1");
//                        }
                        presenter.submitOrder(username,mac,deviceState,memo_name.getText().toString(),image_name,video_name);
                        if(video_file!=null){
                            video_file.delete();
                        }
                    }
                }).start();

            }
        });

        photo_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/devalarm.jpg";
                File temp = new File(imageFilePath);
                if(!temp.exists()){
                    Uri imageFileUri = Uri.fromFile(temp);//???????????????Uri
                    Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//???????????????Activity
                    it.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);//????????????????????????????????????????????????Uri
                    startActivityForResult(it, 102);
                }else{
                    //??????Intent
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(temp), "image/*");
                    startActivity(intent);
                }

            }
        });

        video_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent11=new Intent(UploadAlarmInfoActivity.this, RecordVideoActivity.class);
                startActivityForResult(intent11,66);
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
                        deviceState="4";//??????
                        break;
                    case R.id.radio2:
                        deviceState="3";//??????
                        break;
                }
            }
        });
    }

    private void clearView() {
        memo_name.setText("");
        photo_image.setImageResource(R.drawable.add_photo);
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
            case 66:
                File video_file = new File(video_path);
                if(video_file.exists()){
                    MediaMetadataRetriever media = new MediaMetadataRetriever();
                    media.setDataSource(video_path);
                    Bitmap bitmap = media.getFrameAtTime();
                    video_upload.setImageBitmap(bitmap);
                }
                break;
            case 102:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bmp = BitmapFactory.decodeFile(imageFilePath);
                    try {
                        saveFile(compressBySize(Environment.getExternalStorageDirectory().getAbsolutePath()+"/devalarm.jpg",150,200),Environment.getExternalStorageDirectory().getAbsolutePath()+"/devalarm.jpg");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    photo_image.setImageBitmap(bmp);
                }
                break;
            case 103:
                Bitmap bm = null;
                // ?????????????????????ContentProvider??????????????? ????????????ContentResolver??????
                ContentResolver resolver = getContentResolver();

                try {
                    Uri originalUri = data.getData(); // ???????????????uri

                    bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); // ?????????bitmap??????

                    // ??????????????????????????????????????????????????????

                    String[] proj = {MediaStore.Images.Media.DATA};

                    // ?????????android????????????????????????????????????????????????Android??????
                    @SuppressWarnings("deprecation")
                    Cursor cursor = managedQuery(originalUri, proj, null, null, null);
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
        return bitmap;
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


    public static boolean uploadFile(File imageFile,String userId,String areaId,String uploadtime,String mac,String location,String name) {
        try {
            String requestUrl = ConstantValues.SERVER_IP_NEW+"UploadFileAction";
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", userId);
            params.put("areaId", areaId);
            params.put("time", uploadtime);
//            params.put("mac", mac);
            params.put("mac", name);//?????????
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

    private void toast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void T(String t) {
        T.showShort(mContext,t);
    }

    @Override
    public void dealResult(String t, int resultCode) {
        intent_result=new Intent();
        intent_result.putExtra("isDeal",true);
        intent_result.putExtra("alarmTruth",deviceState);
        intent_result.putExtra("dealDetail",memo_name.getText().toString());
        intent_result.putExtra("image_path",image_name);
        intent_result.putExtra("video_path",video_name);

        if(resultCode==0){
            setResult(1,intent_result);
            T.showShort(mContext,"????????????");
            clearView();
            finish();
        }else{
            T.showShort(mContext,"????????????");
        }

    }

     //????????????????????????????????????????????????
    public static void copy(File source, File target,String name)
    {
        File tarpath = new File(target, name);
        if (source.isDirectory())
        {
            tarpath.mkdir();
            File[] dir = source.listFiles();
            for (int i = 0; i < dir.length; i++)
            {
                copy(dir[i], tarpath,"");
            }
        }
        else
        {
            try
            {
                InputStream is = new FileInputStream(source);
                OutputStream os = new FileOutputStream(tarpath);
                byte[] buf = new byte[1024];
                int len = 0;
                while ((len = is.read(buf)) != -1)
                {
                    os.write(buf, 0, len);
                }
                is.close();
                os.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}