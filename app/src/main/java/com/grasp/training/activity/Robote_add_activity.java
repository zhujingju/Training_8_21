package com.grasp.training.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Player.Core.PlayerClient;
import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.Umeye_sdk.AcSearchDevice;
import com.grasp.training.Umeye_sdk.Constants;
import com.grasp.training.Umeye_sdk.ShowProgress;
import com.grasp.training.fragmet.SmartHomeMain;
import com.grasp.training.tool.AddSQLiteHelper;
import com.grasp.training.tool.BaseActivity;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by zhujingju on 2017/8/30.
 */

public class Robote_add_activity extends BaseMqttActivity {


    @BindView(R.id.r_a_qx)
    TextView rAQx;
    @BindView(R.id.r_a_qd)
    TextView rAQd;
    @BindView(R.id.r_a_zhong)
    TextView rAZhong;
    //    @BindView(R.id.r_a_name)
    EditText rAName;
    //    @BindView(R.id.r_a_pass)
    EditText rAPass;
    Unbinder unbinder;
    private Context context;
    private String name="";
    private String pass="";

    private PlayerClient client;
    private MyApplication appMain;


    @Override
    public int setLayoutId() {
        return R.layout.robote_add_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context = getContext();
        appMain = (MyApplication) ((Activity)context).getApplication();
        rAName= (EditText) findViewById(R.id.r_a_name);
        rAPass= (EditText) findViewById(R.id.r_a_pass);
//        handler.sendEmptyMessage(1000);
    }

    @Override
    public void initView() {
        Intent in=getIntent();
        String na=in.getStringExtra("umid");
        rAName.setText(na);
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

    private String myTopic = "iotbroad/iot/robotwithuser";
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
//                Log.e("qqq","Robote_add messs="+message);
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.getString("cmd");
                    String uname = jsonObject.optString("uname", "");  //
                    if (!uname.equals(MainActivity.NameUser)) {
                        return;
                    }
                    String clientid = jsonObject.optString("clientid", "");
                    if (!clientid.equals(Tool.getIMEI(getContext()))) {
                        return;
                    }
                    switch (cmd){
                        case "addrobotid_ok":
                            handler.sendEmptyMessageDelayed(1000,0);

                            break;
                        case "addrobotid_failed":
                            handler.sendEmptyMessageDelayed(1001,0);
                            break;
                        case "addrobotid_existed":
                            handler.sendEmptyMessageDelayed(1002,0);
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
        handler.removeMessages(1002);
        super.onDestroy();
    }

    @OnClick({R.id.r_a_qx, R.id.r_a_qd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.r_a_qx:
                if(name!=null){
                    rAPass.setText("");
                    rAName.setText("");
                }
                startActivity(new Intent(context,AcSearchDevice.class));
                finish();

                break;
            case R.id.r_a_qd:
                name=rAName.getText().toString();
                pass=rAPass.getText().toString();
                if(!name.equals("")&&!pass.equals("")){
//                    getDevInfo();
                    if(!pass.equals("admin")){
                        Toast.makeText(context,R.string.srmm1, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    push_add(name);
//                    AddSQLiteHelper dbHelper = new AddSQLiteHelper(Robote_add_activity.this, "add.db", null, 1);
//                    SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//                    String s="select * from AddEquipment where uid='"+name+"'";
////                        Log.d("qqq","s="+s);
//                    Cursor cursor = db.rawQuery(s, null);
//                    int num=0;
//                    while (cursor.moveToNext()){
//                        num++;
//                    }
//                    if (num==0) {
//                        String s1="insert into AddEquipment(uid) values('"+name+"')";
//                        db.execSQL(s1);
//                        if(name!=null){
//                            rAPass.setText("");
//                            rAName.setText("");
//                        }
//                        Toast.makeText(context,
//                                R.string.srmm2, Toast.LENGTH_SHORT).show();
//                        finish();
//                    }else{
//                        Toast.makeText(context,
//                                R.string.ybd, Toast.LENGTH_SHORT).show();
//                    }
                }else{
                    Toast.makeText(context,R.string.kong_err, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    Handler handler=new Handler() {
        String xx;

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    Toast.makeText(context,"添加成功",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 1001:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    Toast.makeText(context,"添加失败",Toast.LENGTH_LONG).show();
                    break;
                case 1002:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    Toast.makeText(context,"添加失败，设备已存在",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private ProgressDialog dialog;

    public void showPro() {

        dialog = new ProgressDialog(this);
        dialog.setMessage("正在添加中...");
        dialog.setCancelable(true);
        dialog.show();
    }


    public void push_add(String uid) {  //添加机器人
//        Log.e("qqq","消息 push_read");
        showPro();
        try {
            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "addrobotid");
            jsonObject.put("uname", MainActivity.NameUser);
            jsonObject.put("clientid", Tool.getIMEI(getContext()));
            jsonObject.put("rid", uid);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }
}
