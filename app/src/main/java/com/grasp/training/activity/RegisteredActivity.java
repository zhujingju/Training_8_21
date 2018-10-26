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

import com.grasp.training.R;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.Tool;

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

    EditText userEditText;
    EditText pwdEditText;
    EditText phoneEditText;
    @BindView(R.id.useryzm_ed)
    EditText useryzmEd;
    @BindView(R.id.useryzm_b)
    Button useryzmB;

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
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() > 7) {
//                    tilUsername.setErrorEnabled(true);//设置是否打开错误提示
//                    tilUsername.setError("用户名长度不能超过8个");//设置错误提示的信息
//                } else {
//                    tilUsername.setErrorEnabled(false);
//                }
                if (s.length() == 0) {
                    userPhone.setErrorEnabled(true);//设置是否打开错误提示
                    userPhone.setError("电话不能为空");//设置错误提示的信息
                } else {
                    userPhone.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        userEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
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
                if (s.length() == 0) {
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


    private boolean zc_zt = false;
    private String name = "";
    private int num_time=0;

    @OnClick({R.id.reg_fh, R.id.registered, R.id.useryzm_b})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.useryzm_b:
                if(num_time==0){
                    useryzmB.setClickable(false);
                    useryzmB.setText("60s");
                    num_time=60;
                    handler.sendEmptyMessageDelayed(666,1000);
                }
                break;
            case R.id.reg_fh:
                finish();
                break;
            case R.id.registered:
                Log.e("qqq", getMyTopic());
                handler.removeMessages(1001);
                handler.removeMessages(1000);
                handler.removeMessages(1002);
                handler.removeMessages(1003);
                zc_zt = false;
                registered.setText("注册");
                if (!isConnected()) {
                    Toast.makeText(RegisteredActivity.this, "连接服务器失败，请重试", Toast.LENGTH_SHORT).show();
                    return;
                }

                String phone = phoneEditText.getText().toString();
                name = userEditText.getText().toString();
                String pw = pwdEditText.getText().toString();
                if (!phone.equals("") && !name.equals("") && !pw.equals("")) {
                    zc_zt = true;
                    push(name, pw, phone);
                    handler.sendEmptyMessage(1000);
                } else {
                    Toast.makeText(RegisteredActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }


    /**
     * 构建EasyMqttService对象
     */
    private String myTopic = "iotbroad/iot/user";

    public void push(String uname, String pwd, String phone) {

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "registered");
            jsonObject.put("uname", uname);
            jsonObject.put("pwd", pwd);
            jsonObject.put("phone", phone);
            jsonObject.put("clientid", Tool.getIMEI(getContext()));
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegisteredActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    registered.setText("注册中..");
                    handler.sendEmptyMessageDelayed(1001, 1000);
                    break;
                case 1001:
                    registered.setText("注册中...");
                    handler.sendEmptyMessageDelayed(1000, 1000);
                    break;

                case 1002:
                    handler.removeMessages(1001);
                    handler.removeMessages(1000);
                    handler.removeMessages(1003);
                    registered.setText("注册");
                    Toast.makeText(RegisteredActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 1003:
                    String s = msg.obj.toString();
                    handler.removeMessages(1001);
                    handler.removeMessages(1000);
                    handler.removeMessages(1002);
                    registered.setText("注册");
                    Toast.makeText(RegisteredActivity.this, "注册失败," + s, Toast.LENGTH_LONG).show();
                    break;
                case 666:
                    num_time--;
                    if(num_time!=0){

                        useryzmB.setText(num_time+"s");
                        handler.sendEmptyMessageDelayed(666,1000);
                    }else{
                        useryzmB.setClickable(true);
                        useryzmB.setText("获取验证码");
                    }



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
        handler.removeMessages(1003);
        handler.removeMessages(666);
    }

    @Override
    public String getMyTopic() {
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
        if (!zc_zt) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    JSONObject jsonF;
                    Message me;
                    String js = "";
                    String channel_0 = "";
                    int var = 0;
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.optString("cmd", "");
                    String mName = jsonObject.optString("uname", "");
                    String clientid = jsonObject.optString("clientid", "");

                    if (!clientid.equals(Tool.getIMEI(getContext()))) {
                        return;
                    }
                    switch (cmd) {
                        case "registered_ok":
                            handler.sendEmptyMessage(1002);
                            break;

                        case "registered_failed":
                            String err = jsonObject.optString("err", "");

                            int errCode = jsonObject.optInt("err", -1);
                            Message m = new Message();
                            m.what = 1003;
                            if (errCode == 1) {
                                m.obj = "用户名已存在";
                            } else if (errCode == 2) {
                                m.obj = "手机号已存在";
                            } else {
                                m.obj = err;
                            }

                            handler.sendMessage(m);

                            break;

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

}
