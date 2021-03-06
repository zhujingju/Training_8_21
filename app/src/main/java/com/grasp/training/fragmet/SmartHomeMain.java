package com.grasp.training.fragmet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.location.Location;
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
import com.grasp.training.activity.RoomActivity;
import com.grasp.training.activity.SearchActivity;
import com.grasp.training.activity.SockeActivity;
import com.grasp.training.activity.SwitchActivity;
import com.grasp.training.activity.WebViewActivity;
import com.grasp.training.service.MqttService;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.BaseMqttFragment;
import com.grasp.training.tool.EquipmentData;
import com.grasp.training.tool.Goods;
import com.grasp.training.tool.LocationUtils;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    @BindView(R.id.smart_home_sx_lin)
    LinearLayout smartSxLin;
    @BindView(R.id.smart_home_sx_tv)
    TextView smartSxTv;

    Unbinder unbinder1;
    private String myTopic = MqttService.myTopicDevice;
    private Context context;
    private List<Goods> list;
    private myListViewAdapter adapter;
    private int posi = 0;
    private boolean del_zt = false;
    private final String SmartHomeMain = "SmartHomeMain";
    private String city = "";

    @Override
    public int getInflate() {
        return R.layout.smart_home_main;
    }

    @Override
    public void init(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
//        Log.e("qqq","doRegisterReceiver  onDestroyView  init");
//        Log.e("qqq","doRegisterReceiver  init");
//        if(MqttEquipmentMap!=null){
//            Log.e("size","MqttEquipmentMap.s="+MqttEquipmentMap.size());
//            for (HashMap.Entry<String, MqttEquipment> entry : MqttEquipmentMap.entrySet()) {
//                MqttEquipment e = entry.getValue();
//                if(!e.mReceiverTag){
//                    e.initV();
//                }
//
//            }
//        }

        context = getActivity();
        city = (String) SharedPreferencesUtils.getParam(context, "city_main", "");
        head = (LinearLayout) rootView.findViewById(R.id.head);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        //获取PullToRefreshGridView里面的head布局
        head.addView(gridView.getView(), p);
        initListview();
        handler.sendEmptyMessageDelayed(966,100);

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
//                Log.e("qqq", "home messs=" + message);
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.getString("cmd");
                    String uname = jsonObject.optString("uname", "");  //
                    if (!uname.equals(MainActivity.NameUser)) {
                        return;
                    }
                    String clientid = jsonObject.optString("clientid", "");
                    if (!clientid.equals(Tool.getIMEI(getContext()))) {
                        Log.e("qqq", "home clientid=" + clientid);
                        return;
                    }
                    switch (cmd) {
                        case "querydevicebyuser_ok":
//                            Log.e("qqq", "home messs=" + "querydevicebyuser_ok");
                            SharedPreferencesUtils.setParam(context, MainActivity.NameUser, message);
                            setJson(message);
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
        LocationUtils.unregister();
//        Log.e("qqq","doRegisterReceiver  onDestroyView");
        Map_del();
        tqHander.removeMessages(1000);
        handler.removeMessages(1000);
        handler.removeMessages(2000);
        handler.removeMessages(123);
        handler.removeMessages(966);
        equimentHandler.removeMessages(1000);
        equimentHandler.removeMessages(2000);
        equimentHandler.removeMessages(3000);
        equimentHandler.removeMessages(4000);
        equimentHandler.removeMessages(5000);
        equimentHandler.removeMessages(6000);
        super.onDestroyView();

        unbinder.unbind();
    }

    @OnClick({R.id.smart_hone_main_add,R.id.smart_hame_sx,R.id.smart_home_sx_lin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.smart_hone_main_add:
                add(new ArrayList<String>());
                break;
            case R.id.smart_hame_sx:
                RoomActivity.startActivity(context,null,fj);
                break;
            case R.id.smart_home_sx_lin:
                fj="";
                list=oldlist;
                adapter.setList(list);
                adapter.notifyDataSetChanged();
                gridView.onRefreshComplete();
                smartSxLin.setVisibility(View.GONE);
                smartSxTv.setText(fj);
                break;
        }


    }



    private String fj="";
    private List<Goods> oldlist;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode==((Activity)context).RESULT_OK){

                    zt_rel=true;
                    fj=data.getStringExtra("name");
                    ArrayList<Goods> mlist=new ArrayList<>();
                    for(Goods g:oldlist){
//                        Log.e("qqq","-------- fj="+fj+"  room="+g.getRoom());
                        String room=g.getRoom();
                        if(fj.equals(room)){

                            mlist.add(g);
                        }
                    }
                    list=mlist;
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                    gridView.onRefreshComplete();
                    smartSxLin.setVisibility(View.VISIBLE);
                    smartSxTv.setText(fj);
                }
                break;
        }

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
        }
    }


    private boolean zt_rel;
    @Override
    public void onStart() {
        super.onStart();
        Log.e("qqq","-------- fj= onStart zt_rel="+zt_rel);
        if(zt_rel){
            zt_rel=false;
            return;
        }
        setEquipmentData();
        if (!city.equals("")) {
            new Thread(tq).start();
        } else {
            city = "北京";
            new Thread(tq).start();
        }

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
                    for (int j = 0; j < list.size() - 1; j++) {
                        arrayList.add(list.get(j).getSid());
                    }

//                    for (String s : arrayList) {
//
//                        Log.e("qqq","arrayList s="+s);
//                    }
                    add(arrayList);
                } else {
                    if (list.get(i).getType().equals("socket")) {
                        SockeActivity.starstSockeActivity(context, list.get(i).getSid(), list.get(i).getName());
//                        WebViewActivity.starstEquipmentActivity(context, list.get(i).getSid(), list.get(i).getType(), list.get(i).getName());
                    } else if (list.get(i).getType().equals("switch")) {

                        SwitchActivity.starstEquipmentActivity(context, list.get(i).getSid(), list.get(i).getType(), list.get(i).getName());
                    } else if (list.get(i).getType().equals("light")) {
                        LightActivity.starstEquipmentActivity(context, list.get(i).getSid(), list.get(i).getType(), list.get(i).getName());
                    } else {
//                        EquipmentActivity.starstEquipmentActivity(context, list.get(i).getSid(), list.get(i).getType(), list.get(i).getName());
                        if (list != null && list.get(i).getWeburl() != null && !list.get(i).getWeburl().equals("")) {
                            WebViewActivity.starstEquipmentActivity(context, list.get(i).getSid(), list.get(i).getType(), list.get(i).getName(), list.get(i).getWeburl());
                        } else {
                            Toast.makeText(context, "获取数据不正确，请稍后再试", Toast.LENGTH_LONG).show();
                        }

                    }
                }
            }
        });


    }


    boolean da_zt = true;

    public void setHua(boolean zt) {
        da_zt = zt;
        if (da_zt) {
            if (!city.equals("")) {
                new Thread(tq).start();
            }
            dataListview();
        }
    }

    boolean sx_zt = true;

    public void dataListview() {  //获取list数据
        if(gridView==null){
            return;
        }
        if (!sx_zt) {

            gridView.onRefreshComplete();
            return;
        }
        equimentHandler.removeMessages(1000);
        equimentHandler.removeMessages(2000);
        equimentHandler.removeMessages(4000);
        equimentHandler.removeMessages(5000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                sx_zt = false;
//                subscribe();
                push_read();
                try {
                    Thread.sleep(1000);
                    sx_zt = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                    if(!fj.equals("")){
                        ArrayList<Goods> mlist=new ArrayList<>();
                        for(Goods g:oldlist){
//                        Log.e("qqq","-------- fj="+fj+"  room="+g.getRoom());
                            String room=g.getRoom();
                            if(fj.equals(room)){

                                mlist.add(g);
                            }
                        }
                        list=mlist;
                    }


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
                case 966:
                    Log.e("tianqi", "isLocationEnabled=" + LocationUtils.isLocationEnabled(context) + " LocationUtils.isGpsEnabled(context)=" + LocationUtils.isGpsEnabled(context));
                    if (LocationUtils.isLocationEnabled(context)) {

                        if (LocationUtils.isGpsEnabled(context)) {

                        } else {
//                setGps();
                            Log.e("tianqi", "isGpsEnabled？？？");
                        }

                        boolean register_zt=LocationUtils.register(context, 2000, 1, new LocationUtils.OnLocationChangeListener() {
                            @Override
                            public void getLastKnownLocation(Location location) {
                                Log.e("tianqi", "getLastKnownLocation");
                                double Latitude = location.getLatitude();  //维度
                                double Longitude = location.getLongitude();
                                city = LocationUtils.getLocality(context, Latitude, Longitude);
                                SharedPreferencesUtils.setParam(context, "city_main", city);
                                Log.e("tianqi", "Latitude=" + Latitude + " Longitude=" + Longitude + " city=" + city);
                                if (city == null) {
                                    city = "北京";

                                } else {
                                    if (city.equals("unknown")) {
                                        city = "北京";
                                    }
                                }
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(3000);
                                            LocationUtils.unregister();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
//
                                new Thread(tq).start();
                            }

                            @Override
                            public void onLocationChanged(Location location) {
                                Log.e("tianqi", "onLocationChanged");

                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {
                                Log.e("tianqi", "onStatusChanged");
                            }
                        });
                        if(!register_zt){
                            dl_num++;
                            if(dl_num<4){
                                handler.sendEmptyMessageDelayed(966,5000);
                            }

                        }
                    } else {

                        city = "北京";
                    }


                    break;
            }
        }
    };
int dl_num=0;

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
                if (camera.getType().equals("switchsl") || camera.getType().equals("switch") || camera.getType().equals("switchdh")) {
                    if (camera.getSk1() != null) {
                        too.dy.setVisibility(View.VISIBLE);
                        too.dy.setText("状态：" + camera.getSk1());
                    } else {
                        too.dy.setVisibility(View.INVISIBLE);
                    }

                } else {
                    too.dy.setVisibility(View.VISIBLE);
                    if (camera.isDy()) {
                        too.dy.setText("状态：开启");
                    } else {
                        too.dy.setText("状态：关闭");
                    }
                    if (camera.getType().equals("kettle")){
                        too.dy.setVisibility(View.INVISIBLE);
                    }else{
                        too.dy.setVisibility(View.VISIBLE);
                    }
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
                    ImageLoader.getInstance().displayImage(camera.getIm_url(), too.im, MyApplication.options);
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
        String message = SharedPreferencesUtils.getParam(context, MainActivity.NameUser, "").toString();
        setJson(message);


    }

    private void setJson(String json) {
        try {
            list = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(json);
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
                String weburl = jsonObject1.optString("weburl", "");
                String roomname=jsonObject1.optString("roomname", "");
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
                goods.setRoom(roomname);
                goods.setSid(sid);
                goods.setWz(roomid);
                goods.setIm_url(thumbnail);
                goods.setAdd_zt(false);
                goods.setType(type);
                goods.setWeburl(weburl);
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
                if (oldswitchState != null) {
                    if (oldswitchState.get(sid) != null) {
                        goods.setSk1(oldswitchState.get(sid));
                    }
                }

                list.add(goods);

            }
            Goods goods = new Goods();
            goods.setAdd_zt(true);
            list.add(goods);
            oldlist=list;
            handler.sendEmptyMessageDelayed(1000, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    HashMap<String, String> oldMap;   //备份的map
    HashMap<String, String> newMap;   //使用的map
    HashMap<String, Boolean> oldState;   //备份的map
    HashMap<String, Boolean> newState;   //使用的map
    HashMap<String, MqttEquipment> MqttEquipmentMap;  //存放MqttEquipment的map
    HashMap<String, String> switchState;
    HashMap<String, String> oldswitchState;
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
        switchState = new HashMap<>();
//        }
        if (oldswitchState == null) {
            oldswitchState = new HashMap<>();
        }
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
            Log.e("qqq", "mqttEquipment " + goods.getSid());
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

                                        if (type.equals("switch")) {
                                            String switch_s = "";
                                            String channel_1 = jsonObject.optString("channel_2", "");  //
                                            String channel_2 = jsonObject.optString("channel_1", "");  //
                                            if (channel_1.equals("on")) {
                                                switch_s += "开启";
                                            } else {
                                                switch_s += "关闭";
                                            }
                                            if (channel_2.equals("on")) {
                                                switch_s += "，开启";
                                            } else {
                                                switch_s += "，关闭";
                                            }
                                            switchState.put(getSid(), switch_s);
                                            oldswitchState.put(getSid(), switch_s);
                                        }

                                        if (type.equals("switchdh")) {
                                            String switch_s = "";
                                            String channel_1 = jsonObject.optString("channel_2", "");  //
                                            String channel_2 = jsonObject.optString("channel_1", "");  //
                                            if (channel_1.equals("on")) {
                                                switch_s += "开启";
                                            } else {
                                                switch_s += "关闭";
                                            }
                                            if (channel_2.equals("on")) {
                                                switch_s += "，开启";
                                            } else {
                                                switch_s += "，关闭";
                                            }
                                            switchState.put(getSid(), switch_s);
                                            oldswitchState.put(getSid(), switch_s);
                                        }

                                        if (type.equals("switchsl")) {
                                            String switch_s = "";
                                            String channel_1 = jsonObject.optString("channel_1", "");  //
                                            String channel_2 = jsonObject.optString("channel_2", "");  //
                                            String channel_3 = jsonObject.optString("channel_3", "");  //
                                            if (channel_1.equals("on")) {
                                                switch_s += "开";
                                            } else {
                                                switch_s += "关";
                                            }
                                            if (channel_2.equals("on")) {
                                                switch_s += "，开";
                                            } else {
                                                switch_s += "，关";
                                            }

                                            if (channel_3.equals("on")) {
                                                switch_s += "，开";
                                            } else {
                                                switch_s += "，关";
                                            }
                                            switchState.put(getSid(), switch_s);
                                            oldswitchState.put(getSid(), switch_s);
                                        }

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
        equimentHandler.sendEmptyMessageDelayed(6000, 0);

    }

    public void Map_del() {

        if (MqttEquipmentMap != null) {
            for (HashMap.Entry<String, MqttEquipment> entry : MqttEquipmentMap.entrySet()) {
                MqttEquipment e = entry.getValue();
                e.onDestroy();
            }
        }
        MqttEquipmentMap=null;
//        if (manager != null) {
//            manager.cancel(0);
//        }
//        if (in_manager != null) {
//            in_manager.cancel(0);
//        }

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
            return "";
        }

    }

    Handler equimentHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000://改变单个
                    Message message = new Message();
                    message.what = 4000;
                    message.obj = msg.obj;
                    equimentHandler.sendMessageDelayed(message, 100);
                    break;
                case 2000:
                    equimentHandler.sendEmptyMessageDelayed(5000, 500);
                    break;
                case 3000:
                    Log.e("qqq", "goods " + 3000);
                    equimentHandler.removeMessages(3000);
                    getJh();
                    equimentHandler.sendEmptyMessageDelayed(3000, 5 * 60 * 1000);
                    break;
                case 4000:
                    //                    Log.e("qqq","mqttEquipment message= "+1000);
                    if (msg.obj.toString() == null) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        String sid = list.get(i).getSid();
                        if (sid == null) {
                            break;
                        }
                        if (sid.equals(msg.obj.toString())) {
                            if (newState.get(sid) != null) {
                                list.get(i).setDy(newState.get(sid));
                            }
                            if (switchState.get(sid) != null) {
                                list.get(i).setSk1(switchState.get(sid));
                            }
                            list.get(i).setJh_zt(true);
                            if (sx_zt && da_zt) {
                                adapter.setList(list);
                                adapter.notifyDataSetChanged();
                            }

                            return;
                        }
                    }
                    break;
                case 5000:
