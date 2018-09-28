package com.yuan.base.tools.okhttp;

import android.content.Context;

import com.yuan.base.tools.okhttp.callback.BaseBack;
import com.yuan.base.tools.okhttp.callback.FileBack;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by YuanYe on 2017/9/26.
 */

public class Execute {

    protected Request.Builder requestBuilder;
    protected OkHttpClient client;
    protected Context mContext;

    public Execute(Context context, Request.Builder request, OkHttpClient _client) {
        this.requestBuilder = request;
        this.client = _client;
        this.mContext = context;
    }

    /**
     * ****************************callBack请求封装****************************************
     */
    //统一对requestBuild处理，
    private Request getRequestBuild() {
        return requestBuilder.build();
    }


    //正常json返回的时候使用
    public void execute(BaseBack call) {
        if (call == null) throw new NullPointerException("回调：RxCall == null");
        call.setContext(mContext);
        client.newCall(getRequestBuild())
                .enqueue(call);
    }

    //下载文件时使用
    public void execute(FileBack fileBack) {
        if (fileBack == null) throw new NullPointerException("回调：RxCall == null");
        fileBack.setContext(mContext);
        client.newCall(getRequestBuild())
                .enqueue(fileBack);
    }
}