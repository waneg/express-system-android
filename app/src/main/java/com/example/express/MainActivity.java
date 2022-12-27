package com.example.express;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MAIN_ACTIVITY";
    private EditText mId, mPwd;
    private Button btnAdmin, btnCourier, btnReg;
    private final String baseURL = "http://localhost:8080/express/login?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mId = this.findViewById(R.id.name);
        mPwd = this.findViewById(R.id.pwd);
        btnAdmin = this.findViewById(R.id.admin_login);
        btnCourier = this.findViewById(R.id.courier_login);
        btnReg = this.findViewById(R.id.reg);

        // 跳转到注册页面
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this,"前往注册！",Toast.LENGTH_SHORT).show();
            }
        });

        // 跳转到邮递员界面
        btnCourier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = mId.getText().toString();
                String password = mPwd.getText().toString();
                if (id.equals("") || password.equals("")) {
                    Toast.makeText(MainActivity.this, "工号或者密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                OkHttpClient okHttpClient = new OkHttpClient();
                String url = "http://10.0.2.2:8080/express_system/login";
                RequestBody formBody = new FormBody.Builder()
                        .add("id", id)
                        .add("password", password)
                        .add("job", "courier")
                        .build();

                Request getRequest = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                Call call = okHttpClient.newCall(getRequest);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i(TAG, "onFailures");
                        System.out.println(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i(TAG, "okHttpPost enqueue: \n onResponse:" + response + "\n body:" + response.body().string());
                        if (response.body().equals("fails")) {
                            Toast.makeText(MainActivity.this, "登录失败失败，请检查用户名和密码", Toast.LENGTH_LONG).show();
                        } else {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.putExtra("id", id);
                            intent.setClass(MainActivity.this, CourierActivity.class);
                            startActivity(intent);
                        }
                    }
                });


            }
        });

        // 跳转到管理员界面
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = mId.getText().toString();
                String password = mPwd.getText().toString();
                if (id.equals("") || password.equals("")) {
                    Toast.makeText(MainActivity.this, "工号或者密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                OkHttpClient okHttpClient = new OkHttpClient();
                String url = "http://10.0.2.2:8080/express_system/login";
                RequestBody formBody = new FormBody.Builder()
                        .add("id", id)
                        .add("password", password)
                        .add("job", "admin")
                        .build();

                Request getRequest = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                Call call = okHttpClient.newCall(getRequest);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i(TAG, "onFailures");
                        System.out.println(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i(TAG, "okHttpPost enqueue: \n onResponse:" + response + "\n body:" + response.body().string());
                        if (response.body().equals("fails")) {
                            Toast.makeText(MainActivity.this, "登录失败失败，请检查用户名和密码", Toast.LENGTH_LONG).show();
                        } else {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.putExtra("id", id);
                            intent.setClass(MainActivity.this, AdminActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }
}