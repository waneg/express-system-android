package com.example.express;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class RegisterActivity extends AppCompatActivity {
    private final static String TAG = "REGISTER";
    private EditText mId, mPwd1, mPwd2, mName;
    private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mId = findViewById(R.id.id);
        mName = findViewById(R.id.name);
        mPwd1 = findViewById(R.id.pwd1);
        mPwd2 = findViewById(R.id.pwd2);
        mBtn = findViewById(R.id.reg);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = mId.getText().toString();
                String name = mName.getText().toString();
                String pwd1 = mPwd1.getText().toString();
                String pwd2 = mPwd2.getText().toString();

                if (id.equals("")) {
                    Toast.makeText(RegisterActivity.this, "请输入工号！", Toast.LENGTH_LONG).show();
                    return;
                }
                if (name.equals("")) {
                    Toast.makeText(RegisterActivity.this, "请输入用户名", Toast.LENGTH_LONG).show();
                    return;
                }
                if (pwd1.equals("")) {
                    Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!pwd1.equals(pwd2)) {
                    Toast.makeText(RegisterActivity.this, "两遍密码输入不一", Toast.LENGTH_LONG).show();
                    return;
                }
                OkHttpClient okHttpClient = new OkHttpClient();
                String url = "http://10.0.2.2:8080/express_system/register";
                RequestBody formBody = new FormBody.Builder()
                        .add("id", id)
                        .add("password", pwd1)
                        .add("name", name)
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
                            Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}