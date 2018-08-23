package com.grasp.training.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.zs.easy.mqtt.EasyMqttService;
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
                if (s.length() ==0) {
                    userPhone.setErrorEnabled(true);//设置是否打开错误提示
                    userPhone.setError("电话不能为空");//设置错误提示的信息
                } else {
                    userPhone.setErrorEnabled(false);
                }
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
                if (s.length() ==0) {
                    tilUsername.setErrorEnabled(true);//设置是否打开错误提示
                    tilUsername.setError("用户名不能为空");//设置错误提示的信息
                } else {
                    tilUsername.setErrorEnabled(false);
                }
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
                if (s.length() ==0) {
                    tilPassword.setErrorEnabled(true);//设置是否打开错误提示
                    tilPassword.setError("密码不能为空");//设置错误提示的信息
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
                                    handler.sendEmptyMessage(1002);
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


    private boolean zc_zt=false;
    @OnClick({R.id.reg_fh, R.id.registered})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.reg_fh:
                finish();
                break;
            case R.id.registered:
                String phone=phoneEditText.getText().toString();
                String usre=userEditText.getText().toString();
                String pw=pwdEditText.getText().toString();
                if(!phone.equals("")&&!usre.equals("")&&!pw.equals("")){
                    zc_zt=true;
                    push(usre,pw,phone);
                    handler.sendEmptyMessage(1000);
                }else{
                    Toast.makeText(RegisteredActivity.this,"不能为空",Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }


    /**
     * 构建EasyMqttService对象
     */
    private String myTopic ="iotbroad/iot";
    @Override
    public void buildEasyMqttService() {
        setMyTopic(myTopic);
        mqttService = new EasyMqttService.Builder()
                //设置自动重连
                .autoReconnect(true)
                //设置不清除回话session 可收到服务器之前发出的推送消息
                .cleanSession(false)
                //唯一标示 保证每个设备都唯一就可以 建议 imei
                .clientId(getIMEI(RegisteredActivity.this))
                //mqtt服务器地址 格式例如：tcp://10.0.261.159:1883
//                .serverUrl("tcp://broker.hivemq.com:1883")
                .serverUrl("tcp://broker.hivemq.com:1883")
                //心跳包默认的发送间隔
                .keepAliveInterval(20)
                //构建出EasyMqttService 建议用application的context
                .bulid(this.getApplicationContext());
    }

    public void push( String uname,String pwd,String phone) {

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "registered");
            jsonObject.put("uname", uname);
            jsonObject.put("pwd", pwd);
            jsonObject.put("phone", phone);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegisteredActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    registered.setText("注册中..");
                    handler.sendEmptyMessageDelayed(1001,1000);
                    break;
                case 1001:
                    registered.setText("注册中...");
                    handler.sendEmptyMessageDelayed(1000,1000);
                    break;

                case 1002:
                    handler.removeMessages(1001);
                    handler.removeMessages(1000);
                    registered.setText("注册");
                    Toast.makeText(RegisteredActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1001);
        handler.removeMessages(1000);
        handler.removeMessages(1002);
    }
}
