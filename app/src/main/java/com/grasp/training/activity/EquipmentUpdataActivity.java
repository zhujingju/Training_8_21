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
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class EquipmentUpdataActivity extends BaseMqttActivity {

    public final static String EquipmentUpdataActivity1 = "EquipmentUpdataActivity1";
    public final static String EquipmentUpdataActivity2 = "EquipmentUpdataActivity2";
    public final static String EquipmentUpdataActivity3 = "EquipmentUpdataActivity3";
    public final static String EquipmentUpdataActivity4 = "EquipmentUpdataActivity4";
    @BindView(R.id.equipment_updata_tv)
    TextView equipmentUpdataTv;
    @BindView(R.id.equipment_fh)
    Button equipmentFh;
    @BindView(R.id.eu_layout1_tv)
    TextView euLayout1Tv;
    @BindView(R.id.eu_layout1)
    RelativeLayout euLayout1;
    @BindView(R.id.eu_layout2_tv)
    TextView euLayout2Tv;
    @BindView(R.id.eu_layout2)
    RelativeLayout euLayout2;
    @BindView(R.id.eu_layout3_tv)
    TextView euLayout3Tv;
    @BindView(R.id.eu_layout4_tv)
    TextView euLayout4Tv;
    @BindView(R.id.eu_layout3)
    RelativeLayout euLayout3;
    @BindView(R.id.eu_sys_im)
    ImageView euSysIm;
    @BindView(R.id.eu_layout4)
    RelativeLayout euLayout4;
    private Context context;
    private String sid = "";
    private String type = "";
    private String myTopicding, myTopic;
    private String sys_ver = "", hard_ver = "";

    public static void starstEquipmentActivity(Context context, String sid, String type, String s_v, String h_v) {
        if (s_v.equals("") || h_v.equals("")) {
            Toast.makeText(context, "正在获取当前设备信息，请稍后再试", Toast.LENGTH_LONG).show();
            return;
        }
        Intent in = new Intent(context, EquipmentUpdataActivity.class);
        in.putExtra(EquipmentUpdataActivity1, sid);
        in.putExtra(EquipmentUpdataActivity2, type);
        in.putExtra(EquipmentUpdataActivity3, s_v);
        in.putExtra(EquipmentUpdataActivity4, h_v);
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
        return R.layout.equipment_updata_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context = getContext();
        sid = getIntent().getStringExtra(EquipmentUpdataActivity1);
        type = getIntent().getStringExtra(EquipmentUpdataActivity2);
        sys_ver = getIntent().getStringExtra(EquipmentUpdataActivity3);
        hard_ver = getIntent().getStringExtra(EquipmentUpdataActivity4);
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
        setEquipmentData();
    }

    @Override
    public void initObject() {
        euLayout1Tv.setText(eMap.get(type));
        euLayout2Tv.setText(sys_ver);
        euLayout3Tv.setText(hard_ver);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void init() {
        handler.sendEmptyMessageDelayed(2222, 0);
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
                    if (cmd.equals("i am ok")) {
                       if(bon==2){
                           handler.sendEmptyMessageDelayed(3000,500);
                            return;
                        }

                        String ver = jsonObject.optString("sys_ver", "");
                        Log.e("qqq","i am ok="+ver+"  updata_ver="+updata_ver);

                        if (!ver.equals(updata_ver)) {
                            handler.sendEmptyMessageDelayed(2001, 500);

                        } else {
                            if (!ver.equals("")) {
                                sys_ver = ver;
                            }
                            me = new Message();
                            me.what = 2000;
                            me.obj = ver;
                            handler.sendMessageDelayed(me, 500);
                        }


                    } else if (cmd.equals("wifi_" + type + "_ack")) {
//                        handler.sendEmptyMessageDelayed(2002, 500);
                        String ver= jsonObject.optString("sys_ver","");
                        if(!ver.equals("")){
                            sys_ver=ver;
                        }
                        String hard = jsonObject.optString("hard_ver", "");
                        if (!hard.equals("")) {
                            hard_ver = hard;
                        }
                        handler.sendEmptyMessageDelayed(4000,500);


                    } else if (cmd.equals("wifi_" + type + "_update_ack")) {
                        handler.sendEmptyMessageDelayed(2002, 500);


                    } else if (cmd.equals("wifi_" + type + "_updatefail_ack")) { //更新失败
                        handler.sendEmptyMessageDelayed(2001, 500);

                    } else if (cmd.equals("queryhardwareversion_ok")) {
                        String uname = jsonObject.optString("uname", "");  //
                        if (!uname.equals(MainActivity.NameUser)) {
                            return;
                        }
                        String clientid = jsonObject.optString("clientid", "");
                        if (!clientid.equals(Tool.getIMEI(getContext()))) {
                            return;
                        }
                        String name = jsonObject.optString("vname", "");  //
                        double version = jsonObject.optDouble("version", 0.0);
                        Log.e("qqq",name+" queryhardwareversion "+version);
                        if (!name.equals("")) {
                            String sys = name + "-v" + version;
                            updata_ver = sys;
                            if (!updata_ver.equals(sys_ver)) {
                                updata_zt = true;
                                handler.sendEmptyMessageDelayed(1000, 500);
                            }else{
                                handler.sendEmptyMessageDelayed(1002, 500);
                                if(bon==1){
                                    handler.sendEmptyMessageDelayed(1001, 500);
                                }
                            }

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private boolean dian_layout4;

    @OnClick({R.id.equipment_fh, R.id.eu_layout4, R.id.eu_layout5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.equipment_fh:
                finish();
                break;
            case R.id.eu_layout4:
                bon=1;
                if (updata_zt) {
                    dian_layout4 = true;
                    updata();
                } else {
                    showPro();
                    isUpdata();
                }
                break;
            case R.id.eu_layout5:  //c初始化数据
                bon=2;
                showPro4();
                putInitdata();
                break;
        }
    }

    private int bon=0;

    private ProgressDialog dialog;

    public void showPro() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("检查版本更新...");
        dialog.setCancelable(true);
        dialog.show();
    }

    public void showPro2() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("更新中...");
        dialog.setCancelable(true);
        dialog.show();
    }
    public void showPro3() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("发送中...");
        dialog.setCancelable(true);
        dialog.show();
    }

    public void showPro4() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("初始化中...");
        dialog.setCancelable(true);
        dialog.show();
    }
    private boolean builder_zt;

    private void updata() {   //更新
        builder_zt = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setTitle("是否对设备进行版本更新");
        builder.setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                builder_zt = false;
                showPro3();
                putUpdata();
            }
        });

        builder.setNegativeButton(
                getString(R.string.alert_dialog_cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        builder_zt = false;
                    }
                });
        builder.show();
    }

    private boolean updata_zt = false;
    private String updata_ver = "";

    public void isUpdata() {  //判断版本是否要更新
        final String myTopicding_too = "iotbroad/iot/device";
        subscribe(myTopicding_too);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "queryhardwareversion");
                    jsonObject.put("sid", sid);
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    jsonObject.put("type",type);
                    String js = jsonObject.toString();
                    publish_String3(js, myTopicding_too);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }


    public void putUpdata() {  //版本更新
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_" + type + "_update");
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

    public void putInitdata() {  //版本更新
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_" + type + "_clear");
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

    @Override
    protected void onDestroy() {
        handler.removeMessages(2222);
        handler.removeMessages(1000);
        handler.removeMessages(2000);
        handler.removeMessages(2001);
        handler.removeMessages(2002);
        handler.removeMessages(4000);
        handler.removeMessages(3000);
        handler.removeMessages(1001);
        handler.removeMessages(1002);
        super.onDestroy();
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    euLayout4Tv.setText(updata_ver);
                    euSysIm.setVisibility(View.VISIBLE);
                    if (updata_zt) {
                        if (dian_layout4) {
                            if (!builder_zt) {
                                updata();
                            }
                        }

                    }


                    break;
                case 1001:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "已是最新版本", Toast.LENGTH_LONG).show();

                    break;
                case 1002:
                        euLayout4Tv.setText(updata_ver);
                        euSysIm.setVisibility(View.INVISIBLE);
                        break;
                case 2000:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "更新成功", Toast.LENGTH_LONG).show();
                    euSysIm.setVisibility(View.INVISIBLE);
                    euLayout2Tv.setText(msg.obj.toString());
                    break;
                case 2001:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "更新失败", Toast.LENGTH_LONG).show();
                    break;
                case 2002:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "发送成功", Toast.LENGTH_LONG).show();
                    showPro2();
                    break;

                case 2222:
                    handler.removeMessages(2222);
                    if (sys_ver.equals("")) {
                        handler.sendEmptyMessageDelayed(2222, 1000);
                        return;
                    }
                    if (!isConnected()) {
                        handler.sendEmptyMessageDelayed(2222, 1000);
                        return;
                    }
                    isUpdata();
                    break;
                case 4000:
                    euLayout2Tv.setText(sys_ver);
                    euLayout3Tv.setText(hard_ver);
                    break;
                case 3000:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "初始化成功", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


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

}
