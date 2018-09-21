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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.EquipmentData;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.grasp.training.tool.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SwitchActivity extends BaseMqttActivity {

    public final static String SwitchActivity1 = "SwitchActivity1";
    public final static String SwitchActivity2 = "SwitchActivity2";
    public final static String SwitchActivity3 = "SwitchActivity3";
    @BindView(R.id.switch_tv)
    TextView switchTv;
    @BindView(R.id.switch_fh)
    Button switchFh;
    @BindView(R.id.switch_sys)
    TextView switchSys;
    @BindView(R.id.switch_sys_im)
    ImageView switchSysIm;
    @BindView(R.id.switch_state1)
    Button switchState1;
    @BindView(R.id.switch_state2)
    Button switchState2;
    @BindView(R.id.switch_im_state1)
    ImageView switchImState1;
    @BindView(R.id.switch_im_state2)
    ImageView switchImState2;

    private String sid, type, dname;
    private Context context;
    private String myTopicding, myTopic;
    private String sys_ver = "", hard_ver = "";
    private boolean search_zt1, search_zt2;

    public static void starstEquipmentActivity(Context context, String sid, String type, String name) {
        Intent in = new Intent(context, SwitchActivity.class);
        in.putExtra(SwitchActivity1, sid);
        in.putExtra(SwitchActivity2, type);
        in.putExtra(SwitchActivity3, name);
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
        return R.layout.switch_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context = getContext();
        sid = getIntent().getStringExtra(SwitchActivity1);
        type = getIntent().getStringExtra(SwitchActivity2);
        dname = getIntent().getStringExtra(SwitchActivity3);
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
        switchSysIm.setVisibility(View.INVISIBLE);
        handler.sendEmptyMessageDelayed(2222, 1000);
    }

    @Override
    public void initView() {
        setEquipmentData();
    }

    @Override
    public void initObject() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void init() {
        if (eMap.get(type) != null) {
//            equipmentTv.setText(eMap.get(type));
        }
        switchTv.setText(dname);
    }


    @OnClick({R.id.switch_im_state1,R.id.switch_im_state2,R.id.switch_tv, +R.id.switch_sys, R.id.switch_fh, R.id.switch_state1, R.id.switch_state2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.switch_fh:
                finish();
                break;
            case R.id.switch_state1:
                if (search_zt1) {
                    push1("off");
                } else {
                    push1("on");
                }
                break;
            case R.id.switch_state2:
                if (search_zt2) {
                    push2("off");
                } else {
                    push2("on");
                }
                break;
            case R.id.switch_im_state1:
                if (search_zt1) {
                    push1("off");
                } else {
                    push1("on");
                }
                break;
            case R.id.switch_im_state2:
                if (search_zt2) {
                    push2("off");
                } else {
                    push2("on");
                }
                break;
            case R.id.switch_sys://更新检查
//                if (!sys_ver.equals("")) {
//                    if (updata_zt) {
//                        updata();
//                    }
//                }
                break;
            case R.id.switch_tv:
                setName();
                break;


        }
    }


    public void push1(final String s) {  //改变开关状态
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_" + type);
                    jsonObject.put("state", s);
                    jsonObject.put("sid", sid);
                    jsonObject.put("channel", 1);
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }


    public void push2(final String s) {  //改变开关2状态
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_" + type);
                    jsonObject.put("state", s);
                    jsonObject.put("sid", sid);
                    jsonObject.put("channel", 2);
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }

    public void setStateView(boolean zt) {  //开关
        if (zt) {
            switchState1.setText("关闭");
            switchImState1.setBackgroundResource(R.drawable.zhu_chazuoa_selected);
        } else {
            switchState1.setText("开启");
            switchImState1.setBackgroundResource(R.drawable.zhu_chazuoa_normal);
        }
    }

    public void setStateView2(boolean zt) {  //开关
        if (zt) {
            switchState2.setText("关闭");
            switchImState2.setBackgroundResource(R.drawable.zhu_chazuoa_selected);
        } else {
            switchState2.setText("开启");
            switchImState2.setBackgroundResource(R.drawable.zhu_chazuoa_normal);
        }
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
                        if (channel_1.equals("on")) {
                            search_zt1 = true;
                        } else {
                            search_zt1 = false;
                        }
                        if (channel_2.equals("on")) {
                            search_zt2 = true;
                        } else {
                            search_zt2 = false;
                        }
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


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 233:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    switchTv.setText(dname);
                    break;
                case 1000:
                    switchSys.setText("版本号：" + sys_ver);
                    setStateView(search_zt1);
                    setStateView2(search_zt2);
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

    public void put_sz(View v){
        EquipmentUpdataActivity.starstEquipmentActivity(context,sid,type,sys_ver,hard_ver);
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


    private ArrayList<EquipmentData> elist;
    private HashMap<String, String> eMap;

    private void setEquipmentData() {  //获取所有类型数据
        String data = SharedPreferencesUtils.getParam(getContext(), MainActivity.MainData, "").toString();
//        Log.e("qqq","data: "+data);
        elist = new ArrayList<>();
        eMap = new HashMap<>();
        if (!data.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray js = jsonObject.getJSONArray("data");
                for (int i = 0; i < js.length(); i++) {
                    JSONObject jsonObject1 = js.getJSONObject(i);
                    String dname = jsonObject1.optString("dname", "");//名称
                    String type = jsonObject1.optString("type", "");//类型
                    String thumbnail = jsonObject1.optString("dname", ""); //预览图
                    String stateall = jsonObject1.optString("stateall", "");
                    EquipmentData equipmentData = new EquipmentData();
                    equipmentData.setDname(dname);
                    equipmentData.setType(type);
                    equipmentData.setStateall(stateall);
                    equipmentData.setThumbnail(thumbnail);
                    elist.add(equipmentData);
                    eMap.put(type, dname);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


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

}
