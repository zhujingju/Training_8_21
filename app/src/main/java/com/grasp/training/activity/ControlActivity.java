package com.grasp.training.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import com.grasp.training.service.MqttService;
import com.grasp.training.tool.BaseActivity;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.MyThread;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.zs.easy.mqtt.EasyMqttService;
import com.zs.easy.mqtt.IEasyMqttCallBack;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ControlActivity extends BaseMqttActivity {
    @BindView(R.id.control_im)
    ImageView img;
    @BindView(R.id.control_sx)
    ImageView controlSx;
    @BindView(R.id.control_xs)
    TextView xs;
    @BindView(R.id.control_bs)
    TextView bs;
    @BindView(R.id.control_mc)
    TextView mc;
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

    public static final int MD_STOP = 0; // 停止
    public static final int MD_LEFT = 11; // 左
    public static final int MD_RIGHT = 12; // 右
    public static final int MD_UP = 9; // 上
    public static final int MD_DOWN = 10; // 下

    private PlayerClient playClient;
    private MyApplication appMain;
    private PlayerCore pc;
    private Context context;
    private String Uid="";
    private String myTopic = MqttService.myTopicRobot;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    public int setLayoutId() {
        return R.layout.control;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        Intent in=getIntent();
        Uid=in.getStringExtra("uid");

        context = this;
        mc.setText(Uid);
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

        initePlayCore();
//        ha.sendEmptyMessageDelayed(2000,1000);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);

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

    private final int API_OLD = 0;
    private final int API_NEW = 1;
    @OnClick({R.id.control_sm,R.id.control_fh,R.id.control_sx, R.id.control_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.control_sm: //扫码登录
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    startScan(API_OLD);
                } else {
                    startScan(API_NEW);
                }
                break;
            case R.id.control_fh:
                finish();
                break;
            case R.id.control_sx:
                if (!Uid.equals("")) {
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

                } else {
                    Toast.makeText(getContext(), "绑定设备后再重试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.control_stop:
                push_move(MD_STOP);
//                ping_go("192.168.31.236");
                break;
        }
    }


    private void startScan(int api) {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra("newAPI", api == API_NEW);
//            intent.putExtra("codeType", getCodeType());
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, api);
        }
    }


    private boolean ping_zt=false;
    private String mIp="";
//    private Process process;
//    private  final boolean ping(String s) {
//
//        String result = null;
//
//        try {
//
//            String ip = s;//
//
//            process = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);//ping3次
//
//
//// PING的状态
//
//            int status = process.waitFor();
//
//            if (status == 0) {
//
//                result = "successful~";
//
//                return true;
//
//            } else {
//
//                result = "failed~ cannot reach the IP address";
//
//            }
//
//        } catch (IOException e) {
//
//            result = "failed~ IOException";
//
//        } catch (InterruptedException e) {
//
//            result = "failed~ InterruptedException";
//
//        } finally {
//
//            Log.i("TTT", "result = " + result);
//
//        }
//
//        return false;
//
//    }
//
//
//    public void ping_go(final String s){
//        Log.e("control","ping_go");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ping_zt=ping(s);
////                        Log.e("ip","isIpReachable="+ping("192.168.10.15"));
////                        Log.e("ip","isIpReachable="+ping("192.168.31.236"));
//            }
//        }).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(3000);
//                    if(process!=null){
//                        process.destroy();
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
////                    push_ping(ping_zt);
//                Log.e("control","ping_zt="+ping_zt+"  "+process);
//            }
//        }).start();
//
//    }


    private boolean an_zt = true;

    private class PicOnLongClick implements View.OnTouchListener {  //长按

        private int action = 0;

        public boolean onTouch(View view, MotionEvent event) {
            action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    Log.e("eee", "ACTION_DOWN");
//                    if (!an_zt) {
//                        return false;
//                    }
                    switch (view.getId()) {

                        case R.id.control_up:
                            ha.removeMessages(4000);
                            push_move(MD_UP);

                            break;
                        case R.id.control_down:
                            ha.removeMessages(4000);
                            push_move(MD_DOWN);

                            break;
                        case R.id.control_left:
                            ha.removeMessages(4000);
                            push_move(MD_LEFT);
                            break;
                        case R.id.control_right:
                            ha.removeMessages(4000);
                            push_move(MD_RIGHT);
                            break;

                        case R.id.control_tou_left:
                            ha.removeMessages(3000);
                            push_head(MD_LEFT);
                            break;
                        case R.id.control_tou_right:
                            ha.removeMessages(3000);
                            push_head(MD_RIGHT);
                            break;
                        case R.id.control_tou_up:
                            ha.removeMessages(3000);
                            push_head(MD_UP);
                            break;
                        case R.id.control_tou_down:
                            ha.removeMessages(3000);
                            push_head(MD_DOWN);
                            break;
                    }
//                    an_zt = false;
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(500);
//                                an_zt = true;
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
                    break;

                case MotionEvent.ACTION_UP:
                    switch (view.getId()) {
                        case R.id.control_up:
//                            push_move(MD_STOP);
                            ha.sendEmptyMessageDelayed(4000,500);
                            break;
                        case R.id.control_down:
//                            push_move(MD_STOP);
                            ha.sendEmptyMessageDelayed(4000,500);
                            break;
                        case R.id.control_left:
//                            push_move(MD_STOP);
                            ha.sendEmptyMessageDelayed(4000,500);
                            break;
                        case R.id.control_right:
//                            push_move(MD_STOP);
                            ha.sendEmptyMessageDelayed(4000,500);
                            break;

                        case R.id.control_tou_left:
//                            push_head(MD_STOP);
                            ha.sendEmptyMessageDelayed(3000,500);
                            break;
                        case R.id.control_tou_right:
//                            push_head(MD_STOP);
                            ha.sendEmptyMessageDelayed(3000,500);
                            break;
                        case R.id.control_tou_up:
//                            push_head(MD_STOP);
                            ha.sendEmptyMessageDelayed(3000,500);
                            break;
                        case R.id.control_tou_down:
//                            push_head(MD_STOP);
                            ha.sendEmptyMessageDelayed(3000,500);
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
        if (!(Uid ).equals("")) {
            playVideo();
        }
    }

    public void playVideo() {
        Constants.UMID = Uid;
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
        if (!(Uid).equals("")) {
            playVideo();

        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        push_move(MD_STOP);
        push_head(MD_STOP);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(null);
        ha.removeMessages(1000);
        ha.removeMessages(2000);
        ha.removeMessages(3000);
        ha.removeMessages(4000);
        ha.removeMessages(222);
        if(networkChangeReceiver!=null){
            unregisterReceiver(networkChangeReceiver);
        }

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
        Log.d("qqq", " new StateThread().start();");
        new StateThread().start();
        super.onResume();
    }


    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (pc != null) {
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
     */
    public static boolean isRun = true;

    class StateThread extends Thread {

        @Override
        public void run() {

            try {
                Log.d("qqq", "StateThread " + isRun);
                while (isRun) {
                    Log.d("qqq", "StateThread " + isRun);
                    Thread.sleep(500);
                    Message msg = new Message();
                    msg.what = SHOW_STATE;
                    msg.arg1 = pc.PlayCoreGetCameraPlayerState();
                    if (pc.GetIsSnapVideo()) {
                        msg.arg2 = 1;
                    }
                    // Log.w("state", "state: " + msg.arg1);
                    handler.sendMessage(msg);
                    Log.d("qqq", "StateThread 111" + isRun);
                    TAlarmFrame tAlarmFrame = pc.CameraGetAlarmInfo();
                    if (tAlarmFrame != null) {
                        handler.sendMessage(Message.obtain(handler,
                                ALARM_STATE, tAlarmFrame));
                        Log.d("qqq", "StateThread 2 " + isRun);
                    }
                    Log.d("qqq", "StateThread 111" + isRun);
                }
            } catch (Exception e) {
                // TODO: handle exception
                Log.d("qqq", "StateThread Exception" + isRun);
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
            Log.d("handleMessage111", msg.what + "");
            if (msg.what == SHOW_STATE) {
                Log.d("qqq", GetDescription(context, msg.arg1));

                if (controlSx != null && GetDescription(context, msg.arg1).equals("连接失败")) {
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

                if (controlSx != null) {
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
            } else if (msg.what == 789) {
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
                Log.d("ffff", state + "");
                des = con.getString(R.string.ready);
                if (xs != null)
                    xs.setText(con.getString(R.string.ready));
                break;
            case 1:
                Log.d("ffff", state + "");
                des = con.getString(R.string.connecting);
                if (xs != null)
                    xs.setText(con.getString(R.string.connecting));
                break;
            case 2:
                Log.d("ffff", state + "");
                des = con.getString(R.string.playing);
                if (controlSx != null) {
                    controlSx.setVisibility(View.GONE);
                    xs.setText(con.getString(R.string.playing));
                    xs.setVisibility(View.GONE);
                }

                break;
            case 3:
                Log.d("ffff", state + "");
                des = con.getString(R.string.connect_fail);

                if (controlSx != null) {
//                    Toast.makeText(getContext(),R.string.connect_fail,Toast.LENGTH_SHORT).show();
                    controlSx.setVisibility(View.VISIBLE);
                }
                if (xs != null)
                    xs.setText(con.getString(R.string.connect_fail));
                break;
            case 4:
                Log.d("ffff", state + "");
                des = con.getString(R.string.stop);

                if (controlSx != null) {
//                    Toast.makeText(getContext(),R.string.stop,Toast.LENGTH_SHORT).show();
                    controlSx.setVisibility(View.VISIBLE);
                    xs.setText(con.getString(R.string.stop));
                }

                break;
            case 7:
                Log.d("ffff", state + "");
                des = con.getString(R.string.stop);
//                Toast.makeText(getContext(),R.string.stop,Toast.LENGTH_SHORT).show();
                if (controlSx != null) {
                    controlSx.setVisibility(View.VISIBLE);
                    xs.setText(con.getString(R.string.stop));
                }
                break;
            case SDKError.NPC_D_MPI_MON_ERROR_USERID_ERROR:
                Log.d("ffff", state + "");
                des = con.getString(R.string.usererro);

                if (controlSx != null) {
//                    Toast.makeText(getContext(),R.string.usererro,Toast.LENGTH_SHORT).show();
                    controlSx.setVisibility(View.VISIBLE);
                    xs.setText(con.getString(R.string.usererro));
                }
                break;
            case SDKError.NPC_D_MPI_MON_ERROR_USERPWD_ERROR:
                Log.d("ffff", state + "");
                des = con.getString(R.string.passworderro);

                if (controlSx != null) {
//                    Toast.makeText(getContext(),R.string.passworderro,Toast.LENGTH_SHORT).show();
                    controlSx.setVisibility(View.VISIBLE);
                    xs.setText(con.getString(R.string.passworderro));
                }
                break;
            case 10:
                Log.d("ffff", state + "");
                des = "缓冲中";
                if (xs != null)
                    xs.setText("缓冲中");
                break;
            case SDKError.NPC_D_MPI_MON_ERROR_REJECT_ACCESS:
                Log.d("ffff", state + "");
                des = con.getString(R.string.NPC_D_MPI_MON_ERROR_REJECT_ACCESS);

                if (controlSx != null) {
//                    Toast.makeText(getContext(),R.string.NPC_D_MPI_MON_ERROR_REJECT_ACCESS,Toast.LENGTH_SHORT).show();
                    controlSx.setVisibility(View.VISIBLE);
                    xs.setText(con.getString(R.string.NPC_D_MPI_MON_ERROR_REJECT_ACCESS));
                }
                break;
        }
        return des;

    }



    public void push_move(int var) {


        if (Uid.equals("")) {
            return;
        }

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "move");
            jsonObject.put("var", var);
            jsonObject.put("uid", Uid);
            String js = jsonObject.toString();
            if(ping_zt){
                new MyThread(js,mIp).start();
                return;
            }
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }


    public void push_head(int var) {
        if (Uid.equals("")) {
            return;
        }

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "head");
            jsonObject.put("var", var);
            jsonObject.put("uid", Uid);
            String js = jsonObject.toString();
            if(ping_zt){
                new MyThread(js,mIp).start();
                return;
            }
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }



    public void push_ip() {
        if (Uid.equals("")) {
            return;
        }
        String ip=getIp(context);
        if(ip==null||ip.equals("")){
            return;
        }
        Log.e("ip", Uid+" ip="+ip);
        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "ip");
            jsonObject.put("ip", ip);
            jsonObject.put("uid", Uid);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }


    public  String getIp(final Context context) {
        String ip = null;
        ConnectivityManager conMan = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        // mobile 3G Data Network
//        android.net.NetworkInfo.State mobile = conMan.getNetworkInfo(
//                ConnectivityManager.TYPE_MOBILE).getState();
        // wifi
        android.net.NetworkInfo.State wifi = conMan.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState();

        // 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
//        if (mobile == android.net.NetworkInfo.State.CONNECTED
//                || mobile == android.net.NetworkInfo.State.CONNECTING) {
//            ip =  getLocalIpAddress();
//        }
        if (wifi == android.net.NetworkInfo.State.CONNECTED
                || wifi == android.net.NetworkInfo.State.CONNECTING) {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip =(ipAddress & 0xFF ) + "." +
                    ((ipAddress >> 8 ) & 0xFF) + "." +
                    ((ipAddress >> 16 ) & 0xFF) + "." +
                    ( ipAddress >> 24 & 0xFF) ;
        }
        return ip;

    }








    @Override
    protected void onStart() {
        super.onStart();
//        ha.sendEmptyMessageDelayed(1000, 500);
    }


    Handler ha = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    if (isConnected()) {

                    } else {
//                        mqttService.connect(iEasyMqttCallBack);
                        ha.sendEmptyMessageDelayed(1000, 3000);
                    }

                    break;
                case 2000:
                    Log.e("control", "push_ip 2000 "+isConnected());
                    if (isConnected()) {
                        push_ip();
                    } else {
//                        mqttService.connect(iEasyMqttCallBack);
                        ha.sendEmptyMessageDelayed(2000, 1500);
                    }

                    break;
                case 3000:
                    push_head(MD_STOP);
                    break;
                case 4000:
                    push_move(MD_STOP);
                    break;
                case 222:
                    Log.e("control","ping_zt="+ping_zt);
                    if(bs==null){
                        return;
                    }
                    if(ping_zt){

                        bs.setVisibility(View.VISIBLE);
                    }else{
                        bs.setVisibility(View.GONE);
                    }

                    break;
            }
        }
    };

    @Override
    public String  getMyTopic() {
        return myTopic;
    }

    @Override
    public String getMyTopicDing() {
        return myTopic;
    }

    @Override
    public String getSid() {
        return "";
    }

    @Override
    public void MyMessageArrived(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("control", "  message= " + message);
                try {
                    JSONObject jsonF;
                    Message me;
                    String js = "";
                    String channel_0 = "";
                    int var = 0;
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.getString("cmd");
                    String mSid = jsonObject.optString("uid", "");
                    if (!mSid.equals(Uid)) {
                        return;
                    }

                    switch (cmd) {
                        case "ip_ok":
                            String ip = jsonObject.optString("ip","");
                            boolean ping =jsonObject.optBoolean("ping",false);
                            Log.e("control","ip="+ip);
                            if(!ip.equals("")){
//                                        ping_go(ip);
                                ping_zt=ping;
                                mIp=ip;
                                ha.sendEmptyMessageDelayed(222,0);

                            }

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


    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "网络状态改变", Toast.LENGTH_SHORT).show();
            ha.sendEmptyMessageDelayed(2000,1000);
        }
    }

}
