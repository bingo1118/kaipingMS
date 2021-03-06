package com.obsessive.zbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.smart.cloud.fire.activity.AddDev.AddDevActivity;
import com.smart.cloud.fire.activity.AddNFC.AddNFCActivity;
import com.smart.cloud.fire.global.DeviceType;
import com.smart.cloud.fire.utils.JsonUtils;
import com.smart.cloud.fire.utils.TestAuthorityUtil;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;
import java.lang.reflect.Field;

import fire.cloud.smart.com.smartcloudfire.R;

import android.hardware.Camera;
import android.hardware.camera2.CameraManager;

public class AddCaptureActivity  extends Activity implements View.OnClickListener{

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private com.obsessive.zbar.CameraManager mCameraManager;

    private Context mContext;
    private FrameLayout scanPreview;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private ImageView scanLine;

    private Rect mCropRect = null;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private ImageScanner mImageScanner = null;

    private ImageButton sdsr_btn,light_btn;

    static {
        System.loadLibrary("iconv");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_capture);
        mContext=this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewById();
        testCameraOpen();
        //initViews();
    }

    private void findViewById() {
        scanPreview = (FrameLayout) findViewById(R.id.capture_preview);
        scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        sdsr_btn=(ImageButton)findViewById(R.id.sdsr_btn);
        light_btn=(ImageButton)findViewById(R.id.light_btn);
        sdsr_btn.setOnClickListener(this);
        light_btn.setOnClickListener(this);
    }

    private CameraManager manager;// ??????CameraManager??????
    boolean lightStatus=false;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sdsr_btn:
                Intent intent = new Intent(mContext, AddDevActivity.class);
                startActivity(intent);
                break;
            case R.id.light_btn:
                if (lightStatus) { // ???????????????
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            manager.setTorchMode("0", false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (mCamera != null) {
//                            mCamera.stopPreview();
//                            mCamera.release();
                            final Camera.Parameters parameter = mCamera.getParameters();
                            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            mCamera.setParameters(parameter);
//                            mCamera = null;
                        }
                    }
                    lightStatus=false;
                } else { // ???????????????
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            manager.setTorchMode("0", true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        final PackageManager pm = getPackageManager();
                        final FeatureInfo[] features = pm.getSystemAvailableFeatures();
                        for (final FeatureInfo f : features) {
                            if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) { // ?????????????????????????????????
                                if (null == mCamera) {
                                    mCamera = Camera.open();
                                }
                                final Camera.Parameters parameters = mCamera.getParameters();
                                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                mCamera.setParameters(parameters);
                                mCamera.startPreview();
                            }
                        }
                        lightStatus=true;
                    }
                    break;
                }
        }
    }

    private void testCameraOpen(){
        if (TestAuthorityUtil.testCamera(mContext)) {
            initViews();
        }else {
            finish();
        }
    }

    private void initViews() {
        mImageScanner = new ImageScanner();
        mImageScanner.setConfig(0, Config.X_DENSITY, 3);
        mImageScanner.setConfig(0, Config.Y_DENSITY, 3);

        autoFocusHandler = new Handler();
        mCameraManager = new com.obsessive.zbar.CameraManager(this);
        try {
            mCameraManager.openDriver();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera = mCameraManager.getCamera();
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        scanPreview.addView(mPreview);

        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.85f);
        animation.setDuration(3000);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        scanLine.startAnimation(animation);
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        finish();
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };


    private boolean ifGetData=false;//@@2018.06.08 ???????????????????????????

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Size size = camera.getParameters().getPreviewSize();

            // ????????????????????????data?????????????????????????????????????????????????????????
            byte[] rotatedData = new byte[data.length];
            for (int y = 0; y < size.height; y++) {
                for (int x = 0; x < size.width; x++)
                    rotatedData[x * size.height + size.height - y - 1] = data[x
                            + y * size.width];
            }

            // ??????????????????
            int tmp = size.width;
            size.width = size.height;
            size.height = tmp;

            initCrop();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(rotatedData);
            barcode.setCrop(mCropRect.left, mCropRect.top, mCropRect.width(),
                    mCropRect.height());

            int result = mImageScanner.scanImage(barcode);
            String resultStr = null;

            if (result != 0) {
                SymbolSet syms = mImageScanner.getResults();
                for (Symbol sym : syms) {
                    resultStr = sym.getData();
                }
            }

            if (!TextUtils.isEmpty(resultStr)&&!ifGetData) {
                String temp= JsonUtils.isJson(resultStr);
                if(temp!=null){
                    resultStr=temp;
                }
                DeviceType devType=getDevType(resultStr);
                Intent intent=new Intent(mContext,AddDevActivity.class);
                intent.putExtra("devType",devType.getDeviceName());
                intent.putExtra("mac",resultStr);
                startActivity(intent);
                ifGetData=true;
                finish();
            }
        }
    };



    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    /**
     * ??????????????????????????????
     */
    private void initCrop() {
        int cameraWidth = mCameraManager.getCameraResolution().y;
        int cameraHeight = mCameraManager.getCameraResolution().x;

        /** ??????????????????????????????????????? */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** ??????????????????????????? */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** ?????????????????????????????????????????????x?????? */
        int x = cropLeft * cameraWidth / containerWidth;
        /** ?????????????????????????????????????????????y?????? */
        int y = cropTop * cameraHeight / containerHeight;

        /** ???????????????????????????????????? */
        int width = cropWidth * cameraWidth / containerWidth;
        /** ???????????????????????????????????? */
        int height = cropHeight * cameraHeight / containerHeight;

        /** ?????????????????????????????? */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    private DeviceType getDevType(String smokeMac) {
        int deviceType = 1;//????????????
        String deviceName="";

        String macStr = (String) smokeMac.subSequence(0, 1);
        if (smokeMac.length() == 15) {
//            deviceType="14";//GPS
            deviceType = 41;//??????NB
            deviceName="HM??????";
        } else if (smokeMac.length() == 12) {
            deviceType = 51;//??????
            deviceName="CA??????";
        } else if (smokeMac.length() == 16 || smokeMac.length() == 18) {
            switch (macStr) {
                case "A":
                    smokeMac = smokeMac.substring(1, smokeMac.length());//????????????????????????
                    deviceType = 119;
                    deviceName="??????????????????";
                    break;
                case "W":
                    if ((smokeMac.charAt(smokeMac.length() - 1) + "").equals("W")) {
                        deviceType =19;//@@??????2018.01.02
                        deviceName="???????????????";
                    } else if ((smokeMac.charAt(smokeMac.length() - 1) + "").equals("A")) {
                        deviceType = 124;//@@????????????2018.01.30
                        deviceName="???????????????";
                    } else if ((smokeMac.charAt(smokeMac.length() - 1) + "").equals("B")) {
                        deviceType = 125;//@@????????????2018.01.30
                        deviceName="???????????????";

                    } else {
                        deviceType = 10;//@@??????
                        deviceName="???????????????";
                    }
                    break;
                case "Z":
                    deviceType = 55;
                    deviceName="JD??????";
                    break;
                default:
                    deviceType = 21;//loraOne??????
                    deviceName="??????";
                    break;
            }
        } else if (smokeMac.contains("-")) {
            deviceType = 31;//??????nb??????
            deviceName="SJ??????";
        } else {
            switch (macStr) {
                case "R":
                    if ((smokeMac.charAt(smokeMac.length() - 1) + "").equals("R")) {//@@9.29 ??????NB
                        deviceType = 16;//@@NB??????
                        deviceName="NB???????????????";
                    } else {
                        deviceType = 2;//@@??????
                        deviceName="???????????????";
                    }
                    smokeMac = smokeMac.replace("R", "");//??????
                    break;
                case "Q":
                    deviceType = 5;
                    deviceName="????????????";
                    if ((smokeMac.charAt(smokeMac.length() - 1) + "").equals("Q")) {
//                        electrState=1;
                    }//@@8.26
                    if ((smokeMac.charAt(smokeMac.length() - 1) + "").equals("S")) {
//                        electrState=3;
                    }//@@2018.01.18 ????????????
                    if ((smokeMac.charAt(smokeMac.length() - 1) + "").equals("L")) {
//                        electrState=1;
                        deviceType = 52;
                        deviceName="Lora????????????";
                    }//@@2018.05.15 Lara????????????
                    if ((smokeMac.charAt(smokeMac.length() - 1) + "").equals("N")) {
//                        electrState=1;
                        deviceType = 53;
                        deviceName="NB????????????";
                    }//@@2018.05.15 Lara????????????

                    break;
                case "T":
                    smokeMac = smokeMac.replace("T", "");//???????????????
                    deviceType = 25;
                    deviceName="??????????????????";
                    break;
                case "A":
                    smokeMac = smokeMac.substring(1, smokeMac.length());
                    deviceType = 119;
                    deviceName="????????????";
                    break;
                case "G":
                    smokeMac = smokeMac.replace("G", "");//??????????????? 6
                    deviceType = 7;
                    deviceName="???????????????";
                    break;
                case "K":
                    smokeMac = smokeMac.replace("K", "");//@@????????????????????????2018.01.24
                    deviceType = 20;
                    deviceName="????????????????????????";
                    break;
                case "S":
                    smokeMac = smokeMac.replace("S", "");//????????????????????? 7
                    deviceType = 8;
                    deviceName="???????????????";
                    break;
                case "J":
                    smokeMac = smokeMac.replace("J", "");//????????????
                    deviceType = 9;
                    deviceName="????????????";
                    break;
                case "W":
                    if ((smokeMac.charAt(smokeMac.length() - 1) + "").equals("W")) {
                        deviceType = 19;//@@??????2018.01.02
                        deviceName="???????????????";
                    } else if ((smokeMac.charAt(smokeMac.length() - 1) + "").equals("C")) {
                        deviceType = 42;//@@NB??????
                        deviceName="???????????????";
                        smokeMac = smokeMac.substring(0, smokeMac.length() - 1);
                    } else if ((smokeMac.charAt(smokeMac.length() - 1) + "").equals("L")) {
                        deviceType = 43;//@@Lara??????
                        deviceName="Lora???????????????";
                        smokeMac = smokeMac.substring(0, smokeMac.length() - 1);
                    } else {
                        deviceType = 10;//@@??????
                        deviceName="???????????????";
                    }
                    break;
                case "L":
                    smokeMac = smokeMac.replace("L", "");//????????????
                    deviceType = 11;
                    deviceName="???????????????";
                    break;
                case "M":
                    smokeMac = smokeMac.replace("M", "");//????????????
                    deviceType = 12;
                    deviceName="???????????????";
                    break;
                case "N":
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("N")){
                        deviceType = 56;
                        deviceName="NB??????";//@@NB-iot??????
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("O")){
                        deviceType = 57;
                        deviceName="Onet??????";//@@NB-iot??????
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("Z")){
                        deviceType = 58;
                        deviceName="JD??????";//@@NB-iot??????
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("R")){
                        deviceType = 45;
                        deviceName="HM??????";//@@NB-iot??????
                    }else{
                        deviceName="NB??????";
                    }
                    break;
                case "H":
                    smokeMac = smokeMac.replace("H", "");//???????????????
                    deviceType = 13;
                    deviceName="???????????????";
                    break;
                case "Y":
                    smokeMac = smokeMac.replace("Y", "");//??????
                    deviceType = 15;
                    deviceName="???????????????";
                    break;
                case "P":
                    smokeMac = smokeMac.replace("P", "");//10.31??????
                    deviceType = 18;
                    deviceName="????????????";
                    break;
            }
        }
        return new DeviceType(deviceType,deviceName);
        }
    }
