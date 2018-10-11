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
import android.widget.Toast;

import com.grasp.training.service.MqttService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class BaseMqttActivity extends BaseActivity {
    private ContentReceiver mReceiver;
    private MyServiceConn conn;

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();

        registerReceiver(networkChangeReceiver, intentFilter);
        conn = new MyServiceConn();
        bindService(new Intent(this, MqttService.class), conn,
                BIND_AUTO_CREATE);
        doRegisterReceiver();
        new UdpReceiveThread().start();

//        ha.sendEmptyMessageDelayed(4000, 0);
    }


//    private String myTopic ="iotbroad/iot";

    public abstract String getMyTopic();

    public abstract String getMyTopicDing();

    public abstract String getSid();

    /**
     * 注册广播接收者
     */
    private void doRegisterReceiver() {
        mReceiver = new ContentReceiver();
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
        setDel();
        unbindService(conn);
        ha.removeMessages(1000);
        ha.removeMessages(2000);
        ha.removeMessages(3000);
        ha.removeMessages(4000);
        myHander.removeMessages(1);
        myHander.removeMessages(2);
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
//        canlce();
    }


    public void publish_String(final String set_msg) {  //发送消息


        if (fs_zt) {

            return;
        }
        fs_zt = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    fs_zt = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
//        Log.e("qqq","socket.isClosed()="+socket.isClosed()+" socket.isConnected()"+socket.isConnected());
        if (result_zt) {
            String ip = MqttService.sid_ip.get(getSid());
            Log.e("tcp", "ip =" + ip + " getSid()=" + getSid());
//            ip=null;//要删
            if (ip != null) {

                Log.e("tcp", "ip_zt =" + MqttService.ip_zt.get(ip + getSid()));
                if (MqttService.ip_zt.get(ip + getSid()) != null) {
                    boolean zt = MqttService.ip_zt.get(ip + getSid());
                    if (!getSid().equals("") && zt) {
                        Log.e("tcp", "Tcp ip=" + ip + "  mess=" + set_msg);
//                    Message m = new Message();
//                    m.what = 2000;
//                    m.obj = set_msg;
//                    ha.sendMessage(m);
                        new UdpSendThread(set_msg, ip).start();
                        return;
                    }
                }

            }
        } else {
            new UdpReceiveThread().start();
        }


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
                    Log.e("qqq", "主题 =" + getMyTopic() + "  发送消息 =" + set_msg);
                    publish(msg, topic, qos, retained);

                }
            }
        }).start();

    }


    boolean fs_zt = false;

    public void publish_String2(final String set_msg, final String topic) {  //发送消息
        if (fs_zt) {
            return;
        }
        fs_zt = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    fs_zt = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        if (result_zt) {
            String ip = MqttService.sid_ip.get(getSid());
            Log.e("tcp", "ip =" + ip + " getSid()=" + getSid());
//            ip=null;//要删
            if (ip != null) {

                Log.e("tcp", "ip_zt =" + MqttService.ip_zt.get(ip + getSid()));
                if (MqttService.ip_zt.get(ip + getSid()) != null) {
                    boolean zt = MqttService.ip_zt.get(ip + getSid());
                    if (!getSid().equals("") && zt) {
                        Log.e("tcp", "Tcp ip=" + ip + "  mess=" + set_msg);
//                    Message m = new Message();
//                    m.what = 2000;
//                    m.obj = set_msg;
//                    ha.sendMessage(m);
                        new UdpSendThread(set_msg, ip).start();
                        return;
                    }
                }

            }
        } else {
            new UdpReceiveThread().start();
        }
        Log.e("qqq", "主题 =" + getMyTopic() + "  发送消息 =" + set_msg);

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

                }
            }
        }).start();

    }


    public void publish_String3(final String set_msg, final String topic) {  //发送消息
        Log.e("qqq", "主题 =" + getMyTopic() + "  发送消息 =" + set_msg);
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

                }
            }
        }).start();


    }


    public void publish_String4(final String set_msg) {  //发送消息
        if (result_zt) {
            String ip = MqttService.sid_ip.get(getSid());
            Log.e("tcp", "ip =" + ip + " getSid()=" + getSid());
//            ip=null;//要删
            if (ip != null) {

                Log.e("tcp", "ip_zt =" + MqttService.ip_zt.get(ip + getSid()));
                if (MqttService.ip_zt.get(ip + getSid()) != null) {
                    boolean zt = MqttService.ip_zt.get(ip + getSid());
                    if (!getSid().equals("") && zt) {
                        Log.e("tcp", "Tcp ip=" + ip + "  mess=" + set_msg);
//                    Message m = new Message();
//                    m.what = 2000;
//                    m.obj = set_msg;
//                    ha.sendMessage(m);
                        new UdpSendThread(set_msg, ip).start();
                        return;
                    }
                }

            }
        } else {
            new UdpReceiveThread().start();
        }

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
                    Log.e("qqq", "主题 =" + getMyTopic() + "  发送消息 =" + set_msg);
                    publish(msg, topic, qos, retained);

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
        int[] qoss = new int[]{1};
        MqttService.mqttService.subscribe(topics, qoss);

    }

    public void subscribe(String s) {
        String[] topics = new String[]{s};
        //主题对应的推送策略 分别是0, 1, 2 建议服务端和客户端配置的主题一致
        // 0 表示只会发送一次推送消息 收到不收到都不关心
        // 1 保证能收到消息，但不一定只收到一条
        // 2 保证收到切只能收到一条消息
        int[] qoss = new int[]{1};
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
        if (MqttService.mqttService == null) {
            return false;
        }
        return MqttService.mqttService.isConnected();
    }


    Handler ha = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    if (isConnected()) {
                        subscribe();
                        if (!getSid().equals("")) {
                            if (MqttService.sid_ip.get(getSid()) == null) {
                                push_ping(getSid());
                            }

                        }
                    } else {

                        ha.sendEmptyMessageDelayed(1000, 500);
                    }

                    break;

                case 2000:
