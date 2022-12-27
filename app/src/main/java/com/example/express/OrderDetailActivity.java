package com.example.express;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.express.api.CourierDetail;
import com.example.express.api.ExpressInfoBean;
import com.example.express.api.ExpressInfoList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private final static String TAG = "ORDER_DETAIL_ACTIVITY";
    private String name;
    private String id;
    private TextView mName;
    private TextView mId;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMyAdapter;
    private List<ExpressInfoBean> list;
    private Button mInsertBtn;
    private Handler handler = new MyHandler(this);
    private ExpressInfoBean retInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        mName = findViewById(R.id.name);
        mId = findViewById(R.id.id);
        mRecyclerView = findViewById(R.id.order_info);
        mInsertBtn = findViewById(R.id.btn_plus);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        id = intent.getStringExtra("id");

        mName.setText(name);
        mId.setText(id);

        getInfoList();

        mRecyclerView = findViewById(R.id.order_info);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mMyAdapter = new MyAdapter();

        mInsertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertDialog insertDialog = new InsertDialog(OrderDetailActivity.this) {
                    @Override
                    protected void onStop() {
                        super.onStop();
                        retInfo = this.getExpressInfoBean();
                        if (retInfo == null)
                            return;
                        retInfo.setCourierId(id);
                        list.add(0, retInfo);
                        insertInfo(retInfo);
                        mMyAdapter.notifyItemInserted(0);
                    }
                };
                insertDialog.show();
            }
        });
    }

    /**
     * 获取json
     */
    private void getInfoList() {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = "http://10.0.2.2:8080/express_system/courier_order?id=" + id;
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
                ObjectMapper objectMapper = new ObjectMapper();
                list = objectMapper.readValue(data, ExpressInfoList.class).getList();
                handler.sendEmptyMessage(0);
            }
        });
    }

    private void insertInfo(ExpressInfoBean eif) {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = "http://10.0.2.2:8080/express_system/insert_order";
        FormBody formBody = new FormBody.Builder()
                .add("sender", eif.getSender())
                .add("receiver", eif.getReceiver())
                .add("origin", eif.getOrigin())
                .add("destination", eif.getDestination())
                .add("courierId", eif.getCourierId())
                .build();
        
        Request postRequest = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        
        Call call = okHttpClient.newCall(postRequest);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailures");
                System.out.println(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.i(TAG, "onResponse: res");
                if (res.equals("success")) {
                    Log.i(TAG, "onResponse: success");
                } else {
                    Log.i(TAG, "onResponse: fail");
                }
            }
        });
    }

    private Bitmap getQrCode(String content) throws WriterException {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 600, 600);
        return bitmap;
    }


    private class MyHandler extends Handler {
        //弱引用持有HandlerActivity , GC 回收时会被回收掉
        private WeakReference<OrderDetailActivity> weakReference;

        public MyHandler(OrderDetailActivity activity) {
            this.weakReference = new WeakReference(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            OrderDetailActivity activity = weakReference.get();
            super.handleMessage(msg);
            if (null != activity) {
                // 执行业务逻辑
                mRecyclerView.setAdapter(mMyAdapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(OrderDetailActivity.this);
                mRecyclerView.setLayoutManager(layoutManager);
                // Toast.makeText(activity,"handleMessage",Toast.LENGTH_SHORT).show();
            }
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(OrderDetailActivity.this, R.layout.order_list, null);
            MyViewHolder myViewHolder = new MyViewHolder(view);

            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            ExpressInfoBean expressInfoBean = list.get(position);
            holder.mId.setText(expressInfoBean.getExpressId());
            holder.mReceiver.setText(expressInfoBean.getReceiver());
            holder.mSender.setText(expressInfoBean.getSender());
            holder.mOrigin.setText(expressInfoBean.getOrigin());
            holder.mDestination.setText(expressInfoBean.getDestination());
            holder.qrcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(OrderDetailActivity.this, R.style.NormalDialogStyle);
                    View myView = View.inflate(OrderDetailActivity.this, R.layout.div_dialog, null);
                    dialog.setContentView(myView);
                    dialog.setCanceledOnTouchOutside(true);

                    ImageView mQrCode = myView.findViewById(R.id.qrcode);
                    Bitmap bitmap;
                    try {
                        String json = new ObjectMapper().writeValueAsString(expressInfoBean);
                        bitmap = OrderDetailActivity.this.getQrCode(json);
                        mQrCode.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Window dialogWindow = dialog.getWindow();

                    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.gravity = Gravity.CENTER;
                    dialogWindow.setAttributes(lp);
                    dialog.show();
                }
            });

            holder.mBtnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getAdapterPosition();
                    list.remove(pos);
                    notifyItemRemoved(pos);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mId, mReceiver, mSender, mOrigin, mDestination;
        private Button mBtnDel;
        private ImageView qrcode;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mId = itemView.findViewById(R.id.id);
            mReceiver = itemView.findViewById(R.id.receiver);
            mSender = itemView.findViewById(R.id.sender);
            mOrigin = itemView.findViewById(R.id.origin);
            mDestination = itemView.findViewById(R.id.destination);
            mBtnDel = itemView.findViewById(R.id.btn_del);
            qrcode = itemView.findViewById(R.id.qrcode);
        }
    }
}