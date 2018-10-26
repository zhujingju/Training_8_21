package com.grasp.training.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.grasp.training.tool.Tool;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends BaseMqttActivity {


    @BindView(R.id.cp_fh)
    ImageView cpFh;
    @BindView(R.id.personal_ed2)
    EditText personalEd2;
    @BindView(R.id.personal_lin1)
    LinearLayout personalLin1;
    @BindView(R.id.personal_xian1)
    TextView personalXian1;
    @BindView(R.id.personal_ed3)
    EditText personalEd3;
    @BindView(R.id.personal_lin2)
    LinearLayout personalLin2;
    @BindView(R.id.personal_xian2)
    TextView personalXian2;
    @BindView(R.id.personal_ed4)
    EditText personalEd4;
    @BindView(R.id.personal_lin3)
    LinearLayout personalLin3;
    @BindView(R.id.personal_xian3)
    TextView personalXian3;
    private String myTopic = "iotbroad/iot/user";
    private Context context;

    private String newPw;
    private String oldPw;
    @Override
    public int setLayoutId() {
        return R.layout.change_password_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context=getContext();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initObject() {

    }

    @Override
    public void initListener() {

        personalEd2.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    String pass = (String) SharedPreferencesUtils.getParam(context, "Login_pw", "");
                    oldPw=pass;
                    String input=personalEd2.getText().toString();
//                    Log.d("qqq","setOnEditorActionListener");
                    if (input.equals(pass) ) {
                        personalLin1.setVisibility(View.GONE);
                        personalLin2.setVisibility(View.VISIBLE);
                        personalLin3.setVisibility(View.VISIBLE);

                        personalXian1.setVisibility(View.GONE);
                        personalXian2.setVisibility(View.VISIBLE);
                        personalXian3.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(context, "原始密码错误重新输入！", Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
                return false;
            }

        });


        personalEd4.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    String p1=personalEd3.getText().toString();
                    String p2=personalEd4.getText().toString();
                    if(p1.equals("")){
                        Toast.makeText(context, "不能为空！", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    if(p1.equals(p2)){
                        newPw=p1;
                        showPro();
                        push_name();
//                        personalLin1.setVisibility(View.VISIBLE);
//                        personalLin2.setVisibility(View.GONE);
//                        personalLin3.setVisibility(View.GONE);
//
//                        personalXian1.setVisibility(View.VISIBLE);
//                        personalXian2.setVisibility(View.GONE);
//                        personalXian3.setVisibility(View.GONE);
//                        personalEd2.setText("");
//                        personalEd3.setText("");
//                        personalEd4.setText("");
//                        finish();
//                        SharedPreferencesUtils.getParam(context, "Login_pw", newPw);
//                        Toast.makeText(context, "修改成功！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(context, "密码不一致！", Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
                return false;
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
                        case "updatepwd_ok":
                            handler.sendEmptyMessageDelayed(1000,500);

                            break;
                        case "updatepwd_failed":
                            String err = jsonObject.optString("err", "");
                            Message m=new Message();
                            m.what=1001;
                            m.obj=err;
                            handler.sendMessageDelayed(m,500);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    SharedPreferencesUtils.setParam(context, "Login_pw", newPw);
                    Toast.makeText(context, "修改成功！", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 1001:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    Toast.makeText(context,msg.obj.toString(),Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1000);
        handler.removeMessages(1001);
    }

    @OnClick(R.id.cp_fh)
    public void onViewClicked() {
        finish();
    }

    public void push_name() { //修改名称
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "updatepwd");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    jsonObject.put("pwd",oldPw);
                    jsonObject.put("newpwd",newPw);
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
}
