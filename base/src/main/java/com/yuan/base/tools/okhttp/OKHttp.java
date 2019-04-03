package com.yuan.base.tools.okhttp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * 基于OKHttp的简单封装。
 * 构建地址，创建Client. 其他参数构建详见MethodBuild
 * 这里的
 */
public class OKHttp {

    private OkHttpClient client;
    private Request.Builder requestBuilder;
    private Context mContext;

    /**
     * 通过创建对象方式启动，可以动态配置每次请求参数
     *
     * @param context
     */
    public OKHttp(@NonNull Context context) {
        //获取Client
        client = new RxHttpClient(context).getClient();
        requestBuilder = new Request.Builder();
        this.mContext = context;
    }

    /**
     * 对OKHttpUtil进行基本设置
     *
     * @param config 配置
     */
    public OKHttp(@NonNull Context context, @NonNull OKConfig config) {
        //获取Client
        client = new RxHttpClient(context, config).getClient();
        requestBuilder = new Request.Builder();
        this.mContext = context;
        if (!TextUtils.isEmpty(config.getCommonHead()) && !TextUtils.isEmpty(config.getCommonHeadKey())) {
            //TODO 公共head,可以统一添加
            requestBuilder.addHeader(config.getCommonHeadKey(), config.getCommonHead());
        }
    }

    public ParamBuild post(@NonNull String httpUrl) {
        if (TextUtils.isEmpty(httpUrl)) {
            throw new NullPointerException("地址：url == null");
        }
        requestBuilder.url(httpUrl);
        return new ParamBuild(mContext, requestBuilder, client, ParamBuild.POST, httpUrl);
    }


    public ParamBuild get(@NonNull String httpUrl) {
        if (TextUtils.isEmpty(httpUrl)) {
            throw new NullPointerException("地址：url == null");
        }
        requestBuilder.url(httpUrl);
        return new ParamBuild(mContext, requestBuilder, client, ParamBuild.GET, httpUrl);
    }


    /**
     * 参数配置
     *
     * @author yuanye
     * @date 2018/11/28 13:40
     */
    public class ParamBuild {

        private final static String TAG = "ParamBuild";

        public final static int GET = 1001;

        public final static int POST = 1002;

        public final static int PUT = 1003;

        public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        /**
         * 请求参数
         */
        private Request.Builder requestBuilder;

        /**
         * 执行体
         */
        private OkHttpClient client;

        /**
         * 上传参数集合:<key,value></key,value>
         */
        private Map<String, String> params;
        /**
         * 上传文件集合 : <key,path></key,path>
         */
        private Map<String, String> files;
        /**
         * 上传字节集合
         */
        private List<byte[]> bytes;
        /**
         * context
         */
        private Context mContext;

        /**
         * 请求类型： get,post等
         */
        private int requestType;
        /**
         * 上传文件时统一构造参数
         */
        private MultipartBody.Builder multipartBody = null;

        /**
         * 统一只有参数式构造参数
         */
        private FormBody.Builder paramsBody = null;

        /**
         * 请求地址
         */
        private String url;


        public ParamBuild(@NonNull Context context, @NonNull Request.Builder request,
                          @NonNull OkHttpClient _client, int requestType, String url) {
            this.requestBuilder = request;
            this.client = _client;
            this.mContext = context;
            this.requestType = requestType;
            this.url = url;
            params = new HashMap<>();
            files = new HashMap<>();
            bytes = new ArrayList<byte[]>();
        }

        /**
         * ****************************添加头部****************************************
         */

        public ParamBuild addHead(@NonNull String key, @NonNull String value) {
            if (TextUtils.isEmpty(key)) throw new NullPointerException("参数：head.key == null");
            requestBuilder.addHeader(key, value);
            return this;
        }

        public ParamBuild put(@NonNull String key, @NonNull String value) {
            if (TextUtils.isEmpty(key)) throw new NullPointerException("参数：params.key == null");
            params.put(key, value);
            return this;
        }

        public ParamBuild put(@NonNull Map<String, String> params) {
            if (params == null) throw new NullPointerException("参数：params == null");
            params.putAll(params);
            return this;
        }

