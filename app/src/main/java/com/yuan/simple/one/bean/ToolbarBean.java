package com.yuan.simple.one.bean;

/**
 * Created by YuanYe on 2018/8/4.
 */
public class ToolbarBean {

    private String name;
    private int code;//标记

    public ToolbarBean(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
