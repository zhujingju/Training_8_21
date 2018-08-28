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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grasp.training.service.MqttService;
import com.zs.easy.mqtt.EasyMqttService;
import com.zs.easy.mqtt.IEasyMqttCallBack;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public abstract class BaseMqttActivity extends BaseActivity {

    private ContentReceiver mReceiver;
    private MyServiceConn conn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conn = new MyServiceConn();
        bindService(new Intent(this, MqttService.class), conn,
                BIND_AUTO_CREATE);
        doRegisterReceiver();
        if (isConnected()) {
            subscribe();
        } else {

            ha.sendEmptyMessageDelayed(1000, 500);
        }
    }


//    private String myTopic ="iotbroad/iot";

    public abstract String getMyTopic() ;


    /**
     * 注册广播接收者
     */
    private void doRegisterReceiver() {
        mReceiver=new ContentReceiver();
        IntentFilter filter = new IntentFilter(
                "com.grasp.training.service.content");
        registerReceiver(mReceiver, filter);
    }


    public abstract void MyMessageArrived(String message);
    public class ContentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            MyMessageArrived(message);
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
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unbindService(conn);
        if (mReceiver!=null) {
            unregisterReceiver(mReceiver);
        }
    }


    public boolean publish_String(String set_msg) {  //发送消息
        if (isConnected()) {
            //消息主题
            String topic = getMyTopic() ;
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

    /**
     * 订阅主题 这里订阅三个主题分别是"a", "b", "c"
     */
    public void subscribe() {
        String[] topics = new String[]{getMyTopic() };
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

                        ha.sendEmptyMessageDelayed(1000, 3000);
                    }

                    break;

            }
        }
    };

    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
        if(imei!=null&&!imei.equals("")){

        }else{
            long timeStamp = System.currentTimeMillis();
            imei=timeStamp+"";
        }

        return imei;
    }

}
