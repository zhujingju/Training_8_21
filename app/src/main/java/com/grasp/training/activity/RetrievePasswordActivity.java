package com.grasp.training.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.service.MqttService;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.Tool;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RetrievePasswordActivity extends BaseMqttActivity {
    @BindView(R.id.rpa_fh)
    ImageView rpaFh;
    @BindView(R.id.userPhone)
    TextInputLayout userPhone;
    @BindView(R.id.useryzm_ed)
    EditText useryzmEd;
    @BindView(R.id.useryzm_b)
    Button useryzmB;
    @BindView(R.id.til_password)
    TextInputLayout tilPassword;
    @BindView(R.id.rpa_b)
    Button rpaB;
    private String myTopic = MqttService.myTopicUser;
    private Context context;
    EditText pwdEditText;
    EditText phoneEditText;

    @Override
    public int setLayoutId() {
        return R.layout.retrieve_password_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context = getContext();

    }

    @Override
    public void initView() {
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
                    userPhone.setError("手机号不能为空");//设置错误提示的信息
                }
                else if(s.length() != 11) {
                    userPhone.setErrorEnabled(true);//设置是否打开错误提示
                    userPhone.setError("手机号不正确");//设置错误提示的信息
                }
                else {
                    userPhone.setErrorEnabled(false);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1001);
        handler.removeMessages(1000);
        handler.removeMessages(1002);
        handler.removeMessages(1003);
        handler.removeMessages(666);
        handler.removeMessages(2000);
        handler.removeMessages(2001);
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
                        case "retrievepwd_ok":
                            handler.sendEmptyMessage(1002);
                            break;

                        case "retrievepwd_failed":
                            String err = jsonObject.optString("err", "");

                            int errCode = jsonObject.optInt("errCode", -1);
                            Message m = new Message();
                            m.what = 1003;
                            if (errCode == 6) {
                                m.obj = "验证码错误";
                            } else if (errCode == 7) {
                                m.obj = "验证码超时";
                            } else {
                                m.obj = err;
                            }

                            handler.sendMessage(m);

                            break;

                        case "code_ok":
                            handler.sendEmptyMessage(2000);
                            break;
                        case "code_failed":
                            String err2 = jsonObject.optString("err", "");

                            int errCode2 = jsonObject.optInt("errCode", -1);
                            Message m2 = new Message();
                            m2.what = 2001;
                            if (errCode2 == 10) {
                                m2.obj = "这个手机号未注册";
                            } else {
                                m2.obj = err2;
                            }

                            handler.sendMessage(m2);
                            break;

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private int num_time = 0;

    @OnClick({R.id.rpa_fh, R.id.useryzm_b, R.id.rpa_b})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rpa_fh:
                finish();
                break;
            case R.id.useryzm_b:
                if (num_time == 0) {
                    String phone = phoneEditText.getText().toString();
                    if (!phone.equals("")) {
                        if (phone.length() == 11) {
                            push_yzm(phone);
                        } else {
                            Toast.makeText(getContext(), "请输入正确的手机号", Toast.LENGTH_LONG).show();
                            return;
                        }
                    } else {
                        Toast.makeText(getContext(), "手机号不能为空", Toast.LENGTH_LONG).show();
                        return;
                    }
                    useryzmB.setClickable(false);
                    useryzmB.setText("60s");
                    num_time = 60;
                    handler.sendEmptyMessageDelayed(666, 1000);
                }
                break;
            case R.id.rpa_b:

                String phone = phoneEditText.getText().toString();
                String yzm = useryzmEd.getText().toString();
                String newPw = pwdEditText.getText().toString();

                if (phone.equals("")) {
                    Toast.makeText(context, "手机号不能为空", Toast.LENGTH_LONG).show();
                } else {
                    if (yzm.equals("")) {
                        Toast.makeText(context, "验证码不能为空", Toast.LENGTH_LONG).show();
                    } else {
                        if (newPw.equals("")) {
                            Toast.makeText(context, "新密码不能为空", Toast.LENGTH_LONG).show();
                        } else {
                            //要改
                            if (phone.length() == 11) {
                            } else {
                                Toast.makeText(getContext(), "请输入正确的手机号", Toast.LENGTH_LONG).show();
                                return;
                            }
                            push_name(phone, yzm, newPw);
                            sowPhro();
                        }
                    }
                }
                break;
        }
    }

    public void push_yzm(String phone) {

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "code");
            jsonObject.put("phone", phone);
            jsonObject.put("clientid", Tool.getIMEI(getContext()));
            jsonObject.put("codeflag", 1);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void push_name(final String phone, final String yzm, final String newPw) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "retrievepwd");
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    jsonObject.put("phone", phone);
                    jsonObject.put("code", yzm);
                    jsonObject.put("newpwd", Tool.MD5(newPw));

                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private ProgressDialog dialog;

    public void sowPhro() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("修改中...");
        dialog.setCancelable(true);
        dialog.show();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1002:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "修改成功！", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 1003:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "修改失败！" + msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;

                case 666:
                    num_time--;
                    if (num_time != 0) {

                        useryzmB.setText(num_time + "s");
                        handler.sendEmptyMessageDelayed(666, 1000);
                    } else {
                        useryzmB.setClickable(true);
                        useryzmB.setText("获取验证码");
                    }


                    break;

                case 2000:
                    Toast.makeText(context, "获取验证码成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2001:
                    handler.removeMessages(666);
                    num_time=0;
                    useryzmB.setClickable(true);
                    useryzmB.setText("获取验证码");
                    Toast.makeText(context, "获取验证码失败，" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
