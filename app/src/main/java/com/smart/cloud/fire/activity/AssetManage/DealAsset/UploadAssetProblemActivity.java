package com.smart.cloud.fire.activity.AssetManage.DealAsset;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class UploadAssetProblemActivity extends Activity {

    private Context mContext;
    @Bind(R.id.commit)
    TextView commit;
    @Bind(R.id.title)
    EditText title;
    @Bind(R.id.content)
    EditText content;
    private String akey;
    private String areaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_asset_problem);
        ButterKnife.bind(this);
        mContext=this;

        akey=getIntent().getStringExtra("akey");
        areaId=getIntent().getStringExtra("areaId");
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
    }


    private void commit(){
        String title=this.title.getText().toString();
        String content=this.content.getText().toString();
        if(title.length()==0||content.length()==0){
            T.showShort(mContext,"请完善数据再提交");
            return;
        }
        VolleyHelper helper=VolleyHelper.getInstance(MyApp.app);
        RequestQueue mQueue = helper.getRequestQueue();
        String url= ConstantValues.SERVER_IP_NEW+"addAssetOrder?orderTitle="+title
                +"&orderMemo="+content
                +"&areaId="+areaId
                +"&akey="+akey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int errorCode=response.getInt("errorCode");
                            if(errorCode==0){

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                T.showShort(mContext,"网络错误");
            }
        });
        mQueue.add(jsonObjectRequest);
    }
}

