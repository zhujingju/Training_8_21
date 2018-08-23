package com.grasp.training.activity;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.Player.Core.PlayerClient;
import com.Player.web.websocket.ClientCore;
import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.Umeye_sdk.Constants;
import com.grasp.training.Umeye_sdk.ShowProgress;
import com.grasp.training.tool.BaseActivity;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.Utility;
import com.grasp.training.tool.myActivityManage;
import com.zs.easy.mqtt.EasyMqttService;
import com.zs.easy.mqtt.IEasyMqttCallBack;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseMqttActivity {
    @BindView(R.id.til_username)
    TextInputLayout tilUsername;
    @BindView(R.id.til_password)
    TextInputLayout tilPassword;
    @BindView(R.id.login)
    Button login;
    EditText userEditText;
    EditText pwdEditText;
    @BindView(R.id.login_y)
    ConstraintLayout loginY;
    @BindView(R.id.login_zc)
    TextView loginZc;
    private ClientCore clientCore;
    private PlayerClient playClient;
    private MyApplication appMain;

    @Override
    public int setLayoutId() {
        return R.layout.login_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {

        userEditText = tilUsername.getEditText();
        pwdEditText = tilPassword.getEditText();
        initPlay();
    }

    @Override
    public void initObject() {
//EditText添加文本变化监听
        userEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() ==0) {
                    tilUsername.setErrorEnabled(true);//设置是否打开错误提示
                    tilUsername.setError("用户名不能为空");//设置错误提示的信息
                } else {
                    tilUsername.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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
    public void initListener() {

    }

    @Override
    public void init() {

    }




    ShowProgress pd;

    void initPlay() {
        appMain = (MyApplication) this.getApplicationContext();
        playClient = appMain.getPlayerclient();
//        startBestServer();
    }



    private String name ="";
    private String pw="";
    private boolean zc_zt=false;
    @OnClick({R.id.login_zc, R.id.login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_zc:
                startActivity(new Intent(LoginActivity.this, RegisteredActivity.class));
                break;
            case R.id.login:

                Log.e("qqq", getMyTopic());
                ha.removeMessages(1001);
                ha.removeMessages(1000);
                ha.removeMessages(1002);
                zc_zt=false;
                login.setText("LOGIN");
//                if (!isConnected()) {
//                    Toast.makeText(LoginActivity.this,"连接服务器失败，请重试",Toast.LENGTH_SHORT).show();
//                    return;
//                }else{
//                    connect();
//                }

                name=userEditText.getText().toString();
                pw=pwdEditText.getText().toString();
                if(!name.equals("")&&!pw.equals("")){
                    zc_zt=true;
                    ha.sendEmptyMessage(1000);
                    startBestServer();
                }else{
                    Toast.makeText(LoginActivity.this,"不能为空",Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }


    /**
     * 新接口，获取最优P2P服务器，然后连接
     */
    void startBestServer() {
        clientCore = ClientCore.getInstance();
        int language = 1;
        clientCore.setupHost(this, Constants.server, 0, Utility.getImsi(this),
                language, Constants.CustomName, Utility.getVersionName(this),
                "");
        clientCore.getCurrentBestServer(this, handler);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            super.handleMessage(msg);
            Log.e("test", "startBestServer");
//            ha.sendEmptyMessageDelayed(222, 500);
//            if (pd != null) {
//                pd.dismiss();
//            }
            ha.sendEmptyMessage(1002);
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            finish();
//            push(name,pw);

        }

    };


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
                        if(!zc_zt){
                            return;
                        }
                        try {
                            JSONObject jsonF;
                            Message me;
                            String js = "";
                            String channel_0 = "";
                            int var = 0;
                            JSONObject jsonObject = new JSONObject(message);
                            String cmd = jsonObject.getString("cmd");


                            switch (cmd) {
                                case "login_ok":
                                    String mName = jsonObject.optString("uname", "");
                                    if (!mName.equals(name)) {
                                        return;
                                    }
                                    ha.sendEmptyMessage(1002);
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
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

    /**
     * 构建EasyMqttService对象
     */
    private String myTopic ="iotbroad/iot/login";
    @Override
    public void buildEasyMqttService() {
        setMyTopic(myTopic);
        mqttService = new EasyMqttService.Builder()
                //设置自动重连
                .autoReconnect(true)
                //设置不清除回话session 可收到服务器之前发出的推送消息
                .cleanSession(false)
                //唯一标示 保证每个设备都唯一就可以 建议 imei
                .clientId(getIMEI(LoginActivity.this))
                //mqtt服务器地址 格式例如：tcp://10.0.261.159:1883
//                .serverUrl("tcp://broker.hivemq.com:1883")
                .serverUrl("tcp://192.168.31.60:3000")
                //心跳包默认的发送间隔
                .keepAliveInterval(20)
                //构建出EasyMqttService 建议用application的context
                .bulid(this.getApplicationContext());
    }

    public void push( String uname,String pwd) {

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "login");
            jsonObject.put("uname", uname);
            jsonObject.put("pwd", pwd);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }



    Handler ha=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    login.setText("LOGIN..");
                    ha.sendEmptyMessageDelayed(1001,1000);
                    break;
                case 1001:
                    login.setText("LOGIN...");
                    ha.sendEmptyMessageDelayed(1000,1000);
                    break;

                case 1002:
                    ha.removeMessages(1001);
                    ha.removeMessages(1000);
                    login.setText("LOGIN");
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    break;
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ha.removeMessages(1001);
        ha.removeMessages(1000);
        ha.removeMessages(1002);
    }
}
