package com.yuan.simple.two.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.yuan.base.tools.other.Views;
import com.yuan.base.ui.mvp.MvpFragment;
import com.yuan.base.widget.title.TitleBar;
import com.yuan.simple.R;

/**
 * Created by YuanYe on 2018/4/13.
 */
public class TwoFragment extends MvpFragment {

    @Override
    public int getLayoutId() {
        return R.layout.frag_two_layout;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        TitleBar titleBar = Views.find(mView, R.id.title_bar);
        titleBar.setCenterText("简介");
    }

}
