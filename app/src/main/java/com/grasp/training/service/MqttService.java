package com.grasp.training.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.zs.easy.mqtt.EasyMqttService;
import com.zs.easy.mqtt.IEasyMqttCallBack;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class MqttService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        // The service is being created
        // 创建服务
        Log.e("qqq", "onCreate 服务");
        ip_zt = new HashMap<>();
        sid_ip = new HashMap<>();
        buildEasyMqttService();
        initIEasyMqttCallBack();
        connect();
    }

    //把我定义的中间人对象返回

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }


    /**
     * 发送广播
     */
    protected void sendContentBroadcast(String message) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("com.grasp.training.service.content");
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }


    public class MyBinder extends Binder {

        public MqttService getService() {
            return MqttService.this;
        }

    }


    public static EasyMqttService mqttService;

    public static HashMap<String, Boolean> ip_zt;
    public static HashMap<String, String> sid_ip;

    /**
     * 连接Mqtt服务器
     */
    public void connect() {
        mqttService.connect(iEasyMqttCallBack);
    }

    public IEasyMqttCallBack iEasyMqttCallBack;

    public void initIEasyMqttCallBack() {
        iEasyMqttCallBack = new IEasyMqttCallBack() {
            @Override
            public void messageArrived(final String topic, final String message, final int qos) {
                //推送消息到达
                Log.e("service", "service messageArrived  message= " + message);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(message);
                            String cmd = jsonObject.getString("cmd");
                            switch (cmd) {
                                case "wifi_socket_ping_ack":
                                    String f_ip=jsonObject.optString("ip","");
                                    String f_sid=jsonObject.optString("sid","");
                                    Log.e("qqq","wifi_socket_ping_ack "+f_ip+ "  "+f_sid);
                                    if(!f_ip.equals("")){
                                        sid_ip.put(f_sid,f_ip);
                                        String ip = getIp(MqttService.this);
                                        if (ip != null && !ip.equals("")) {
                                            ping_go(f_ip,f_sid);
                                        }
                                    }


//                        Tcp_zt=true;
//                        ip_zt.put("ip",true);
//                        sid_ip.put("sid","ip");
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                sendContentBroadcast(message);
            }

            @Override
            public void connectionLost(Throwable arg0) {
                //连接断开
                Log.e("qqq", "connectionLost");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken arg0) {
                //发送成功
                Log.e("qqq", "deliveryComplete");

            }

            @Override
            public void connectSuccess(IMqttToken arg0) {
                //连接成功
                Log.e("qqq", "connectSuccess");
//                if (isConnected()) {
//                    subscribe();
//                }
            }

            @Override
            public void connectFailed(IMqttToken arg0, Throwable arg1) {
                //连接失败
                Log.e("qqq", "connectFailed");
            }
        };
    }


    private Process process;

    private final boolean ping(String s) {

        String result = null;

        try {

            String ip = s;//

            process = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);//ping3次


// PING的状态

            int status = process.waitFor();

            if (status == 0) {

                result = "successful~";

                return true;

            } else {

                result = "failed~ cannot reach the IP address";

            }

        } catch (IOException e) {

            result = "failed~ IOException";

        } catch (InterruptedException e) {

            result = "failed~ InterruptedException";

        } finally {

            Log.i("TTT", "result = " + result);

        }

        return false;

    }

    boolean ping_zt = false;

    public void ping_go(final String s, final String sid) {
        Log.e("qqq", "ping_go");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ping_zt = ping(s);
//                        Log.e("ip","isIpReachable="+ping("192.168.10.15"));
//                        Log.e("ip","isIpReachable="+ping("192.168.31.236"));
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    if (process != null) {
                        process.destroy();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ip_zt.put(s+sid,ping_zt);
                Log.e("qqq", "ping_zt=" + ping_zt + "  " + process);
            }
        }).start();

    }


    public String getIp(final Context context) {
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
            ip = (ipAddress & 0xFF) + "." +
                    ((ipAddress >> 8) & 0xFF) + "." +
                    ((ipAddress >> 16) & 0xFF) + "." +
                    (ipAddress >> 24 & 0xFF);
        }
        return ip;

    }

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
                .clientId(getIMEI(this))
                //mqtt服务器地址 格式例如：tcp://10.0.261.159:1883
                .serverUrl("tcp://192.168.31.60:3000")  //贤贵
////                .serverUrl("tcp://broker.hivemq.com:1883")
                //心跳包默认的发送间隔
                .keepAliveInterval(20)
                //构建出EasyMqttService 建议用application的context
                .bulid(this.getApplicationContext());
    }

    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
        if (imei != null && !imei.equals("")) {

        } else {
            long timeStamp = System.currentTimeMillis();
            imei = timeStamp + "";
        }

        return imei+"S1";
    }


    @Override
    public void onDestroy() {
        Log.e("qqq", "onDestroy 服务");
        super.onDestroy();
        disconnect();
        close();
    }

    private String myTopic = "iotbroad/iot";

    public String getMyTopic() {
        return myTopic;
    }

    public void setMyTopic(String myTopic) {
        this.myTopic = myTopic;
    }


}
