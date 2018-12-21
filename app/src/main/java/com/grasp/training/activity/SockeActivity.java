package com.grasp.training.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.service.MqttService;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.BaseTcpMqttActpvity;
import com.grasp.training.tool.Tool;

import org.json.JSONException;
import org.json.JSONObject;

public class SockeActivity extends BaseTcpMqttActpvity implements View.OnClickListener {
    public static String socket = "Main_sockets";
    public static String socket2 = "Main_sockets2";
    private String sid = "";
    private String dname="";
    private String myTopicding = "iotbroad/iot/socket_ack/" + sid;
    private String myTopic = "iotbroad/iot/socket/" + sid;
    private Context context;
    private Button state, ds, djs,zdgb;
    private LinearLayout dsLayout;
    private ImageView ds_del;
    private TextView ds_tv,mc_tv,tv_sys;
    private ImageView im,tv_sys_im,im_sx;
    private String sys_ver="",hard_ver = "";

    private TextView tv_ip;
    private TextView tv_gl;
    private double w=0;
    private int power=0;
    private boolean stop_flag;

    public static void starstSockeActivity(Context context, String sid,String dname) {
        Intent in = new Intent(context, SockeActivity.class);
        in.putExtra(socket, sid);
        in.putExtra(socket2, dname);
        context.startActivity(in);
    }

    @Override
    public int setLayoutId() {
        return R.layout.socke_activity;
    }

    @Override
    public void initData() {
        context = getContext();
        sid = getIntent().getStringExtra(socket);
        dname= getIntent().getStringExtra(socket2);
        myTopicding = "iotbroad/iot/socket_ack/" + sid;
        myTopic = "iotbroad/iot/socket/" + sid;

//        handler.sendEmptyMessageDelayed(2222,1000);
    }

    @Override
    public void initView() {
        state = (Button) findViewById(R.id.search_state);
        ds = (Button) findViewById(R.id.search_ds);
        djs = (Button) findViewById(R.id.search_djs);
        dsLayout = (LinearLayout) findViewById(R.id.search_ds_layout);
        ds_del = (ImageView) findViewById(R.id.search_ds_del);
        ds_tv = (TextView) findViewById(R.id.search_ds_tv);
        im = (ImageView) findViewById(R.id.search_im);
        mc_tv=(TextView) findViewById(R.id.socke_tv);
        tv_sys=(TextView) findViewById(R.id.socke_sys);
        tv_sys_im = (ImageView) findViewById(R.id.socke_sys_im);
        tv_ip=(TextView) findViewById(R.id.socke_ip);
        im_sx = (ImageView) findViewById(R.id.socke_xh);
        tv_gl=(TextView) findViewById(R.id.socke_gl);
        zdgb = (Button) findViewById(R.id.search_zdgb);
    }

    @Override
    public void initObject() {
        mc_tv.setText(dname);
        tv_sys_im.setVisibility(View.INVISIBLE);
    }

