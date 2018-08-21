package com.grasp.training.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Player.Core.PlayerClient;
import com.Player.Core.PlayerCore;
import com.Player.Source.LogOut;
import com.Player.Source.SDKError;
import com.Player.Source.TAlarmFrame;
import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.Umeye_sdk.Constants;
import com.grasp.training.tool.BaseActivity;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.zs.easy.mqtt.EasyMqttService;
import com.zs.easy.mqtt.IEasyMqttCallBack;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ControlActivity extends BaseActivity {
    @BindView(R.id.control_im)
    ImageView img;
    @BindView(R.id.control_sx)
    ImageView controlSx;
    @BindView(R.id.control_xs)
    TextView xs;
    @BindView(R.id.control_tou_left)
    Button controlTouLeft;
    @BindView(R.id.control_tou_right)
    Button controlTouRight;
    @BindView(R.id.control_tou_up)
    Button controlTouUp;
    @BindView(R.id.control_tou_down)
    Button controlTouDown;
    @BindView(R.id.control_up)
    Button up;
    @BindView(R.id.control_down)
    Button down;
    @BindView(R.id.control_left)
    Button left;
    @BindView(R.id.control_right)
    Button right;
    @BindView(R.id.control_stop)
    Button controlStop;

    public static final int MD_STOP_ZHUAN = -1; // 停止
    public static final int MD_STOP = 0; // 停止
    public static final int MD_LEFT = 11; // 左
    public static final int MD_RIGHT = 12; // 右
    public static final int MD_UP = 9; // 上
    public static final int MD_DOWN = 10; // 下

    private PlayerClient playClient;
    private MyApplication appMain;
    private PlayerCore pc;
    private Context context;

    @Override
    public int setLayoutId() {
        return R.layout.control;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context = this;
        appMain = (MyApplication) ((Activity) context).getApplication();
        playClient = appMain.getPlayerclient();
        PicOnLongClick pic = new PicOnLongClick();
        down.setOnTouchListener(pic);
        up.setOnTouchListener(pic);
        left.setOnTouchListener(pic);
        right.setOnTouchListener(pic);
        controlTouLeft.setOnTouchListener(pic);
        controlTouRight.setOnTouchListener(pic);
        controlTouUp.setOnTouchListener(pic);
        controlTouDown.setOnTouchListener(pic);
        SharedPreferencesUtils.setParam(context, MainActivity.MainSB, "zw2017060802");
        initePlayCore();
        buildEasyMqttService();
        connect();
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


    @OnClick({R.id.control_sx, R.id.control_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.control_sx:
                if(!SharedPreferencesUtils.getParam(context, MainActivity.MainSB,"").equals("")){
                    controlSx.setVisibility(View.GONE);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(pc!=null){
//                                pc.Stop();
//                            }
//                            new StateThread().start();
//                        }
//                    }).start();
                    setData();

                }else{
                    Toast.makeText(getContext(),"绑定设备后再重试",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.control_stop:
                push_move(MD_STOP);
                push_move(MD_STOP_ZHUAN);
                break;
        }
    }

    private boolean an_zt=true;
    private class PicOnLongClick implements View.OnTouchListener {  //长按

        private int action = 0;

        public boolean onTouch(View view, MotionEvent event) {
            action = event.getAction();
            switch (action){
                case MotionEvent.ACTION_DOWN:
                    Log.e("eee","ACTION_DOWN");
                    if(!an_zt){
                        return false;
                    }
                    switch (view.getId()) {

                        case R.id.control_up:
                            push_move(MD_UP);

                            break;
                        case R.id.control_down:
                            push_move(MD_DOWN);

                            break;
                        case R.id.control_left:
                            push_move(MD_LEFT);
                            break;
                        case R.id.control_right:
                            push_move(MD_RIGHT);
                            break;

                        case R.id.control_tou_left:
                            push_head(MD_LEFT);
                            break;
                        case R.id.control_tou_right:
                            push_head(MD_RIGHT);
                            break;
                        case R.id.control_tou_up:
                            push_head(MD_UP);
                            break;
                        case R.id.control_tou_down:
                            push_head(MD_DOWN);
                            break;
                    }
                    an_zt=false;
                    new  Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                an_zt=true;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;

                case MotionEvent.ACTION_UP:
                    switch (view.getId()) {
                        case R.id.control_up:
                            push_move(MD_STOP);

                            break;
                        case R.id.control_down:
                            push_move(MD_STOP);

                            break;
                        case R.id.control_left:
                            push_move(MD_STOP_ZHUAN);
                            break;
                        case R.id.control_right:
                            push_move(MD_STOP_ZHUAN);
                            break;

                        case R.id.control_tou_left:
                            push_head(MD_STOP);
                            break;
                        case R.id.control_tou_right:
                            push_head(MD_STOP);
                            break;
                        case R.id.control_tou_up:
                            push_head(MD_STOP);
                            break;
                        case R.id.control_tou_down:
                            push_head(MD_STOP);
                            break;
                    }
                    break;
            }


            return false;
        }
    }


    public void initePlayCore() {
        pc = new PlayerCore(context);
        pc.InitParam("", -1, img);
        pc.SetPPtMode(true);
        pc.OpenAudio();
        if (!((String) SharedPreferencesUtils.getParam(context, MainActivity.MainSB, "")).equals("")) {
            playVideo();
        }
    }

    public void playVideo() {
        Constants.UMID = (String) SharedPreferencesUtils.getParam(context, MainActivity.MainSB, "");
        Constants.user = "admin";
        Log.d("qqq", Constants.UMID + "  " + Constants.user + "   " + Constants.password);
        Stop(new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                pc.PlayP2P(Constants.UMID, "admin",
                        Constants.password, 0, 1);
//                pc.PlayP2P(Constants.UMID, Constants.user,
//                        Constants.password, which, 1);
                // pc.PlayAddress(1009, "192.168.10.247", 5800,
                // "admin",
                // "", 0, 1);
//                rel_zr.setVisibility(View.GONE);
                super.handleMessage(msg);
            }
        });
    }

    public void Stop(final Handler handler) {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                // if (!isStart) {
                // pc.CameraStopGetAlarmInfo();
                // }
                if (pc.GetIsPPT()) {
                    pc.StopPPTAudio();
                }
                pc.Stop();
                // isStart = false;

                if (handler != null) {
                    handler.sendEmptyMessage(0);
                }

            }
        }.start();
    }

    //    @Override
//    public void onStop() {
//        super.onStop();
//        Stop(null);
//    }
    public void setData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (pc != null) {
                    pc.Stop();
                }
//                isRun = false;
            }
        }).start();
        if (!((String) SharedPreferencesUtils.getParam(context, MainActivity.MainSB, "")).equals("")) {
            playVideo();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
        close();
        handler.removeCallbacks(null);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        isRun = true;
//        if(!SharedPreferencesUtils.getParam(context, MainActivity.MainSB,"").equals("")){
//            controlSx.setVisibility(View.GONE);
//
//            new StateThread().start();
//        }else{
////            Toast.makeText(getContext(),"绑定设备后再重试",Toast.LENGTH_SHORT).show();
//        }
        setData();
        Log.d("qqq"," new StateThread().start();");
        new StateThread().start();
        super.onResume();
    }


    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(pc!=null){
                    pc.Stop();
                }
                isRun = false;
            }
        }).start();

        super.onPause();
    }
    /**
     * 状态显示线程
     *
     * @author Simula
     *
     */
    public static boolean isRun = true;

    class StateThread extends Thread {

        @Override
        public void run() {

            try {
                Log.d("qqq","StateThread "+isRun);
                while (isRun) {
                    Log.d("qqq","StateThread "+isRun);
                    Thread.sleep(500);
                    Message msg = new Message();
                    msg.what = SHOW_STATE;
                    msg.arg1 = pc.PlayCoreGetCameraPlayerState();
                    if (pc.GetIsSnapVideo()) {
                        msg.arg2 = 1;
                    }
                    // Log.w("state", "state: " + msg.arg1);
                    handler.sendMessage(msg);
                    Log.d("qqq","StateThread 111"+isRun);
                    TAlarmFrame tAlarmFrame = pc.CameraGetAlarmInfo();
                    if (tAlarmFrame != null) {
                        handler.sendMessage(Message.obtain(handler,
                                ALARM_STATE, tAlarmFrame));
                        Log.d("qqq","StateThread 2 "+isRun);
                    }
                    Log.d("qqq","StateThread 111"+isRun);
                }
            } catch (Exception e) {
                // TODO: handle exception
                Log.d("qqq","StateThread Exception"+isRun);
            }

        }

    }

    public static final byte SHOW_STATE = 0;

    public static final byte ALARM_STATE = 1;

    public static final int CREATE_CILENT = 0x123;
    public static final int DESTORY_CILENT = 0x124;
    private Handler handler = new Handler() {

        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d("handleMessage111",msg.what+"");
            if (msg.what == SHOW_STATE) {
                Log.d("qqq",GetDescription(context, msg.arg1));

                if(controlSx!=null&&GetDescription(context, msg.arg1).equals("连接失败")){
                    controlSx.setVisibility(View.VISIBLE);
                }
                // txtState.setText(GetDescription(RemoteVideoActivity.this, msg.arg1));
                // 是否显示录像
                // txtRec.setVisibility(msg.arg2 == 1 ? View.VISIBLE : View.GONE);

                // if (msg.arg1 == SDKError.Statue_PLAYING) {
                // if (!pc.GetIsPPT()) {
                // pc.StartPPTAudio();
                // }
                // if (pc.GetIsVoicePause()) {
                // pc.OpenAudio();
                // }
                //
                // } else {
                // if (pc.GetIsPPT()) {
                // pc.StopPPTAudio();
                // }
                // if (!pc.GetIsVoicePause()) {
                // pc.CloseAudio();
                // }
                //
                // }

            } else if (msg.what == CREATE_CILENT) {
//                showProgress.dismiss();
            } else if (msg.what == DESTORY_CILENT) {
//                showProgress.dismiss();

                if(controlSx!=null){
                    Toast.makeText(context,
                            R.string.srmm4,
                            Toast.LENGTH_LONG).show();
                    controlSx.setVisibility(View.VISIBLE);
                }
            } else if (msg.what == ALARM_STATE) {
                TAlarmFrame tAlarmFrame = (TAlarmFrame) msg.obj;
                if (tAlarmFrame != null) {
                    LogOut.d("tAlarmFrame", "tAlarmFrame:"
                            + tAlarmFrame.AlarmInfo + ",nAlarmType:"
                            + tAlarmFrame.nAlarmType);
                    Toast.makeText(context,
                            "tAlarmFrame:" + tAlarmFrame.AlarmInfo,
                            Toast.LENGTH_LONG).show();
                }

                Vibrate(context, 1000);
            } else if (msg.what ==789) {
//                control_zt=true;
            }


            // else if (msg.what == SetWIFIRunable.SET_WIFI_SUCCESS) {
            // Toast.makeText(PlayActivity.this, "设置wifi成功",
            // Toast.LENGTH_SHORT).show();
            // } else if (msg.what == SetWIFIRunable.SET_WIFI_FAILED) {
            // Toast.makeText(PlayActivity.this, "设置wifi失败~~",
            // Toast.LENGTH_SHORT).show();
            // }

            super.handleMessage(msg);
        }

    };

    public void Vibrate(Context context, long milliseconds) {
        Vibrator vib = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }


    public String GetDescription(Context con, int state) {
        Log.i("GetDescription", "GetDescription:" + state);
        String des = con.getString(R.string.connect_fail);



        switch (state) {
            case 0:
                Log.d("ffff",state+"");
                des = con.getString(R.string.ready);
                if(xs!=null)
                    xs.setText(con.getString(R.string.ready));
                break;
            case 1:
                Log.d("ffff",state+"");
                des = con.getString(R.string.connecting);
                if(xs!=null)
                    xs.setText(con.getString(R.string.connecting));
                break;
            case 2:
                Log.d("ffff",state+"");
                des = con.getString(R.string.playing);
                if(controlSx!=null){
                    controlSx.setVisibility(View.GONE);
                    xs.setText(con.getString(R.string.playing));
                    xs.setVisibility(View.GONE);
                }

                break;
            case 3:
                Log.d("ffff",state+"");
                des = con.getString(R.string.connect_fail);

                if(controlSx!=null){
//                    Toast.makeText(getContext(),R.string.connect_fail,Toast.LENGTH_SHORT).show();
                    controlSx.setVisibility(View.VISIBLE);
                }
                if(xs!=null)
                    xs.setText(con.getString(R.string.connect_fail));
                break;
            case 4:
                Log.d("ffff",state+"");
                des = con.getString(R.string.stop);

                if(controlSx!=null){
//                    Toast.makeText(getContext(),R.string.stop,Toast.LENGTH_SHORT).show();
                    controlSx.setVisibility(View.VISIBLE);
                    xs.setText(con.getString(R.string.stop));
                }

                break;
            case 7:
                Log.d("ffff",state+"");
                des = con.getString(R.string.stop);
//                Toast.makeText(getContext(),R.string.stop,Toast.LENGTH_SHORT).show();
                if(controlSx!=null){
                    controlSx.setVisibility(View.VISIBLE);
                    xs.setText(con.getString(R.string.stop));
                }
                break;
            case SDKError.NPC_D_MPI_MON_ERROR_USERID_ERROR:
                Log.d("ffff",state+"");
                des = con.getString(R.string.usererro);

                if(controlSx!=null){
//                    Toast.makeText(getContext(),R.string.usererro,Toast.LENGTH_SHORT).show();
                    controlSx.setVisibility(View.VISIBLE);
                    xs.setText(con.getString(R.string.usererro));
                }
                break;
            case SDKError.NPC_D_MPI_MON_ERROR_USERPWD_ERROR:
                Log.d("ffff",state+"");
                des = con.getString(R.string.passworderro);

                if(controlSx!=null){
//                    Toast.makeText(getContext(),R.string.passworderro,Toast.LENGTH_SHORT).show();
                    controlSx.setVisibility(View.VISIBLE);
                    xs.setText(con.getString(R.string.passworderro));
                }
                break;
            case 10:
                Log.d("ffff",state+"");
                des = "缓冲中";
                if(xs!=null)
                    xs.setText("缓冲中");
                break;
            case SDKError.NPC_D_MPI_MON_ERROR_REJECT_ACCESS:
                Log.d("ffff",state+"");
                des = con.getString(R.string.NPC_D_MPI_MON_ERROR_REJECT_ACCESS);

                if(controlSx!=null){
//                    Toast.makeText(getContext(),R.string.NPC_D_MPI_MON_ERROR_REJECT_ACCESS,Toast.LENGTH_SHORT).show();
                    controlSx.setVisibility(View.VISIBLE);
                    xs.setText(con.getString(R.string.NPC_D_MPI_MON_ERROR_REJECT_ACCESS));
                }
                break;
        }
        return des;

    }

    private EasyMqttService mqttService;

    /**
     * 判断服务是否连接
     */
    private boolean isConnected() {
        return mqttService.isConnected();
    }

    /**
     * 发布消息
     */
    private void publish(String msg, String topic, int qos, boolean retained) {
        mqttService.publish(msg, topic, qos, retained);
    }

    /**
     * 断开连接
     */
    private void disconnect() {
        mqttService.disconnect();
    }

    /**
     * 关闭连接
     */
    private void close() {
        mqttService.close();
    }

    /**
     * 订阅主题 这里订阅三个主题分别是"a", "b", "c"
     */
    private void subscribe() {
//        String[] topics2 = new String[]{"#"};
//        mqttService.unSubscribe(topics2);
        String[] topics = new String[]{"iotbroad/iot"};
//        String[] topics = new String[]{"#"};
        //主题对应的推送策略 分别是0, 1, 2 建议服务端和客户端配置的主题一致
        // 0 表示只会发送一次推送消息 收到不收到都不关心
        // 1 保证能收到消息，但不一定只收到一条
        // 2 保证收到切只能收到一条消息
        int[] qoss = new int[]{0};
        mqttService.subscribe(topics, qoss);


    }

    /**
     * 连接Mqtt服务器
     */
    private IEasyMqttCallBack iEasyMqttCallBack;
    private String sid = MainActivity.SID;

    private void connect() {
        iEasyMqttCallBack = new IEasyMqttCallBack() {
            @Override
            public void messageArrived(final String topic, final String message, final int qos) {
                //推送消息到达

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("messageArrived", "messageArrived  message= " + message);
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

                            switch (cmd) {
                                case "read_ok":
//                                    String data = jsonObject.optString("data");
//                                    me = new Message();
//                                    me.what = setTWO;
//                                    me.obj = data;
//                                    ha.sendMessage(me);
                                    break;

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }

            @Override
            public void connectionLost(Throwable arg0) {
                //连接断开
                Log.e("qqq", "connectionLost");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken arg0) {
                //发送成功
                try {
                    Log.e("qqq", "deliveryComplete" + arg0.getMessage().toString());
                    JSONObject jsonObject = new JSONObject(arg0.getMessage().toString());
                    String cmd = jsonObject.getString("cmd");

                    switch (cmd) {
                        case "read":

                            break;

                        case "training_mode":
                            break;
                    }

                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void connectSuccess(IMqttToken arg0) {
                //连接成功
                Log.e("qqq", "connectSuccess");

            }

            @Override
            public void connectFailed(IMqttToken arg0, Throwable arg1) {
                //连接失败
                Log.e("qqq", "connectFailed");
            }
        };

        mqttService.connect(iEasyMqttCallBack);


    }

    /**
     * 构建EasyMqttService对象
     */
    private void buildEasyMqttService() {
        mqttService = new EasyMqttService.Builder()
                //设置自动重连
                .autoReconnect(true)
                //设置不清除回话session 可收到服务器之前发出的推送消息
                .cleanSession(false)
                //唯一标示 保证每个设备都唯一就可以 建议 imei
                .clientId(getIMEI(context))
                //mqtt服务器地址 格式例如：tcp://10.0.261.159:1883
                .serverUrl("tcp://broker.hivemq.com:1883")
                //心跳包默认的发送间隔
                .keepAliveInterval(20)
                //构建出EasyMqttService 建议用application的context
                .bulid(context.getApplicationContext());
    }




    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();

        return imei;
    }

    public void push_move( int var ) {
        String uid=SharedPreferencesUtils.getParam(context, MainActivity.MainSB,"").toString();
        if(uid.equals("")){
            return;
        }

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "move");
            jsonObject.put("var", var);
            jsonObject.put("uid", uid);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }


    public void push_head( int var ) {
        String uid=SharedPreferencesUtils.getParam(context, MainActivity.MainSB,"").toString();
        if(uid.equals("")){
            return;
        }

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "head");
            jsonObject.put("var", var);
            jsonObject.put("uid", uid);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean publish_String(String set_msg) {  //发送消息
        if (isConnected()) {
            //消息主题
            String topic = "iotbroad/iot";
            //消息内容
            String msg = set_msg;

            //消息策略
            int qos = 0;
            //是否保留
            boolean retained = false;
            //发布消息
            publish(msg, topic, qos, retained);

            return true;
        }
        return false;
    }
}
