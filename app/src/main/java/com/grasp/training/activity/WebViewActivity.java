package com.grasp.training.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.tool.BaseTcpMqttActpvity;
import com.grasp.training.tool.Tool;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebViewActivity extends BaseTcpMqttActpvity {
    public final static String WebViewActivity1 = "WebViewActivity1";
    public final static String WebViewActivity2 = "WebViewActivity2";
    public final static String WebViewActivity3 = "WebViewActivity3";
    @BindView(R.id.webView_tv)
    TextView webViewTv;
    @BindView(R.id.webView_fh)
    Button webViewFh;
    @BindView(R.id.webView_sz)
    Button webViewSz;

    private String sid, type, dname;
    private Context context;
    private String myTopicding, myTopic;
    private String sys_ver = "", hard_ver = "";

    public static void starstEquipmentActivity(Context context, String sid, String type, String name) {
        Intent in = new Intent(context, WebViewActivity.class);
        in.putExtra(WebViewActivity1, sid);
        in.putExtra(WebViewActivity2, type);
        in.putExtra(WebViewActivity3, name);
        context.startActivity(in);
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

    @Override
    public void initView() {

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
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.getString("cmd");
                    String mSid = jsonObject.optString("sid", "");  //设备号
                    if (!mSid.equals(sid)) {

                        return;
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
                        me.what = 1000;
                        String channel_1 = jsonObject.optString("channel_1");
                        String channel_2 = jsonObject.optString("channel_2");
//                        if (channel_1.equals("on")) {
//                            search_zt1 = true;
//                        } else {
//                            search_zt1 = false;
//                        }
//                        if (channel_2.equals("on")) {
//                            search_zt2 = true;
//                        } else {
//                            search_zt2 = false;
//                        }
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

        final String myTopicding_too = "iotbroad/iot/device";
        subscribe(myTopicding_too);
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