    @Override
    public void initListener() {
        state.setOnClickListener(this);
        ds.setOnClickListener(this);
        djs.setOnClickListener(this);
        dsLayout.setOnClickListener(this);
        ds_del.setOnClickListener(this);
        im.setOnClickListener(this);
        mc_tv.setOnClickListener(this);
        tv_sys.setOnClickListener(this);
        im_sx.setOnClickListener(this);
        zdgb.setOnClickListener(this);
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
        Log.e("qqq", "socke message=" + message);
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
                        case "wifi_socket_ack":
                            Log.e("qqq", "socke  wifi_socket_ack" + message);
                            handler.removeMessages(3000);
                            handler.removeMessages(5000);
                            state = jsonObject.optString("state");
                            String ver= jsonObject.optString("sys_ver","");
                            if(!ver.equals("")){
                                sys_ver=ver;
                            }
                            String hard = jsonObject.optString("hard_ver", "");
                            if (!hard.equals("")) {
                                hard_ver = hard;
                            }

                            int i_stop_flag = jsonObject.optInt("stop_flag", 0);
                            if(i_stop_flag==1){
                                stop_flag=true;
                            }else{
                                stop_flag=false;
                            }

                            me = new Message();
                            me.what = 1000;
                            me.obj = state;
                            handler.sendMessage(me);

                            break;
                        case "wifi_socket_count_down_ack":

                            data = jsonObject.optString("data");
                            search = jsonObject.optString("state");
                            if (search.equals("on")) {
                                ds_state = true;
                            } else {
                                ds_state = false;
                            }

                            me = new Message();
                            me.what = 2000;
                            me.obj = data;
                            handler.removeMessages(2000);
                            handler.removeMessages(2001);
                            handler.sendMessageDelayed(me,500);
                            break;

                        case "wifi_socket_count_down_act":
                            data = jsonObject.optString("data");
                            me = new Message();
                            me.what = 2000;
                            me.obj = data;
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
                            search = jsonObject.optString("state", "");
                            if (search.equals("on")) {
                                ds_state = true;
                            } else {
                                ds_state = false;
                            }

                            me = new Message();
                            me.what = 2000;
                            me.obj = data;
                            handler.removeMessages(2000);
                            handler.removeMessages(2001);
                            handler.sendMessage(me);
                            break;
                        case "updatedevicename_ok":
                            String uname = jsonObject.optString("uname", "");  //
                            if (!uname.equals(MainActivity.NameUser)) {
                                return;
                            }
                            String clientid = jsonObject.optString("clientid", "");
                            if (!clientid.equals(Tool.getIMEI(getContext()))) {
                                return;
                            }
                            dname= jsonObject.optString("dname", "");
                            handler.sendEmptyMessageDelayed(233,500);

                            break;
                        case "wifi_equipment_ping_ack":
                            String ip = jsonObject.optString("ip", "");  //
                            Message me2=new Message();
                            me2.what=1666;
                            me2.obj=ip;
                            handler.sendMessage(me2);
                            break;
                        case "wifi_socket_power_check":
                            power = jsonObject.optInt("power", -1);  //
                            w=jsonObject.optInt("w", -1);
                            Message me3=new Message();
                            me3.what=1667;
                            me3.obj="功率："+power+"w   用电量:"+(w/1000)+"kwh";
                            handler.sendMessage(me3);
                            break;


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean ds_state = false;//倒计时将要变的状态

    public void push(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                }
            }
        }).start();

    }

    public void push_red_down() {  //查询倒计时
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_socket_read_down");
                    jsonObject.put("sid", sid);
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void push_read() {  //获取插座状态
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_socket_read");
                    jsonObject.put("sid", sid);
                    String js = jsonObject.toString();
                    publish_String(js);
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
                case 2222:
                    handler.removeMessages(2222);
                    if(sys_ver.equals("")){
                        handler.sendEmptyMessageDelayed(2222,1000);
                        return;
                    }
                    if(!isConnected()){
                        handler.sendEmptyMessageDelayed(2222,1000);
                        return;
                    }
                    isUpdata(sys_ver);

                    break;

                case 233:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    mc_tv.setText(dname);

                    break;

                case 1000:
                    String s = msg.obj.toString();
                    if (s.equals("on")) {
                        search_zt = true;
                    } else {
                        search_zt = false;
                    }
                    setStateView(search_zt);
                    if(stop_flag){
                        zdgb.setTextColor(getResources().getColor(R.color.c_1eac94));
                    }else{
                        zdgb.setTextColor(getResources().getColor(R.color.c_000000));
                    }

//                    tv_sys.setText("版本号："+sys_ver);
                    break;

                case 2000:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    String time[] = msg.obj.toString().split(",");
                    num = Integer.valueOf(time[0]) * 60 * 60 + Integer.valueOf(time[1]) * 60 + Integer.valueOf(time[2]);
                    Log.e("qqq", "time=" + num);

                    if (num == 0) {
                        dsLayout.setVisibility(View.GONE);
                    } else {

                        h = Integer.valueOf(time[0]);
                        m = Integer.valueOf(time[1]);
                        h_s = Integer.valueOf(time[2]);
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

                        handler.sendEmptyMessageDelayed(2001, 0);
                    }

                    break;

                case 2001:
                    if (num == 0) {
                        dsLayout.setVisibility(View.GONE);
                    } else {
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
                            ds_tv.setText(s_h + ":" + s_m + ":" + s_s + " 后开启");
                        } else {
                            ds_tv.setText(s_h + ":" + s_m + ":" + s_s + " 后关闭");
                        }

                        dsLayout.setVisibility(View.VISIBLE);
                        num--;
                        handler.sendEmptyMessageDelayed(2001, 1000);
                    }
                    break;

                case 3000:
                    handler.sendEmptyMessageDelayed(5000, 0);//循环获取开关状态
                    handler.sendEmptyMessageDelayed(3000, 1000);
//                    if(isConnected()){
//                        push_read();
//                        handler.sendEmptyMessageDelayed(4000,1000);
////                        handler.sendEmptyMessageDelayed(5000,1000);
//                    }else{
//                        handler.sendEmptyMessageDelayed(3000,1000);
//                    }
                    break;
                case 4000:
                    handler.sendEmptyMessageDelayed(6000, 0);//循环获取定时状态
                    handler.sendEmptyMessageDelayed(4000, 1000);
//                    if(isConnected()){
//                        push_red_down();
//                    }else{
//                        handler.sendEmptyMessageDelayed(4000,1000);
//                    }
                    break;
                case 5000:
                    if (isConnected()) {
                        push_read();
                    } else {
                    }
                    break;
                case 6000:
                    if (isConnected()) {
                        push_red_down();
                    } else {
                    }
                    break;

                case 1666:
                    tv_ip.setText(msg.obj.toString());
                    break;
                case 1667:
                    tv_gl.setText(msg.obj.toString());
                    break;
            }
        }
    };

    private int h = 0, m = 1, h_s = 0;

    @Override
    protected void onDestroy() {
        handler.removeMessages(1666);
        handler.removeMessages(1667);
        handler.removeMessages(1000);
        handler.removeMessages(2000);
        handler.removeMessages(2001);
        handler.removeMessages(3000);
        handler.removeMessages(4000);
        handler.removeMessages(5000);
        handler.removeMessages(6000);
        handler.removeMessages(233);
        handler.removeMessages(2222);
        super.onDestroy();

    }

    public void tj(View v) {
        Intent in = new Intent(context, SearchActivity.class);
        startActivity(in);
    }


    public void fh(View v) {
        finish();
    }

    @Override
    protected void onStart() {

//        myTopicding = "iotbroad/iot/socket_ack/" + sid;
//        myTopic = "iotbroad/iot/socket/" + sid;
        if (!sid.equals("")) {
            handler.removeMessages(3000);
            handler.removeMessages(4000);
            handler.sendEmptyMessageDelayed(3000, 000);
            handler.sendEmptyMessageDelayed(4000, 000);
        }
//        Log.e("qqq", "sid=" + sid);
        super.onStart();

    }


    private boolean search_zt = false;  //插座开关

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_zdgb:
                if(stop_flag){
                    push_stop_flag(0);
                }else{
                    push_stop_flag(1);
                }

                break;
            case R.id.socke_xh://循环
                CycleActivity.strateCycleActivity(context,sid,"socket");

                break;
            case R.id.socke_sys://更新检查
