package com.grasp.training.activity;

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
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.BaseTcpMqttActpvity;
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

public class EquipmentActivity extends BaseTcpMqttActpvity {


    public final static String equipment1 = "Equipment_1";
    public final static String equipment2 = "Equipment_2";
    public final static String equipment3 = "Equipment_3";
    @BindView(R.id.equipment_im)
    ImageView im;
    @BindView(R.id.equipment_tv)
    TextView equipmentTv;
    @BindView(R.id.equipment_fh)
    Button equipmentFh;
    @BindView(R.id.search_ds_tv)
    TextView ds_tv;
    @BindView(R.id.search_ds_del)
    ImageView searchDsDel;
    @BindView(R.id.search_ds_layout)
    LinearLayout dsLayout;
    @BindView(R.id.equipment_ds)
    Button equipmentDs;
    @BindView(R.id.equipment_state)
    Button state;
    @BindView(R.id.equipment_djs)
    Button equipmentDjs;
    @BindView(R.id.equipment_sys)
    TextView tv_sys;
    @BindView(R.id.equipment_sys_im)
    ImageView tv_sys_im;
    private String sid, type,dname;
    private Context context;
    private String myTopicding, myTopic;
    private String sys_ver="",hard_ver = "";

    public static void starstEquipmentActivity(Context context, String sid, String type,String name) {
        Intent in = new Intent(context, EquipmentActivity.class);
        in.putExtra(equipment1, sid);
        in.putExtra(equipment2, type);
        in.putExtra(equipment3, name);
        context.startActivity(in);
    }

    @Override
    public int setLayoutId() {
        return R.layout.equipment_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context = getContext();
        sid = getIntent().getStringExtra(equipment1);
        type = getIntent().getStringExtra(equipment2);
        dname = getIntent().getStringExtra(equipment3);
        myTopicding = "iotbroad/iot/"+type+"_ack/" + sid;
        myTopic = "iotbroad/iot/"+type+"/" + sid;
        if (sid==null || type==null) {
            Toast.makeText(context, "数据错误", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (sid.equals("") || type.equals("")) {
            Toast.makeText(context, "数据错误", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        tv_sys_im.setVisibility(View.INVISIBLE);

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
        if(eMap.get(type)!=null){
//            equipmentTv.setText(eMap.get(type));
        }
        equipmentTv.setText(dname);

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
                    if(cmd.equals("wifi_"+type+"_ack")){
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
                        me=new Message();
                        me.what=1000;
                        me.obj=state;
                        handler.sendMessage(me);
                    }else if(cmd.equals("wifi_"+type+"_count_down_ack")){
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
                    }else if(cmd.equals("wifi_"+type+"_count_down_act")){
                        data = jsonObject.optString("data");
                        me=new Message();
                        me.what=2000;
                        me.obj=data;
                        handler.removeMessages(2000);
                        handler.removeMessages(2001);
                        handler.sendMessage(me);
                    }else if(cmd.equals("wifi_"+type+"_read_down_ack")){
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
                    }else if(cmd.equals("updatedevicename_ok")){
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
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        if(!sid.equals("")){
            handler.removeMessages(3000);
            handler.removeMessages(4000);
            handler.sendEmptyMessageDelayed(3000,000);
            handler.sendEmptyMessageDelayed(4000,000);
        }
        super.onStart();
    }

    public void put_sz(View v){
        EquipmentUpdataActivity.starstEquipmentActivity(context,sid,type,sys_ver,hard_ver);
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

    private boolean ds_state=false;//倒计时将要变的状态
    int num = 0; //倒计时长
    private boolean search_zt=false;  //插座开关
    private int h=0,m=1,h_s=0;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case 233:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    equipmentTv.setText(dname);

                    break;
                case 1000:
                    String s=msg.obj.toString();
                    if(s.equals("on")){
                        search_zt=true;
                    }else{
                        search_zt=false;
                    }
//                    tv_sys.setText("版本号："+sys_ver);
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

    public void setStateView(boolean zt) {  //开关
        if (zt) {
            state.setText("关闭");
            im.setBackgroundResource(R.drawable.zhu_chazuo_selected);
        } else {
            state.setText("开启");
            im.setBackgroundResource(R.drawable.zhu_chazuo_normal);
        }
    }


    @OnClick({R.id.equipment_sys,R.id.equipment_tv,R.id.equipment_im, R.id.equipment_fh, R.id.search_ds_del, R.id.search_ds_layout, R.id.equipment_ds, R.id.equipment_state, R.id.equipment_djs})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.equipment_sys://更新检查
//                if(!sys_ver.equals("")){
//                    if(updata_zt){
//                        updata();
//                    }
//                }
                break;
            case R.id.equipment_tv:
                setName();
                break;
            case R.id.equipment_im:
                if(search_zt){
                    push("off");
                }else{
                    push("on");
                }
                break;
            case R.id.equipment_fh:
                finish();
                break;
            case R.id.search_ds_del:
                push_djs_del();
                break;
            case R.id.search_ds_layout:
                showTimePick(h,m,h_s);
                break;
            case R.id.equipment_ds:
                Intent in=new Intent(context, TimingActivity.class);
                in.putExtra("Timing_sid",sid);
                in.putExtra("Timing_type",type);
                startActivity(in);
                break;
            case R.id.equipment_state:
                if(search_zt){
                    push("off");
                }else{
                    push("on");
                }
                break;
            case R.id.equipment_djs:

                showTimePick(h,m,h_s);
                break;
        }
    }



    public void push(final String s) {  //改变开关状态
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_"+type);
                    jsonObject.put("state", s);
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


    public void push_red_down() {  //查询倒计时
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_"+type+"_read_down");
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

    public void push_read() {  //获取状态

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_"+type+"_read");
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



    public void push_d_time(final String i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_"+type+"_count_down");
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
        }).start();


    }

    public void push_djs_del() { //删除倒计时
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_"+type+"_count_down");
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
        }).start();

    }

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




    private ArrayList<EquipmentData> elist;
    private HashMap<String,String> eMap;

    private void setEquipmentData() {  //获取所有类型数据
        String data = SharedPreferencesUtils.getParam(getContext(), MainActivity.MainData, "").toString();
//        Log.e("qqq","data: "+data);
        elist = new ArrayList<>();
        eMap=new HashMap<>();
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
                    eMap.put(type,dname);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
