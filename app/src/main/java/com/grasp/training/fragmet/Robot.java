package com.grasp.training.fragmet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.activity.ControlActivity;
import com.grasp.training.tool.BaseMqttFragment;
import com.zs.easy.mqtt.IEasyMqttCallBack;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Robot extends BaseMqttFragment {
    @BindView(R.id.jqr)
    Button jqr;
    Unbinder unbinder;

    @Override
    public int getInflate() {
        return R.layout.robot;
    }

    @Override
    public void init(View v) {
        unbinder = ButterKnife.bind(this, v);

    }


    private String sid = MainActivity.SID;

    @Override
    public void connect() {
        mqttService.connect(iEasyMqttCallBack);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void initIEasyMqttCallBack() {
        iEasyMqttCallBack = new IEasyMqttCallBack() {
            @Override
            public void messageArrived(final String topic, final String message, final int qos) {
                //推送消息到达

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("messageArrived", "robot messageArrived  message= " + message);
                        try {
                            JSONObject jsonF;
                            Message me;
                            String js = "";
                            String channel_0 = "";
                            int var = 0;
                            JSONObject jsonObject = new JSONObject(message);
                            String cmd = jsonObject.getString("cmd");
                            String mSid = jsonObject.optString("sid", "");
                            if (!mSid.equals(sid)) {
                                return;
                            }

                            switch (cmd) {
                                case "read_ok":
//                                    String data = jsonObject.optString("data");
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
                if (isConnected()) {
                    subscribe();
                }
            }

            @Override
            public void connectFailed(IMqttToken arg0, Throwable arg1) {
                //连接失败
                Log.e("qqq", "connectFailed");
            }
        };
    }


    @OnClick(R.id.jqr)
    public void onViewClicked() {
        context.startActivity(new Intent(context, ControlActivity.class));
    }
}