//                if(!sys_ver.equals("")){
//                    if(updata_zt){
//                        updata();
//                    }
//
//                }
                break;
            case R.id.socke_tv:
                setName();

                break;
            case R.id.search_im:
                if (search_zt) {
                    push("off");
                } else {
                    push("on");
                }
                break;
            case R.id.search_state:
                if (search_zt) {
                    push("off");
                } else {
                    push("on");
                }
                break;

            case R.id.search_ds:
                if (sid.equals("")) {
                    Toast.makeText(context, "先添加设备", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent in = new Intent(context, TimingActivity.class);
                in.putExtra("Timing_sid", sid);
                in.putExtra("Timing_type", "socket");
                startActivity(in);

                break;

            case R.id.search_djs:
//                if(sid.equals("")){
//                    Toast.makeText(context,"先添加设备",Toast.LENGTH_LONG).show();
//                    return;
//                }
                showTimePick(h, m, h_s);

                break;
            case R.id.search_ds_del:
                showPro2();
                push_djs_del();
                break;

            case R.id.search_ds_layout:
                showTimePick(h, m, h_s);
                break;
        }
    }


    private ProgressDialog dialog;

    public void showPro2() {

        dialog = new ProgressDialog(context);
        dialog.setMessage("删除倒计时中...");
        dialog.setCancelable(true);

        dialog.show();
    }
    public void put_sz(View v){
        EquipmentUpdataActivity.starstEquipmentActivity(context,sid,"socket",sys_ver,hard_ver);
    }

    public void setStateView(boolean zt) {  //开关插座
        if (zt) {
            state.setText("关闭");
            im.setBackgroundResource(R.drawable.zjj_chazuo_selected);
        } else {
            state.setText("开启");
            im.setBackgroundResource(R.drawable.zjj_chazuo_normal);
        }
    }

    int num = 0; //倒计时长
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
//                    handler.sendEmptyMessageDelayed(2000,0);
                    push_d_time(s_h + "," + s_m + "," + s_s);

                }
            }).create();

        }

        String ss="";
        if(search_zt){
            ss="开";
        }else{
            ss="关";
        }
        builder.setText_sj(h, m, s, ss);
        timeDialog.show();

    }

    public void push_d_time(final String i) {
        if (sid.equals("")) {
            Toast.makeText(context, "先添加设备", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_socket_count_down");
                    jsonObject.put("data", i);
                    jsonObject.put("sid", sid);
                    if (search_zt) {
                        jsonObject.put("state", "off");
                    } else {
                        jsonObject.put("state", "on");
                    }

                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void push_djs_del() { //删除倒计时
        if (sid.equals("")) {
            Toast.makeText(context, "先添加设备", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                }
            }
        }).start();

    }


    private String mc = "";

    public void setName() {
        Log.e("qqq","setName");
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

        final String myTopicding_too =  MqttService.myTopicDevice;
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
                }
            }
        }).start();

    }



    public void showPro() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("修改中...");
        dialog.setCancelable(true);
        dialog.show();
    }



    private boolean updata_zt=false;
    private void updata() {   //更新

        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setTitle("是否对设备进行版本更新");
        builder.setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
            }
        });

        builder.setNegativeButton(
                getString(R.string.alert_dialog_cancel), null);
        builder.show();
    }

    public void isUpdata(final String sys) {  //判断版本是否要更新
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
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("sys",sys );
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    String js = jsonObject.toString();
                    publish_String3(js, myTopicding_too);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void push_stop_flag(final int stop) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_socket_stop");
                    jsonObject.put("stop_flag", stop);
                    jsonObject.put("sid", sid);
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
