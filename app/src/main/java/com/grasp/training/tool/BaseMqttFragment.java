package com.grasp.training.tool;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grasp.training.R;
import com.grasp.training.service.MqttService;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zs.easy.mqtt.EasyMqttService;
import com.zs.easy.mqtt.IEasyMqttCallBack;


public abstract class BaseMqttFragment extends Fragment {

    public Context context;
    private ContentReceiver mReceiver;
    private MyServiceConn conn;


//    private String myTopic ="iotbroad/iot";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(getInflate(), container, false);
        context = getActivity();
        conn = new MyServiceConn();
        context.bindService(new Intent(context, MqttService.class), conn,
                context.BIND_AUTO_CREATE);
        doRegisterReceiver();

        init(view);
        return view;
    }

    public abstract int getInflate();

    public abstract void init(View v);



    public  abstract  String getMyTopic();
    public abstract String getMyTopicDing() ;

    /**
     * 注册广播接收者
     */
    private void doRegisterReceiver() {
        mReceiver=new ContentReceiver();
        IntentFilter filter = new IntentFilter(
                "com.grasp.training.service.content");
//        context.registerReceiver(mReceiver, filter);
        LocalBroadcastManager.getInstance(context).registerReceiver(mReceiver, filter);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (isConnected()) {
            subscribe();
        } else {

            ha.sendEmptyMessageDelayed(1000, 0);
        }

    }

    public abstract void MyMessageArrived(String message);
    public class ContentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new Thread(() -> {
                String message = intent.getStringExtra("message");
                MyMessageArrived(message);
            }).start();
        }
    }

    public class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            // service = ((LocalBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            // service = null;
        }
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(conn!=null){
            context.unbindService(conn);
        }

        if (mReceiver!=null) {
//            context.unregisterReceiver(mReceiver);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(mReceiver);
        }
    }


    public void publish_String(final String set_msg) {  //发送消息
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isConnected()) {
                    //消息主题
                    String topic = getMyTopic();
                    //消息内容
                    String msg = set_msg;

                    //消息策略
                    int qos = 0;
                    //是否保留
                    boolean retained = false;
                    //发布消息
                    publish(msg, topic, qos, retained);
                    Log.e("qqq",topic+" 发送消息="+set_msg);

                    return ;
                }
            }
        }).start();

        return ;
    }

    public void publish_String2(final String set_msg, final String topic) {  //发送消息
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isConnected()) {
                    //消息主题
                    //消息内容
                    String msg = set_msg;

                    //消息策略
                    int qos = 0;
                    //是否保留
                    boolean retained = false;
                    //发布消息
                    publish(msg, topic, qos, retained);
                    Log.e("qqq",topic+" 发送消息="+set_msg);

                }
            }
        }).start();

    }

    /**
     * 订阅主题 这里订阅三个主题分别是"a", "b", "c"
     */
    public void subscribe() {
        String[] topics = new String[]{getMyTopicDing()};
        //主题对应的推送策略 分别是0, 1, 2 建议服务端和客户端配置的主题一致
        // 0 表示只会发送一次推送消息 收到不收到都不关心
        // 1 保证能收到消息，但不一定只收到一条
        // 2 保证收到切只能收到一条消息
        int[] qoss = new int[]{0};
        MqttService.mqttService.subscribe(topics, qoss);


    }
    public void subscribe(String s) {
        String[] topics = new String[]{s};
        //主题对应的推送策略 分别是0, 1, 2 建议服务端和客户端配置的主题一致
        // 0 表示只会发送一次推送消息 收到不收到都不关心
        // 1 保证能收到消息，但不一定只收到一条
        // 2 保证收到切只能收到一条消息
        int[] qoss = new int[]{0};
        MqttService.mqttService.subscribe(topics, qoss);


    }

    /**
     * 发布消息
     */
    public void publish(String msg, String topic, int qos, boolean retained) {
        MqttService.mqttService.publish(msg, topic, qos, retained);
    }

    /**
     * 判断服务是否连接
     */
    public boolean isConnected() {
        if(MqttService.mqttService==null){
            return false;
        }
        return MqttService.mqttService.isConnected();
    }


    Handler ha=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    if (isConnected()) {
                        subscribe();
                    } else {

                        ha.sendEmptyMessageDelayed(1000, 500);
                    }

                    break;

            }
        }
    };

}
