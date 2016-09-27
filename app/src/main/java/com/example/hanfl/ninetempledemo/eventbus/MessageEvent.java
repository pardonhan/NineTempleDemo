package com.example.hanfl.ninetempledemo.eventbus;

/**
 * Created by HanFL on 2016/9/27.
 */

public class MessageEvent {
    public String code;
    public String message;

    public MessageEvent(String c, String m) {
        this.code = c;
        this.message = m;
    }
}
