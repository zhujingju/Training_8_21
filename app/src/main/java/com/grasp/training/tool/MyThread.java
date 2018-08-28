package com.grasp.training.tool;



import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by zhujingju on 2018/2/24.
 */

public class MyThread  extends Thread {

    public String content;
    public String ip;

    public MyThread(String str,String ip) {
        content = str;
        this.ip=ip;
    }

    @Override
    public void run() {
        //定义消息
        Message msg = new Message();
        msg.what = 1;
        try {
            //连接服务器 并设置连接超时为5秒
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, 30000), 1000);

            //获取输入输出流
            OutputStream ou = socket.getOutputStream();
            //获取输出输出流
            BufferedReader bff = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            //向服务器发送信息
            ou.write(content.getBytes("utf-8"));
            ou.flush();

            //读取发来服务器信息
            String result = "";
            String buffer = "";
            while ((buffer = bff.readLine()) != null) {
                result = result + buffer;
            }
            msg.obj = result.toString();
            //发送消息 修改UI线程中的组件
            myHandler.sendMessage(msg);
            //关闭各种输入输出流
            bff.close();
            ou.close();
            socket.close();
        } catch (SocketTimeoutException aa) {
            //连接超时 在UI界面显示消息
            msg.what=2;
            msg.obj =  "服务器连接失败！请检查网络是否打开";
            //发送消息 修改UI线程中的组件
            myHandler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
//                result.append("server:" + msg.obj + "\n");
            }else{ //失败

            }
        }

    };
}


