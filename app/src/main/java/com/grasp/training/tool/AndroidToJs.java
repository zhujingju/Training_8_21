package com.grasp.training.tool;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.grasp.training.service.MqttService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AndroidToJs extends Object {
    final String TAG = "Print-AndroidToJs";
    private String sid = "";
    private String type = "";
    @JavascriptInterface
    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
    @JavascriptInterface
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JavascriptInterface
    public void hello(String msg){
//        System.out.println("JS调用了Androidhello方法");
        Log.i(TAG, "hello: msg="+msg);
    }
    // web发送数据给webview，希望webview发送给设备
    @JavascriptInterface
    public void jsToAndroid(String jsonStr){
        Log.i(TAG, "jsToAndroid: jsonStr="+jsonStr);
        publish_String(jsonStr);
    }

//    public boolean publish_String(String set_msg) {  //发送消息
//
//
//        if (isConnected()) {
//            //消息主题
//            String topic = "iotbroad/iot/" + "switch" + "/" + "73e6b1640ef";
//            //消息内容
//            String msg = set_msg;
//
//            //消息策略
//            int qos = 0;
//            //是否保留
//            boolean retained = false;
//            //发布消息
//            Log.e("qqq", "主题 ="+topic+"  发送消息 =" + set_msg);
//            publish(msg, topic, qos, retained);
//
//            return true;
//        }
//        return false;
//    }
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


    private String jsonStr = "";
    public void publish_String(final String set_msg) {  //发送消息
        Log.i(TAG, "publish_String: set_msg="+set_msg);//set_msg={"params":"channel1,state","state":"off","channel":1}


        try {
            JSONObject jsonObject = new JSONObject(set_msg);
            Log.i(TAG, "publish_String: ");
            jsonObject.remove("params");
            String cmdTemp = jsonObject.optString("cmd", "");
            jsonObject.put("cmd", cmdTemp.replace("*", type));// + "_read"
            jsonObject.put("sid", sid);

            jsonStr = jsonObject.toString();
            Log.i(TAG, "publish_String: jsonStr="+jsonStr);//jsonStr={"state":"off","channel":1,"cmd":"wifi_switch","sid":"73e6b1640ef"}
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (fs_zt) {
            Log.e(TAG, "publish_String: fs_zt="+fs_zt);
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
        if(result_zt){
            String ip = MqttService.sid_ip.get(getSid());
//            ip=null;//要删
            Log.e("tcp", "ip =" + ip + " getSid()=" + getSid());
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
//                        new UdpSendThread(set_msg,ip).start();
                        Log.i(TAG, "publish_String: 发送了");
                        new UdpSendThread(jsonStr,ip).start();
                        return;
                    }else{
                        Log.e(TAG, "publish_String: getSid()="+getSid()+",zt="+zt );
                    }
                }else{
                    Log.e(TAG, "publish_String: MqttService.ip_zt.get(ip + getSid())="+MqttService.ip_zt.get(ip + getSid()));
                }

            }
            else{
                Log.e(TAG, "publish_String: ip="+ip);
            }
        }else{
            Log.e(TAG, "publish_String: result_zt"+result_zt);//页面刚刚加载的时候这里是false
        }



        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isConnected()) {
                    //消息主题
                    String topic = getMyTopic();
                    //消息内容
                    String msg = jsonStr;

                    //消息策略
                    int qos = 0;
                    //是否保留
                    boolean retained = false;
                    //发布消息
                    Log.e("qqq", "主题 =" + getMyTopic() + "  发送消息 =" + jsonStr);
                    publish(msg, topic, qos, retained);

                }
            }
        }).start();

    }


    boolean fs_zt = false;
    private String myTopic;

    public String getMyTopic() {
        return myTopic;
    }

    public void setMyTopic(String myTopic) {
        this.myTopic = myTopic;
    }



    private boolean result_zt=false;

    private   DatagramSocket socket;

    public DatagramSocket getSocket() {

        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        result_zt=true;
        this.socket = socket;
    }

    private void setDel(){
        result_zt=false;
        if(socket!=null){
            socket.close();
        }

    }
    public class UdpSendThread extends Thread {

        public static final String TAG = "UdpSendThread";
        private  int i = 0; //静态变量，记录发送消息的次数
        private String data;
        private String ip;
        public UdpSendThread(String data,String ip){
            this.data=data;
            this.ip=ip;
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


                if(socket==null){
//                    socket = new DatagramSocket(post); //自定端口号
//                    socket.setReuseAddress(true);
//                    socket.bind(new InetSocketAddress(8888));
                    return;
                }


                InetAddress address = InetAddress.getByName(ip); //通过当前 IP 建立相应的 InetAddress
//                String data = "I love you" + "( " + i++ + " )";
                byte dataByte[] = data.getBytes(); //建立数据
                DatagramPacket packet = new DatagramPacket(dataByte, dataByte.length, address, 8888); //通过该数据建包
                socket.send(packet); //开始发送该包
//                socket.close(); //其实对于发送方来说没必要关闭 socket，但为了防止无法预知的意外，建议关闭
                Log.e(TAG, "send done，data: " + data );

            }catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "fa err= " + e.getMessage() );
            }

        }


        /**
         * 获取本机ip的广播地址
         *
         */


    }
}