//                    if (oldMqttEquipmentMap == null) {
//                        oldMqttEquipmentMap = new HashMap<>();
//                    }
                    oldMap = newMap;
                    Log.e("qqq", "man oldMap.size()=" + oldMap.size() + " newMap.size()= " + newMap.size());
                    oldState = newState;
                    oldswitchState = switchState;
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
                        } else {
                            list.get(i).setJh_zt(false);
                        }
                        if (oldState.get(sid) != null) {
                            list.get(i).setDy(oldState.get(sid));
                        }
                        if (oldswitchState.get(sid) != null) {
                            list.get(i).setSk1(oldswitchState.get(sid));
                        }

                    }
                    if (sx_zt && da_zt) {
                        adapter.setList(list);
                        adapter.notifyDataSetChanged();
                    }
                    break;

                case 6000:
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
                            ArrayList<String> MqttEquipmentList = new ArrayList();
                            for (HashMap.Entry<String, MqttEquipment> entry : MqttEquipmentMap.entrySet()) {
                                MqttEquipmentList.add(entry.getKey());
                            }
                            for (String s : MqttEquipmentList) {
                                if(MqttEquipmentMap==null){
                                    return;
                                }
                                if (MqttEquipmentMap.get(s) != null) {
                                    MqttEquipment e = MqttEquipmentMap.get(s);
                                    String myTopicding = "iotbroad/iot/" + e.getType() + "_ack/" + e.getSid();
                                    e.subscribe(myTopicding);
                                    e.publish_String(push_read(e.getType(), e.getSid()));
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e1) {
                                        e1.printStackTrace();
                                    }
                                }

                            }
