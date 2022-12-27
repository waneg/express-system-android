package com.example.express;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.express.api.CourierDetail;
import com.example.express.api.CourierInfoList;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminActivity extends AppCompatActivity {
    private final static String TAG = "Admin";
    private RecyclerView mRecyclerView;
    private MyAdapter mMyAdapter;
    private Handler handler = new MyHandler(this);

    private List<CourierDetail> courierList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        getInfoList();
        mRecyclerView = findViewById(R.id.courier_info);
        mMyAdapter = new MyAdapter();
    }

    private class MyHandler extends Handler {

        //弱引用持有HandlerActivity , GC 回收时会被回收掉
        private WeakReference<AdminActivity> weakReference;

        public MyHandler(AdminActivity activity) {
            this.weakReference = new WeakReference(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AdminActivity activity = weakReference.get();
            super.handleMessage(msg);
            if (null != activity) {
                // 执行业务逻辑
                mRecyclerView.setAdapter(mMyAdapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(AdminActivity.this);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                mRecyclerView.setLayoutManager(layoutManager);
                // Toast.makeText(activity,"handleMessage",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getInfoList() {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = "http://10.0.2.2:8080/express_system/courier_info";
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
                courierList = objectMapper.readValue(data, CourierInfoList.class).getList();
                handler.sendEmptyMessage(0);
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(AdminActivity.this).inflate(R.layout.item_list, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            myViewHolder.mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = myViewHolder.getAdapterPosition();
//                    Toast.makeText(view.getContext(), "点击了第" + position + "个按钮", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminActivity.this, OrderDetailActivity.class);
                    intent.putExtra("id", courierList.get(position).getId());
                    intent.putExtra("name", courierList.get(position).getName());
                    startActivity(intent);
                }
            });
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            CourierDetail courierDetail = courierList.get(position);
            holder.mId.setText(courierDetail.getId());
            holder.mName.setText(courierDetail.getName());
        }

        @Override
        public int getItemCount() {
            return courierList.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mId, mName;
        private Button mBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mId = itemView.findViewById(R.id.textView);
            mName = itemView.findViewById(R.id.textView2);
            mBtn = itemView.findViewById(R.id.detail);
        }
    }
}