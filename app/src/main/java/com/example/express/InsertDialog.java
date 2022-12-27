package com.example.express;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.express.api.ExpressInfoBean;

public class InsertDialog extends Dialog {
    private final static String TAG = "InsertDialog";
    private final static String WARNING = "您输入的数据不合法，请重新输入";
    private Context context;
    private String courierId;
    private EditText mSender;
    private EditText mReceiver;
    private EditText mOrigin;
    private EditText mDestination;

    private Button mBtnCancel;
    private Button mBtnConfirm;

    public ExpressInfoBean getExpressInfoBean() {
        return expressInfoBean;
    }

    private ExpressInfoBean expressInfoBean;

    public InsertDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_dialog);
        setCanceledOnTouchOutside(true);
        // 绑定输入信息
        mSender = findViewById(R.id.sender);
        mReceiver = findViewById(R.id.receiver);
        mOrigin = findViewById(R.id.origin);
        mDestination = findViewById(R.id.destination);
        //绑定确定取消按钮
        mBtnCancel = findViewById(R.id.cancel);
        mBtnConfirm = findViewById(R.id.confirm);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertDialog.this.dismiss();
            }
        });

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "confirm");
                String sender = mSender.getText().toString();
                String receiver = mReceiver.getText().toString();
                String origin = mOrigin.getText().toString();
                String destination = mDestination.getText().toString();

                expressInfoBean = new ExpressInfoBean("107", sender, receiver, origin, destination, courierId);
                if (!InsertDialog.this.checkExpressInfo(expressInfoBean)) {
                    Toast.makeText(context, "输入信息不合法", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(context, "添加订单成功", Toast.LENGTH_SHORT).show();
                InsertDialog.this.dismiss();
            }
        });
    }
    private boolean checkExpressInfo(ExpressInfoBean eif) {
        if (eif.getSender().length() == 0 || eif.getSender().length() == 0 || eif.getSender().length() == 0 || eif.getSender().length() == 0) {
            return false;
        }
        return true;
    }

    public void setCourierId(String courierId) {
        this.courierId = courierId;
    }


    public void setOnSubmitListener(View.OnClickListener listener) {
        mBtnConfirm.setOnClickListener(listener);
    }
}