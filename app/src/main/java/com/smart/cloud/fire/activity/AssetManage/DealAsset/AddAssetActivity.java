package com.smart.cloud.fire.activity.AssetManage.DealAsset;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blankj.utilcode.util.StringUtils;
import com.obsessive.zbar.CaptureActivity;
import com.smart.cloud.fire.GetLocationActivity;
import com.smart.cloud.fire.activity.AssetManage.AssetByCkey.AssetByCkeyEntity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.utils.NFCHelper;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.UploadUtil;
import com.smart.cloud.fire.utils.Utils;
import com.smart.cloud.fire.utils.VolleyHelper;
import com.smart.cloud.fire.view.AreaChooceList2View;
import com.smart.cloud.fire.view.TypeChooceListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;

public class AddAssetActivity extends Activity {

    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.add_fire_dev_btn)
    RelativeLayout addFireDevBtn;
    @Bind(R.id.uid_edit)
    EditText uid_edit;
    @Bind(R.id.pid_edit)
    AreaChooceList2View pid_edit;
    @Bind(R.id.name_edit)
    EditText name_edit;
    @Bind(R.id.type2_choose)
    TypeChooceListView type_edit;
    @Bind(R.id.address_edit)
    EditText address_edit;
    @Bind(R.id.principal_edit)
    EditText principal_edit;
    @Bind(R.id.phone_edit)
    EditText phone_edit;
    @Bind(R.id.memo_edit)
    EditText memo_edit;
    @Bind(R.id.overtime_edit)
    TextView overtime_edit;
    @Bind(R.id.nfc_rela)
    RelativeLayout nfc_rela;
    @Bind(R.id.spinner_type)
    Spinner spinner_type;
    @Bind(R.id.photo_image)
    ImageView photo_image;//@@????????????

    private Context mContext;

    private NFCHelper nfcHelper;

    private String uploadTime;
    private String imageFilePath;
    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/filename.jpg");//@@9.30



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_asset);

        ButterKnife.bind(this);
        mContext = this;
        initView();
        initNFC();
    }

    private void initNFC() {
        nfcHelper=NFCHelper.getInstance(this);
        if (!nfcHelper.isSupportNFC()) {
            T.showShort(mContext,"???????????????NFC??????");
            return;
        }
    }

    private void initView() {
        addFireDevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBarOnUiThread();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(StringUtils.isEmpty(uid_edit.getText().toString())){
                            T.showShort(mContext,"?????????????????????");
                        }
                        if(StringUtils.isEmpty(name_edit.getText().toString())){
                            T.showShort(mContext,"???????????????");
                        }
                        if(pid_edit.getChoocedArea()==null){
                            T.showShort(mContext,"???????????????");
                        }
                        if(StringUtils.isEmpty(memo_edit.getText().toString())){
                            T.showShort(mContext,"???????????????");
                        }
                        if(type_edit.getChoocedPArea()==null||type_edit.getChoocedArea()==null){
                            T.showShort(mContext,"???????????????");
                        }
                        if(StringUtils.isEmpty(address_edit.getText().toString())){
                            T.showShort(mContext,"???????????????");
                        }
                        if(StringUtils.isEmpty(principal_edit.getText().toString())){
                            T.showShort(mContext,"??????????????????");
                        }
                        if(StringUtils.isEmpty(phone_edit.getText().toString())){
                            T.showShort(mContext,"????????????????????????");
                        }
                        if(StringUtils.isEmpty(overtime_edit.getText().toString())){
                            T.showShort(mContext,"?????????????????????");
                        }

                        boolean isSuccess = false;
                        boolean isHavePhoto = false;
                        if (imageFilePath != null) {
                            File file = new File(imageFilePath); //?????????path?????????????????????????????????
                            uploadTime = System.currentTimeMillis() + "";
                            if (f.exists()) {
                                isHavePhoto = true;
                            }//@@11.07
                            isSuccess = UploadUtil.uploadFile(file, MyApp.getUserID(), "1", uploadTime, "", "assetImage");
                        }

                        VolleyHelper helper = VolleyHelper.getInstance(mContext);
                        RequestQueue mQueue = helper.getRequestQueue();
                        String url;

                        if (isHavePhoto && isSuccess) {
                            File file = new File(imageFilePath);//9.29
                            file.delete();//@@9.29
                            url = ConstantValues.SERVER_IP_NEW + "addAsset?akey=" + uid_edit.getText().toString()
                                    + "&named="+name_edit.getText().toString()
                                    + "&areaId="+pid_edit.getChoocedArea().getAreaId()
                                    + "&memo="+memo_edit.getText().toString()
                                    + "&atPid="+type_edit.getChoocedPArea().getAreaId()
                                    + "&atId="+type_edit.getChoocedArea().getAreaId()
                                    + "&address="+address_edit.getText().toString()
                                    + "&principal="+principal_edit.getText().toString()
                                    + "&phone="+phone_edit.getText().toString()
                                    + "&overTime="+overtime_edit.getText().toString()
                                    + "&picture=" + URLEncoder.encode(uploadTime+imageFilePath.substring(imageFilePath.lastIndexOf(".")));
                        } else {
                            if (isHavePhoto && !isSuccess) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        T.showShort(mContext, "??????????????????");
                                    }
                                });
                            }
                            url = ConstantValues.SERVER_IP_NEW + "addAsset?akey=" + uid_edit.getText().toString()
                                    + "&named="+name_edit.getText().toString()
                                    + "&areaId="+pid_edit.getChoocedArea().getAreaId()
                                    + "&memo="+memo_edit.getText().toString()
                                    + "&atPid="+type_edit.getChoocedPArea().getAreaId()
                                    + "&atId="+type_edit.getChoocedArea().getAreaId()
                                    + "&address="+address_edit.getText().toString()
                                    + "&principal="+principal_edit.getText().toString()
                                    + "&phone="+phone_edit.getText().toString()
                                    + "&overTime="+overtime_edit.getText().toString();
                        }

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            int errorCode = response.getInt("errorcode");
                                            String error = response.getString("error");
                                            if (errorCode == 0) {
                                                T.showShort(mContext, "????????????");
                                                clearView();
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
                                dismissProgressBarOnUiThread();
                            }
                        });
                        mQueue.add(jsonObjectRequest);
                    }
                }).start();

            }
        });
        photo_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/filename.jpg";
                File temp = new File(imageFilePath);
                if (!temp.exists()) {
                    Uri imageFileUri = Uri.fromFile(temp);//???????????????Uri
                    Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//???????????????Activity
                    it.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);//????????????????????????????????????????????????Uri
                    startActivityForResult(it, 102);
                } else {
                    //??????Intent
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(temp), "image/*");
                    startActivity(intent);
                }
            }
        });
    }

    @OnClick({R.id.overtime_edit})
    public void onclick(View v){
        switch (v.getId()){
            case R.id.overtime_edit:
                showDatePickDlg();
                break;
        }
    }

    private void clearView() {

    }

    @OnClick({R.id.scan_er_wei_ma})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_er_wei_ma:
                Intent openCameraIntent = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 102:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bmp = BitmapFactory.decodeFile(imageFilePath);
                    try {
                        saveFile(compressBySize(Environment.getExternalStorageDirectory().getAbsolutePath() + "/devimage.jpg", 1500, 2000), Environment.getExternalStorageDirectory().getAbsolutePath() + "/devimage.jpg");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int screenWidth = dm.widthPixels;
                    if (bmp.getWidth() <= screenWidth) {
                        photo_image.setImageBitmap(bmp);
                    } else {
                        Bitmap mp = Bitmap.createScaledBitmap(bmp, screenWidth, bmp.getHeight() * screenWidth / bmp.getWidth(), true);
                        photo_image.setImageBitmap(mp);
                    }
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
            case 0:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString("result");
                    uid_edit.setText(scanResult);

                }
                break;
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

    public void showDatePickDlg () {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                overtime_edit.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    private Tag mDetectedTag;
    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode
        if (!nfcHelper.ismWriteMode() && (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
                ||NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))) {//@@10.19
            byte[] myNFCID = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String UID = Utils.ByteArrayToHexString(myNFCID);
            uid_edit.setText(UID);
            mDetectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
        // Tag writing mode
        if (nfcHelper.ismWriteMode() && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            nfcHelper.writeTag(detectedTag);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcHelper.isSupportNFC()) {
            nfcHelper.changeToReadMode();
        }
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
}
