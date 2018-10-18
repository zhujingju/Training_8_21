package com.grasp.training.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.fragmet.SmartHomeMain;
import com.grasp.training.receiver.MyReceiver;
import com.grasp.training.tool.BaseMqttFragment;
import com.grasp.training.tool.EquipmentData;
import com.grasp.training.tool.Goods;
import com.grasp.training.tool.MqttEquipment;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.grasp.training.tool.Tool;
import com.zs.easy.mqtt.EasyMqttService;
import com.zs.easy.mqtt.IEasyMqttCallBack;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MqttService extends Service {

    public static boolean appZt=false;
    public final static String MqttService1 = "MqttService1";
    public final static String MqttService2 = "MqttService2";
    private static boolean fi_zt=false;

    @Override
    public void onCreate() {
        super.onCreate();
        // The service is being created
        // 创建服务
        Log.e("qqq", "onCreate 服务");
        if(!fi_zt){
            Log.e("qqq", "onCreate 启动服务");
            buildEasyMqttService();
            initIEasyMqttCallBack();
            connect();
            fi_zt=true;
        }
        doRegisterReceiver();
        equimentHandler.sendEmptyMessageDelayed(4000, 5000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("qqq", "onStartCommand 服务");
        return START_STICKY_COMPATIBILITY;
//return super.onStartCommand(intent, flags, startId);
    }

    //把我定义的中间人对象返回


    @Override
    public void onStart(Intent intent, int startId) {
// 再次动态注册广播
        Log.e("qqq", "onStart服务");
        IntentFilter localIntentFilter = new IntentFilter("android.intent.action.USER_PRESENT");
        localIntentFilter.setPriority(Integer.MAX_VALUE);// 整形最大值
        MyReceiver searchReceiver = new MyReceiver();
        registerReceiver(searchReceiver, localIntentFilter);

        super.onStart(intent, startId);
    }


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
                                case "wifi_equipment_ping_ack":
                                    String f_ip = jsonObject.optString("ip", "");
                                    String f_sid = jsonObject.optString("sid", "");
                                    Log.e("qqq", "wifi_equipment_ping_ack " + f_ip + "  " + f_sid);
                                    if (!f_ip.equals("")) {
                                        sid_ip.put(f_sid, f_ip);
                                        String ip = getIp(MqttService.this);
                                        if (ip != null && !ip.equals("")) {
                                            ping_go(f_ip, f_sid);
                                        }
                                    }


//                        Tcp_zt=true;
//                        ip_zt.put("ip",true);
//                        sid_ip.put("sid","ip");
                                    break;

                                case "querydevicebyuser_ok":
                                    Log.e("qqq", "home messs=" + "querydevicebyuser_ok");
//                                    SharedPreferencesUtils.setParam(this, "SmartHomeMain", message);
                                    list = new ArrayList<>();
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        String sid = jsonObject1.optString("sid", "");//名称
                                        String type = jsonObject1.optString("type", "");//类型
                                        String thumbnail = jsonObject1.optString("thumbnail", ""); //预览图
                                        String state = jsonObject1.optString("state", "");
                                        String roomid = jsonObject1.optString("roomid", "");
                                        String alive = jsonObject1.optString("alive", "0");
                                        String dname = jsonObject1.optString("dname", "");
                                        if (roomid.equals("null")) {
                                            roomid = "";
                                        }
                                        if (thumbnail.equals("null")) {
                                            thumbnail = "";
                                        }
                                        if (state.equals("null")) {
                                            state = "";
                                        }

                                        Goods goods = new Goods();
                                        goods.setSid(sid);
                                        goods.setWz(roomid);
                                        goods.setIm_url(thumbnail);
                                        goods.setAdd_zt(false);
                                        goods.setType(type);
                                        if (state.equals("on")) {
                                            goods.setDy(true);
                                        } else {
                                            goods.setDy(false);
                                        }

                                        goods.setName(dname);
//                                Log.e("qqq","oldMap="+newMap);
                                        if (oldMap != null) {
                                            if (oldMap.get(sid) != null) {
                                                goods.setJh_zt(true);
                                            } else {
                                                goods.setJh_zt(false);
                                            }
                                        }
                                        if (oldState != null) {
                                            if (oldState.get(sid) != null) {
                                                goods.setDy(oldState.get(sid));
                                            }
                                        }

                                        list.add(goods);

                                    }
                                    Goods goods = new Goods();
                                    goods.setAdd_zt(true);
                                    list.add(goods);
                                    equimentHandler.sendEmptyMessageDelayed(5000, 0);
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
                ip_zt.put(s + sid, ping_zt);
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
//                .serverUrl("tcp://192.168.31.60:3000")  //贤贵
                .serverUrl("tcp://192.168.1.3:3000")  //贤贵
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

        return imei + "S1";
    }


    @Override
    public void onDestroy() {
        Log.e("qqq", "onDestroy 服务");
        Map_del();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        Intent localIntent = new Intent();
        localIntent.setClass(this, MqttService.class); // 销毁时重新启动Service
        this.startService(localIntent);
//        Log.e("qqq", "sid_ip=" + sid_ip);
//        spUtils.setMap(MqttService2, sid_ip);
//
//        HashMap<String,String> hashMap=new HashMap();
//        for(Map.Entry<String, Boolean> entry: ip_zt.entrySet())
//        {
//            if(entry.getValue()){
//                hashMap.put(entry.getKey(),"on");
//            }else{
//                hashMap.put(entry.getKey(),"off");
//            }
//
//        }

//        spUtils.setMap(MqttService1, hashMap);

        super.onDestroy();
//        disconnect();
//        close();
    }

    private String myTopic = "iotbroad/iot/device";

    public String getMyTopic() {
        return myTopic;
    }

    public void setMyTopic(String myTopic) {
        this.myTopic = myTopic;
    }


    public <Srring, V> void saveMap(HashMap<String, V> map, String mz) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, V> entry : map.entrySet()) {
            try {
                jsonObject.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SharedPreferencesUtils.setParam(this, mz, jsonObject.toString());
    }


    public <Srring, V> HashMap<String, V> putMap(String mz) {
        HashMap hashMap = new HashMap();
        String s = SharedPreferencesUtils.getParam(this, mz, "").toString();
        if (!s.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                Iterator keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    V var = (V) jsonObject.opt(key);
                    hashMap.put(key, var);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return hashMap;

    }


    private List<Goods> list;

    Handler equimentHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000://改变单个
//                    Log.e("qqq","mqttEquipment message= "+1000);
                    for (int i = 0; i < list.size(); i++) {
                        String sid = list.get(i).getSid();
                        if (sid == null) {
                            break;
                        }
                        if (sid.equals(msg.obj.toString())) {
                            if (newState.get(sid) != null) {
                                list.get(i).setDy(newState.get(sid));
                            }
                            list.get(i).setJh_zt(true);
                            return;
                        }
                    }


                    break;
                case 2000:
//                    if (oldMqttEquipmentMap == null) {
//                        oldMqttEquipmentMap = new HashMap<>();
//                    }
                    oldMap = newMap;
                    Log.e("qqq", "oldMap.size()=" + oldMap.size() + " newMap.size()= " + newMap.size());
                    oldState = newState;
                    for (int i = 0; i < list.size(); i++) {

                        String sid = list.get(i).getSid();
                        if (sid == null) {
                            break;
                        }
                        if (oldMap.get(sid) != null) {
                            list.get(i).setJh_zt(true);
//                            if (MqttEquipmentMap.get(sid) != null) {
//                                oldMqttEquipmentMap.put(sid, MqttEquipmentMap.get(sid));
//                            }
                        } else {
                            list.get(i).setJh_zt(false);
                        }
                        if (oldState.get(sid) != null) {
                            list.get(i).setDy(newState.get(sid));
                        }

                    }
                    if (manager == null) {
                        dataNotification();
                    }
                    sxNotification();
                    break;
                case 3000:
                    Log.e("qqq", "goods  service" + 3000);
                    equimentHandler.removeMessages(3000);
//                    if(!appZt){
                        getJh();
//                    }
//                    equimentHandler.sendEmptyMessageDelayed(3000, 5 * 60 * 1000);
                    break;
                case 5000:
                    equimentHandler.removeMessages(3000);
                    equimentHandler.removeMessages(5000);
                    equimentHandler.sendEmptyMessageDelayed(3000, 0);

                    break;
                case 4000:
                    equimentHandler.removeMessages(4000);
                    equimentHandler.removeMessages(3000);
                    equimentHandler.removeMessages(5000);
                    if(!appZt){
                        Log.e("qqq", "goods  service " + 4000);
                        String s = (String) SharedPreferencesUtils.getParam(MqttService.this, MyApplication.NAME_USER, "");
                        if (!s.equals("")) {
                            dataListview(s);
                        }
                    }


                    equimentHandler.sendEmptyMessageDelayed(4000, 60 * 1000 * 5);
                    break;
            }
        }
    };


    public void dataListview(String s) {  //获取list数据
        Log.e("qqq", "goods  service dataListview" );
        subscribe();
        push_read(s);
    }


    public void push_read(String s) {  //获取设备列表
//        Log.e("qqq","消息 push_read");
        try {
            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "querydevicebyuser");
            jsonObject.put("uname", s);
            jsonObject.put("clientid", Tool.getIMEI(this));
            String js = jsonObject.toString();

            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
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
                    Log.e("qqq", topic + " 发送消息=" + set_msg);

                    return;
                }
            }
        }).start();

        return;
    }


    HashMap<String, String> oldMap;   //备份的map
    HashMap<String, String> newMap;   //使用的map
    HashMap<String, Boolean> oldState;   //备份的map
    HashMap<String, Boolean> newState;   //使用的map
    HashMap<String, MqttEquipment> MqttEquipmentMap;  //存放MqttEquipment的map
    //    HashMap<String, MqttEquipment> oldMqttEquipmentMap;  //放菜单中的数据
    Goods goods;

    public void getJh() {
//        int a=0;
//        if(a==0){
//            return;
//        }


        if (MqttEquipmentMap == null) {
            MqttEquipmentMap = new HashMap<>();
        }

//        if(newMap==null){
        newMap = new HashMap<>();
        newState = new HashMap<>();
//        }
        if (oldMap == null) {
            oldMap = new HashMap<>();
        }
        if (oldState == null) {
            oldState = new HashMap<>();
        }
        for (int i = 0; i < list.size(); i++) {
            goods = list.get(i);

            if (goods.getSid() == null) {
                break;
            }
            Log.e("qqq", "mqttEquipment " + goods.getSid());
            if (MqttEquipmentMap.get(goods.getSid()) == null) {
                String type = goods.getType();
                String myTopic = "iotbroad/iot/" + type + "/" + goods.getSid();
                String myTopicding = "iotbroad/iot/" + type + "_ack/" + goods.getSid();
                String sid = goods.getSid();
                String url = goods.getIm_url();
                MqttEquipment mqttEquipment = new MqttEquipment(this, sid, type, myTopic, myTopicding, url) {
                    @Override
                    public void MyMessageArrived(final String message) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jsonObject = new JSONObject(message);
                                    String cmd = jsonObject.getString("cmd");
                                    String mSid = jsonObject.optString("sid", "");  //设备号
                                    if (!mSid.equals(getSid())) {
                                        return;
                                    }
                                    String type = getType();
//                                    Log.e("qqq","mqttEquipment sid= "+getSid());
//                                    Log.e("qqq","mqttEquipment type= "+type);
                                    if (cmd.equals("wifi_" + type + "_ack")) {
//                                        Log.e("qqq","mqttEquipment message= "+message);
                                        newMap.put(getSid(), type);
                                        oldMap.put(getSid(), type);
                                        String state = jsonObject.optString("state", "");  //
                                        boolean state_zt;
                                        if (state.equals("on")) {
                                            state_zt = true;
                                        } else {
                                            state_zt = false;
                                        }
                                        newState.put(getSid(), state_zt);
                                        oldState.put(getSid(), state_zt);
                                        Message m = new Message();
                                        m.what = 1000;
                                        m.obj = getSid();
                                        equimentHandler.sendMessage(m);
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                };
//                Log.e("qqq","mqttEquipment cun "+mqttEquipment.getSid()+" "+mqttEquipment.getType());

                mqttEquipment.setName(goods.getName());

                MqttEquipmentMap.put(goods.getSid(), mqttEquipment);
//                mqttEquipment.onDestroy();

            }
        }
        //发送
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (MqttEquipmentMap == null) {

                    return;
                }
                if (MqttEquipmentMap.size() == 0) {

                    return;
                }
                for (HashMap.Entry<String, MqttEquipment> entry : MqttEquipmentMap.entrySet()) {

                    MqttEquipment e = entry.getValue();
//                    Log.e("qqq","goods fa "+e.getSid()+" "+e.getType());
                    String myTopicding = "iotbroad/iot/" + e.getType() + "_ack/" + e.getSid();
                    e.subscribe(myTopicding);
                    e.publish_String(push_read(e.getType(), e.getSid()));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        }).start();


        equimentHandler.sendEmptyMessageDelayed(2000, 500 * MqttEquipmentMap.size() + 2000);
    }

    public void Map_del() {

        if (MqttEquipmentMap != null) {
            for (HashMap.Entry<String, MqttEquipment> entry : MqttEquipmentMap.entrySet()) {
                MqttEquipment e = entry.getValue();
                e.onDestroy();
            }
        }

        if (manager != null) {
            manager.cancel(3);
        }
        if (in_manager != null) {
            in_manager.cancel(6);
        }
        equimentHandler.removeMessages(4000);

    }

    public String push_read(String type, String sid) {  //获取状态

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_" + type + "_read");
            jsonObject.put("sid", sid);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

    }


    private RemoteViews contentView;
    private Notification notification;
    private NotificationManager manager;

    public void dataNotification() {

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification.Builder(this).setSmallIcon(R.drawable.icon).build();
//        notification.flags|= Notification.FLAG_ONGOING_EVENT;
        sendNotification();
    }

    /**
     * 发送通知
     */
    public void sendNotification() {
        Log.e("qqq", "dataNotification ");
        contentView = new RemoteViews(this.getPackageName(), R.layout.data_notification);
        notification.contentView = contentView;
//        manager.notify(3, notification);
    }

    public static final String BT_REFRESH_ACTION = "BT_REFRESH_ACTION";
    public static final String COLLECTION_VIEW_ACTION = "COLLECTION_VIEW_ACTION";
    public static final String COLLECTION_VIEW_EXTRA = "COLLECTION_VIEW_EXTRA";

    private void sxNotification() {

        if (MqttEquipmentMap != null && contentView != null && notification != null && manager != null) {
            Log.e("qqq", "oldMap.s=" + oldMap.size());
//            if (MqttEquipmentMap.size() == 0) {
//                return;
//            }
            int i = 0;
            contentView.setViewVisibility(R.id.data_notification_layout1, View.VISIBLE);
            contentView.setViewVisibility(R.id.data_notification_layout2, View.VISIBLE);
            contentView.setViewVisibility(R.id.data_notification_layout3, View.VISIBLE);
            for (HashMap.Entry<String, MqttEquipment> entry : MqttEquipmentMap.entrySet()) {
                MqttEquipment e = entry.getValue();
                String type = e.getType();
                String sid = e.getSid();
                if (oldMap != null) {
                    if (oldMap.get(sid) != null) {

                        if (type != null && !type.equals("")) {  //添加菜单栏
                            if (type.equals("socket") || type.equals("switch") || type.equals("light")) {
//                    if (type.equals("switch") || type.equals("light")) {
                                if (i == 0) {
                                    contentView.setTextViewText(R.id.data_notification_tv1, e.getName());
                                    setImage1(e.getIm_url());
                                    Intent btIntent = new Intent().setAction(BT_REFRESH_ACTION);
                                    btIntent.putExtra("sid", e.getSid());
                                    btIntent.putExtra("type", e.getType());
                                    btIntent.putExtra("num", 1);
//                            if (oldState != null && oldState.get(e.getSid()) != null) {
//                                btIntent.putExtra("state", oldState.get(e.getSid()));
//                            }

                                    PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    contentView.setOnClickPendingIntent(R.id.data_notification_layout1, btPendingIntent);
                                } else if (i == 1) {
                                    contentView.setTextViewText(R.id.data_notification_tv2, e.getName());
                                    setImage2(e.getIm_url());
                                    Intent btIntent = new Intent().setAction(COLLECTION_VIEW_ACTION);
                                    btIntent.putExtra("sid", e.getSid());
                                    btIntent.putExtra("type", e.getType());
                                    btIntent.putExtra("num", 2);
//                            if (oldState != null && oldState.get(e.getSid()) != null) {
//                                btIntent.putExtra("state", oldState.get(e.getSid()));
//                            }
                                    PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    contentView.setOnClickPendingIntent(R.id.data_notification_layout2, btPendingIntent);
                                } else if (i == 2) {
                                    contentView.setTextViewText(R.id.data_notification_tv3, e.getName());
                                    setImage3(e.getIm_url());
                                    Intent btIntent = new Intent().setAction(COLLECTION_VIEW_EXTRA);
                                    btIntent.putExtra("sid", e.getSid());
                                    btIntent.putExtra("type", e.getType());
                                    btIntent.putExtra("num", 3);
//                            if (oldState != null && oldState.get(e.getSid()) != null) {
//                                btIntent.putExtra("state", oldState.get(e.getSid()));
//                            }
                                    PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    contentView.setOnClickPendingIntent(R.id.data_notification_layout3, btPendingIntent);
                                }
                                i++;
                            }
                        }
                    }

                }

            }
            Log.e("qqq", "oldMap  i=" + i);
            notification.contentView = contentView;
            if (i != 0) {
                if (i == 1) {
                    contentView.setViewVisibility(R.id.data_notification_layout2, View.INVISIBLE);
                    contentView.setViewVisibility(R.id.data_notification_layout3, View.INVISIBLE);
                } else if (i == 2) {
                    contentView.setViewVisibility(R.id.data_notification_layout3, View.INVISIBLE);
                }

                manager.notify(3, notification);
            } else {
                if (manager != null) {
                    manager.cancel(3);
                }
                if (in_manager != null) {
                    in_manager.cancel(6);
                }
            }

        }
    }


    private Bitmap bitmap1, bitmap2, bitmap3;
    private HashMap<String, Bitmap> BitmapMap = new HashMap<>();

    private void setImage1(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (BitmapMap.get(url) == null) {
                    bitmap1 = getURLimage(url);
                    image_hander.sendEmptyMessageDelayed(1000, 0);
                } else {
                    bitmap1 = BitmapMap.get(url);
                    image_hander.sendEmptyMessageDelayed(1000, 0);
                }

            }
        }).start();
    }

    private void setImage2(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (BitmapMap.get(url) == null) {
                    bitmap2 = getURLimage(url);
                    image_hander.sendEmptyMessageDelayed(1001, 0);
                } else {
                    bitmap2 = BitmapMap.get(url);
                    image_hander.sendEmptyMessageDelayed(1001, 0);
                }
            }
        }).start();
    }


    private void setImage3(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (BitmapMap.get(url) == null) {
                    bitmap3 = getURLimage(url);
                    image_hander.sendEmptyMessageDelayed(1002, 0);
                } else {
                    bitmap3 = BitmapMap.get(url);
                    image_hander.sendEmptyMessageDelayed(1002, 0);
                }
            }
        }).start();
    }

    Handler image_hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    if (bitmap1 != null) {
                        contentView.setImageViewBitmap(R.id.data_notification_im1, bitmap1);
                        notification.contentView = contentView;
                        manager.notify(3, notification);
                    }

                    break;
                case 1001:
                    if (bitmap2 != null) {
                        contentView.setImageViewBitmap(R.id.data_notification_im2, bitmap2);
                        notification.contentView = contentView;
                        manager.notify(3, notification);
                    }
                    break;
                case 1002:
                    if (bitmap3 != null) {
                        contentView.setImageViewBitmap(R.id.data_notification_im3, bitmap3);
                        notification.contentView = contentView;
                        manager.notify(3, notification);

                    }
                    break;

                case 2000:
