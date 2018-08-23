package com.grasp.training.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.tool.BaseMqttActivity;
import com.zs.easy.mqtt.IEasyMqttCallBack;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisteredActivity extends BaseMqttActivity {

    @BindView(R.id.userPhone)
    TextInputLayout userPhone;
    @BindView(R.id.til_username)
    TextInputLayout tilUsername;
    @BindView(R.id.til_password)
    TextInputLayout tilPassword;
    @BindView(R.id.registered)
    Button registered;
    @BindView(R.id.login_y)
    ConstraintLayout loginY;
    private String sid = MainActivity.SID;

    EditText userEditText;
    EditText pwdEditText;
    EditText phoneEditText;
    @Override
    public int setLayoutId() {
        return R.layout.registered_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {
        userEditText = tilUsername.getEditText();
        pwdEditText = tilPassword.getEditText();
        phoneEditText = userPhone.getEditText();
    }

    @Override
    public void initObject() {

    }

    @Override
    public void initListener() {
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("qqq", "beforeTextChanged执行了....s = " + s + "---start = " + start + "---count = " + count + "---after = " + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("qqq", "onTextChanged执行了....s = " + s + "---start = " + start + "---count = " + count + "---before = " + before);
//                if (s.length() > 7) {
//                    tilUsername.setErrorEnabled(true);//设置是否打开错误提示
//                    tilUsername.setError("用户名长度不能超过8个");//设置错误提示的信息
//                } else {
//                    tilUsername.setErrorEnabled(false);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("qqq", "afterTextChanged执行了....s = " + s);
            }
        });

        userEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("qqq", "beforeTextChanged执行了....s = " + s + "---start = " + start + "---count = " + count + "---after = " + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("qqq", "onTextChanged执行了....s = " + s + "---start = " + start + "---count = " + count + "---before = " + before);
//                if (s.length() > 7) {
//                    tilUsername.setErrorEnabled(true);//设置是否打开错误提示
//                    tilUsername.setError("用户名长度不能超过8个");//设置错误提示的信息
//                } else {
//                    tilUsername.setErrorEnabled(false);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("qqq", "afterTextChanged执行了....s = " + s);
            }
        });

        pwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 6) {
                    tilPassword.setErrorEnabled(true);
                    tilPassword.setError("密码长度不能小于6个");
                } else {
                    tilPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void init() {

    }

    @Override
    public void connect() {
        mqttService.connect(iEasyMqttCallBack);
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


}
