package com.grasp.training.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liangmutian.mypicker.TimePickerDialog;
import com.grasp.training.R;
import com.grasp.training.tool.BaseMqttActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class SockeActivity extends BaseMqttActivity implements View.OnClickListener {
    public static String socket="Main_sockets";
    private String sid="";

    private String myTopicding = "iotbroad/iot/socket_ack/"+sid;
    private String myTopic = "iotbroad/iot/socket/"+sid;
    private Context context;
    private Button state,ds,djs;
    private LinearLayout dsLayout;
    private ImageView ds_del;
    private TextView ds_tv;
    private ImageView im;

    public static  void starstSockeActivity(Context context,String sid){
        Intent in=new Intent(context,SockeActivity.class);
        in.putExtra(socket,sid);
        context.startActivity(in);
    }

    @Override
    public int setLayoutId() {
        return R.layout.socke_activity;
    }

    @Override
    public void initData() {
        context=getContext();
        sid=getIntent().getStringExtra(socket);
        myTopicding = "iotbroad/iot/socket_ack/"+sid;
        myTopic = "iotbroad/iot/socket/"+sid;
    }

    @Override
    public void initView() {
        state= (Button) findViewById(R.id.search_state);
        ds= (Button) findViewById(R.id.search_ds);
        djs= (Button) findViewById(R.id.search_djs);
        dsLayout=(LinearLayout) findViewById(R.id.search_ds_layout);
        ds_del=(ImageView)findViewById(R.id.search_ds_del);
        ds_tv= (TextView) findViewById(R.id.search_ds_tv);
        im= (ImageView) findViewById(R.id.search_im);
    }

    @Override
    public void initObject() {

    }

    @Override
    public void initListener() {
        state.setOnClickListener(this);
        ds.setOnClickListener(this);
        djs.setOnClickListener(this);
        dsLayout.setOnClickListener(this);
        ds_del.setOnClickListener(this);
        im.setOnClickListener(this);
    }

    @Override
    public void init() {
//        push_read();
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
    public void MyMessageArrived(final String message) {
        Log.e("qqq","message="+message);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonF;
                    Message me;
                    String js = "";
                    String channel_0 = "";
                    int var = 0;
                    String data="";
                    String search="";
                    String state="";
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.getString("cmd");
                    String mSid = jsonObject.optString("sid", "");  //设备号
                    if(!mSid.equals(sid)){

                        return;
                    }
                    switch (cmd) {
                        case "wifi_socket_ack":
                            handler.removeMessages(3000);
                            handler.removeMessages(5000);
                            state = jsonObject.optString("state");
                            me=new Message();
                            me.what=1000;
                            me.obj=state;
                            handler.sendMessage(me);

                            break;
                        case "wifi_socket_count_down_ack":

                            data = jsonObject.optString("data");
                            search=jsonObject.optString("state");
                            if(search.equals("on")){
                                ds_state=true;
                            }else{
                                ds_state=false;
                            }

                            me=new Message();
                            me.what=2000;
                            me.obj=data;
                            handler.removeMessages(2000);
                            handler.removeMessages(2001);
                            handler.sendMessage(me);
                            break;

                        case "wifi_socket_count_down_act":
                            data = jsonObject.optString("data");
                            me=new Message();
                            me.what=2000;
                            me.obj=data;
                            handler.removeMessages(2000);
                            handler.removeMessages(2001);
                            handler.sendMessage(me);
                            break;
//                        case "wifi_socket_read_ack":
//                            state = jsonObject.optString("state");
//                            me=new Message();
//                            me.what=1000;
//                            me.obj=state;
//                            handler.sendMessage(me);
//
//                            break;

                        case "wifi_socket_read_down_ack":
                            handler.removeMessages(4000);
                            handler.removeMessages(6000);
                            data = jsonObject.optString("data");
                            search=jsonObject.optString("state","");
                            if(search.equals("on")){
                                ds_state=true;
                            }else{
                                ds_state=false;
                            }

                            me=new Message();
                            me.what=2000;
                            me.obj=data;
                            handler.removeMessages(2000);
                            handler.removeMessages(2001);
                            handler.sendMessage(me);
                            break;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean ds_state=false;//倒计时将要变的状态

    public void push(String s) {
        if(sid.equals("")){
            Toast.makeText(context,"先添加设备",Toast.LENGTH_LONG).show();
            return;
        }

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_socket");
            jsonObject.put("state", s);
            jsonObject.put("sid", sid);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }

    public void push_red_down() {  //查询倒计时
        if(sid.equals("")){
//            Toast.makeText(context,"先添加设备",Toast.LENGTH_LONG).show();
            return;
        }

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_socket_read_down");
            jsonObject.put("sid", sid);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }

    public void push_read() {  //获取插座状态
        if(sid.equals("")){
//            Toast.makeText(context,"先添加设备",Toast.LENGTH_LONG).show();
            return;
        }

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_socket_read");
            jsonObject.put("sid", sid);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    String s=msg.obj.toString();
                    if(s.equals("on")){
                        search_zt=true;
                    }else{
                        search_zt=false;
                    }
                    setStateView(search_zt);
                    break;

                case 2000:
                    String time[]=msg.obj.toString().split(",");
                    num=Integer.valueOf(time[0])*60*60+Integer.valueOf(time[1])*60+Integer.valueOf(time[2]);
                    Log.e("qqq","time="+num);

                    if(num==0){
                        dsLayout.setVisibility(View.GONE);
                    }else{

                        h=Integer.valueOf(time[0]);
                        m=Integer.valueOf(time[1]);
                        h_s=Integer.valueOf(time[2]);
                        String s_h="";
                        String s_m="";
                        String s_s="";
                        if(h<10){
                            s_h="0"+h;
                        }else{
                            s_h=""+h;
                        }

                        if(m<10){
                            s_m="0"+m;
                        }else{
                            s_m=""+m;
                        }
                        if(h_s<10){
                            s_s="0"+h_s;
                        }else{
                            s_s=""+h_s;
                        }

                        handler.sendEmptyMessageDelayed(2001,0);
                    }

                    break;

                case 2001:
                    if(num==0){
                        dsLayout.setVisibility(View.GONE);
                    }else {
                        h = num / 60 / 60;
                        m = (num % (60 * 60)) / 60;
                        h_s = (num % (60 * 60)) % 60;
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
                        if (h_s < 10) {
                            s_s = "0" + h_s;
                        } else {
                            s_s = "" + h_s;
                        }
                        if (ds_state) {
                            ds_tv.setText( s_h + ":" + s_m + ":" + s_s+" 后开启" );
                        } else {
                            ds_tv.setText( s_h + ":" + s_m + ":" + s_s+" 后关闭" );
                        }

                        dsLayout.setVisibility(View.VISIBLE);
                        num--;
                        handler.sendEmptyMessageDelayed(2001, 1000);
                    }
                    break;

                case 3000:
                    handler.sendEmptyMessageDelayed(5000,0);//循环获取开关状态
                    handler.sendEmptyMessageDelayed(3000,1000);
//                    if(isConnected()){
//                        push_read();
//                        handler.sendEmptyMessageDelayed(4000,1000);
////                        handler.sendEmptyMessageDelayed(5000,1000);
//                    }else{
//                        handler.sendEmptyMessageDelayed(3000,1000);
//                    }
                    break;
                case 4000:
                    handler.sendEmptyMessageDelayed(6000,0);//循环获取定时状态
                    handler.sendEmptyMessageDelayed(4000,1000);
//                    if(isConnected()){
//                        push_red_down();
//                    }else{
//                        handler.sendEmptyMessageDelayed(4000,1000);
//                    }
                    break;
                case 5000:
                    if(isConnected()){
                        push_read();
                    }else{
                    }
                    break;
                case 6000:
                    if(isConnected()){
                        push_red_down();
                    }else{
                    }
                    break;
            }
        }
    };

    private int h=0,m=1,h_s=0;
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
    }

    public void tj(View v){
        Intent in=new Intent(context, SearchActivity.class);
        startActivity(in);
    }


    public void fh(View v){
        finish();
    }
    @Override
    protected void onStart() {

        myTopicding = "iotbroad/iot/socket_ack/"+sid;
        myTopic = "iotbroad/iot/socket/"+sid;
        if(!sid.equals("")){
            handler.removeMessages(3000);
            handler.removeMessages(4000);
            handler.sendEmptyMessageDelayed(3000,1000);
            handler.sendEmptyMessageDelayed(4000,1000);
        }
        Log.e("qqq","sid="+sid);
        super.onStart();

    }


    private boolean search_zt=false;  //插座开关

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.socke_tv:
                setName();

                break;
            case R.id.search_im:
                if(search_zt){
                    push("off");
                }else{
                    push("on");
                }
                break;
            case R.id.search_state:
                if(search_zt){
                    push("off");
                }else{
                    push("on");
                }
                break;

            case R.id.search_ds:
                if(sid.equals("")){
                    Toast.makeText(context,"先添加设备",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent in=new Intent(context, TimingActivity.class);
                in.putExtra("Timing_sid",sid);
                in.putExtra("Timing_type","socket");
                startActivity(in);

                break;

            case R.id.search_djs:
//                if(sid.equals("")){
//                    Toast.makeText(context,"先添加设备",Toast.LENGTH_LONG).show();
//                    return;
//                }
                showTimePick(h,m,h_s);

                break;
            case R.id.search_ds_del:
                push_djs_del();
                break;

            case R.id.search_ds_layout:
                showTimePick(h,m,h_s);
                break;
        }
    }

    public void setStateView(boolean zt){  //开关插座
        if(zt){
            state.setText("关闭");
            im.setBackgroundResource(R.drawable.zjj_chazuo_selected);
        }else{
            state.setText("开启");
            im.setBackgroundResource(R.drawable.zjj_chazuo_normal);
        }
    }

    int num = 0; //倒计时长
    private TimePickerDialog.Builder builder = null;
    private Dialog timeDialog;
    private void showTimePick(int h, int m,int s) {

        if (timeDialog == null) {

            builder = new TimePickerDialog.Builder(this);

            timeDialog = builder.setOnTimeSelectedListener(new TimePickerDialog.OnTimeSelectedListener() {
                @Override
                public void onTimeSelected(String[] times) {

//                    mTextView.setText(times[0] + ":" + times[1]);
                    int h=Integer.valueOf(times[0]);
                    int m=Integer.valueOf(times[1]);
                    int s=Integer.valueOf(times[2]);
                    int long_l=h*60*60+m*60+s;
                    num =long_l;

                    String s_h="";
                    String s_m="";
                    String s_s="";
                    if(h<10){
                        s_h="0"+h;
                    }else{
                        s_h=""+h;
                    }

                    if(m<10){
                        s_m="0"+m;
                    }else{
                        s_m=""+m;
                    }
                    if(s<10){
                        s_s="0"+s;
                    }else{
                        s_s=""+s;
                    }
//                    handler.sendEmptyMessageDelayed(2000,0);
                    push_d_time(s_h+","+s_m+","+s_s);

                }
            }).create();

        }


        builder.setText_sj(h, m,s,search_zt);
        timeDialog.show();

    }

    public void push_d_time(String i) {
        if(sid.equals("")){
            Toast.makeText(context,"先添加设备",Toast.LENGTH_LONG).show();
            return;
        }

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_socket_count_down");
            jsonObject.put("data", i);
            jsonObject.put("sid", sid);
            if(search_zt){
                jsonObject.put("state","off");
            }else{
                jsonObject.put("state","on");
            }

            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }

    public void push_djs_del() { //删除倒计时
        if(sid.equals("")){
            Toast.makeText(context,"先添加设备",Toast.LENGTH_LONG).show();
            return;
        }

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_socket_count_down");
            jsonObject.put("state", "cancel");
            jsonObject.put("data", "00,00,00");
            jsonObject.put("sid", sid);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }


    private String mc = "";
    public void setName(){
        final EditText et = new EditText(this);

        new AlertDialog.Builder(this).setTitle("改变名称")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "内容不能为空！" + input, Toast.LENGTH_LONG).show();
                        } else {
                            mc = input;
                        }
                    }
                })
                .setNegativeButton("取消", null).show();

    }



    public void push_name() { //修改名称

        final String myTopicding_too = "iotbroad/iot/socket_ack/";
        subscribe(myTopicding_too);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_socket_count_down");
                    jsonObject.put("state", "cancel");
                    jsonObject.put("data", "00,00,00");
                    String js = jsonObject.toString();
                    publish_String2(js,myTopicding_too);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }

}