        public ParamBuild file(@NonNull String key, @NonNull String value) {
            if (TextUtils.isEmpty(key)) throw new NullPointerException("参数：files.key == null");
            files.put(key, value);
            return this;
        }

        public ParamBuild file(@NonNull Map<String, String> params) {
            if (params == null) throw new NullPointerException("参数：files == null");
            files.putAll(params);
            return this;
        }

        public ParamBuild bytes(@NonNull byte[] bytes1) {
            if (bytes1 == null) throw new NullPointerException("上传文件字节为空");
            bytes.add(bytes1);
            return this;
        }


        /**
         * ****************************最后的执行部分****************************************
         */

        public Execute json(@NonNull String json) {
            RequestBody requestBody = RequestBody.create(JSON, json);
            switch (requestType) {
                case GET:
                    requestBuilder.url(HttpUrlUtil.addParams(url, params));
                    requestBuilder.get();
                    break;
                case PUT:
                    requestBuilder.url(url);
                    requestBuilder.put(requestBody);
                    break;
                case POST:
                    requestBuilder.url(url);
                    requestBuilder.post(requestBody);
                    break;
            }

            return new Execute(mContext, requestBuilder, client);
        }

        public void execute(@NonNull BaseMainBack mainCall) {

            if (bytes.size() > 0 || files.size() > 0) {
                /**
                 * 当存在文件数据时，上传文件的方式
                 */
                multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);

                //设置参数
                for (Map.Entry<String, String> param : params.entrySet()) {
                    multipartBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.getKey() + "\""),
                            RequestBody.create(null, param.getValue()));
                }
                //设置上传文件
                for (Map.Entry<String, String> file : files.entrySet()) {
                    File fileDir = new File(file.getValue());
                    RequestBody fileBody = RequestBody.create(MediaType
                            .parse(getMimeType(file.getValue())), fileDir);
                    multipartBody.addFormDataPart(file.getKey(), fileDir.getName(), fileBody);
                }
                //设置上传字节
                for (byte[] byte1 : bytes) {
                    multipartBody.addPart(Headers.of("Content-Disposition", "octet-stream;"),
                            RequestBody.create(null, byte1));
                }
                switch (requestType) {
                    case GET:
                        requestBuilder.url(HttpUrlUtil.addParams(url, params));
                        requestBuilder.get();
                        break;
                    case PUT:
                        requestBuilder.url(url);
                        requestBuilder.put(multipartBody.build());
                        break;
                    case POST:
                        requestBuilder.url(url);
                        requestBuilder.post(multipartBody.build());
                        break;
                }
            } else {
                paramsBody = new FormBody.Builder();
                //设置参数
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    paramsBody.add(entry.getKey(), entry.getValue());
                }
                switch (requestType) {
                    case GET:
                        requestBuilder.url(HttpUrlUtil.addParams(url, params));
                        requestBuilder.get();
                        break;
                    case PUT:
                        requestBuilder.url(url);
                        requestBuilder.put(paramsBody.build());
                        break;
                    case POST:
                        requestBuilder.url(url);
                        requestBuilder.post(paramsBody.build());
                        break;
                }
            }
            new Execute(mContext, requestBuilder, client).execute(mainCall);
        }

        /**
         * 获取文件MimeType
         *
         * @param filename 文件名
         * @return
         */
        private String getMimeType(@NonNull String filename) {
            FileNameMap filenameMap = URLConnection.getFileNameMap();
            String contentType = filenameMap.getContentTypeFor(filename);
            if (contentType == null) {
                contentType = "application/octet-stream"; //* exe,所有的可执行程序
            }
            return contentType;
        }
    }

    /**
     * 最后的执行
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

        //统一返回
        public void execute(BaseMainBack mainBack) {
            if (mainBack == null) throw new NullPointerException("回调：RxCall == null");
            MainCall baseFileBack = new MainCall(mainBack);
            client.newCall(getRequestBuild())
                    .enqueue(baseFileBack);
        }
    }
}
