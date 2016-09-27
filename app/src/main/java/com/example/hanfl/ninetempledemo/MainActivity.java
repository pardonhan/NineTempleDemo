package com.example.hanfl.ninetempledemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.hanfl.ninetempledemo.adapter.DemoAdapter;
import com.example.hanfl.ninetempledemo.data.PrizeData;
import com.example.hanfl.ninetempledemo.eventbus.MessageEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.x_recycler_view)
    RecyclerView xRecyclerView;
    DemoAdapter demoAdapter;
    List<PrizeData> prizeList = new ArrayList<>();
    private boolean isRun = true;
    private int count = 0;
    private MainHandler mainHandler = new MainHandler(new WeakReference<>(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        xRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        demoAdapter = new DemoAdapter(this, prizeList);
        xRecyclerView.setAdapter(demoAdapter);
        getData();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {

                    while (isRun) {
                        sleep(150);
                        if (count % 9 == 4) count++;
                        int m = count % 9;
                        for (int i = 0; i < prizeList.size(); i++) {
                            prizeList.get(m).img = null;
                        }
                        prizeList.get(m).img = "1";
                        //demoAdapter.notifyDataSetChanged();
                        mainHandler.sendEmptyMessage(1);
                        count++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    private void getData() {
        RequestParams params = new RequestParams("http://114.215.92.83/jfshop/index.php/api/User/getPrizeList");
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    if (10000 == result.getInt("errcode") && "操作成功".equals(result.getString("msg"))) {
                        String data = result.getString("data");
                        Gson gson = new Gson();
                        List<PrizeData> list = gson.fromJson(data, new TypeToken<List<PrizeData>>() {
                        }.getType());
                        prizeList.clear();
                        for (int i = 0; i < list.size(); i++) {
                            if (i == 4) {
                                PrizeData prizeData = new PrizeData();
                                prizeData.id = UUID.randomUUID().toString();
                                prizeList.add(prizeData);
                            }
                            prizeList.add(list.get(i));
                        }
                        demoAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private static class MainHandler extends Handler {
        private WeakReference<MainActivity> weakReference;

        protected MainHandler(WeakReference<MainActivity> wk) {
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = weakReference.get();
            if (activity == null) {
                return;
            }
            activity.demoAdapter.notifyDataSetChanged();
        }
    }

}
