package com.grasp.training.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.service.MqttService;
import com.grasp.training.tool.AndroidToJs;
import com.grasp.training.tool.BaseTcpMqttActpvity;
import com.grasp.training.tool.Tool;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebViewActivity extends BaseTcpMqttActpvity {
    public final static String TAG = "Print-WebViewActivity";
    public final static String WebViewActivity1 = "WebViewActivity1";
    public final static String WebViewActivity2 = "WebViewActivity2";
    public final static String WebViewActivity3 = "WebViewActivity3";
    public final static String WebViewActivity4 = "WebViewActivity4";
    @BindView(R.id.webView_tv)
    TextView webViewTv;
    @BindView(R.id.webView_fh)
    Button webViewFh;
    @BindView(R.id.webView_sz)
    Button webViewSz;
    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.web_lin)
    LinearLayout web_lin;
    final int version = Build.VERSION.SDK_INT;
    private String sid, type, dname, url;
    private Context context;
    private String myTopicding, myTopic;
    private String sys_ver = "", hard_ver = "";

    public static void starstEquipmentActivity(Context context, String sid, String type, String name,String url) {
        Intent in = new Intent(context, WebViewActivity.class);
        in.putExtra(WebViewActivity1, sid);
        in.putExtra(WebViewActivity2, type);
        in.putExtra(WebViewActivity3, name);
        in.putExtra(WebViewActivity4, url);
        context.startActivity(in);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //这里将我们临时输入的一些数据存储起来
        outState.putString("get_name", dname);
    }

    @Override
    public String getMyTopic() {
        return myTopic;
    }

    @Override
    public String getMyTopicDing() {
        return myTopicding;
    }

    @Override
    public String getSid() {
        return sid;
    }


    @Override
    public int setLayoutId() {
        return R.layout.webview_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context = getContext();
        sid = getIntent().getStringExtra(WebViewActivity1);
        type = getIntent().getStringExtra(WebViewActivity2);
        dname = getIntent().getStringExtra(WebViewActivity3);
        if (getSavedInstanceState() != null) {
//
            dname = getSavedInstanceState().getString("get_name", ":");
        }
        url = getIntent().getStringExtra(WebViewActivity4);
        myTopicding = "iotbroad/iot/" + type + "_ack/" + sid;
        myTopic = "iotbroad/iot/" + type + "/" + sid;
        if (sid == null || type == null) {
            Toast.makeText(context, "数据错误", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (sid.equals("") || type.equals("")) {
            Toast.makeText(context, "数据错误", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

    }
    private boolean web_err=true;

    private AndroidToJs androidToJs;
    @Override
    public void initView() {
        WebSettings webSettings = mWebView.getSettings();


        // 1、LayoutAlgorithm.NARROW_COLUMNS ： 适应内容大小
// 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
        initWebView();
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS );
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        androidToJs= new AndroidToJs();
        androidToJs.setMyTopic(myTopic);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    DatagramSocket socket = getSocket();
                    if (socket != null) {

                        androidToJs.setSocket(socket);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (version < 18) {
                                    mWebView.loadUrl("javascript:initData()");//initData(pSid,pType);
                                }else{
                                    mWebView.evaluateJavascript("javascript:initData()",new ValueCallback<String>(){//javascript:initData()
                                        @Override
                                        public void onReceiveValue(String value) {
                                            Log.i("qqq", "onReceiveValue: value="+value);//onReceiveValue: value=null
                                        }
                                    });
                                }
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        androidToJs.setSid(sid);
        androidToJs.setType(type);
        mWebView.addJavascriptInterface(androidToJs, "androidlistener");//JS通过WebView调用 Android 代码
//        url = url.replace("3006", "3008");
        Log.i(TAG, "initView: url="+url);
//        url="https://iot.iotbroad.com/download/switchdh_2.html";
        mWebView.loadUrl(url);//file:///android_asset/javascript.html

//        mWebView.loadUrl("https://blog.csdn.net/carson_ho/article/details/64904691/");//file:///android_asset/javascript.html
//        mWebView.loadUrl("file:///android_asset/javascript.html");
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //通过 WebViewClient 的方法shouldOverrideUrlLoading ()回调拦截 url
                Uri uri = Uri.parse(url);
                if (uri.getScheme().equals("js")) {
                    if (uri.getAuthority().equals("webview")) {
                        Log.i("qqq", "shouldOverrideUrlLoading: js调用了Android的方法");
                        HashMap<String, String> params = new HashMap<>();
                        Set<String> colloection = uri.getQueryParameterNames();
                        Iterator<String> it = colloection.iterator();
                        while (it.hasNext()) {
                            String parameterName = it.next();
                            Log.i("qqq", "shouldOverrideUrlLoading: parameterName="+parameterName);//parameterName=arg1 parameterName=arg2
                        }
                    }
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                // 这个方法在6.0才出现
                int statusCode = errorResponse.getStatusCode();
                System.out.println("onReceivedHttpError code = " + statusCode);
                Log.e("onReceivedHttpError","onReceivedHttpError statusCode="+statusCode);
                if (404 == statusCode || 500 == statusCode) {
                    web_lin.setVisibility(View.VISIBLE);
                    mWebView.setVisibility(View.GONE);
                    web_err=false;
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // 断网或者网络连接超时
                Log.e("onReceivedHttpError","onReceivedError errorCode="+errorCode);
                if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
                    web_lin.setVisibility(View.VISIBLE);
                    mWebView.setVisibility(View.GONE);
                    web_err=false;
                }
            }
        });


        mWebView.setWebChromeClient(new WebChromeClient(){//只有prompt（）可以返回任意类型的值，操作最全面方便、更加灵活；而alert（）对话框没有返回值；confirm（）对话框只能返回两种状态（确定 / 取消）两个值
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {//弹出警告框
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alert");
                builder.setMessage(message);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                builder.setCancelable(false);
                builder.create().show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {//弹出确认框
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {//弹出输入框
                Uri uri = Uri.parse(message);
                if (uri.getScheme().equals("js")) {
                    if (uri.getAuthority().equals("webview")) {
                        Log.i("qqq", "onJsPrompt: js调用Android的方法");
                        HashMap<String, String> params = new HashMap<>();
                        Set<String> collection = uri.getQueryParameterNames();
                        result.confirm("js调用Android的方法--android");
                    }
                    return true;
                }
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
            @Override
            public void onReceivedTitle(WebView view, String title) { //404 或者505
                super.onReceivedTitle(view, title);
                // android 6.0 以下通过title获取
                Log.e("onReceivedHttpError","onReceivedTitle title="+title);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (title.contains("404") || title.contains("500") || title.contains("Error")) {
                        web_lin.setVisibility(View.VISIBLE);
                        mWebView.setVisibility(View.GONE);
                        web_err=false;
                    }
                }
            }
        });
    }

    private void initWebView() {

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        // 开启 DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        //开启 database storage API 功能
        mWebView.getSettings().setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath()+"training_webview/";
        //      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
        //设置数据库缓存路径
        mWebView.getSettings().setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        mWebView.getSettings().setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        mWebView.getSettings().setAppCacheEnabled(true);
    }

    @Override
    public void initObject() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void init() {
        webViewTv.setText(dname);
    }

    @Override
    public void MyMessageArrived(final String message) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonF;
                    Message me;
                    String js = "";
                    String channel_0 = "";
                    int var = 0;
                    String data = "";
                    String search = "";
                    String state = "";
                    final JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.getString("cmd");
                    String mSid = jsonObject.optString("sid", "");  //设备号
                    if (!mSid.equals(sid)) {

                        return;
                    }
                    JSONObject tempJson = new JSONObject();
                    Iterator<String> keys = jsonObject.keys();

                    String tempKey = "";
                    while(keys.hasNext()){
                        tempKey = keys.next();
                        if (tempKey.equals("cmd")){
                            tempJson.put(tempKey, jsonObject.optString(tempKey, "").replace(type, "*"));
                        }
                        else if(!tempKey.equals("sid") && !tempKey.equals("sys_ver") && !tempKey.equals("hard_ver") && !tempKey.equals("ip")){

                            tempJson.put(tempKey, jsonObject.optString(tempKey, ""));
                        }
                    }
                    final JSONObject tempJson2 = tempJson;
                    Log.i(TAG, "run: tempJson2="+tempJson2+",tempJson2.keys().hasNext()="+tempJson2.keys().hasNext());
                    if (tempJson2.keys().hasNext()) {
                        mWebView.post(new Runnable() {
                            @Override
                            public void run() {

//                            mWebView.loadUrl("javascript:androidToJSStatus("+message+")");//androidToJSStatus(jsonStr) 发message或jsonObject给JS都可以

                                mWebView.loadUrl("javascript:androidToJSStatus("+tempJson2+")");//androidToJSStatus(jsonStr)
                            }
                        });
                    }

                    if (cmd.equals("wifi_" + type + "_ack")) {
                        handler.removeMessages(3000);
                        handler.removeMessages(5000);

                        String ver = jsonObject.optString("sys_ver", "");
                        if (!ver.equals("")) {
                            sys_ver = ver;
                        }
                        String hard = jsonObject.optString("hard_ver", "");
                        if (!hard.equals("")) {
                            hard_ver = hard;
                        }
                        me = new Message();
                        me.obj=message;
                        me.what = 1000;
                        handler.sendMessage(me);

                    } else if (cmd.equals("updatedevicename_ok")) {
                        String uname = jsonObject.optString("uname", "");  //
                        if (!uname.equals(MainActivity.NameUser)) {
                            return;
                        }
                        String clientid = jsonObject.optString("clientid", "");
                        if (!clientid.equals(Tool.getIMEI(getContext()))) {
                            return;
                        }
                        dname = jsonObject.optString("dname", "");
                        handler.sendEmptyMessageDelayed(233, 500);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    protected void onStart() {
        if (!sid.equals("")) {
            handler.removeMessages(3000);
            handler.sendEmptyMessageDelayed(3000, 000);
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1000);
        handler.removeMessages(2000);
        handler.removeMessages(2001);
        handler.removeMessages(3000);
        handler.removeMessages(4000);
        handler.removeMessages(5000);
        handler.removeMessages(6000);
        handler.removeMessages(233);
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 233:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    webViewTv.setText(dname);
                    break;
                case 1000:
//                    switchSys.setText("版本号：" + sys_ver);
//                    setStateView(search_zt1);
//                    setStateView2(search_zt2);
                    if(!web_err){
                        Log.e("qqq","刷新");
                        web_err=true;
                        mWebView.reload();
                        web_lin.setVisibility(View.GONE);
                        mWebView.setVisibility(View.VISIBLE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        final JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(msg.obj.toString());
                            JSONObject tempJson = new JSONObject();
                            Iterator<String> keys = jsonObject.keys();

                            String tempKey = "";
                            while(keys.hasNext()){
                                tempKey = keys.next();
                                if (tempKey.equals("cmd")){
                                    tempJson.put(tempKey, jsonObject.optString(tempKey, "").replace(type, "*"));
                                }
                                else if(!tempKey.equals("sid") && !tempKey.equals("sys_ver") && !tempKey.equals("hard_ver") && !tempKey.equals("ip")){

                                    tempJson.put(tempKey, jsonObject.optString(tempKey, ""));
                                }
                            }
                            final JSONObject tempJson2 = tempJson;
                            Log.i(TAG, "run: tempJson2="+tempJson2+",tempJson2.keys().hasNext()="+tempJson2.keys().hasNext());
                            if (tempJson2.keys().hasNext()) {
                                mWebView.post(new Runnable() {
                                    @Override
                                    public void run() {

//                            mWebView.loadUrl("javascript:androidToJSStatus("+message+")");//androidToJSStatus(jsonStr) 发message或jsonObject给JS都可以

                                        mWebView.loadUrl("javascript:androidToJSStatus("+tempJson2+")");//androidToJSStatus(jsonStr)
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    break;

                case 3000:
                    handler.sendEmptyMessageDelayed(5000, 0);//循环获取开关状态
                    handler.sendEmptyMessageDelayed(3000, 1000);
                    break;
                case 5000:
                    if (isConnected()) {
                        push_read();
                    } else {
                    }

                    break;
            }
        }
    };
    public void push_read() {  //获取状态

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_" + type + "_read");
                    jsonObject.put("sid", sid);
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }


    private String mc = "";

    public void setName() {
        Log.e("qqq", "setName");
        final EditText et = new EditText(this);
        et.setText(dname);
        new AlertDialog.Builder(this).setTitle("改变名称")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "内容不能为空！" + input, Toast.LENGTH_LONG).show();
                        } else {
                            mc = input;
                            push_name();
                            showPro();
                        }
                    }
                })
                .setNegativeButton("取消", null).show();

    }


    public void push_name() { //修改名称

        final String myTopicding_too = MqttService.myTopicDevice;
//        subscribe(myTopicding_too);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "updatedevicename");
                    jsonObject.put("sid", sid);
                    jsonObject.put("dname", mc);
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    String js = jsonObject.toString();
                    publish_String3(js, myTopicding_too);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }


    private ProgressDialog dialog;

    public void showPro() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("修改中...");
        dialog.setCancelable(true);
        dialog.show();
    }

    public void put_sz(View v) {
        EquipmentUpdataActivity.starstEquipmentActivity(context, sid, type, sys_ver, hard_ver);
    }


    @OnClick({R.id.webView_tv, R.id.webView_fh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.webView_tv:
                setName();
                break;
            case R.id.webView_fh:
                finish();
                break;
        }
    }

}
