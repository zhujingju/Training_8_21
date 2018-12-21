package com.grasp.training.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liangmutian.mypicker.TimePickerDialog;
import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.tool.BaseTcpMqttActpvity;
import com.grasp.training.tool.Tool;
import com.grasp.training.view.SlideSwitch;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CycleActivity extends BaseTcpMqttActpvity {
    @BindView(R.id.cycle_fh)
    ImageView cycleFh;
    @BindView(R.id.cycle_bc)
    TextView cycleBc;
    @BindView(R.id.add_timing_tv1)
    TextView addTimingTv1;
    @BindView(R.id.add_timing_layout1)
    LinearLayout addTimingLayout1;
    @BindView(R.id.add_timing_cf)
    TextView addTimingCf;
    @BindView(R.id.cycle_tv3)
    TextView cycleTv3;
    @BindView(R.id.cycle_layout3)
    RelativeLayout cycleLayout3;
    @BindView(R.id.cycle_tv4)
    TextView cycleTv4;
    @BindView(R.id.cycle_tv2)
    TextView cycleTv2;
    @BindView(R.id.cycle_layout4)
    RelativeLayout cycleLayout4;
    private Context context;
    private String sid = "";
    private String type = "";


    public final static String equipment1 = "Cycle_sid";
    public final static String equipment2 = "Cycle_type";

    public static void strateCycleActivity(Context context, String sid, String type) {
        Intent in = new Intent(context, CycleActivity.class);
        in.putExtra(equipment1, sid);
        in.putExtra(equipment2, type);
        context.startActivity(in);
    }

    @Override
    public String getMyTopic() {
        return "iotbroad/iot/" + type + "/" + sid;
    }

    @Override
    public String getMyTopicDing() {
        return "iotbroad/iot/" + type + "_ack/" + sid;
    }

    @Override
    public String getSid() {
        return sid;
    }


    @Override
    public int setLayoutId() {
        return R.layout.cycle_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context = getContext();
        sid = getIntent().getStringExtra(equipment1) + "";
        type = getIntent().getStringExtra(equipment2) + "";
        showPro();
        handler.sendEmptyMessageDelayed(3000,0);
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
//                    Log.e("qqq", "socke 222222222 sid="+sid );
                    if (!mSid.equals(sid)) {

                        return;
                    }
                    switch (cmd) {
                        case "wifi_socket_cycle_ack":
                            state = jsonObject.optString("state","");
                            if(!state.equals("")){
                                if(state.equals("on")){
                                    zt_kg=true;
                                }else if(state.equals("off")){
                                    zt_kg=false;
                                }
                            }
                            String off_time = jsonObject.optString("off_time", "");
                            String on_time = jsonObject.optString("on_time", "");

                            if(!on_time.equals("")){

                                on_time_cy=on_time;

                                String time[] = on_time.split(",");
                                h=Integer.valueOf(time[0]);
                                m=Integer.valueOf(time[1]);
                                h_s=Integer.valueOf(time[2]);

                                if(h>23){
                                    h=23;
                                }
                                if(m>59){
                                    m=59;
                                }
                                if(h_s>59){
                                    h_s=59;
                                }

                            }
                            if(!off_time.equals("")){

                                off_time_cy=off_time;

                                String time[] = off_time.split(",");
                                int num = Integer.valueOf(time[0]) * 60 * 60 + Integer.valueOf(time[1]) * 60 + Integer.valueOf(time[2]);
                                h2=Integer.valueOf(time[0]);
                                m2=Integer.valueOf(time[1]);
                                h_s2=Integer.valueOf(time[2]);
                                if(h2>23){
                                    h2=23;
                                }
                                if(m2>59){
                                    m2=59;
                                }
                                if(h_s2>59){
                                    h_s2=59;
                                }
                            }
                            me = new Message();
                            me.what = 1000;
                            handler.sendEmptyMessageDelayed(1000,500);

                            break;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean zt_kg=false;
    private boolean zy_hq=false;

    @OnClick({R.id.cycle_fh, R.id.cycle_bc, R.id.cycle_layout3, R.id.cycle_layout4, R.id.cycle_layout2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cycle_fh:
                finish();
                break;
            case R.id.cycle_bc:
                if(zy_hq){
                    showPro2();
                    if(zt_kg){
                        push("on",on_time_cy,off_time_cy);
                    }else{
                        push("off",on_time_cy,off_time_cy);
                    }

                }else{
                    Toast.makeText(context,"正在获取数据中，请稍等",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.cycle_layout3:
                if(!zy_hq){
                    Toast.makeText(context,"正在获取数据中，请稍等",Toast.LENGTH_LONG).show();
                    return;
                }
                showTimePick(h,m,h_s);
                break;
            case R.id.cycle_layout4:
                if(!zy_hq){
                    Toast.makeText(context,"正在获取数据中，请稍等",Toast.LENGTH_LONG).show();
                    return;
                }
                showTimePick2(h2,m2,h_s2);
                break;
            case R.id.cycle_layout2:
                if(!zy_hq){
                    Toast.makeText(context,"正在获取数据中，请稍等",Toast.LENGTH_LONG).show();
                    return;
                }
                if(zt_kg){
                    cycleTv2.setText("关闭");
                }else{
                    cycleTv2.setText("开启");
                }
                zt_kg=!zt_kg;
                break;
        }
    }

    private String on_time_cy="00,00,01";
    private String off_time_cy="00,00,01";
    private int h = 0, m = 0, h_s = 1;

    int num = 0; //开启时长
    private TimePickerDialog.Builder builder = null;
    private Dialog timeDialog;

    private void showTimePick(int h, int m, int s) {

        if (timeDialog == null) {

            builder = new TimePickerDialog.Builder(this);

            timeDialog = builder.setOnTimeSelectedListener(new TimePickerDialog.OnTimeSelectedListener() {
                @Override
                public void onTimeSelected(String[] times) {

//                    mTextView.setText(times[0] + ":" + times[1]);
                    int h = Integer.valueOf(times[0]);
                    int m = Integer.valueOf(times[1]);
                    int s = Integer.valueOf(times[2]);
                    int long_l = h * 60 * 60 + m * 60 + s;
                    num = long_l;

                    if(long_l==0){
                        handler.sendEmptyMessageDelayed(4000,0);
                        return;
                    }

                    String s_h = "";
                    String s_m = "";
                    String s_s = "";
                    if (h < 10) {
                        s_h = "0" + h;
                    } else {
                        s_h = "" + h;
                    }

                    if (m < 10) {
                        s_m = "0" + m;
                    } else {
                        s_m = "" + m;
                    }
                    if (s < 10) {
                        s_s = "0" + s;
                    } else {
                        s_s = "" + s;
                    }
                    CycleActivity.this.h=Integer.valueOf(s_h);
                    CycleActivity.this.m=Integer.valueOf(s_m);
                    h_s=Integer.valueOf(s_s);
//                    handler.sendEmptyMessageDelayed(2000,0);
                    on_time_cy=s_h + "," + s_m + "," + s_s;
                    handler.sendEmptyMessageDelayed(2000,0);

                }
            }).create();

        }


        builder.setText_sj(h, m, s, "开启间隔");
        timeDialog.show();

    }

    private int h2 = 0, m2 = 0, h_s2 = 1;
    int num2 = 0; //关闭时长

    private TimePickerDialog.Builder builder2 = null;
    private Dialog timeDialog2;
    private void showTimePick2(int h, int m, int s) {

        if (timeDialog2 == null) {

            builder2 = new TimePickerDialog.Builder(this);

            timeDialog2 = builder2.setOnTimeSelectedListener(new TimePickerDialog.OnTimeSelectedListener() {
                @Override
                public void onTimeSelected(String[] times) {

//                    mTextView.setText(times[0] + ":" + times[1]);
                    int h = Integer.valueOf(times[0]);
                    int m = Integer.valueOf(times[1]);
                    int s = Integer.valueOf(times[2]);
                    int long_l = h * 60 * 60 + m * 60 + s;
                    num = long_l;
                    if(long_l==0){
                        handler.sendEmptyMessageDelayed(4000,0);
                        return;
                    }
                    String s_h = "";
                    String s_m = "";
                    String s_s = "";
                    if (h < 10) {
                        s_h = "0" + h;
                    } else {
                        s_h = "" + h;
                    }

                    if (m < 10) {
                        s_m = "0" + m;
                    } else {
                        s_m = "" + m;
                    }
                    if (s < 10) {
                        s_s = "0" + s;
                    } else {
                        s_s = "" + s;
                    }
                    h2=Integer.valueOf(s_h);
                    m2=Integer.valueOf(s_m);
                    h_s2=Integer.valueOf(s_s);

//                    handler.sendEmptyMessageDelayed(2000,0);
                    off_time_cy=s_h + "," + s_m + "," + s_s;
                    handler.sendEmptyMessageDelayed(2000,0);
                }
            }).create();

        }


        builder2.setText_sj(h, m, s, "关闭间隔");
        timeDialog2.show();

    }


    public void push(final String state,final String on_time,final String off_time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_socket_cycle");
                    jsonObject.put("state", state);
                    jsonObject.put("sid", sid);
                    jsonObject.put("on_time", on_time);
                    jsonObject.put("off_time", off_time);
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public void push_read() { //获取状态
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_socket_cycle_read");
                    jsonObject.put("sid", sid);
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    if(dialog!=null){
                       dialog.dismiss();
                    }
                    if(zy_hq){
                        Toast.makeText(context,"修改成功",Toast.LENGTH_LONG).show();
                    }
                    if(!zt_kg){
                        cycleTv2.setText("关闭");
                    }else{
                        cycleTv2.setText("开启");
                    }

//                    cycleTv4.setText(off_time_cy);
//                    cycleTv3.setText(on_time_cy);
                    cycleTv4.setText((h2*60*60+m2*60+h_s2)+"s");
                    cycleTv3.setText((h*60*60+m*60+h_s)+"s");
                    zy_hq=true;

                    break;

                case 2000:
                    cycleTv4.setText((h2*60*60+m2*60+h_s2)+"s");
                    cycleTv3.setText((h*60*60+m*60+h_s)+"s");
                    break;
                case 3000:
                    handler.removeMessages(3000);
                    if(zy_hq){

                    }else{
                        push_read();
                        handler.sendEmptyMessageDelayed(3000,1000);
                    }

                    break;
                case 4000:
                    Toast.makeText(context,"时间间隔不能为0",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };



    private ProgressDialog dialog;

    public void showPro2() {

        dialog = new ProgressDialog(context);
        dialog.setMessage("修改中...");
        dialog.setCancelable(true);

        dialog.show();
    }
    public void showPro() {

        dialog = new ProgressDialog(context);
        dialog.setMessage("获取数据中...");
        dialog.setCancelable(true);

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1000);
        handler.removeMessages(2000);
        handler.removeMessages(3000);
        handler.removeMessages(4000);
    }
}