//                            for (HashMap.Entry<String, MqttEquipment> entry : MqttEquipmentMap.entrySet()) {
//
//                                MqttEquipment e = entry.getValue();
////                    Log.e("qqq","goods fa "+e.getSid()+" "+e.getType());
//                                String myTopicding = "iotbroad/iot/" + e.getType() + "_ack/" + e.getSid();
//                                e.subscribe(myTopicding);
//                                e.publish_String(push_read(e.getType(), e.getSid()));
//                                try {
//                                    Thread.sleep(500);
//                                } catch (InterruptedException e1) {
//                                    e1.printStackTrace();
//                                }
//                            }

                        }
                    }).start();


                    equimentHandler.sendEmptyMessageDelayed(2000, 500 * MqttEquipmentMap.size() + 2000);
                    break;
            }
        }
    };


    private Thread tq = new Thread() {

        StringBuffer sb = new StringBuffer();
        String line = null;
        BufferedReader buffer = null;

        public void run() {
            Log.e("tianqi", "tianqi");
            HttpURLConnection connection = null;
            try {
                sb.delete(0, sb.length());
                String tq = "http://wthrcdn.etouch.cn/weather_mini?city=" + city;
                URL url = new URL(tq);
                connection = (HttpURLConnection) url.openConnection();
                // 设置请求方法，默认是GET
                connection.setRequestMethod("GET");
                // 设置字符集
                connection.setRequestProperty("Charset", "UTF-8");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                buffer = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                while ((line = buffer.readLine()) != null) {
                    sb.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (buffer != null) {
                        buffer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String result = sb.toString();
            Log.e("tianqi", result);
//            Message msg = Message.obtain();
//            msg.what = 0;
//            getHandler.sendMessage(msg);
            Message message = new Message();
            message.what = 1000;
            message.obj = result;
            tqHander.sendMessage(message);

        }
    };


    Handler tqHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(smartTvWd==null){
                return;
            }
            switch (msg.what) {
                case 1000:
                    try {
                        JSONObject obj = new JSONObject(msg.obj.toString());
                        JSONObject array = obj.getJSONObject("data");
                        String wendu = array.getString("wendu"); //温度
                        String ganmao = array.getString("ganmao");  //天气状况
                        JSONArray forecast = array.getJSONArray("forecast");
                        JSONObject for_obj = forecast.getJSONObject(0);
                        String fengxiang = for_obj.getString("fengxiang"); //风向
                        String fengli = for_obj.getString("fengli"); //风力
                        String type = for_obj.getString("type");  //天气
                        Log.e("tianqi","wd="+wendu);
                        smartTvWd.setText(wendu);
                        smartTvSd.setText(type);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };


    private void setGps() {
        city = "北京";
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setTitle("是否去打开Gps已获取天气信息");
        builder.setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                LocationUtils.openGpsSettings(context);
            }
        });

        builder.setNegativeButton(
                getString(R.string.alert_dialog_cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub

                    }
                });
        builder.show();
    }
}
