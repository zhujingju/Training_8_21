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
import android.support.v4.content.LocalBroadcastManager;
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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

public abstract class BaseMqttToActivity extends BaseActivity {

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
//        registerReceiver(mReceiver, filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
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
    protected void onDestroy() {
        // TODO Auto-generated method stub

        unbindService(conn);
        ha.removeMessages(1000);
        ha.removeMessages(2000);
        ha.removeMessages(3000);
        myHander.removeMessages(1);
        myHander.removeMessages(2);
        if (mReceiver != null) {
//            unregisterReceiver(mReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        }
        if(networkChangeReceiver!=null){
            unregisterReceiver(networkChangeReceiver);
        }
        super.onDestroy();
//        canlce();
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
                            if(MqttService.sid_ip.get(getSid())==null){
                                push_ping(getSid());
                            }

                        }
                    } else {

                        ha.sendEmptyMessageDelayed(1000, 500);
                    }

                    break;

                case 2000:
                    new MyThread(msg.obj.toString(), MqttService.sid_ip.get(getSid())).start();
                    break;

                case 3000:

                    if (isConnected()) {
                        if (!getSid().equals("")) {
//                            push_ping(getSid());
                            if(MqttService.sid_ip.get(getSid())==null){
                                push_ping(getSid());
                            }else{
                                if(MqttService.ip_zt.get(MqttService.sid_ip.get(getSid()))==null){
                                    push_ping(getSid());
                                }else{
                                    if(!MqttService.ip_zt.get(MqttService.sid_ip.get(getSid()))){
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
//        if (isConnected()) {
//            subscribe();
//        } else {
        super.onStart();
        ha.sendEmptyMessageDelayed(1000, 0);
//        }
    }


    private boolean tcp_zt = false;

    class MyThread extends Thread {

        public String content;
        public String ip;

        public MyThread(String str, String ip) {
            Log.e("MyThread", str);
            content = str;
            this.ip = ip;
        }

        @Override
        public void run() {
            //定义消息
            Message msg = new Message();
            msg.what = 1;
            try {
                myIp = ip;
                Log.e("MyThread", "服务器 ip=" + myIp);
//                连接服务器 并设置连接超时为10秒
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(ip, 8888), 5000);
                //                //获取输入输出流

                InputStream mInStream = null;
                OutputStream ou;
                ou = socket.getOutputStream();
//                //向服务器发送信息
                ou.write(content.getBytes("utf-8"));
                ou.flush();

//
                if (socket != null) {
                    //获取输出流、输入流
                    mInStream = socket.getInputStream();
                }
                byte b[] = new byte[1024];        // 所有的内容都读到此数组之中
                int len = mInStream.read(b);        // 读取内容
                // 关闭输出流\

                //读取发来服务器信息
                Log.e("tcp", "len" + len + " b=" + b);
                if (len < 0) {
                    msg.what = 3;
                    msg.obj = "len=-1";
                    //发送消息 修改UI线程中的组件
                    myHander.sendMessage(msg);
                    mInStream.close();
                    ou.close();
                    socket.close();
                    return;
                }
                String result = "";
                result = new String(b, 0, len);
                msg.obj = result.toString();
//                Log.e("qqq", "result.toString()=" + result.toString());
//                //发送消息 修改UI线程中的组件
                myHander.sendMessage(msg);


                //关闭各种输入输出流
                mInStream.close();
                ou.close();
                socket.close();
                Log.e("MyThread", "good");
            } catch (SocketTimeoutException aa) {
                //连接超时 在UI界面显示消息
                msg.what = 2;
                msg.obj = "连接超时";
                //发送消息 修改UI线程中的组件
                myHander.sendMessage(msg);

                Log.e("MyThread", "err1=" + aa.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("MyThread", "err2=" + e.getMessage());
                msg.what = 2;
                msg.obj = e.getMessage();
                //发送消息 修改UI线程中的组件
                myHander.sendMessage(msg);
            }
        }
    }

//    private Socket socket;
//    private InputStream mInStream = null;
//    private  OutputStream ou;
//    private void initSockect(String content, String ip){
//        Log.e("MyThread", "服务器 ip=" + myIp);
//        //连接服务器 并设置连接超时为10秒
//        Message msg = new Message();
//        msg.what = 1;
//        try {
//            socket = new Socket();
//            socket.connect(new InetSocketAddress(ip, 8888), 5000);
//
//
//            if (socket != null) {
//                //获取输出流、输入流
//                mInStream = socket.getInputStream();
//            }
//            byte b[] = new byte[1024];        // 所有的内容都读到此数组之中
//            int len = mInStream.read(b);        // 读取内容
//            // 关闭输出流\
//
//            //读取发来服务器信息
//            Log.e("qqq", "len" + len+" b="+b);
//            if(len==-1){
//
//                return;
//            }
//            String result = "";
//            result = new String(b, 0, len);
//            msg.obj = result.toString();
//            Log.e("qqq", "result.toString()=" + result.toString());
////                //发送消息 修改UI线程中的组件
//            myHander.sendMessage(msg);
//        } catch (IOException e) {
//            e.printStackTrace();
//            msg.what = 2;
//            msg.obj = "服务器连接失败！请检查网络是否打开";
//            //发送消息 修改UI线程中的组件
//            myHander.sendMessage(msg);
//        }
//    }
//    private void canlce(){
//        try {
//            mInStream.close();
//            ou.close();
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }


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
                        MqttService.ip_zt.put(myIp+getSid(), false);
//                        canlce();
//                        socket = null;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                    fa_zt=true;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        if (!getSid().equals("")) {
                            if(fa_zt){
                                fa_zt=false;
                                push_ping(getSid());
                            }

                        }


                    }
                    break;
            }
        }
    };
    private boolean fa_zt=true;
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
                    publish_String3(js,getMyTopic());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void publish_String(final String set_msg) {  //发送消息

        if(fs_zt){

            return ;
        }
        fs_zt=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    fs_zt=false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        String ip = MqttService.sid_ip.get(getSid());
        Log.e("tcp", "ip =" + ip + " getSid()=" + getSid());
        if (ip != null) {

            Log.e("tcp", "ip_zt =" + MqttService.ip_zt.get(ip + getSid()));
            if(MqttService.ip_zt.get(ip + getSid())!=null){
                boolean zt = MqttService.ip_zt.get(ip + getSid());
                if (!getSid().equals("") && zt) {
                    Log.e("tcp", "Tcp ip=" + ip + "  mess=" + set_msg);
                    Message m = new Message();
                    m.what = 2000;
                    m.obj = set_msg;
                    ha.sendMessage(m);

                    return ;
                }
            }


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
                    Log.e("qqq", "主题 ="+getMyTopic()+"  发送消息 =" + set_msg);
                    publish(msg, topic, qos, retained);

                }
            }
        }).start();

    }


    boolean fs_zt=false;
    public void publish_String2(final String set_msg, final String topic) {  //发送消息
        if(fs_zt){
            return ;
        }
        fs_zt=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    fs_zt=false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        String ip = MqttService.sid_ip.get(getSid());
        Log.e("tcp", "ip =" + ip + " getSid()=" + getSid());
        if (ip != null) {

            Log.e("tcp", "ip_zt =" + MqttService.ip_zt.get(ip + getSid()));
            if(MqttService.ip_zt.get(ip + getSid())!=null){
                boolean zt = MqttService.ip_zt.get(ip + getSid());
                if (!getSid().equals("") && zt) {
                    Log.e("tcp", "Tcp ip=" + ip + "  mess=" + set_msg);
                    Message m = new Message();
                    m.what = 2000;
                    m.obj = set_msg;
                    ha.sendMessage(m);

                    return ;
                }
            }


        }
        Log.e("qqq", "主题 ="+getMyTopic()+"  发送消息 =" + set_msg);

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
        Log.e("qqq", "主题 ="+getMyTopic()+"  发送消息 =" + set_msg);
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

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "网络状态改变", Toast.LENGTH_SHORT).show();
            ha.sendEmptyMessageDelayed(1000, 0);
            Log.e("qqq","网络状态改变");
            int im=0;
//            while (im<3){
//                im++;
            ha.sendEmptyMessageDelayed(3000,500);
//            }
        }
    }


}
