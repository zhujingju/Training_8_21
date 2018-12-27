package com.grasp.training.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.example.liangmutian.mypicker.TimePickerDialog3;
import com.grasp.training.R;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.view.WeekPopwinDialog;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTimingActivity extends BaseMqttActivity {
    @BindView(R.id.search_fh)
    ImageView searchFh;
    @BindView(R.id.add_timing_tv1)
    TextView addTimingTv1;
    @BindView(R.id.add_timing_tv2)
    TextView addTimingTv2;
    @BindView(R.id.add_timing_tv3)
    TextView addTimingTv3;
    @BindView(R.id.add_timing_tv4)
    TextView addTimingTv4;
    @BindView(R.id.add_timing_lin)
    LinearLayout lin;

    private String sid = "";
    private String type="";
    private String myTopicding = "iotbroad/iot/socket_ack/" + sid;
    private String myTopic = "iotbroad/iot/socket/" + sid;
    private Context context;
    private String data_s = "";
    private String week = "0000000";
    private int timer = 0;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //这里将我们临时输入的一些数据存储起来
        String ontime = "";
        String offtime = "";
        if (addTimingTv3.getText().equals("")) {
            ontime = "close";
        } else {
            ontime = addTimingTv3.getText().toString();
        }

        if (addTimingTv4.getText().equals("")) {
            offtime = "close";
        } else {
            offtime = addTimingTv4.getText().toString();
        }

        outState.putString("get_week", get_save(ontime, offtime, week, timer));
    }
    @Override
    public int setLayoutId() {
        return R.layout.add_timing_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context = getContext();
        Intent data = getIntent();
        data_s = data.getStringExtra("Timing_s") + "";
        sid = data.getStringExtra("Timing_sid") + "";
        type= data.getStringExtra("Timing_type") + "";
        if (getSavedInstanceState() != null) {
            data_s = getSavedInstanceState().getString("get_week", "");
        }
        if (data_s.equals("")) {

        } else { //获取已有数据
            setAddTimingData(data_s);
        }
        Log.e("qqq", "data_s=" + data_s + " sid=" + sid);

    }

    @Override
    public void initView() {
        takePhotoPopWin = new WeekPopwinDialog(context);
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
    public String getMyTopic() {
        return "iotbroad/iot/socket/" + sid;
    }

    @Override
    public String getMyTopicDing() {
        return "iotbroad/iot/socket_ack/" + sid;
    }

    @Override
    public String getSid() {
        return sid;
    }

    @Override
    public void MyMessageArrived(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("qqq", "messageArrived  message= " + message);
                try {
                    JSONObject jsonF;
                    Message me;
                    String js = "";
                    String channel_0 = "";
                    int var = 0;
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.getString("cmd");
                    String mSid = jsonObject.optString("sid", "");
                    if (!mSid.equals(sid)) {
                        return;
                    }
                    if(cmd.equals("wifi_"+type+"_timing_ack")){
                        handler.sendEmptyMessageDelayed(1000,500);
                    }else if(cmd.equals("wifi_"+type+"_timeout_ack") ){
                        handler.sendEmptyMessageDelayed(2000,500);
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1000);
        handler.removeMessages(2000);
        handler.removeMessages(666);
}

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 666:
                    if(dialog!=null){
                        dialog.cancel();
                    }
                    Toast.makeText(context,"发送失败", Toast.LENGTH_LONG).show();
                    break;
                case 1000:  //完成
                    if(dialog!=null){
                        dialog.cancel();
                    }

                    Toast.makeText(context,"完成", Toast.LENGTH_LONG).show();
                    finish();

                    break;
                case 2000:  //完成
                    if(dialog!=null){
                        dialog.cancel();
                    }

                    Toast.makeText(context,"定时数量最多20个，无法继续添加", Toast.LENGTH_LONG).show();

                    break;
            }
        }
    };
    public void fh(View view) {
        finish();
    }


    public void save(View view) {
        String ontime = "";
        String offtime = "";
        if (addTimingTv3.getText().equals("")) {
            ontime = "close";
        } else {
            ontime = addTimingTv3.getText().toString();
        }

        if (addTimingTv4.getText().equals("")) {
            offtime = "close";
        } else {
            offtime = addTimingTv4.getText().toString();
        }
        push_save(ontime, offtime, week, timer);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void push_save(String ontime, String offtime, String week, int timer) {
        if (sid.equals("")) {
            Toast.makeText(context, "先添加设备", Toast.LENGTH_LONG).show();
            return;
        }
        showPro();
        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_"+type+"_timing");
            jsonObject.put("day", week);
            jsonObject.put("sid", sid);
            jsonObject.put("timer_state", "on");
            jsonObject.put("ontime", ontime);
            jsonObject.put("offtime", offtime);
            jsonObject.put("timer", timer);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }

    private ProgressDialog dialog;
    public void showPro(){
        handler.removeMessages(666);
        handler.sendEmptyMessageDelayed(666,5000);
        dialog = new ProgressDialog(this);
        dialog.setMessage("保存中...");
        dialog.setCancelable(true);
        dialog.show();
    }


    @OnClick({R.id.add_timing_layout1, R.id.add_timing_layout2, R.id.add_timing_layout3, R.id.add_timing_layout4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_timing_layout1:
                setBq();
                break;
            case R.id.add_timing_layout2:
                if (week.equals("")) {
                    showPopWinHasReser(false, false, false, false, false, false, false);
                } else {
                    showPopWinHasReser(z1, z2, z3, z4, z5, z6, z7);
                }

                break;
            case R.id.add_timing_layout3:
                showTimePick(on_h, on_m);
                break;
            case R.id.add_timing_layout4:
                showTimePick2(off_h, off_m);
                break;
        }
    }

    String bq = "标签";

    public void setBq() {

        final EditText et = new EditText(this);

        new AlertDialog.Builder(this).setTitle("标签")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "标签内容不能为空！" + input, Toast.LENGTH_LONG).show();
                        } else {
                            bq = input;
                            addTimingTv1.setText(bq);
                        }
                    }
                })
                .setNegativeButton("取消", null).show();

    }

    private int on_h = 10, on_m = 0;
    private int off_h = 16, off_m = 0;
    private TimePickerDialog3.Builder builder = null, builder2 = null;
    private Dialog timeDialog, timeDialog2;

    private void showTimePick(int h, int m) {

        if (timeDialog == null) {

            builder = new TimePickerDialog3.Builder(this);

            timeDialog = builder.setOnTimeSelectedListener(new TimePickerDialog3.OnTimeSelectedListener() {
                @Override
                public void onTimeSelected(String[] times) {

//                    mTextView.setText(times[0] + ":" + times[1]);
                    int h = Integer.valueOf(times[0]);
                    int m = Integer.valueOf(times[1]);
                    on_h = h;
                    on_m = m;
                    String s_h = "";
                    String s_m = "";
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
                    addTimingTv3.setText(s_h + ":" + s_m);
                }
            }).create();

        }
        builder.setText_sj(h, m);
        timeDialog.show();

    }

    private void showTimePick2(int h, int m) {

        if (timeDialog2 == null) {

            builder2 = new TimePickerDialog3.Builder(this);

            timeDialog2 = builder2.setOnTimeSelectedListener(new TimePickerDialog3.OnTimeSelectedListener() {
                @Override
                public void onTimeSelected(String[] times) {

//                    mTextView.setText(times[0] + ":" + times[1]);
                    int h = Integer.valueOf(times[0]);
                    int m = Integer.valueOf(times[1]);
                    off_h = h;
                    off_m = m;
                    String s_h = "";
                    String s_m = "";
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
                    addTimingTv4.setText(s_h + ":" + s_m);
                }
            }).create();

        }
        builder2.setText_sj(h, m);
        timeDialog2.show();

    }


    private WindowManager.LayoutParams params;
    private WeekPopwinDialog takePhotoPopWin;


    public void showPopWinHasReser(boolean z1, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7) { //日期设定
        Log.e("qqq", "showPopWinHasReser=" + week);
        takePhotoPopWin.setWeek(z1, z2, z3, z4, z5, z6, z7);
        takePhotoPopWin.setWeekInterface(new WeekPopwinDialog.weekInterface() {
            @Override
            public void onWeekInterface(boolean z1, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7) {
                week = "";
                if (z1) {
                    week += "1";
                } else {
                    week += "0";
                }

                if (z2) {
                    week += "2";
                } else {
                    week += "0";
                }

                if (z3) {
                    week += "3";
                } else {
                    week += "0";
                }

                if (z4) {
                    week += "4";
                } else {
                    week += "0";
                }
                if (z5) {
                    week += "5";
                } else {
                    week += "0";
                }
                if (z6) {
                    week += "6";
                } else {
                    week += "0";
                }
                if (z7) {
                    week += "7";
                } else {
                    week += "0";
                }
                setWeekView(week);
                Log.e("qqq", "week=" + week);
            }
        });
        takePhotoPopWin.showAtLocation(lin, Gravity.BOTTOM, 0, 0);
        params = ((Activity) context).getWindow().getAttributes();
        //当弹出Popupwindow时，背景变半透明
        params.alpha = 0.8f;
        ((Activity) context).getWindow().setAttributes(params);
        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        takePhotoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params = ((Activity) context).getWindow().getAttributes();
                params.alpha = 1f;
                ((Activity) context).getWindow().setAttributes(params);

            }
        });

    }

    private boolean z1, z2, z3, z4, z5, z6, z7;

    private void setWeekView(String s) {

        if (s.length() == 7) {
            String we = "";
            if (s.substring(0, 1).equals("1")) {
                z1 = true;
                we += "周一";
            } else {
                z1 = false;
            }

            if (s.substring(1, 2).equals("2")) {
                z2 = true;
                if (we.equals("")) {
                    we += "周二";
                } else {
                    we += "，周二";
                }
            } else {
                z2 = false;
            }

            if (s.substring(2, 3).equals("3")) {
                z3 = true;
                if (we.equals("")) {
                    we += "周三";
                } else {
                    we += "，周三";
                }
            } else {
                z3 = false;
            }

            if (s.substring(3, 4).equals("4")) {
                z4 = true;
                if (we.equals("")) {
                    we += "周四";
                } else {
                    we += "，周四";
                }
            } else {
                z4 = false;
            }
            if (s.substring(4, 5).equals("5")) {
                z5 = true;
                if (we.equals("")) {
                    we += "周五";
                } else {
                    we += "，周五";
                }
            } else {
                z5 = false;
            }
            if (s.substring(5, 6).equals("6")) {
                z6 = true;
                if (we.equals("")) {
                    we += "周六";
                } else {
                    we += "，周六";
                }
            } else {
                z6 = false;
            }
            if (s.substring(6, 7).equals("7")) {
                z7 = true;
                if (we.equals("")) {
                    we += "周日";
                } else {
                    we += "，周日";
                }
            } else {
                z7 = false;
            }
            if (z1 && z2 && z3 && z4 & z5 && z6 && z7) {
                we = "每天";
            } else if (!z1 && !z2 && !z3 && !z4 & !z5 && !z6 && !z7) {
                we = "永不";
            } else if (z1 && z2 && z3 && z4 & z5 && !z6 && !z7) {
                we = "工作日";
            } else if (!z1 && !z2 && !z3 && !z4 & !z5 && z6 && z7) {
                we = "周末";
            }
            addTimingTv2.setText(we);
        }
    }


    private void setAddTimingData(String data){
        try {
            JSONObject js=new JSONObject(data);
            week=js.getString("day");
            String ontime=js.getString("ontime");
            String offtime=js.getString("offtime");
            if(!ontime.equals("close")){
                String on[]=ontime.split(":");
                if(on.length==2){
                    on_h= Integer.valueOf(on[0]);
                    on_m= Integer.valueOf(on[1]);
                    setAddTimingView();
                }
            }else{

            }
            if(!offtime.equals("close")){
                String off[]=offtime.split(":");
                if(off.length==2){
                    off_h= Integer.valueOf(off[0]);
                    off_m= Integer.valueOf(off[1]);
                    setAddTimingView2();
                }

            }else{

            }

            timer=js.getInt("timer");
            setWeekView(week);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setAddTimingView() {  //更改ui


        int h = on_h;
        int m = on_m;
        String s_h = "";
        String s_m = "";
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
        addTimingTv3.setText(s_h + ":" + s_m);

    }

    private void setAddTimingView2() {  //更改ui


        int h = off_h;
        int m = off_m;
        String s_h = "";
        String s_m = "";

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
        addTimingTv4.setText(s_h + ":" + s_m);
    }



    public String get_save(String ontime, String offtime, String week, int timer) {
        String str="";
        try {
            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_"+type+"_timing");
            jsonObject.put("day", week);
            jsonObject.put("sid", sid);
            jsonObject.put("timer_state", "on");
            jsonObject.put("ontime", ontime);
            jsonObject.put("offtime", offtime);
            jsonObject.put("timer", timer);
            str = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return str;
    }
}