//                    new MyThread(msg.obj.toString(), MqttService.sid_ip.get(getSid())).start();
                    break;

                case 3000:

                    if (isConnected()) {
                        if (!getSid().equals("")) {
//                            push_ping(getSid());
                            if (MqttService.sid_ip.get(getSid()) == null) {
                                push_ping(getSid());
                            } else {
                                if (MqttService.ip_zt.get(MqttService.sid_ip.get(getSid())) == null) {
                                    push_ping(getSid());
                                } else {
                                    if (!MqttService.ip_zt.get(MqttService.sid_ip.get(getSid()))) {
                                        push_ping(getSid());
                                    }
                                }
                            }

                        }
                    } else {

                        ha.sendEmptyMessageDelayed(3000, 1000);
                    }
                    break;


            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
//        if (isConnected()) {
//            subscribe();
//        } else {

        ha.sendEmptyMessageDelayed(1000, 0);
//        }
    }


    private int num = 0;
    private String myIp = "";

    Handler myHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("tcp", "Tcp " + msg.obj.toString());
            switch (msg.what) {
                case 1:
                    num = 0;

                    MyMessageArrived(msg.obj.toString());
                    break;

                case 2:
//                    MqttService.sid_ip.get(getSid());
//                    Toast.makeText(getContext(),msg.obj.toString(),Toast.LENGTH_LONG).show();
                    num++;
                    if (num > 3) {
                        Log.e("MyThread", "num>3");
                        MqttService.ip_zt.put(myIp + getSid(), false);
//                        canlce();
//                        socket = null;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                    fa_zt = true;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        if (!getSid().equals("")) {
                            if (fa_zt) {
                                fa_zt = false;
                                push_ping(getSid());
                            }

                        }


                    }
                    break;
            }
        }
    };

    private boolean fa_zt = true;

    public void push_ping(final String sid) { //ping
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_equipment_ping");
                    jsonObject.put("sid", sid);
                    String js = jsonObject.toString();
                    publish_String3(js, getMyTopic());
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
//            if (tcpClient != null) {
//                tcpClient.del();
//                tcpClient = null;
//            }
//            result_zt = false;

            ha.sendEmptyMessageDelayed(1000, 0);
            Log.e("qqq", "网络状态改变");
            int im = 0;
//            while (im<3){
//                im++;
            ha.sendEmptyMessageDelayed(3000, 500);
//            }
        }
    }


    private int post = 7777;

    private void setPost() {
        if (socket == null) {
            Random random = new Random();
            post = random.nextInt(9000) + 1000;
        }


    }

    //（接收线程）
    class UdpReceiveThread extends Thread {
        private final String TAG = "UdpReceiveThread";

        @Override
        public void run() {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            setPost();
            Log.e("qqq", "post" + post);
            result_zt = true;
            Log.e("UdpReceiveThread", "new UdpReceiveThread");
            while (result_zt && isAlive()) { //循环接收，isAlive() 判断防止无法预知的错误
                try {
                    Log.e("UdpReceiveThread", "+++UdpReceiveThread");
//                    sleep(20); //让它好好休息一会儿
                    if (socket == null) {
                        socket = new DatagramSocket(post); //建立 socket，其中 8888 为端口号
                    }
                    byte data[] = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    socket.receive(packet); //阻塞式，接收发送方的 packet
                    String result = new String(packet.getData(), packet.getOffset(), packet.getLength()); //packet 转换
                    Log.e(TAG, "UDP result: " + result);
                    MyMessageArrived(result);
//                    socket2.close(); //必须及时关闭 socket，否则会出现 error
                } catch (Exception e) {
                    e.printStackTrace();
//                    new UdpReceiveThread().start();
//                    break; //当 catch 到错误时，跳出循环
                    Log.e(TAG, "UDP result: err=" + e.getMessage() + "  result_zt=" + result_zt);
                    if (result_zt) {
                        Log.e(TAG, "UDP result: 错误");
                        if (e.getMessage().equals("bind failed: EADDRINUSE (Address already in use)")) {//端口相同
                            Random random = new Random();
                            post = random.nextInt(9000) + 1000;
                        } else {
                            MqttService.ip_zt.put(myIp + getSid(), false);
                            if (!getSid().equals("")) {
                                if (fa_zt) {
                                    fa_zt = false;
                                    push_ping(getSid());
                                }
                            }
                        }

                    } else {

                    }

                    result_zt = false;
                    break;
                }
            }
        }
    }

    private boolean result_zt = true;

    private DatagramSocket socket;

    private void setDel() {
        result_zt = false;
        if (socket != null) {
            socket.close();
        }

    }

    public class UdpSendThread extends Thread {

        public static final String TAG = "UdpSendThread";
        private int i = 0; //静态变量，记录发送消息的次数
        private String data;
        private String ip;

        public UdpSendThread(String data, String ip) {
            this.data = data;
            this.ip = ip;
        }

        @Override
        public void run() {
            try {
//                Set<Map.Entry<String, String> > entrySet = getAllLocalBroadIp().entrySet();
//                String broadIp = null;
//                for (Map.Entry<String, String> entry : entrySet) { //遍历当前的 IP
//                    String localIp = entry.getKey();
//                    broadIp = entry.getValue();
//                    Log.e(TAG, "broadIp:" + broadIp + "\nlocalIp:" + localIp);
//                }
//
//                Log.e(TAG, "*** run udp send ***");


                if (socket == null) {
                    socket = new DatagramSocket(post); //自定端口号
//                    socket.setReuseAddress(true);
//                    socket.bind(new InetSocketAddress(8888));
                }


                InetAddress address = InetAddress.getByName(ip); //通过当前 IP 建立相应的 InetAddress
//                String data = "I love you" + "( " + i++ + " )";
                byte dataByte[] = data.getBytes(); //建立数据
                DatagramPacket packet = new DatagramPacket(dataByte, dataByte.length, address, 8888); //通过该数据建包
                socket.send(packet); //开始发送该包
//                socket.close(); //其实对于发送方来说没必要关闭 socket，但为了防止无法预知的意外，建议关闭
                Log.e(TAG, "send done，data: " + data);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "fa err= " + e.getMessage());
            }

        }


        /**
         * 获取本机ip的广播地址
         *
         */


    }


    private Map<String, String> getAllLocalBroadIp() {
        Map<String, String> LocalIpAndbroadcastIp = new HashMap<>();
        try {
            Enumeration<NetworkInterface> b = NetworkInterface.getNetworkInterfaces();
            while (b.hasMoreElements()) {
                for (InterfaceAddress f : b.nextElement().getInterfaceAddresses()) {
                    if (f.getBroadcast() != null) {
                        LocalIpAndbroadcastIp.put(f.getAddress().getHostAddress(), f.getBroadcast().getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return LocalIpAndbroadcastIp;
    }


}