package com.example.hanfl.ninetempledemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.hanfl.ninetempledemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HanFL on 2016/9/27.
 * DemoViewHolder
 */

public class DemoViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text_view_item)
    TextView textView;

    public DemoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
