package com.yuan.base.ui.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yuan.base.tools.other.ReflexUtil;
import com.yuan.base.ui.activity.FragActivity;

/**
 * Created by YuanYe on 2018/1/4.
 */
public abstract class MvpFragActivity<T extends MvpPresenter> extends FragActivity {

    private T presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        presenter = ReflexUtil.getT(MvpFragActivity.this, 0);
        if (presenter != null) {
            presenter.attachView(this);
        }
        super.onCreate(savedInstanceState);
    }

    public T getP() {
        if (presenter == null) {
            try {
                throw new NullPointerException("使用presenter,MVPActivity泛型不能为空");
            } catch (NullPointerException e) {
                throw e;
            }
        }
        return presenter;
    }
}