package com.grasp.training.fragmet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.activity.EquipmentActivity;
import com.grasp.training.activity.LightActivity;
import com.grasp.training.activity.SearchActivity;
import com.grasp.training.activity.SockeActivity;
import com.grasp.training.activity.SwitchActivity;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.BaseMqttFragment;
import com.grasp.training.tool.EquipmentData;
import com.grasp.training.tool.MqttEquipment;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.grasp.training.tool.Tool;
import com.grasp.training.view.HorizontalListView;
import com.grasp.training.view.MyGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SmartHomeMain extends BaseMqttFragment {

    Unbinder unbinder;
    @BindView(R.id.smart_tv_wd)
    TextView smartTvWd;
    @BindView(R.id.smart_tv_sd)
    TextView smartTvSd;
    @BindView(R.id.smart_tv_pm)
    TextView smartTvPm;
    @BindView(R.id.smart_home_MyGridView)
    MyGridView gridView;
    Unbinder unbinder1;
    private String myTopic = "iotbroad/iot/device";
    private Context context;
    private List<Goods> list;
    private myListViewAdapter adapter;
    private int posi = 0;
    private boolean del_zt = false;
    private final String SmartHomeMain = "SmartHomeMain";

    @Override
    public int getInflate() {
        return R.layout.smart_home_main;
    }

    @Override
    public void init(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        context = getActivity();
        doRegisterReceiver();
        head = (LinearLayout) rootView.findViewById(R.id.head);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        //获取PullToRefreshGridView里面的head布局
        head.addView(gridView.getView(), p);
        initListview();

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
    public void MyMessageArrived(final String message) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("qqq", "home messs=" + message);
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
                    switch (cmd) {
                        case "querydevicebyuser_ok":
                            Log.e("qqq", "home messs=" + "querydevicebyuser_ok");
                            SharedPreferencesUtils.setParam(context, SmartHomeMain, message);
                            list = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String sid = jsonObject1.optString("sid", "");//名称
                                String type = jsonObject1.optString("type", "");//类型
                                String thumbnail = jsonObject1.optString("thumbnail", ""); //预览图
                                String state = jsonObject1.optString("state", "");
                                String roomid = jsonObject1.optString("roomid", "");
                                String alive = jsonObject1.optString("alive", "0");
                                String dname = jsonObject1.optString("dname", "");
                                if (roomid.equals("null")) {
                                    roomid = "";
                                }
                                if (thumbnail.equals("null")) {
                                    thumbnail = "";
                                }
                                if (state.equals("null")) {
                                    state = "";
                                }

                                Goods goods = new Goods();
                                goods.setSid(sid);
                                goods.setWz(roomid);
                                goods.setIm_url(thumbnail);
                                goods.setAdd_zt(false);
                                goods.setType(type);
                                if (state.equals("on")) {
                                    goods.setDy(true);
                                } else {
                                    goods.setDy(false);
                                }

                                goods.setName(dname);
//                                Log.e("qqq","oldMap="+newMap);
                                if (oldMap != null) {
                                    if (oldMap.get(sid) != null) {
                                        goods.setJh_zt(true);
                                    } else {
                                        goods.setJh_zt(false);
                                    }
                                }
                                if (oldState != null) {
                                    if (oldState.get(sid) != null) {
                                        goods.setDy(oldState.get(sid));
                                    }
                                }

                                list.add(goods);

                            }
                            Goods goods = new Goods();
                            goods.setAdd_zt(true);
                            list.add(goods);
                            handler.sendEmptyMessageDelayed(1000, 0);
                            break;

                        case "deletedevice_ok":
                            handler.sendEmptyMessageDelayed(2000, 0);

                            break;

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public void add(ArrayList<String> slist) {
        if (MainActivity.NameUser.equals("")) {
            Toast.makeText(context, getString(R.string.uid_no), Toast.LENGTH_LONG).show();
            return;
        }
        Intent in = new Intent(context, SearchActivity.class);
        in.putStringArrayListExtra("sid_List", slist);
        context.startActivity(in);
    }



    @Override
    public void onDestroyView() {
        Map_del();
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
        }
        handler.removeMessages(1000);
        handler.removeMessages(2000);
        handler.removeMessages(123);
        equimentHandler.removeMessages(1000);
        equimentHandler.removeMessages(2000);
        equimentHandler.removeMessages(3000);
        image_hander.removeMessages(1000);
        image_hander.removeMessages(1001);
        image_hander.removeMessages(1002);
        super.onDestroyView();

        unbinder.unbind();
    }

    @OnClick(R.id.smart_hone_main_add)
    public void onViewClicked() {
        add(new ArrayList<String>());
    }


    public void push_read() {  //获取设备列表
//        Log.e("qqq","消息 push_read");
        try {
            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "querydevicebyuser");
            jsonObject.put("uname", MainActivity.NameUser);
            jsonObject.put("clientid", Tool.getIMEI(getContext()));
            String js = jsonObject.toString();

            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        setEquipmentData();

        handler.sendEmptyMessageDelayed(123, 0);
    }

    private LinearLayout head;

    private void initListview() {
        gridView.setCacheColorHint(0);
        adapter = new myListViewAdapter(context, list);
//        dataListview();

        dataL();


        gridView.setAdapter(adapter);
        gridView.setonRefreshListener(new MyGridView.OnRefreshListener() { //刷新

            @Override
            public void onRefresh() {
                handler.removeMessages(123);
                handler.sendEmptyMessageDelayed(123, 0);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("qqq", "i=" + i);
                //删除设备
                if (i + 1 == list.size()) {

                } else {
                    posi = i;
                    del();

                }
                return true;
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (i + 1 == list.size()) {
                    ArrayList<String> arrayList = new ArrayList();
                    for (int j = 0; j < list.size(); j++) {
                        arrayList.add(list.get(i).getSid());
                    }
                    add(arrayList);
                } else {
                    if (list.get(i).getType().equals("socket")) {
                        SockeActivity.starstSockeActivity(context, list.get(i).getSid(), list.get(i).getName());
                    } else if (list.get(i).getType().equals("switch")) {
                        SwitchActivity.starstEquipmentActivity(context, list.get(i).getSid(), list.get(i).getType(), list.get(i).getName());
                    } else if (list.get(i).getType().equals("light")) {
                        LightActivity.starstEquipmentActivity(context, list.get(i).getSid(), list.get(i).getType(), list.get(i).getName());
                    } else {
                        EquipmentActivity.starstEquipmentActivity(context, list.get(i).getSid(), list.get(i).getType(), list.get(i).getName());

                    }
                }
            }
        });


    }


    public void dataListview() {  //获取list数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                subscribe();
                push_read();
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 123:
                    Log.e("qqq", "home=" + 123);
                    handler.removeMessages(123);
                    Log.e("qqq", "home=" + isConnected());
                    if (isConnected()) {
                        dataListview();
                    } else {
                        handler.sendEmptyMessageDelayed(123, 1000);
                    }


                    break;

                case 1000:
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                    gridView.onRefreshComplete();
                    equimentHandler.removeMessages(3000);
                    equimentHandler.sendEmptyMessageDelayed(3000, 0);

                    break;
                case 2000:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, getString(R.string.progress_pergood), Toast.LENGTH_LONG).show();
                    handler.sendEmptyMessageDelayed(123, 0);
                    break;
            }
        }
    };


    private void del() {   //删除

        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setTitle(getString(R.string.shuru2) + "“"
                + list.get(posi).getName() + "”");
        builder.setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                delData();
            }
        });

        builder.setNegativeButton(
                getString(R.string.alert_dialog_cancel), null);
        builder.show();
    }


    private void delData() {
        push_del();
    }


    private ProgressDialog dialog;

    public void showPro() {

        dialog = new ProgressDialog(context);
        dialog.setMessage("删除中...");
        dialog.setCancelable(true);

        dialog.show();
    }

    public void push_del() {  //删除机器人
//        Log.e("qqq","消息 push_read");

        showPro();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "deletedevice");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    jsonObject.put("sid", list.get(posi).getSid());
                    String js = jsonObject.toString();

                    publish_String(js);  //主题
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }

    class myListViewAdapter extends BaseAdapter {

        LayoutInflater inflater = null;
        private Context context;
        private List<Goods> list = null;

        public myListViewAdapter(Context context, List<Goods> list) {
            this.context = context;
            this.list = list;
            inflater = ((Activity) (context)).getLayoutInflater();
        }


        public List<Goods> getList() {
            return list;
        }


        public void setList(List<Goods> list) {
            this.list = list;
        }


        @Override
        public int getCount() {
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        @Override
        public Goods getItem(int arg0) {
            if (list != null) {
                return list.get(arg0);
            }
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = convertView;
            Goods camera = getItem(position);
            ListViewTool too;
            if (convertView == null) {
                view = inflater.inflate(R.layout.items_smart_home, parent, false);
                too = new ListViewTool();
//
                too.name = (TextView) view.findViewById(R.id.items_smart_home_tv1);
                too.wz = (TextView) view.findViewById(R.id.items_smart_home_tv2);
                too.dy = (TextView) view.findViewById(R.id.items_smart_home_tv3);
                too.im = (ImageView) view.findViewById(R.id.items_smart_home_im);
                too.layout2 = (LinearLayout) view.findViewById(R.id.items_smart_home_rel2);
                too.layout = (RelativeLayout) view.findViewById(R.id.items_smart_home_rel);

                view.setTag(too);
            } else {
                too = (ListViewTool) view.getTag();
            }
            if (camera == null) return null;
            // too.del.setOnClickListener(click);

            //  too.up.setTag(position);
            if (camera.isAdd_zt()) {
                too.layout.setVisibility(View.GONE);
                too.layout2.setVisibility(View.VISIBLE);
            } else {
                too.layout.setVisibility(View.VISIBLE);
                too.layout2.setVisibility(View.GONE);
                too.name.setText(camera.getName());
                too.wz.setText(camera.getWz());
                if (camera.isDy()) {
                    too.dy.setText("状态：开启");
                } else {
                    too.dy.setText("状态：关闭");
                }
                if (camera.isJh_zt()) {
                    too.name.setTextColor(getResources().getColor(R.color.c_000000));
                    too.wz.setTextColor(getResources().getColor(R.color.c_b32f54e9));
                    too.dy.setTextColor(getResources().getColor(R.color.c_000000));
                    too.wz.setText("在线");
                } else {
                    too.name.setTextColor(getResources().getColor(R.color.main_hui));
                    too.wz.setTextColor(getResources().getColor(R.color.main_hui));
                    too.dy.setTextColor(getResources().getColor(R.color.main_hui));
                    too.wz.setText("离线");
                }
                if (camera.getIm_url().equals("")) {
                    too.im.setBackgroundResource(R.color.white);
                } else {
                    ImageLoader.getInstance().displayImage(camera.getIm_url(), too.im, MyApplication.options2);
                }

            }

            return view;
        }

        class ListViewTool {

            public TextView name, wz, dy;
            public ImageView im;
            public RelativeLayout layout;
            public LinearLayout layout2;
        }
    }


    class Goods {
        private String name;//名称
        private String wz;//位置
        private boolean dy; //是否开启
        private boolean add_zt;
        private String Im_url;
        private String Sid;
        private String type;
        private boolean jh_zt;  //设备是否在线

        public boolean isJh_zt() {
            return jh_zt;
        }

        public void setJh_zt(boolean jh_zt) {
            this.jh_zt = jh_zt;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSid() {
            return Sid;
        }

        public void setSid(String sid) {
            Sid = sid;
        }

        public String getIm_url() {
            return Im_url;
        }

        public void setIm_url(String im_url) {
            Im_url = im_url;
        }

        public boolean isAdd_zt() {
            return add_zt;
        }

        public void setAdd_zt(boolean add_zt) {
            this.add_zt = add_zt;
        }

        public String getWz() {
            return wz;
        }

        public void setWz(String wz) {
            this.wz = wz;
        }

        public boolean isDy() {
            return dy;
        }

        public void setDy(boolean dy) {
            this.dy = dy;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    private ArrayList<EquipmentData> elist;
    private HashMap<String, String> eMap;

    private void setEquipmentData() {
        String data = SharedPreferencesUtils.getParam(getContext(), MainActivity.MainData, "").toString();
//        Log.e("qqq","data: "+data);
        elist = new ArrayList<>();
        eMap = new HashMap<>();
        if (!data.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray js = jsonObject.getJSONArray("data");
                for (int i = 0; i < js.length(); i++) {
                    JSONObject jsonObject1 = js.getJSONObject(i);
                    String dname = jsonObject1.optString("dname", "");//名称
                    String type = jsonObject1.optString("type", "");//类型
                    String thumbnail = jsonObject1.optString("dname", ""); //预览图
                    String stateall = jsonObject1.optString("stateall", "");
                    EquipmentData equipmentData = new EquipmentData();
                    equipmentData.setDname(dname);
                    equipmentData.setType(type);
                    equipmentData.setStateall(stateall);
                    equipmentData.setThumbnail(thumbnail);
                    elist.add(equipmentData);
                    eMap.put(type, dname);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }


    private void dataL() {
        String message = SharedPreferencesUtils.getParam(context, SmartHomeMain, "").toString();
        list = new ArrayList<>();
        if (!message.equals("")) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(message);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String sid = jsonObject1.optString("sid", "");//名称
                    String type = jsonObject1.optString("type", "");//类型
                    String thumbnail = jsonObject1.optString("thumbnail", ""); //预览图
                    String state = jsonObject1.optString("state", "");
                    String roomid = jsonObject1.optString("roomid", "");
                    String alive = jsonObject1.optString("alive", "0");
                    String dname = jsonObject1.optString("dname", "");
                    if (roomid.equals("null")) {
                        roomid = "";
                    }
                    if (thumbnail.equals("null")) {
                        thumbnail = "";
                    }
                    if (state.equals("null")) {
                        state = "";
                    }

                    Goods goods = new Goods();
                    goods.setSid(sid);
                    goods.setWz(roomid);
                    goods.setIm_url(thumbnail);
                    goods.setAdd_zt(false);
                    goods.setType(type);
                    if (state.equals("on")) {
                        goods.setDy(true);
                    } else {
                        goods.setDy(false);
                    }

                    goods.setName(dname);

                    goods.setJh_zt(false);
                    list.add(goods);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Goods goods = new Goods();
        goods.setAdd_zt(true);
        list.add(goods);
        handler.sendEmptyMessageDelayed(1000, 0);


    }


    HashMap<String, String> oldMap;   //备份的map
    HashMap<String, String> newMap;   //使用的map
    HashMap<String, Boolean> oldState;   //备份的map
    HashMap<String, Boolean> newState;   //使用的map
    HashMap<String, MqttEquipment> MqttEquipmentMap;  //存放MqttEquipment的map
//    HashMap<String, MqttEquipment> oldMqttEquipmentMap;  //放菜单中的数据
    Goods goods;

    public void getJh() {
//        int a=0;
//        if(a==0){
//            return;
//        }


        if (MqttEquipmentMap == null) {
        MqttEquipmentMap = new HashMap<>();
        }

//        if(newMap==null){
        newMap = new HashMap<>();
        newState = new HashMap<>();
//        }
        if (oldMap == null) {
            oldMap = new HashMap<>();
        }
        if (oldState == null) {
            oldState = new HashMap<>();
        }
        for (int i = 0; i < list.size(); i++) {
            goods = list.get(i);

            if (goods.getSid() == null) {
                break;
            }
            Log.e("qqq","mqttEquipment "+goods.getSid());
            if (MqttEquipmentMap.get(goods.getSid()) == null) {
                String type = goods.getType();
                String myTopic = "iotbroad/iot/" + type + "/" + goods.getSid();
                String myTopicding = "iotbroad/iot/" + type + "_ack/" + goods.getSid();
                String sid = goods.getSid();
                String url = goods.getIm_url();
                MqttEquipment mqttEquipment = new MqttEquipment(context, sid, type, myTopic, myTopicding, url) {
                    @Override
                    public void MyMessageArrived(final String message) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jsonObject = new JSONObject(message);
                                    String cmd = jsonObject.getString("cmd");
                                    String mSid = jsonObject.optString("sid", "");  //设备号
                                    if (!mSid.equals(getSid())) {
                                        return;
                                    }
                                    String type = getType();
//                                    Log.e("qqq","mqttEquipment sid= "+getSid());
//                                    Log.e("qqq","mqttEquipment type= "+type);
                                    if (cmd.equals("wifi_" + type + "_ack")) {
//                                        Log.e("qqq","mqttEquipment message= "+message);
                                        newMap.put(getSid(), type);
                                        oldMap.put(getSid(), type);
                                        String state = jsonObject.optString("state", "");  //
                                        boolean state_zt;
                                        if (state.equals("on")) {
                                            state_zt = true;
                                        } else {
                                            state_zt = false;
                                        }
                                        newState.put(getSid(), state_zt);
                                        oldState.put(getSid(), state_zt);
                                        Message m = new Message();
                                        m.what = 1000;
                                        m.obj = getSid();
                                        equimentHandler.sendMessage(m);
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                };
//                Log.e("qqq","mqttEquipment cun "+mqttEquipment.getSid()+" "+mqttEquipment.getType());

                mqttEquipment.setName(goods.getName());

                MqttEquipmentMap.put(goods.getSid(), mqttEquipment);
//                mqttEquipment.onDestroy();

            }
        }
        //发送
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (MqttEquipmentMap == null) {

                    return;
                }
                if (MqttEquipmentMap.size() == 0) {

                    return;
                }
                for (HashMap.Entry<String, MqttEquipment> entry : MqttEquipmentMap.entrySet()) {

                    MqttEquipment e = entry.getValue();
//                    Log.e("qqq","goods fa "+e.getSid()+" "+e.getType());
                    e.publish_String(push_read(e.getType(), e.getSid()));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        }).start();


        equimentHandler.sendEmptyMessageDelayed(2000, 1000 * MqttEquipmentMap.size() + 2000);
    }

    public void Map_del() {

        if (MqttEquipmentMap != null) {
            for (HashMap.Entry<String, MqttEquipment> entry : MqttEquipmentMap.entrySet()) {
                MqttEquipment e = entry.getValue();
                e.onDestroy();
            }
        }

        if (manager != null) {
            manager.cancel(0);
        }
        if (in_manager != null) {
            in_manager.cancel(0);
        }

    }

    public String push_read(String type, String sid) {  //获取状态

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_" + type + "_read");
            jsonObject.put("sid", sid);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
            return "";
        }

    }

    Handler equimentHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000://改变单个
//                    Log.e("qqq","mqttEquipment message= "+1000);
                    for (int i = 0; i < list.size(); i++) {
                        String sid = list.get(i).getSid();
                        if (sid == null) {
                            break;
                        }
                        if (sid.equals(msg.obj.toString())) {
                            if (newState.get(sid) != null) {
                                list.get(i).setDy(newState.get(sid));
                            }
                            list.get(i).setJh_zt(true);
                            adapter.setList(list);
                            adapter.notifyDataSetChanged();
                            return;
                        }
                    }


                    break;
                case 2000:
//                    if (oldMqttEquipmentMap == null) {
//                        oldMqttEquipmentMap = new HashMap<>();
//                    }
                    oldMap = newMap;
                    Log.e("qqq","oldMap.size()="+oldMap.size()+" newMap.size()= "+newMap.size());
                    oldState = newState;
                    for (int i = 0; i < list.size(); i++) {

                        String sid = list.get(i).getSid();
                        if (sid == null) {
                            break;
                        }
                        if (oldMap.get(sid) != null) {
                            list.get(i).setJh_zt(true);
//                            if (MqttEquipmentMap.get(sid) != null) {
//                                oldMqttEquipmentMap.put(sid, MqttEquipmentMap.get(sid));
//                            }
                        }else{
                            list.get(i).setJh_zt(false);
                        }
                        if (oldState.get(sid) != null) {
                            list.get(i).setDy(newState.get(sid));
                        }

                    }
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                    if (manager == null) {
                        dataNotification();
                    }
                    sxNotification();
                    break;
                case 3000:
                    Log.e("qqq", "goods " + 3000);
                    equimentHandler.removeMessages(3000);
                    getJh();
                    equimentHandler.sendEmptyMessageDelayed(3000, 2 * 60 * 1000);
                    break;
            }
        }
    };
    private RemoteViews contentView;
    private Notification notification;
    private NotificationManager manager;

    public void dataNotification() {

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification.Builder(context).setSmallIcon(R.drawable.icon).build();
//        notification.flags|= Notification.FLAG_ONGOING_EVENT;
        sendNotification();
    }

    /**
     * 发送通知
     */
    public void sendNotification() {
        Log.e("qqq", "dataNotification ");
        contentView = new RemoteViews(context.getPackageName(), R.layout.data_notification);
        notification.contentView = contentView;
//        manager.notify(3, notification);
    }

    public static final String BT_REFRESH_ACTION = "BT_REFRESH_ACTION";
    public static final String COLLECTION_VIEW_ACTION = "COLLECTION_VIEW_ACTION";
    public static final String COLLECTION_VIEW_EXTRA = "COLLECTION_VIEW_EXTRA";

    private void sxNotification() {

        if (MqttEquipmentMap != null && contentView != null && notification != null && manager != null) {
            Log.e("qqq", "oldMap.s=" + oldMap.size());
            if (MqttEquipmentMap.size() == 0) {
                return;
            }
            int i = 0;
            for (HashMap.Entry<String, MqttEquipment> entry : MqttEquipmentMap.entrySet()) {
                MqttEquipment e = entry.getValue();
                String type = e.getType();
                String sid = e.getSid();
                if (oldMap != null) {
                    if (oldMap.get(sid) != null) {

                        if (type != null && !type.equals("")) {  //添加菜单栏
                            if (type.equals("socket") || type.equals("switch") || type.equals("light")) {
//                    if (type.equals("switch") || type.equals("light")) {
                                if (i == 0) {
                                    contentView.setTextViewText(R.id.data_notification_tv1, e.getName());
                                    setImage1(e.getIm_url());
                                    Intent btIntent = new Intent().setAction(BT_REFRESH_ACTION);
                                    btIntent.putExtra("sid", e.getSid());
                                    btIntent.putExtra("type", e.getType());
                                    btIntent.putExtra("num", 1);
//                            if (oldState != null && oldState.get(e.getSid()) != null) {
//                                btIntent.putExtra("state", oldState.get(e.getSid()));
//                            }

                                    PendingIntent btPendingIntent = PendingIntent.getBroadcast(context, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    contentView.setOnClickPendingIntent(R.id.data_notification_layout1, btPendingIntent);
                                } else if (i == 1) {
                                    contentView.setTextViewText(R.id.data_notification_tv2, e.getName());
                                    setImage2(e.getIm_url());
                                    Intent btIntent = new Intent().setAction(COLLECTION_VIEW_ACTION);
                                    btIntent.putExtra("sid", e.getSid());
                                    btIntent.putExtra("type", e.getType());
                                    btIntent.putExtra("num", 2);
//                            if (oldState != null && oldState.get(e.getSid()) != null) {
//                                btIntent.putExtra("state", oldState.get(e.getSid()));
//                            }
                                    PendingIntent btPendingIntent = PendingIntent.getBroadcast(context, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    contentView.setOnClickPendingIntent(R.id.data_notification_layout2, btPendingIntent);
                                } else if (i == 2) {
                                    contentView.setTextViewText(R.id.data_notification_tv3, e.getName());
                                    setImage3(e.getIm_url());
                                    Intent btIntent = new Intent().setAction(COLLECTION_VIEW_EXTRA);
                                    btIntent.putExtra("sid", e.getSid());
                                    btIntent.putExtra("type", e.getType());
                                    btIntent.putExtra("num", 3);
//                            if (oldState != null && oldState.get(e.getSid()) != null) {
//                                btIntent.putExtra("state", oldState.get(e.getSid()));
//                            }
                                    PendingIntent btPendingIntent = PendingIntent.getBroadcast(context, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    contentView.setOnClickPendingIntent(R.id.data_notification_layout3, btPendingIntent);
                                }
                                i++;
                            }
                    }
                }

            }

        }
        notification.contentView = contentView;
        manager.notify(3, notification);
    }
}


    private Bitmap bitmap1, bitmap2, bitmap3;

    private void setImage1(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bitmap1 = getURLimage(url);
                image_hander.sendEmptyMessageDelayed(1000, 0);
            }
        }).start();
    }

    private void setImage2(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bitmap2 = getURLimage(url);
                image_hander.sendEmptyMessageDelayed(1001, 0);
            }
        }).start();
    }


    private void setImage3(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bitmap3 = getURLimage(url);
                image_hander.sendEmptyMessageDelayed(1002, 0);
            }
        }).start();
    }

    Handler image_hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    if (bitmap1 != null) {
                        contentView.setImageViewBitmap(R.id.data_notification_im1, bitmap1);
                        notification.contentView = contentView;
                        manager.notify(3, notification);
                    }

                    break;
                case 1001:
                    if (bitmap2 != null) {
                        contentView.setImageViewBitmap(R.id.data_notification_im2, bitmap2);
                        notification.contentView = contentView;
                        manager.notify(3, notification);
                    }
                    break;
                case 1002:
                    if (bitmap3 != null) {
                        contentView.setImageViewBitmap(R.id.data_notification_im3, bitmap3);
                        notification.contentView = contentView;
                        manager.notify(3, notification);

                    }
                    break;
            }
        }
    };


    //加载图片
    public Bitmap getURLimage(String url1) {
//        Log.e("qqq", "dataNotification e=" + url1);
        try {
            URL url = new URL(url1);
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setConnectTimeout(6000);// 设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);// 不缓存
            conn.connect();
            int code = conn.getResponseCode();
            Bitmap bitmap = null;
            if (code == 200) {
                InputStream is = conn.getInputStream();// 获得图片的数据流
                bitmap = BitmapFactory.decodeStream(is);
            }
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ContentReceiver mReceiver;
    private MyServiceConn conn;

    private void doRegisterReceiver() {
        mReceiver = new ContentReceiver();
        IntentFilter filter = new IntentFilter(
                "BT_REFRESH_ACTION");
        context.registerReceiver(mReceiver, filter);
        IntentFilter filter2 = new IntentFilter(
                "COLLECTION_VIEW_ACTION");
        context.registerReceiver(mReceiver, filter2);
        IntentFilter filter3 = new IntentFilter(
                "COLLECTION_VIEW_EXTRA");
        context.registerReceiver(mReceiver, filter3);

        IntentFilter filter4 = new IntentFilter(
                socketInNotification1);
        context.registerReceiver(mReceiver, filter4);

        IntentFilter filter5 = new IntentFilter(
                socketInNotification2);
        context.registerReceiver(mReceiver, filter5);

        IntentFilter filter6 = new IntentFilter(
                swithInNotification1);
        context.registerReceiver(mReceiver, filter6);

        IntentFilter filter7 = new IntentFilter(
                swithInNotification2);
        context.registerReceiver(mReceiver, filter7);
        IntentFilter filter8 = new IntentFilter(
                swithInNotification3);
        context.registerReceiver(mReceiver, filter8);
        IntentFilter filter9 = new IntentFilter(
                swithInNotification4);
        context.registerReceiver(mReceiver, filter9);

        IntentFilter filter10 = new IntentFilter(
                lightInNotification1);
        context.registerReceiver(mReceiver, filter10);

        IntentFilter filter11 = new IntentFilter(
                lightInNotification2);
        context.registerReceiver(mReceiver, filter11);

        IntentFilter filter12 = new IntentFilter(
                lightInNotification3);
        context.registerReceiver(mReceiver, filter12);
    }

public class ContentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String sid = intent.getStringExtra("sid");
        String type = intent.getStringExtra("type");
        Log.e("qqq", "onReceive type=" + type);
        int num = intent.getIntExtra("num", -1);
        if (num == -10) {  //第二次
            if (type.equals("socket")) {
                boolean state = intent.getBooleanExtra("state", false);
                if (MqttEquipmentMap.get(sid) != null) {
                    MqttEquipment e = MqttEquipmentMap.get(sid);
                    e.publish_String(push_socke(e.getType(), e.getSid(), state));
                }
            } else if (type.equals("switch")) {
                boolean state = intent.getBooleanExtra("state", false);
                int channel = intent.getIntExtra("channel", -1);
                if (MqttEquipmentMap.get(sid) != null && channel != -1) {
                    Log.e("qqq", "onReceive type=fa");
                    MqttEquipment e = MqttEquipmentMap.get(sid);
                    e.publish_String(push_switch(e.getType(), e.getSid(), state, channel));
                }

            } else if (type.equals("light")) {
                int blight = intent.getIntExtra("blight", -1);
                if (MqttEquipmentMap.get(sid) != null) {
                    MqttEquipment e = MqttEquipmentMap.get(sid);
                    Log.e("qqq", "onReceive blight=" + blight);
                    if (blight != -1) {
                        e.publish_String(push_light(e.getType(), e.getSid(), blight));
                    } else {
                        e.publish_String(push_light(e.getType(), e.getSid(), false));
                    }

                }

            }

        } else {
            Bitmap bitmap = null;
            if (num == 1) {
                bitmap = bitmap1;
            } else if (num == 2) {
                bitmap = bitmap2;
            } else if (num == 3) {
                bitmap = bitmap3;
            }
            if (sid != null && !sid.equals("")) {  //具体操作
                Log.e("qqq", "sid=" + sid);
                if (type != null) {
                    if (type.equals("socket")) {
                        if (in_manager == null) {
                            inNotification();
                        }
                        socketInNotification(bitmap, sid, type);
                    } else if (type.equals("switch")) {
                        if (in_manager == null) {
                            inNotification();
                        }
                        swithInNotification(bitmap, sid, type);

                    } else if (type.equals("light")) {
                        if (in_manager == null) {
                            inNotification();
                        }
                        lightInNotification(bitmap, sid, type);
                    }
                }

            }
        }


    }

}


    private RemoteViews in_contentView;
    private Notification in_notification;
    private NotificationManager in_manager;

    public void inNotification() {

        in_manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        in_notification = new Notification.Builder(context).setSmallIcon(R.drawable.icon).build();
//        notification.flags|= Notification.FLAG_ONGOING_EVENT;
    }


    private final static String socketInNotification1 = "socketInNotification1";
    private final static String socketInNotification2 = "socketInNotification2";
    private final static String swithInNotification1 = "swithInNotification1";
    private final static String swithInNotification2 = "swithInNotification2";
    private final static String swithInNotification3 = "swithInNotification3";
    private final static String swithInNotification4 = "swithInNotification4";
    private final static String lightInNotification1 = "swithInNotification1";
    private final static String lightInNotification2 = "swithInNotification2";
    private final static String lightInNotification3 = "swithInNotification3";

    /**
     * 发送通知
     */
    public void socketInNotification(Bitmap bitmap, String sid, String type) {  //插座
        Log.e("qqq", "dataInNotification ");
        in_contentView = new RemoteViews(context.getPackageName(), R.layout.socket_in_notification);
        in_notification.contentView = in_contentView;
        in_manager.notify(6, in_notification);
        if (bitmap != null) {
            in_contentView.setImageViewBitmap(R.id.socket_in_notification_im1, bitmap);

            Intent btIntent = new Intent().setAction(socketInNotification1);
            btIntent.putExtra("sid", sid);
            btIntent.putExtra("type", type);
            btIntent.putExtra("num", -10);
            btIntent.putExtra("state", true);
            PendingIntent btPendingIntent = PendingIntent.getBroadcast(context, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.socket_in_notification_im2, btPendingIntent);

            Intent btIntent2 = new Intent().setAction(socketInNotification2);
            btIntent2.putExtra("sid", sid);
            btIntent2.putExtra("type", type);
            btIntent2.putExtra("num", -10);
            btIntent2.putExtra("state", false);
            PendingIntent btPendingIntent2 = PendingIntent.getBroadcast(context, 0, btIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.socket_in_notification_im3, btPendingIntent2);

            in_notification.contentView = in_contentView;
            in_manager.notify(6, in_notification);
        }

    }


    public String push_socke(String type, String sid, boolean state) {  //获取状态

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_" + type);
            if (state) {
                jsonObject.put("state", "on");
            } else {
                jsonObject.put("state", "off");
            }

            jsonObject.put("sid", sid);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
            return "";
        }
    }


    public void swithInNotification(Bitmap bitmap, String sid, String type) {  //开关
        Log.e("qqq", "swithInNotification ");
        in_contentView = new RemoteViews(context.getPackageName(), R.layout.swith_in_notification);
        in_notification.contentView = in_contentView;
        in_manager.notify(6, in_notification);
        if (bitmap != null) {
            in_contentView.setImageViewBitmap(R.id.switch_in_notification_im1, bitmap);

            Intent btIntent = new Intent().setAction(swithInNotification1);
            btIntent.putExtra("sid", sid);
            btIntent.putExtra("type", type);
            btIntent.putExtra("num", -10);
            btIntent.putExtra("state", true);
            btIntent.putExtra("channel", 1);
            PendingIntent btPendingIntent = PendingIntent.getBroadcast(context, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.switch_in_notification_im2, btPendingIntent);

            Intent btIntent2 = new Intent().setAction(swithInNotification2);
            btIntent2.putExtra("sid", sid);
            btIntent2.putExtra("type", type);
            btIntent2.putExtra("num", -10);
            btIntent2.putExtra("state", false);
            btIntent2.putExtra("channel", 1);
            PendingIntent btPendingIntent2 = PendingIntent.getBroadcast(context, 0, btIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.switch_in_notification_im3, btPendingIntent2);

            Intent btIntent3 = new Intent().setAction(swithInNotification3);
            btIntent3.putExtra("sid", sid);
            btIntent3.putExtra("type", type);
            btIntent3.putExtra("num", -10);
            btIntent3.putExtra("state", true);
            btIntent3.putExtra("channel", 2);
            PendingIntent btPendingIntent3 = PendingIntent.getBroadcast(context, 0, btIntent3, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.switch_in_notification_im4, btPendingIntent3);

            Intent btIntent4 = new Intent().setAction(swithInNotification4);
            btIntent4.putExtra("sid", sid);
            btIntent4.putExtra("type", type);
            btIntent4.putExtra("num", -10);
            btIntent4.putExtra("state", false);
            btIntent4.putExtra("channel", 2);
            PendingIntent btPendingIntent4 = PendingIntent.getBroadcast(context, 0, btIntent4, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.switch_in_notification_im5, btPendingIntent4);
            in_notification.contentView = in_contentView;
            in_manager.notify(6, in_notification);
        }

    }


    public String push_switch(String type, String sid, boolean state, int channel) {

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_" + type);
            if (state) {
                jsonObject.put("state", "on");
            } else {
                jsonObject.put("state", "off");
            }
            jsonObject.put("channel", channel);
            jsonObject.put("sid", sid);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
            return "";
        }
    }


    public void lightInNotification(Bitmap bitmap, String sid, String type) {  //灯
        Log.e("qqq", "dataInNotification ");
        in_contentView = new RemoteViews(context.getPackageName(), R.layout.light_in_notification);
        in_notification.contentView = in_contentView;
        in_manager.notify(6, in_notification);
        if (bitmap != null) {
            in_contentView.setImageViewBitmap(R.id.light_in_notification_im1, bitmap);

            Intent btIntent = new Intent().setAction(lightInNotification1);
            btIntent.putExtra("sid", sid);
            btIntent.putExtra("type", type);
            btIntent.putExtra("num", -10);
            btIntent.putExtra("blight", 80);
            PendingIntent btPendingIntent = PendingIntent.getBroadcast(context, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.light_in_notification_im2, btPendingIntent);

            Intent btIntent2 = new Intent().setAction(lightInNotification2);
            btIntent2.putExtra("sid", sid);
            btIntent2.putExtra("type", type);
            btIntent2.putExtra("num", -10);
            btIntent2.putExtra("blight", 30);
            PendingIntent btPendingIntent2 = PendingIntent.getBroadcast(context, 0, btIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.light_in_notification_im3, btPendingIntent2);


            Intent btIntent3 = new Intent().setAction(lightInNotification3);
            btIntent3.putExtra("sid", sid);
            btIntent3.putExtra("type", type);
            btIntent3.putExtra("num", -10);
            PendingIntent btPendingIntent3 = PendingIntent.getBroadcast(context, 0, btIntent3, PendingIntent.FLAG_UPDATE_CURRENT);
            in_contentView.setOnClickPendingIntent(R.id.light_in_notification_im4, btPendingIntent3);

            in_notification.contentView = in_contentView;
            in_manager.notify(6, in_notification);
        }

    }


    public String push_light(String type, String sid, boolean state) {

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_" + type);
            if (state) {
                jsonObject.put("state", "on");
            } else {
                jsonObject.put("state", "off");
            }

            jsonObject.put("sid", sid);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
            return "";
        }
    }


    public String push_light(String type, String sid, int blight) {

        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_" + type + "_setok");
            jsonObject.put("sid", sid);
            jsonObject.put("blight", blight);
            jsonObject.put("alight", 0);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
            return "";
        }
    }
}
