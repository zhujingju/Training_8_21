package com.grasp.training.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class SetPersonalActivity extends BaseMqttActivity {
    @BindView(R.id.sp_tv1)
    TextView tv1;
    @BindView(R.id.sp_tv2)
    TextView tv2;
    @BindView(R.id.cp_fh)
    ImageView cpFh;
    @BindView(R.id.sp_ed)
    EditText spEd;
    @BindView(R.id.personal_lin1)
    LinearLayout personalLin1;  //修改昵称
    private String myTopic = MqttService.myTopicUser;

    private Context context;
    private String nc;
    private int tpye=0;
    public final static String stastSetPersonal="stastSetPersonal";

    public static void stastSetPersonal(Context context,int tpye ){
        Intent in=new Intent(context,SetPersonalActivity.class);
        in.putExtra(stastSetPersonal,tpye);
        context.startActivity(in);
    }

    @Override
    public int setLayoutId() {
        return R.layout.set_personal_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context = getContext();
        tpye=getIntent().getIntExtra(stastSetPersonal,0);
        if(tpye==0){//昵称
            tv1.setText("更改昵称");
            tv2.setText("昵称：");
        }else  if(tpye==1){ //手机号
            tv1.setText("更改手机号");
            tv2.setText("手机号：");
        }

    }

    @Override
    public void initView() {

    }

    @Override
    public void initObject() {

    }

    @Override
    public void initListener() {
        spEd.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    nc=spEd.getText().toString();
                    if(!nc.equals("")){
                        showPro();
                        push_name();
                    }else{
                        Toast.makeText(context, "不能为空！", Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
                return false;
            }

        });
    }

    @Override
    public void init() {
        nc= SharedPreferencesUtils.getParam(context,LoginActivity.c_nc,"").toString();
        if(tpye==0){//昵称
            spEd.setText(nc);
        }else  if(tpye==1){ //手机号
        }
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
                            if(tpye==0){
                                JSONObject js=jsonObject.getJSONObject("modify");
                                String nickname=js.optString("nickname", "");
                                SharedPreferencesUtils.setParam(context,LoginActivity.c_nc,nickname);
                            }

                            handler.sendEmptyMessageDelayed(1000,500);

                            break;
                        case "updateuser_failed":
                            String err=jsonObject.getString("err");
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


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "修改成功！", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 1001:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "修改失败！"+msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1000);
    }


    public void push_name() { //修改名称
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
                    if(tpye==0){//昵称
                        jsonObjects.put("nickname", nc);
                    }else  if(tpye==1){ //手机号
                        jsonObjects.put("phone", nc);
                    }

                    jsonObject.put("modify",jsonObjects);


                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
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


    @OnClick(R.id.cp_fh)
    public void onViewClicked() {
        finish();
    }
}
