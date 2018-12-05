package com.grasp.training.activity;

import android.annotation.SuppressLint;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.service.MqttService;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.BaseTcpMqttActpvity;
import com.grasp.training.tool.Tool;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LightActivity extends BaseTcpMqttActpvity {

    public final static String LightActivity1 = "LightActivity_1";
    public final static String LightActivity2 = "LightActivity_2";
    public final static String LightActivity3 = "LightActivity_3";
    @BindView(R.id.light_tv)
    TextView lightTv;
    @BindView(R.id.light_fh)
    Button lightFh;
    @BindView(R.id.light_im)
    ImageView lightIm;
    @BindView(R.id.light_im2)
    ImageView lightIm2;
    @BindView(R.id.light_im_deng)
    ImageView lightImDeng;
    @BindView(R.id.light_seekbar)
    SeekBar lightSeekbar;
    @BindView(R.id.light_seekbar2)
    SeekBar lightSeekbar2;
    @BindView(R.id.smart_tv_seekbarByc1)
    TextView smartTvSeekbarByc1;
    @BindView(R.id.smart_tv_seekbarByc2)
    TextView smartTvSeekbarByc2;

    private String sid, type, dname;
    private Context context;
    private String myTopicding, myTopic;
    private String sys_ver = "", hard_ver = "";
    private int alight = -1, blight = -1;
    private boolean smartBycSeekbar_zt, smartBycSeekbar_zt2=true;
    private boolean smartBycSeekbar_zt3, smartBycSeekbar_zt4=true;
    private int bu_in=-1, bu_in2=-1;
    private int huan_1=-1,huan_2=-1;

    public static void starstEquipmentActivity(Context context, String sid, String type, String name) {
        Intent in = new Intent(context, LightActivity.class);
        in.putExtra(LightActivity1, sid);
        in.putExtra(LightActivity2, type);
        in.putExtra(LightActivity3, name);
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
        return R.layout.light_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context = getContext();
        sid = getIntent().getStringExtra(LightActivity1);
        type = getIntent().getStringExtra(LightActivity2);
        dname = getIntent().getStringExtra(LightActivity3);
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
        lightTv.setText(dname);
        lightIm.getBackground().setAlpha(0);
        lightIm2.getBackground().setAlpha(0);
        lightSeekbar2.setEnabled(false);
        lightSeekbar.setEnabled(false);
        lightImDeng.getBackground().setAlpha(120);
    }

    @Override
    public void initObject() {

    }

    private int seek_in1=-1,seek_in2=-1;

    @Override
    public void initListener() {
        lightSeekbar.setMax(89);
        lightSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("Range")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (alight == -1 || blight == -1) {
                    Toast.makeText(context, "没获取到数据", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!smartBycSeekbar_zt) {
                    return;
                }
                if(!b){
                    return;
                }

                int seek_in = i+10;
                if (seek_in == 99) {
                    seek_in = 100;

                }
//                lightIm.getBackground().setAlpha(i*255/90);
//                Log.d("qqq", "seek_in="+seek_in);
                smartTvSeekbarByc1.setText(seek_in + "%");
                smartTvSeekbarByc1.setVisibility(View.VISIBLE);
                handler.removeMessages(2000);
                handler.sendEmptyMessageDelayed(2000, 1500);

                Log.e("seek", seekBar.getProgress() + " i=" + i + " b=" + b);
                if (!smartBycSeekbar_zt2) {
                    return;
                }
                smartBycSeekbar_zt2 = false;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                            smartBycSeekbar_zt2 = true;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
//                if(seek_in1!=-1&&Math.abs(seek_in1-seek_in)>10){  //判断滑动范围大于10就不发mqtt
//                    seek_in1=seek_in;
//                    return;
//                }
                seek_in1=seek_in;
                if(seek_in==100){
                    push_blight(99);
//                    push_blight_config(99);
                }else{
                    push_blight(seek_in);
//                    push_blight_config(seek_in);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                smartBycSeekbar_zt = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (alight == -1 || blight == -1) {
                    Toast.makeText(context, "没获取到数据", Toast.LENGTH_LONG).show();
                    return;
                }
                smartBycSeekbar_zt = false;
//                if (!smartBycSeekbar_zt2) {

                int seek_in = (seekBar.getProgress())+10;
                if (seek_in == 99) {
                    seek_in = 100;
                }

                smartTvSeekbarByc1.setText(seek_in + "%");
                smartTvSeekbarByc1.setVisibility(View.VISIBLE);
                handler.removeMessages(2000);
                handler.sendEmptyMessageDelayed(2000, 1500);
                bu_in = seek_in;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                            if(bu_in==100){
                                huan_1=99;
//                                push_blight(99);
                                push_blight_config2(99);
                            }else{
                                huan_1=bu_in;
//                                push_blight(bu_in);
                                push_blight_config2(bu_in);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
//                }
//                smartTvSeekbarByc.setText(( seekBar.getProgress()) + "");
//                smartTvSeekbarByc.setVisibility(View.VISIBLE);
//                handler.removeMessages(2000);
//                handler.sendEmptyMessageDelayed(2000, 1500);
//                push_var("blinds_percentage",  seekBar.getProgress());
            }
        });



        lightSeekbar2.setMax(89);
        lightSeekbar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("Range")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (alight == -1 || blight == -1) {
                    Toast.makeText(context, "没获取到数据", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!smartBycSeekbar_zt3) {
                    return;
                }
                if(!b){
                    return;
                }

                int seek_in = i+10;
                if (seek_in == 99) {
                    seek_in = 100;

                }else{
                }
//                lightIm.getBackground().setAlpha(i*255/90);
//                Log.d("qqq", "seek_in="+seek_in);
                smartTvSeekbarByc2.setText(seek_in + "%");
                smartTvSeekbarByc2.setVisibility(View.VISIBLE);
                handler.removeMessages(2001);
                handler.sendEmptyMessageDelayed(2001, 1500);

                Log.e("seek", seekBar.getProgress() + " i=" + i + " b=" + b);
                if (!smartBycSeekbar_zt4) {
                    return;
                }
                smartBycSeekbar_zt4 = false;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                            smartBycSeekbar_zt4 = true;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                if(seek_in1!=-1&&Math.abs(seek_in1-seek_in)>10){  //判断滑动范围大于10就不发mqtt
                    seek_in1=seek_in;
                    return;
                }
                seek_in1=seek_in;
                if(seek_in==100){
                    push_alight(99);
                }else{
                    push_alight(seek_in);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                smartBycSeekbar_zt3 = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (alight == -1 || blight == -1) {
                    Toast.makeText(context, "没获取到数据", Toast.LENGTH_LONG).show();
                    return;
                }
                smartBycSeekbar_zt3 = false;
//                if (!smartBycSeekbar_zt2) {

                int seek_in = (seekBar.getProgress())+10;
                if (seek_in == 99) {
                    seek_in = 100;
                }

                smartTvSeekbarByc2.setText(seek_in + "%");
                smartTvSeekbarByc2.setVisibility(View.VISIBLE);
                handler.removeMessages(2001);
                handler.sendEmptyMessageDelayed(2001, 1500);
                bu_in2 = seek_in;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                            if(bu_in2==100){
                                huan_2=99;
//                                push_alight(99);
                                push_alight_config2(99);
                            }else{
                                huan_2=bu_in2;
//                                push_alight(bu_in2);
                                push_alight_config2(bu_in2);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
//                }
//                smartTvSeekbarByc.setText(( seekBar.getProgress()) + "");
//                smartTvSeekbarByc.setVisibility(View.VISIBLE);
//                handler.removeMessages(2000);
//                handler.sendEmptyMessageDelayed(2000, 1500);
//                push_var("blinds_percentage",  seekBar.getProgress());
            }
        });
    }

    @Override
    public void init() {

    }

    @Override
    public void MyMessageArrived(final String message) {
        Log.e("qqq", "EquipmentActivity message=" + message);
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
                        alight = jsonObject.optInt("alight", -1);
                        blight = jsonObject.optInt("blight", -1);
                        if(alight==-1||blight==-1){
                            state_zt=false;

                        }else{
                            if(alight==0&&blight==0){
                                state_zt=false;
                            }else{
                                state_zt=true;
                            }
                        }
                        handler.sendEmptyMessageDelayed(1000, 200);
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
                        handler.sendEmptyMessageDelayed(233, 200);
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

    public void put_sz(View v) {
        EquipmentUpdataActivity.starstEquipmentActivity(context, sid, type, sys_ver, hard_ver);
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
        handler.removeMessages(2222);
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
                    lightTv.setText(dname);

                    break;
                case 1000:
//                    if(s.equals("on")){
//                        search_zt=true;
//                    }else{
//                        search_zt=false;
//                    }
                    setStateView();
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

                case 2000:
                    smartTvSeekbarByc1.setVisibility(View.INVISIBLE);
                    break;
                case 2001:
                    smartTvSeekbarByc2.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };

    //lightIm->blight   lightIm2->alight
    public void setStateView() {
//        if(smartBycSeekbar_zt){
//            return;
//        }
//        if(smartBycSeekbar_zt3){
//            return;
//        }
        if(state_zt){
            lightImDeng.getBackground().setAlpha(255);
            Log.e("qqq","huan_1="+huan_1+" blight="+blight);
            if(huan_1!=-1&&huan_1!=blight){
//                push_blight(huan_1);
                push_blight_config(huan_1);
                return;
            }
            if(huan_2!=-1&&huan_2!=alight){
//                push_alight(huan_2);
                push_alight_config(huan_2);
                return;
            }
        }else{
            lightImDeng.getBackground().setAlpha(120);
        }
        if (alight != -1) {
            lightSeekbar2.setEnabled(true);
            lightSeekbar.setEnabled(true);
            if(blight!=0){
                lightIm.getBackground().setAlpha(blight*225/99+30);
            }else{
                lightIm.getBackground().setAlpha(0);
            }

            if(alight!=0){
                lightIm2.getBackground().setAlpha(alight*225/99+30);
            }else{
                lightIm2.getBackground().setAlpha(0);
            }

            int a=alight-10;
            int b=blight-10;
            if(a<=0){
                a=0;
            }
            if(b<=0){
                b=0;
            }

            Log.e("qqq","blight="+b);
            lightSeekbar.setProgress(b);
            lightSeekbar2.setProgress(a);


        }else{
            lightSeekbar2.setEnabled(false);
            lightSeekbar.setEnabled(false);
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

    public void push_alight(final int aL) {  //改变a的亮度
        if (alight == -1 || blight == -1) {
            Toast.makeText(context, "没获取到数据", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_" + type+"_set");
                    jsonObject.put("sid", sid);
                    jsonObject.put("alight", aL);
                    jsonObject.put("blight", blight);
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }


    public void push_blight(final int bL) {  //改变a的亮度
        if (alight == -1 || blight == -1) {
            Toast.makeText(context, "没获取到数据", Toast.LENGTH_LONG).show();
            return;
        }
        Log.e("seek", "bL="+bL);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_" + type+"_set");
                    jsonObject.put("sid", sid);
                    jsonObject.put("alight", alight);
                    jsonObject.put("blight", bL);
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }


    public void push_alight_config(final int aL) {  //改变a的亮度
        if (alight == -1 || blight == -1) {
            Toast.makeText(context, "没获取到数据", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_" + type+"_setok");
                    jsonObject.put("sid", sid);
                    jsonObject.put("alight", aL);
                    jsonObject.put("blight", blight);
                    String js = jsonObject.toString();
//                    publish_String3(js,myTopic);
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }



    public void push_blight_config(final int bL) {  //改变a的亮度
        if (alight == -1 || blight == -1) {
            Toast.makeText(context, "没获取到数据", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_" + type+"_setok");
                    jsonObject.put("sid", sid);
                    jsonObject.put("alight", alight);
                    jsonObject.put("blight", bL);
                    String js = jsonObject.toString();
//                    publish_String3(js,myTopic);
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }


    public void push_alight_config2(final int aL) {  //改变a的亮度
        if (alight == -1 || blight == -1) {
            Toast.makeText(context, "没获取到数据", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_" + type+"_setok");
                    jsonObject.put("sid", sid);
                    jsonObject.put("alight", aL);
                    jsonObject.put("blight", blight);
                    String js = jsonObject.toString();
//                    publish_String3(js,myTopic);
                    publish_String4(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }



    public void push_blight_config2(final int bL) {  //改变b的亮度
        if (alight == -1 || blight == -1) {
            Toast.makeText(context, "没获取到数据", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_" + type+"_setok");
                    jsonObject.put("sid", sid);
                    jsonObject.put("alight", alight);
                    jsonObject.put("blight", bL);
                    String js = jsonObject.toString();
//                    publish_String3(js,myTopic);
                    publish_String4(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }
    public void push_state(final boolean state) {  //开关
        if (alight == -1 || blight == -1) {
            Toast.makeText(context, "没获取到数据", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_" + type+"");
                    jsonObject.put("sid", sid);
                    if(!state){
                        jsonObject.put("state", "on");
                    }else{
                        jsonObject.put("state", "off");
                    }

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


    private boolean state_zt=false;
    @OnClick({R.id.light_tv, R.id.light_fh,R.id.light_im_deng})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.light_tv:
                setName();
                break;
            case R.id.light_fh:
                finish();
                break;
            case R.id.light_im_deng:
                push_state(state_zt);
                break;
        }
    }

}
