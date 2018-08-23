package com.grasp.training.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;

import com.zs.easy.mqtt.EasyMqttService;
import com.zs.easy.mqtt.IEasyMqttCallBack;

public abstract class BaseMqttFragmentActivity extends FragmentActivity {

    public EasyMqttService mqttService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildEasyMqttService();
        initIEasyMqttCallBack();
        connect();
    }

    /**
     * 连接Mqtt服务器
     */
    public abstract void connect() ;
    /**
     * 判断服务是否连接
     */
    public boolean isConnected() {
        return mqttService.isConnected();
    }

    /**
     * 发布消息
     */
    public void publish(String msg, String topic, int qos, boolean retained) {
        mqttService.publish(msg, topic, qos, retained);
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        mqttService.disconnect();
    }

    /**
     * 关闭连接
     */
    public void close() {
        mqttService.close();
    }

    /**
     * 订阅主题 这里订阅三个主题分别是"a", "b", "c"
     */
    public void subscribe() {
        String[] topics = new String[]{myTopic};
        //主题对应的推送策略 分别是0, 1, 2 建议服务端和客户端配置的主题一致
        // 0 表示只会发送一次推送消息 收到不收到都不关心
        // 1 保证能收到消息，但不一定只收到一条
        // 2 保证收到切只能收到一条消息
        int[] qoss = new int[]{0};
        mqttService.subscribe(topics, qoss);


    }


    /**
     * 构建EasyMqttService对象
     */
    public void buildEasyMqttService() {
        mqttService = new EasyMqttService.Builder()
                //设置自动重连
                .autoReconnect(true)
                //设置不清除回话session 可收到服务器之前发出的推送消息
                .cleanSession(false)
                //唯一标示 保证每个设备都唯一就可以 建议 imei
                .clientId(getIMEI(BaseMqttFragmentActivity.this))
                //mqtt服务器地址 格式例如：tcp://10.0.261.159:1883
                .serverUrl("tcp://broker.hivemq.com:1883")
                //心跳包默认的发送间隔
                .keepAliveInterval(20)
                //构建出EasyMqttService 建议用application的context
                .bulid(this.getApplicationContext());
    }

    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();

        return imei;
    }

    public boolean publish_String(String set_msg) {  //发送消息
        if (isConnected()) {
            //消息主题
            String topic = myTopic;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
        close();
        ha.removeMessages(1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isConnected()) {

        } else {
            ha.sendEmptyMessageDelayed(1000, 500);
        }
    }

    /**
     * 连接Mqtt服务器
     */
    public IEasyMqttCallBack iEasyMqttCallBack;
    public abstract void initIEasyMqttCallBack();

    Handler ha=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    if (isConnected()) {

                    } else {
                        mqttService.connect(iEasyMqttCallBack);
                        ha.sendEmptyMessageDelayed(1000, 3000);
                    }

                    break;
            }
        }
    };

    private String myTopic ="iotbroad/iot";

    public String getMyTopic() {
        return myTopic;
    }

    public void setMyTopic(String myTopic) {
        this.myTopic = myTopic;
    }

}
