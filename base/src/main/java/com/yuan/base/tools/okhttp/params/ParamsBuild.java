package com.yuan.base.tools.okhttp.params;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yuan.base.tools.okhttp.Execute;
import com.yuan.base.tools.okhttp.callback.BaseBack;
import com.yuan.base.tools.okhttp.callback.FileBack;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by YuanYe on 2017/9/8.
 * 用于构建get(),post()等参数方法
 */
public class ParamsBuild {

    private Request.Builder requestBuilder;

    private FormBody.Builder postBuilder;
    private int postPartNum = 0; //标记postBuilder添加Part的数量

    private MultipartBody.Builder fileBuilder;
    private int filePartNum = 0; //标记fileBuilder添加Part的数量

    private OkHttpClient client;
    private Context mContext;


    public ParamsBuild(@NonNull Context context, @NonNull Request.Builder request,
                       @NonNull OkHttpClient _client) {
        this.requestBuilder = request;
        this.client = _client;
        this.mContext = context;
        postBuilder = new FormBody.Builder();
        postPartNum = 0;
        filePartNum = 0;
    }

    /**
     * ****************************添加头部****************************************
     * 如果只有头部，默认执行post请求
     */
    public ParamsBuild addHead(@NonNull String key, @NonNull String value) {
        if (TextUtils.isEmpty(key)) throw new NullPointerException("参数：head.key == null");
        requestBuilder.addHeader(key, value);
        return this;
    }

    /**
     * ****************************post请求封装****************************************
     */
    public PostBuilder post(@NonNull Map<String, String> params) {
        if (params == null) throw new NullPointerException("参数：params == null");
        //设置参数
        for (Map.Entry<String, String> entry : params.entrySet()) {
            postBuilder.add(entry.getKey(), entry.getValue());
        }
        return new PostBuilder(mContext, requestBuilder, client, postBuilder);
    }

    public PostBuilder post(@NonNull String key, @NonNull String value) {
        if (TextUtils.isEmpty(key)) throw new NullPointerException("参数：params.key == null");
        postBuilder.add(key, value);
        return new PostBuilder(mContext, requestBuilder, client, postBuilder);
    }

    /**
     * ****************************上传文件请求封装****************************************
     */
    public FileBuilder file(@NonNull String key, @NonNull String fileUrl) {
        File file = new File(fileUrl);
        if (file == null) {
            new Throwable("上传文件不存在。。。");
        }
        if (fileBuilder == null) {
            fileBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        }
        //上传文件
        RequestBody fileBody = RequestBody.create(MediaType.parse(getMimeType(fileUrl)), file);
        fileBuilder.addFormDataPart(key, file.getName(), fileBody);
        return new FileBuilder(mContext, requestBuilder, client, fileBuilder);
    }

    /**
     * ****************************上传字节输入请求封装****************************************
     */
    public Execute bytes(@NonNull byte[] bytes) {
        if (bytes == null) {
            new Throwable("上传数据不能为空。。。");
        }
        if (fileBuilder == null) {
            fileBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        }
        fileBuilder.addPart(Headers.of("Content-Disposition", "octet-stream;"),
                RequestBody.create(null, bytes));
        requestBuilder.post(fileBuilder.build());
        return new Execute(mContext, requestBuilder, client);
    }

    /**
     * ****************************get请求封装****************************************
     */
    public Execute get() {
        requestBuilder.get();
        return new Execute(mContext, requestBuilder, client);
    }

    /**
     * ****************************Json上传****************************************
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Execute json(@NonNull String json) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        requestBuilder.post(requestBody);
        return new Execute(mContext, requestBuilder, client);
    }

    /**
     * ****************************最后的执行部分****************************************
     *
     * @return
     */
    public void execute(@NonNull BaseBack jsonBack) {
        requestBuilder.post(postBuilder.build());
        new Execute(mContext, requestBuilder, client).execute(jsonBack);
    }

    public void execute(@NonNull FileBack fileBack) {
        requestBuilder.post(postBuilder.build());
        new Execute(mContext, requestBuilder, client).execute(fileBack);
    }

    /*
     * *******************************Common工具*********************************************
     */

    /**
     * 获取文件MimeType
     *
     * @param filename 文件名
     * @return
     */
    public static String getMimeType(@NonNull String filename) {
        FileNameMap filenameMap = URLConnection.getFileNameMap();
        String contentType = filenameMap.getContentTypeFor(filename);
        if (contentType == null) {
            contentType = "application/octet-stream"; //* exe,所有的可执行程序
        }
        return contentType;
    }
}