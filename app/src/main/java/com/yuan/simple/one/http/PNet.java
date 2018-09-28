package com.yuan.simple.one.http;


import com.yuan.base.tools.okhttp.OKHttp;
import com.yuan.base.tools.okhttp.callback.BaseBack;
import com.yuan.base.tools.log.ToastUtil;
import com.yuan.base.ui.mvp.MvpPresenter;

import okhttp3.Call;

/**
 * Created by YuanYe on 2018/8/15.
 */
public class PNet extends MvpPresenter<NetActivity> {

    public void get() {
        new OKHttp(getV()).url("http://mobile.weather.com.cn/data/sk/101010100.html?_=1381891661455")
                .get().execute(new BaseBack() {
            @Override
            public void onSuccess(Call call, String obj) {
                ToastUtil.showShort(mContext, obj);
            }
        });
    }
}