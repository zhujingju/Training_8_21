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
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.Tool;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetPhoneActivity extends BaseMqttActivity {
    @BindView(R.id.cp_fh)
    ImageView cpFh;
    @BindView(R.id.sp_tv1)
    TextView spTv1;
    @BindView(R.id.userPhone)
    TextInputLayout userPhone;
    @BindView(R.id.useryzm_ed)
    EditText useryzmEd;
    @BindView(R.id.useryzm_b)
    Button useryzmB;
    @BindView(R.id.rpa_b)
    Button rpaB;//更改手机号

    private String myTopic = "iotbroad/iot/user";

    private Context context;
    EditText phoneEditText;
    @Override
    public int setLayoutId() {
        return R.layout.set_phone_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context = getContext();

    }

    @Override
    public void initView() {
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

    }

    @Override
    public void init() {

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
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.optString("cmd", "");
                    String uname = jsonObject.optString("uname", "");
                    if (!uname.equals(MainActivity.NameUser)) {
                        return;
                    }
                    String clientid = jsonObject.optString("clientid", "");
                    if (!clientid.equals(Tool.getIMEI(getContext()))) {
                        return;
                    }
                    switch (cmd) {
                        case "updateuser_ok":

                            handler.sendEmptyMessageDelayed(1000, 500);

                            break;
                        case "updateuser_failed":
                            String err = jsonObject.getString("err");
                            Message m = new Message();
                            m.what = 1001;
                            m.obj = err;
                            handler.sendMessageDelayed(m, 500);

                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "修改成功！", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 1001:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "修改失败！" + msg.obj.toString(), Toast.LENGTH_LONG).show();
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
        handler.removeMessages(1000);
        handler.removeMessages(1001);
        handler.removeMessages(666);
    }


    public void push_name(final String phone,final String yzm) { //修改名称
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "updateuser");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    JSONObject jsonObjects = new JSONObject();
                    jsonObjects.put("phone", phone);

                    jsonObject.put("modify", jsonObjects);


                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private ProgressDialog dialog;

    public void showPro() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("更改中...");
        dialog.setCancelable(true);
        dialog.show();
    }

    private int num_time=0;
    @OnClick({R.id.cp_fh, R.id.useryzm_b, R.id.rpa_b})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cp_fh:
                finish();
                break;
            case R.id.useryzm_b:
                if(num_time==0){
                    useryzmB.setClickable(false);
                    useryzmB.setText("60s");
                    num_time=60;
                    handler.sendEmptyMessageDelayed(666,1000);
                }
                break;
            case R.id.rpa_b:
                String phone=phoneEditText.getText().toString();
                String yzm=useryzmEd.getText().toString();
                if(phone.equals("")){
                    Toast.makeText(context,"手机号不能为空",Toast.LENGTH_LONG).show();
                }else{
                    if(yzm.equals("")){
                        Toast.makeText(context,"验证码不能为空",Toast.LENGTH_LONG).show();
                    }else{
                        showPro();
                        push_name(phone,yzm);
                    }
                }

                break;
        }
    }
}
