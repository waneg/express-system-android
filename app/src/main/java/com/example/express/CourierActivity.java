package com.example.express;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.express.api.CourierDetail;
import com.example.express.api.CourierInfoList;
import com.example.express.api.ExpressInfoBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CourierActivity extends AppCompatActivity {
    private final static String TAG = "CourierActivity";
    private int SCAN_REQUEST_CODE = 200;
    private int SELECT_IMAGE_REQUEST_CODE = 201;
    protected final int PERMS_REQUEST_CODE = 202;
    private ObjectMapper objectMapper = null;
    private Button btnScan;
    private TextView mExpressId;
    private TextView mSender;
    private TextView mReceiver;
    private TextView mOrigin;
    private TextView mDestination;
    private TextView mCourierId;

    private TextView mInfoId, mInfoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier);
        // 绑定组件
        btnScan = (Button) findViewById(R.id.scanner);
        mExpressId = (TextView) findViewById(R.id.expressId);
        mSender = (TextView) findViewById(R.id.sender);
        mReceiver = (TextView) findViewById(R.id.receiver);
        mOrigin = (TextView) findViewById(R.id.origin);
        mDestination = (TextView) findViewById(R.id.destination);
        mCourierId = (TextView) findViewById(R.id.courier_id);
        mInfoId = findViewById(R.id.id);
        mInfoName = findViewById(R.id.name);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        mInfoId.setText(id);
        getInfoList(id);


        // 申请权限
        String[] permissions = new String[]{Manifest.permission.
                WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            requestPermissions(permissions, PERMS_REQUEST_CODE);
        }

        // 扫描按钮绑定事件
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(CourierActivity.this).
                        setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                        .setPrompt("请对准二维码")// 设置提示语
                        .setCameraId(0)// 选择摄像头,可使用前置或者后置
                        .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                        .initiateScan();// 初始化扫码
            }
        });
    }

    private void getInfoList(String id) {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = "http://10.0.2.2:8080/express_system/courier_detail?id="+id;
        Request getRequest = new Request.Builder().url(url).get().build();

        Call call = okHttpClient.newCall(getRequest);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailures");
                System.out.println(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Log.i(TAG, "okHttpPost enqueue: \n onResponse:" + response + "\n body:" + data);
                mInfoName.setText(data);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_SHORT).show();
                objectMapper = new ObjectMapper();
                try {
                    ExpressInfoBean eib = objectMapper.readValue(result.getContents(), ExpressInfoBean.class);
                    updateView(eib);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateView(ExpressInfoBean eib) {
        if (!eib.getCourierId().equals(mInfoId.getText().toString())) {
            Toast.makeText(this, "您没有权限查看改快递信息", Toast.LENGTH_SHORT).show();
            return;
        }
        mExpressId.setText(eib.getExpressId());
        mSender.setText(eib.getSender());
        mOrigin.setText(eib.getOrigin());
        mReceiver.setText(eib.getReceiver());
        mDestination.setText(eib.getDestination());
        mCourierId.setText(eib.getCourierId());
    }
}