//                    if (in_manager != null && in_notification != null) {
//                        in_manager.notify(6, in_notification);
//                    }
                    if (in_manager == null) {
                        inNotification();
                    }
                    socketInNotification(Notification_bitmap, Notification_sid, Notification_type);
                    break;
            }
        }
    };


    private String Notification_sid,Notification_type;
    private Bitmap Notification_bitmap;

    //加载图片
    public Bitmap getURLimage(String url1) {
//        Log.e("qqq", "dataNotification e=" + url1);
        try {
            URL url = new URL(url1);
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setConnectTimeout(6000);// 设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);// 不缓存
            conn.connect();
            int code = conn.getResponseCode();
            Bitmap bitmap = null;
            if (code == 200) {
                InputStream is = conn.getInputStream();// 获得图片的数据流
                bitmap = BitmapFactory.decodeStream(is);
            }
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ContentReceiver mReceiver;
//    private MyServiceConn conn;

    private void doRegisterReceiver() {
        mReceiver = new ContentReceiver();
        IntentFilter filter = new IntentFilter(
                "BT_REFRESH_ACTION");
        registerReceiver(mReceiver, filter);
        IntentFilter filter2 = new IntentFilter(
                "COLLECTION_VIEW_ACTION");
        registerReceiver(mReceiver, filter2);
        IntentFilter filter3 = new IntentFilter(
                "COLLECTION_VIEW_EXTRA");
        registerReceiver(mReceiver, filter3);

        IntentFilter filter4 = new IntentFilter(
                socketInNotification1);
        registerReceiver(mReceiver, filter4);

        IntentFilter filter5 = new IntentFilter(
                socketInNotification2);
        registerReceiver(mReceiver, filter5);

        IntentFilter filter6 = new IntentFilter(
                swithInNotification1);
        registerReceiver(mReceiver, filter6);

        IntentFilter filter7 = new IntentFilter(
                swithInNotification2);
        registerReceiver(mReceiver, filter7);
        IntentFilter filter8 = new IntentFilter(
                swithInNotification3);
        registerReceiver(mReceiver, filter8);
        IntentFilter filter9 = new IntentFilter(
                swithInNotification4);
        registerReceiver(mReceiver, filter9);

        IntentFilter filter10 = new IntentFilter(
                lightInNotification1);
        registerReceiver(mReceiver, filter10);

        IntentFilter filter11 = new IntentFilter(
                lightInNotification2);
        registerReceiver(mReceiver, filter11);

        IntentFilter filter12 = new IntentFilter(
                lightInNotification3);
        registerReceiver(mReceiver, filter12);
    }

    public class ContentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MqttEquipmentMap == null) {
                return;
            }
            String sid = intent.getStringExtra("sid");
            String type = intent.getStringExtra("type");
            Log.e("qqq", "onReceive type=" + type);
            int num = intent.getIntExtra("num", -1);
            if (num == -10) {  //第二次
                if (type.equals("socket")) {
                    boolean state = intent.getBooleanExtra("state", false);
                    if (MqttEquipmentMap.get(sid) != null) {
                        MqttEquipment e = MqttEquipmentMap.get(sid);
                        e.publish_String(push_socke(e.getType(), e.getSid(), state));
                    }
                } else if (type.equals("switch")) {
                    boolean state = intent.getBooleanExtra("state", false);
                    int channel = intent.getIntExtra("channel", -1);
                    if (MqttEquipmentMap.get(sid) != null && channel != -1) {
                        Log.e("qqq", "onReceive type=fa");
                        MqttEquipment e = MqttEquipmentMap.get(sid);
                        e.publish_String(push_switch(e.getType(), e.getSid(), state, channel));
                    }

                } else if (type.equals("light")) {
                    int blight = intent.getIntExtra("blight", -1);
                    if (MqttEquipmentMap.get(sid) != null) {
                        MqttEquipment e = MqttEquipmentMap.get(sid);
                        Log.e("qqq", "onReceive blight=" + blight);
                        if (blight != -1) {
                            e.publish_String(push_light(e.getType(), e.getSid(), blight));
                        } else {
                            e.publish_String(push_light(e.getType(), e.getSid(), false));
                        }

                    }

                }

            } else {

                Bitmap bitmap = null;
                if (num == 1) {
                    bitmap = bitmap1;
                } else if (num == 2) {
                    bitmap = bitmap2;
                } else if (num == 3) {
                    bitmap = bitmap3;
                }
                if (bitmap == null) {
                    return;
                }
                if (sid != null && !sid.equals("")) {  //具体操作
                    Log.e("qqq", "sid=" + sid);
                    if (type != null) {
                        if (type.equals("socket")) {
                            if (in_manager == null) {
                                inNotification();
                            }
                            socketInNotification(bitmap, sid, type);
//                            Notification_sid=sid;
//                            Notification_type=type;
//                            Notification_bitmap=bitmap;
//                            image_hander.sendEmptyMessage(2000);

                        } else if (type.equals("switch")) {
                            if (in_manager == null) {
                                inNotification();
                            }
                            swithInNotification(bitmap, sid, type);

                        } else if (type.equals("light")) {
                            if (in_manager == null) {
                                inNotification();
                            }
                            lightInNotification(bitmap, sid, type);
                        }
                    }

                }
            }


        }

    }


    private RemoteViews in_contentView;
    private Notification in_notification;
    private NotificationManager in_manager;

    public void inNotification() {

        in_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        in_notification = new Notification.Builder(this).setSmallIcon(R.drawable.icon).build();
//        notification.flags|= Notification.FLAG_ONGOING_EVENT;
    }


    private final static String socketInNotification1 = "socketInNotification1";
    private final static String socketInNotification2 = "socketInNotification2";
    private final static String swithInNotification1 = "swithInNotification1";
    private final static String swithInNotification2 = "swithInNotification2";
    private final static String swithInNotification3 = "swithInNotification3";
    private final static String swithInNotification4 = "swithInNotification4";
    private final static String lightInNotification1 = "swithInNotification1";
    private final static String lightInNotification2 = "swithInNotification2";
    private final static String lightInNotification3 = "swithInNotification3";

    /**
     * 发送通知
     */
    public void socketInNotification(Bitmap bitmap, String sid, String type) {  //插座
        Log.e("qqq", "dataInNotification ");
        in_contentView = new RemoteViews(this.getPackageName(), R.layout.socket_in_notification);
//        in_contentView = new RemoteViews(this.getPackageName(), R.layout.data_notification);
        in_notification.contentView = in_contentView;
        in_manager.notify(6, in_notification);
        if (bitmap != null) {
            in_contentView.setImageViewBitmap(R.id.socket_in_notification_im1, bitmap);

            Intent btIntent = new Intent().setAction(socketInNotification1);
            btIntent.putExtra("sid", sid);
            btIntent.putExtra("type", type);
            btIntent.putExtra("num", -10);
            btIntent.putExtra("state", true);
            PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.socket_in_notification_im2, btPendingIntent);

            Intent btIntent2 = new Intent().setAction(socketInNotification2);
            btIntent2.putExtra("sid", sid);
            btIntent2.putExtra("type", type);
            btIntent2.putExtra("num", -10);
            btIntent2.putExtra("state", false);
            PendingIntent btPendingIntent2 = PendingIntent.getBroadcast(this, 0, btIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.socket_in_notification_im3, btPendingIntent2);

            in_notification.contentView = in_contentView;
            in_notification.contentIntent=btPendingIntent2;
            in_manager.notify(6, in_notification);
//            image_hander.sendEmptyMessage(2000);
        }

    }


    public String push_socke(String type, String sid, boolean state) {  //获取状态

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_" + type);
            if (state) {
                jsonObject.put("state", "on");
            } else {
                jsonObject.put("state", "off");
            }

            jsonObject.put("sid", sid);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }


    public void swithInNotification(Bitmap bitmap, String sid, String type) {  //开关
        Log.e("qqq", "swithInNotification ");
        in_contentView = new RemoteViews(this.getPackageName(), R.layout.swith_in_notification);
        in_notification.contentView = in_contentView;
        in_manager.notify(6, in_notification);
        if (bitmap != null) {
            in_contentView.setImageViewBitmap(R.id.switch_in_notification_im1, bitmap);

            Intent btIntent = new Intent().setAction(swithInNotification1);
            btIntent.putExtra("sid", sid);
            btIntent.putExtra("type", type);
            btIntent.putExtra("num", -10);
            btIntent.putExtra("state", true);
            btIntent.putExtra("channel", 2);
            PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.switch_in_notification_im2, btPendingIntent);

            Intent btIntent2 = new Intent().setAction(swithInNotification2);
            btIntent2.putExtra("sid", sid);
            btIntent2.putExtra("type", type);
            btIntent2.putExtra("num", -10);
            btIntent2.putExtra("state", false);
            btIntent2.putExtra("channel", 2);
            PendingIntent btPendingIntent2 = PendingIntent.getBroadcast(this, 0, btIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.switch_in_notification_im3, btPendingIntent2);

            Intent btIntent3 = new Intent().setAction(swithInNotification3);
            btIntent3.putExtra("sid", sid);
            btIntent3.putExtra("type", type);
            btIntent3.putExtra("num", -10);
            btIntent3.putExtra("state", true);
            btIntent3.putExtra("channel", 1);
            PendingIntent btPendingIntent3 = PendingIntent.getBroadcast(this, 0, btIntent3, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.switch_in_notification_im4, btPendingIntent3);

            Intent btIntent4 = new Intent().setAction(swithInNotification4);
            btIntent4.putExtra("sid", sid);
            btIntent4.putExtra("type", type);
            btIntent4.putExtra("num", -10);
            btIntent4.putExtra("state", false);
            btIntent4.putExtra("channel", 1);
            PendingIntent btPendingIntent4 = PendingIntent.getBroadcast(this, 0, btIntent4, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.switch_in_notification_im5, btPendingIntent4);
            in_notification.contentView = in_contentView;
            in_manager.notify(6, in_notification);
        }

    }


    public String push_switch(String type, String sid, boolean state, int channel) {

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_" + type);
            if (state) {
                jsonObject.put("state", "on");
            } else {
                jsonObject.put("state", "off");
            }
            jsonObject.put("channel", channel);
            jsonObject.put("sid", sid);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }


    public void lightInNotification(Bitmap bitmap, String sid, String type) {  //灯
        Log.e("qqq", "dataInNotification bitmap1" + bitmap);
        in_contentView = new RemoteViews(this.getPackageName(), R.layout.light_in_notification);
        in_notification.contentView = in_contentView;
        in_manager.notify(6, in_notification);
        if (bitmap != null) {
            in_contentView.setImageViewBitmap(R.id.light_in_notification_im1, bitmap);

            Intent btIntent = new Intent().setAction(lightInNotification1);
            btIntent.putExtra("sid", sid);
            btIntent.putExtra("type", type);
            btIntent.putExtra("num", -10);
            btIntent.putExtra("blight", 80);
            PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.light_in_notification_im2, btPendingIntent);

            Intent btIntent2 = new Intent().setAction(lightInNotification2);
            btIntent2.putExtra("sid", sid);
            btIntent2.putExtra("type", type);
            btIntent2.putExtra("num", -10);
            btIntent2.putExtra("blight", 30);
            PendingIntent btPendingIntent2 = PendingIntent.getBroadcast(this, 0, btIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.light_in_notification_im3, btPendingIntent2);


            Intent btIntent3 = new Intent().setAction(lightInNotification3);
            btIntent3.putExtra("sid", sid);
            btIntent3.putExtra("type", type);
            btIntent3.putExtra("num", -10);
            PendingIntent btPendingIntent3 = PendingIntent.getBroadcast(this, 0, btIntent3, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.light_in_notification_im4, btPendingIntent3);

            in_notification.contentView = in_contentView;
            in_manager.notify(6, in_notification);
        }

    }


    public String push_light(String type, String sid, boolean state) {

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_" + type);
            if (state) {
                jsonObject.put("state", "on");
            } else {
                jsonObject.put("state", "off");
            }

            jsonObject.put("sid", sid);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }


    public String push_light(String type, String sid, int blight) {

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_" + type + "_setok");
            jsonObject.put("sid", sid);
            jsonObject.put("blight", blight);
            jsonObject.put("alight", 0);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
