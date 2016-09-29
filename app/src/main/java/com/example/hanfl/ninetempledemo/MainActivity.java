package com.example.hanfl.ninetempledemo;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.hanfl.ninetempledemo.adapter.DemoAdapter;
import com.example.hanfl.ninetempledemo.data.PrizeData;
import com.example.hanfl.ninetempledemo.eventbus.MessageEvent;
import com.example.hanfl.ninetempledemo.utils.AppJsonFileReader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.x_recycler_view)
    RecyclerView xRecyclerView;
    DemoAdapter demoAdapter;
    List<PrizeData> prizeList = new ArrayList<>();// adapter 数据源
    private MainHandler mainHandler = new MainHandler(new WeakReference<>(this));

    private volatile boolean isRun = true;//点击后启动抽奖线程，循环的标志，当奖品开出后，并旋转到奖品位置时停止线程。
    private volatile int count = 0;//旋转计数器，每跳一格+1,然后取余8得到array数组的下标，
    private int[] array = new int[]{0, 1, 2, 5, 8, 7, 6, 3};//旋转数组，按照这个数组的排列顺序来改变方块背景色表示正在抽奖
    private int result = 9;//开奖结果，当数字属于0-8时表示已经开奖，此时要停止线程
    private boolean isResult = true;// 是否生成结果，（正式使用时可以是判断是否去请求开奖结果的标志）
    AlertDialog.Builder builder;//提示奖品的dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        xRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        demoAdapter = new DemoAdapter(this, prizeList);
        xRecyclerView.setAdapter(demoAdapter);
        getJsonData();//获取所有奖品数据
        EventBus.getDefault().register(this);
        builder = new AlertDialog.Builder(this);
    }

    /**
     * 获取数据源
     */
    private void getJsonData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String jsonStr = AppJsonFileReader.getJson(getBaseContext(), "result.json");
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    String data = jsonObject.getString("data");
                    Gson gson = new Gson();
                    List<PrizeData> list = gson.fromJson(data, new TypeToken<List<PrizeData>>() {
                    }.getType());
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = list;
                    mainHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
                refreshView();
            }
        }.start();
    }

    private synchronized void refreshView() {
        try {
            while (isRun) {
                Thread.sleep(200);
                int m = count % 8;
                if (m == 0) {
                    prizeList.get(array[array.length - 1]).img = null;
                } else {
                    prizeList.get(array[m - 1]).img = null;
                }
                prizeList.get(array[m]).img = "2";
                mainHandler.sendEmptyMessage(1);
                count++;
                if (m == result) {
                    isRun = false;
                    mainHandler.sendEmptyMessage(3);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写了一个生成随机数的方法，生成0-7的随机数
     * <p>
     * 在使用时可替换为获取开奖结果的方法，然后延迟几秒给result赋值
     */
    private void toLoginActivity() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Random r = new Random();
                result = r.nextInt(7);
                Log.d("开奖结果---->", result + "");
            }
        };
        timer.schedule(task, 1000 * 8);
    }

    private void getResultData() {

    }

    private static class MainHandler extends Handler {
        private WeakReference<MainActivity> weakReference;

        protected MainHandler(WeakReference<MainActivity> wk) {
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final MainActivity activity = weakReference.get();
            if (activity == null) {
                return;
            }
            if (msg.what == 1) {
                if (activity.isResult) {
                    //获取抽奖结果，并关闭该判断，防止重复获取数据
                    activity.toLoginActivity();
                    activity.isResult = false;
                }
                activity.demoAdapter.notifyDataSetChanged();
            }
            if (msg.what == 2) {
                List<PrizeData> list = (List<PrizeData>) msg.obj;
                activity.prizeList.clear();
                for (int i = 0; i < list.size(); i++) {
                    if (i == 4) {
                        PrizeData prizeData = new PrizeData();
                        prizeData.id = UUID.randomUUID().toString();
                        activity.prizeList.add(prizeData);
                    }
                    activity.prizeList.add(list.get(i));
                }
            }
            if (msg.what == 3) {
                activity.builder.setMessage(activity.prizeList.get(activity.array[activity.result]).name);
                activity.builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                activity.builder.show();
            }
        }
    }
}
