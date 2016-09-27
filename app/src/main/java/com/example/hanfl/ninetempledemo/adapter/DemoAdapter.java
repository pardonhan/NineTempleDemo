package com.example.hanfl.ninetempledemo.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hanfl.ninetempledemo.R;
import com.example.hanfl.ninetempledemo.data.PrizeData;
import com.example.hanfl.ninetempledemo.eventbus.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.util.LogUtil;

import java.util.List;

/**
 * Created by HanFL on 2016/9/27.
 * DemoAdapter34
 */

public class DemoAdapter extends RecyclerView.Adapter<DemoViewHolder> {
    private Context mContext;
    private List<PrizeData> list;

    public DemoAdapter(Context context, List<PrizeData> datas) {
        this.mContext = context;
        this.list = datas;
    }

    @Override
    public DemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.d("onCreateViewHolder");
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_main_item, parent, false);
        return new DemoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DemoViewHolder holder, int position) {
        LogUtil.d("onBindViewHolder");
        if (position == 4) {
            holder.textView.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_default_award));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.d("------------");
                    EventBus.getDefault().post(new MessageEvent("Hello ", "everyone!"));
                }
            });
        } else {
            if (list.get(position).img == null) {
                holder.textView.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_default));
            } else {
                holder.textView.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_select));
            }
            holder.textView.setText(list.get(position).name);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
