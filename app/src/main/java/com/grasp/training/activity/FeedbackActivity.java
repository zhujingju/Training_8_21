package com.grasp.training.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.service.MqttService;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.grasp.training.tool.Tool;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends BaseMqttActivity {//用户反馈

    @BindView(R.id.cp_fh)
    ImageView cpFh;
    @BindView(R.id.fk_ed)
    EditText fkEd;
    @BindView(R.id.fk_fs)
    Button fkFs;
    @BindView(R.id.personal_lin1)
    LinearLayout personalLin1;
    private String myTopic = MqttService.myTopicUser;
    private Context context;


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
    public int setLayoutId() {
        return R.layout.feedback_activity;
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

    }

    @Override
    public void init() {

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
                        case "feedback_ok":
                            handler.sendEmptyMessageDelayed(1000,500);

                            break;
                        case "feedback_failed":
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


    @Override
    protected void onDestroy() {
        handler.removeMessages(1000);
        handler.removeMessages(1001);
        super.onDestroy();
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
                    Toast.makeText(context, "发送反馈成功！", Toast.LENGTH_LONG).show();
                    break;
                case 1001:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    break;
            }
        }
    };

    @OnClick({R.id.cp_fh, R.id.fk_fs})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cp_fh:
                finish();
                break;
            case R.id.fk_fs:
                if(!fkEd.getText().toString().equals("")){
                    showPro();
                    push_fk(fkEd.getText().toString());
                }else{
                    Toast.makeText(context,"内容不能为空",Toast.LENGTH_LONG).show();
                }

                break;
        }
    }


    public void push_fk(final String mess) { //
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "feedback");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    jsonObject.put("content",mess);
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
        dialog.setMessage("发送中...");
        dialog.setCancelable(true);
        dialog.show();
    }
}
