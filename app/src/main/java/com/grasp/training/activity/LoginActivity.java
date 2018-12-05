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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Player.Core.PlayerClient;
import com.Player.web.websocket.ClientCore;
import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.Umeye_sdk.Constants;
import com.grasp.training.Umeye_sdk.ShowProgress;
import com.grasp.training.service.MqttService;
import com.grasp.training.tool.BaseActivity;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.CreateSecretKey;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.grasp.training.tool.Tool;
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
    @BindView(R.id.login_checkbox)
    CheckBox checkBox;
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

    public static final String c_name="Login_name";
    public static final String c_pw="Login_pw";
    public static final String c_im="Login_im";
    public static final String c_nc="Login_nc";
    private   String c_zt="Login_zw";

    private boolean dl=false;

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
//        CreateSecretKey.main();
        userEditText = tilUsername.getEditText();
        pwdEditText = tilPassword.getEditText();

        userEditText.setText((String) SharedPreferencesUtils.getParam(getContext(),c_name,""));
//        pwdEditText.setText((String) SharedPreferencesUtils.getParam(getContext(),c_pw,""));
        checkBox.setChecked((Boolean) SharedPreferencesUtils.getParam(getContext(),c_zt,false));
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
    @OnClick({R.id.login_zc, R.id.login,R.id.login_im,R.id.login_wjmm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_wjmm://忘记密码
//                Toast.makeText(getContext(),"此功能未上线",Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, RetrievePasswordActivity.class));
                break;
            case R.id.login_im:
//                ha.sendEmptyMessageDelayed(1002,0);

//                startBestServer2();
                break;
            case R.id.login_zc:
                startActivity(new Intent(LoginActivity.this, RegisteredActivity.class));
                break;
            case R.id.login:

                Log.e("qqq", getMyTopic()+" "+isConnected());
                ha.removeMessages(1001);
                ha.removeMessages(1000);
                ha.removeMessages(1002);
                ha.removeMessages(1003);
                zc_zt=false;
                login.setText("LOGIN");
                if (!isConnected()) {
////                    connect();
                    Toast.makeText(LoginActivity.this,"连接服务器失败，请重试",Toast.LENGTH_LONG).show();
                    return;
                }

                name=userEditText.getText().toString();
                pw=pwdEditText.getText().toString();
                if(!name.equals("")&&!pw.equals("")){
                    zc_zt=true;
                    ha.sendEmptyMessage(1000);
//                    startBestServer();
                    push(name,pw);
                }else{
                    Toast.makeText(LoginActivity.this,"不能为空",Toast.LENGTH_LONG).show();
                }


                break;
        }
    }



    /**
     * 新接口，获取最优P2P服务器，然后连接
     */
//    void startBestServer() {
//        clientCore = ClientCore.getInstance();
//        int language = 1;
//        clientCore.setupHost(this, Constants.server, 0, Utility.getImsi(this),
//                language, Constants.CustomName, Utility.getVersionName(this),
//                "");
//        clientCore.getCurrentBestServer(this, handler);
//    }

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
//            ha.sendEmptyMessage(1002);
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            finish();
            push(name,pw);

        }

    };


    void startBestServer2() {
        clientCore = ClientCore.getInstance();
        int language = 1;
        clientCore.setupHost(this, Constants.server, 0, Utility.getImsi(this),
                language, Constants.CustomName, Utility.getVersionName(this),
                "");
        clientCore.getCurrentBestServer(this, handler2);
    }

    private Handler handler2 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            super.handleMessage(msg);
            Log.e("test", "startBestServer");
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        }

    };




    private String myTopic = MqttService.myTopicUser;


    public void push( String uname,String pwd) {

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "login");
            jsonObject.put("uname", uname);

            jsonObject.put("pwd", Tool.MD5(pwd));
            jsonObject.put("clientid", Tool.getIMEI(getContext()));
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
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
                    if(dl){
                       return;
                    }
                    dl=true;
                    ha.removeMessages(1001);
                    ha.removeMessages(1000);
                    ha.removeMessages(1003);
                    login.setText("LOGIN");
                    SharedPreferencesUtils.setParam(getContext(), "Login_pw", pw);
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    break;
                case 1003:
                    ha.removeMessages(1001);
                    ha.removeMessages(1000);
                    ha.removeMessages(1002);
                    login.setText("LOGIN");
                    String s=msg.obj.toString();
                    Toast.makeText(LoginActivity.this,"登录失败"+s,Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

    @Override
    public String  getMyTopic() {
        return myTopic;
    }

    @Override
    public String getMyTopicDing() {
        return myTopic;
    }

    @Override
    public String getSid() {
        return "";
    }

    @Override
    public void MyMessageArrived(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("messageArrived", "login messageArrived  message= " + message);
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
                    String clientid = jsonObject.optString("clientid", "");
                    if (!clientid.equals(Tool.getIMEI(getContext()))) {
                        return;
                    }

                    switch (cmd) {
                        case "login_ok":
                            boolean zt= checkBox.isChecked();
                            SharedPreferencesUtils.setParam(getContext(),c_zt,zt);
//                            if(zt){
                                SharedPreferencesUtils.setParam(getContext(),c_name,name);
//                                SharedPreferencesUtils.setParam(getContext(),c_pw,pw);

//                            }else{
//                                SharedPreferencesUtils.setParam(getContext(),c_name,"");
//                                SharedPreferencesUtils.setParam(getContext(),c_pw,"");
//                            }

                            String mName = jsonObject.optString("uname", "");
                            String nickname = jsonObject.optString("nickname", "");
                            String uthumbnail = jsonObject.optString("uthumbnail", "");
                            SharedPreferencesUtils.setParam(getContext(),MyApplication.NAME_USER,mName);

                            SharedPreferencesUtils.setParam(getContext(),c_im,uthumbnail);
                            SharedPreferencesUtils.setParam(getContext(),c_nc,nickname);
                            ha.sendEmptyMessage(1002);
                            break;
                        case "login_failed":
                            String err = jsonObject.optString("err", "");
                            int errCode = jsonObject.optInt("errCode", -1);
                            Message m=new Message();
                            m.what=1003;
                            if(errCode==4){
                                m.obj="用户名不存在";
                            }
                            else  if(errCode==5){
                                m.obj="用户名和密码不匹配";
                            }
                            else{
                                m.obj=err;
                            }

                            ha.sendMessage(m);
                            break;

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ha.removeMessages(1001);
        ha.removeMessages(1000);
        ha.removeMessages(1002);
        ha.removeMessages(1003);
    }
}
