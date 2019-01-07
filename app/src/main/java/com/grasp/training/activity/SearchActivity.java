package com.grasp.training.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.service.MqttService;
import com.grasp.training.tool.BaseMqttToActivity;
import com.grasp.training.tool.EquipmentData;
import com.grasp.training.tool.LocationUtils;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.grasp.training.tool.Tool;
import com.grasp.training.tool.WifiAdmin;
import com.grasp.training.view.PopwinDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ru.alexbykov.nopermission.PermissionHelper;

public class SearchActivity extends BaseMqttToActivity {

    private String myTopicding =  MqttService.myTopicDevice;
    private String myTopic =  MqttService.myTopicDevice;
    private Context context;
    private ListView listview;
    private List<Goods> list;
    private myListViewAdapter adapter;
    private LinearLayout lin;
    private RelativeLayout dong_rel;
    private ImageView dong_im;

    private int oldNetworkId;
    private List<String> sid_List;  //所有设备sid的列表
    private boolean good_zt = false;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
//		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
////			setContentView(setLayoutId());
//			Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
////			setContentView(setLayoutId());
//		}
        setContentView(setLayoutId());
        setContext(this);
        initData();
        initView();
        initObject();
        initListener() ;
        init();
    }
    @Override
    public int setLayoutId() {
        return R.layout.search_activity;

    }

    @Override
    public void initData() {
        context = getContext();
        handler.removeMessages(204);
        handler.removeMessages(99);
        handler.removeMessages(1000);
        handler.removeMessages(100);
        handler.removeMessages(200);
        handler.removeMessages(300);
        handler.removeMessages(400);
        handler.removeMessages(500);
        handler.removeMessages(600);
        handler.removeMessages(700);
        handler.removeMessages(800);
        handler.removeMessages(900);
        handler.removeMessages(201);
        handler.removeMessages(202);
        showPro_zt1=true;

//            Log.e("qqq","s="+s);
//        }
        sid_List = getIntent().getStringArrayListExtra("sid_List");
        if(sid_List==null){
            sid_List=new ArrayList<>();
        }
        listview = (ListView) findViewById(R.id.search_list);
        lin = (LinearLayout) findViewById(R.id.search_lin);
        dong_im = (ImageView) findViewById(R.id.search_dong);
        dong_rel = (RelativeLayout) findViewById(R.id.search_rel);
        takePhotoPopWin = new PopwinDialog(context);

        permissionHelper = new PermissionHelper(this);
        getWifiSSid();

        wifiadmin = new WifiAdmin(context);  //wifi工具类
        oldNetworkId = wifiadmin.getNetWordId();

        initListview();
//        new UdpReceiveThread().start();  //启动udp接收
    }

    @Override
    public void initView() {

        if(LocationUtils.isGpsEnabled(context)){

        }else{
                setGps();
        }
    }

    @Override
    public void initObject() {
        setEquipmentData();
        Log.e("qqq", "elist.size()=" + elist.size());


        handler.sendEmptyMessageDelayed(1000, 500);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void init() {
        handler.sendEmptyMessageDelayed(99, 1000);

    }



    private PermissionHelper permissionHelper;

    private void getWifiSSid() {
        permissionHelper.check(Manifest.permission.ACCESS_FINE_LOCATION).onSuccess(this::onSuccess).onDenied(this::onDenied).onNeverAskAgain(this::onNeverAskAgain).run();
        permissionHelper.check(Manifest.permission.ACCESS_COARSE_LOCATION).onSuccess(this::onSuccess).onDenied(this::onDenied).onNeverAskAgain(this::onNeverAskAgain).run();
        permissionHelper.check(Manifest.permission.ACCESS_NETWORK_STATE).onSuccess(this::onSuccess).onDenied(this::onDenied).onNeverAskAgain(this::onNeverAskAgain).run();

        if (ContextCompat.checkSelfPermission(this,Manifest.permission_group.LOCATION)!= PackageManager.PERMISSION_GRANTED){
// 获取wifi连接需要定位权限,没有获取权限
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE
            },1);
            return;
        }

    }
    private void onSuccess() {

//        mTvSSID.setText(DeviceUtil.INSTANCE.getWIFISSID(this));
//        Toast.makeText(context,"获取权限成功",Toast.LENGTH_LONG).show();
    }

    private void onDenied() {
        Toast.makeText(context,"权限被拒绝，9.0系统无法获取SSID",Toast.LENGTH_LONG).show();
    }

    private void onNeverAskAgain() {
        Toast.makeText(context,"权限被拒绝，9.0系统无法获取SSID,下次不会在询问了",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }

            }
        }
    }


    @Override
    public String getMyTopic() {
        return myTopic;
    }


    @Override
    public String getMyTopicDing() {
        return myTopicding;
    }


    @Override
    public void MyMessageArrived(final String message) {
        Log.e("qqq", "Search message=" + message);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonF;
                    Message me;
                    String js = "";
                    String channel_0 = "";
                    int var = 0;
                    String data = "";
                    String state = "";
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.getString("cmd");
                    String mSid = jsonObject.optString("sid", "");  //设备号
                    if (!mSid.equals(sid)) {

                        return;
                    }
                    switch (cmd) {
//                        case "wifi_socket_ack":
//                            if (good_zt) {
//                                return;
//                            }
//                            good_zt = true;
//                            handler.removeMessages(400);
//                            handler.removeMessages(500);
//                            handler.sendEmptyMessageDelayed(500, 0);
//                            break;

                        case "adddevice_ok":
                            String uname = jsonObject.optString("uname", "");  //
                            if (!uname.equals(MainActivity.NameUser)) {
                                return;
                            }
                            String clientid = jsonObject.optString("clientid", "");
                            if (!clientid.equals(Tool.getIMEI(getContext()))) {
                                return;
                            }
                            sid_List.add(sid);
                            handler.sendEmptyMessageDelayed(700, 0);
                            break;

                        case "adddevice_failed":
                            handler.sendEmptyMessageDelayed(800, 0);
                            break;
                        case "adddevice_existed":
                            handler.sendEmptyMessageDelayed(900, 0);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private String ssid = "";  //要连接的wifi
    private ScanResult myScanResult;

    private void initListview() {
        listview.setCacheColorHint(0);
        adapter = new myListViewAdapter(context, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                type = list.get(arg2).getType();
                ssid = list.get(arg2).getName();
                myScanResult = list.get(arg2).getScanResult();
                showPopWinHasReser();
//                String ssid9=wifiadmin.getWIFISSID((Activity) context);
//                Log.e("qqq","ssid="+ssid+" ssid9="+ssid9);
            }
        });

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 99:
                    dong();
                    break;
                case 1000:
                    new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if(showPro_zt1){
                                        ss();
                                    }

                                }
                            }

                    ).start();

                    handler.sendEmptyMessageDelayed(1000, 3000);
                    break;

                case 100:
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                    break;
                case 200:
                    Log.e("qqq", "200");
                    good_zt = false;
                    if (dialog == null) {
                        showPro();
                    }
                    if (!dialog.isShowing()) {
                        showPro();
                    }
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(2000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
//                    handler.sendEmptyMessageDelayed(202, 0); //判断是否连上

                    break;
                case 201:

                    Toast.makeText(context, "添加失败，请重置设备", Toast.LENGTH_LONG).show();
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    break;
                case 202:
                    Log.e("qqq", "202");
                    if (showPro_zt1) {  //取消连接
                        return;
                    }

                    if (isWifiConnect()) {  //判断联网
                        WifiInfo wifi = wifiadmin.getCurrentWifiInfo();
                        Log.e("qqq", "202 " + wifi.getSSID() + "  ssid= " + ssid);
                        if (wifi.getSSID().equals("\"" + ssid + "\"")) {

//                            if (!result_zt) {
                                new UdpReceiveThread().start();  //启动udp接收
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(2000);
                                            push_move(l_ssid, l_pw, l_pw.length());//
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        handler.sendEmptyMessageDelayed(204, 60*1000); //发送没收到
                                    }
                                }).start();
//                            } else {
//                                push_move(l_ssid, l_pw, l_pw.length());//
//                                handler.sendEmptyMessageDelayed(204, 10000); //发送没收到
//                            }


                        } else {
                            handler.sendEmptyMessageDelayed(203, 1000); //判断是否连上
                        }
                    } else {
                        handler.sendEmptyMessageDelayed(202, 1000); //判断是否连上
                    }


                    break;
                case 203: //连接指定网络
                    Log.e("qqq", "203");
                    if (showPro_zt1) {  //取消连接
                        return;
                    }
                    boolean zt = wifiadmin.connectSpecificAP(myScanResult);  //连接摄像头   账号 密码 类型

                    Log.e("qqq", "203 zt="+zt+" ssid="+ssid+" ");
                    if(zt){
                    handler.sendEmptyMessageDelayed(202, 2000); //判断是否连上
                    }else{
                        handler.sendEmptyMessageDelayed(203,1000); //判断是否连上
                    }

                    break;

                case 204:
                    if (!showPro_zt1) {
                        showPro_zt1=true;
                        if(dialog!=null){
                            dialog.cancel();
                        }
                        Toast.makeText(context,"发送失败请复位设备后重试",Toast.LENGTH_LONG).show();
                    }



                    break;

                case 300:
                    if (dialog != null && !dialog.isShowing()) {
                        showPro2();
                    }
                    if (isConnected()) {
                        myTopicding = "iotbroad/iot/socket_ack/" + sid;
                        myTopic = "iotbroad/iot/socket/" + sid;
                        subscribe();
                        handler.sendEmptyMessageDelayed(400, 0);

                    } else {
                        handler.sendEmptyMessageDelayed(300, 1000);
                    }


                    break;

                case 400:
                    handler.removeMessages(400);
                    handler.removeMessages(500);
                    push_read();
                    handler.sendEmptyMessageDelayed(400, 1000);
                    break;
                case 500:
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    handler.removeMessages(400);
                    handler.removeMessages(500);
                    Toast.makeText(context, "添加成功", Toast.LENGTH_LONG).show();
                    break;

                case 600:

                    if (dialog != null && !dialog.isShowing()) {
                        showPro3();
                    }
                    if(sid_List==null){
                        Toast.makeText(context,"错误",Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (String s : sid_List) {
                                if (sid.equals(s)) {  //判断sid是否在列表里
                                    handler.sendEmptyMessageDelayed(900, 0);
                                    return;
                                }
                            }
                            if (isConnected()) {
                                myTopicding =  MqttService.myTopicDevice;
                                myTopic =  MqttService.myTopicDevice;
                                subscribe();
                                push_add(sid, type);
                            } else {
                                handler.sendEmptyMessageDelayed(600, 1000);
                            }
                        }
                    }).start();


                    break;

                case 700:
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    Toast.makeText(context, "添加成功", Toast.LENGTH_LONG).show();
//                    handler.sendEmptyMessageDelayed(300, 0);
                    finish();
                    break;
                case 800:
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    Toast.makeText(context, "添加失败，配网成功", Toast.LENGTH_LONG).show();
                    break;
                case 900:
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    Toast.makeText(context, "添加失败，配网成功,设备已存在", Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        }
    };


    public void hintKeyBoard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private WindowManager.LayoutParams params;
    private PopwinDialog takePhotoPopWin;


    private String l_ssid, l_pw;

    public void showPopWinHasReser() { //接入wifi密码设定

        takePhotoPopWin.setList(listb, false);
        takePhotoPopWin.setGetPopwinInterface(new PopwinDialog.popwinInterface() {
            @Override
            public void getPopwinInterface(ScanResult scanResult, String pw) {
                Log.e("qqq", scanResult.SSID + "  " + pw);
                showPro_zt1=false;
                l_ssid = scanResult.SSID;
                l_pw = pw;
                hintKeyBoard();


//                if (isWifiConnect()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessageDelayed(200, 0);
                        handler.sendEmptyMessageDelayed(203, 0);
//                        boolean zt = wifiadmin.connectSpecificAP(myScanResult);  //连接摄像头   账号 密码 类型
//                        Log.e("qqq", ssid + " -- " + getAuthType(myScanResult) + " " + zt);
                    }
                }).start();

//                } else {
//                    Toast.makeText(context, "请打开wifi后重试", Toast.LENGTH_LONG).show();
//                }
            }
        });
        //  takePhotoPopWin.setOnClickTrue();
        takePhotoPopWin.showAtLocation(lin, Gravity.CENTER, 0, 0);
        params = ((Activity) context).getWindow().getAttributes();
        //当弹出Popupwindow时，背景变半透明
        params.alpha = 0.8f;
        ((Activity) context).getWindow().setAttributes(params);
        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        takePhotoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params = ((Activity) context).getWindow().getAttributes();
                params.alpha = 1f;
                ((Activity) context).getWindow().setAttributes(params);
                hintKeyBoard();

            }
        });

    }

    //    private boolean di_zt=false;   //添加成功后变false
    public void showPopWinHasReser2() { //接入wifi密码设定
//        if(!di_zt){
//            return;
//        }
        takePhotoPopWin.setList(listb, true);
        takePhotoPopWin.setGetPopwinInterface(new PopwinDialog.popwinInterface() {
            @Override
            public void getPopwinInterface(ScanResult scanResult, String pw) {
                Log.e("qqq", scanResult.SSID + "  " + pw);
                showPro_zt1=false;
                l_ssid = scanResult.SSID;
                l_pw = pw;
                hintKeyBoard();
//                oldNetworkId = wifiadmin.GetCurrentNetwordId();
                if (isWifiConnect()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessageDelayed(200, 0);
//                            boolean zt = wifiadmin.connectSpecificAP(myScanResult);  //连接摄像头   账号 密码 类型
                            handler.sendEmptyMessageDelayed(203, 0);
//                            Log.e("qqq", ssid + " -- " + getAuthType(myScanResult) + " " + zt);
                        }
                    }).start();

                } else {
                    Toast.makeText(context, "请打开wifi后重试", Toast.LENGTH_LONG).show();
                }
            }
        });
//          takePhotoPopWin.setOnClickTrue();
        takePhotoPopWin.showAtLocation(lin, Gravity.CENTER, 0, 0);
        params = ((Activity) context).getWindow().getAttributes();
        //当弹出Popupwindow时，背景变半透明
        params.alpha = 0.8f;
        ((Activity) context).getWindow().setAttributes(params);
        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        takePhotoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params = ((Activity) context).getWindow().getAttributes();
                params.alpha = 1f;
                ((Activity) context).getWindow().setAttributes(params);
                hintKeyBoard();

            }
        });

    }

    public void push_move(String ssid, String pw, int pw_len) {  //获取设备数据


        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_config");
            jsonObject.put("ssid", ssid);
            jsonObject.put("ssid_len", ssid.length());
            jsonObject.put("password", pw);
            jsonObject.put("password_len", pw_len);
            String js = jsonObject.toString();
//            new MyThread(js, "192.168.5.1").start();
            new UdpSendThread(js, "192.168.5.1").start();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }

    public void push_add(String sid, String type) {  //添加设备

        try {
            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "adddevice");
            jsonObject.put("sid", sid);
            jsonObject.put("type", type);
            jsonObject.put("uname", MainActivity.NameUser);
            jsonObject.put("clientid", Tool.getIMEI(getContext()));
            String js = jsonObject.toString();

            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }

    public int getAuthType(ScanResult result) {
        String cap = result.capabilities;
        if (cap.contains("WEP")) {
            return 1;
        } else if (cap.contains("PSK")) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    protected void onDestroy() {
        setDel();
        super.onDestroy();
        handler.removeMessages(204);
        handler.removeMessages(99);
        handler.removeMessages(1000);
        handler.removeMessages(100);
        handler.removeMessages(200);
        handler.removeMessages(300);
        handler.removeMessages(400);
        handler.removeMessages(500);
        handler.removeMessages(600);
        handler.removeMessages(700);
        handler.removeMessages(800);
        handler.removeMessages(900);
        handler.removeMessages(201);
        handler.removeMessages(202);
    }

    private WifiAdmin wifiadmin;
    List<ScanResult> listb;

    public void ss() {
//        Log.e("qqq", isWifiConnect() + "  ");
        Log.e("qqq","ss()");
        if (!isWifiConnect()) {
            return;
        }

        wifiadmin.openWifi();//打开wifi
        wifiadmin.startScan();  //扫描

//        wifiManager.startScan();
        list = new ArrayList<>();
//        listb = wifiManager.getScanResults();
        listb = wifiadmin.getWifiList();

        if (listb == null) {
            return;
        }
        //数组初始化要注意
//        String[] listk = new String[listb.size()];
        if (listb != null) {
            for (int i = 0; i < listb.size(); i++) {
                ScanResult scanResult = listb.get(i);
//                listk[i] = scanResult.SSID;

                if (scanResult.SSID.length() > 5 && scanResult.SSID.substring(0, 5).equals("grasp")) {
                    String sb[] = scanResult.SSID.split("-");
//                    if (sb.length > 1 && sb[0].equals("grasp_socket")) {  //判断是开关
                    Goods goods = new Goods();
                    goods.setName(scanResult.SSID);
                    goods.setScanResult(scanResult);
                    goods.setType("");
                    goods.setDname("其他");
                    if (sb.length > 1) {  //判断是开关
                        String type[] = sb[0].split("_");
                        if (type.length > 1) {
                            if (eMap.size() > 0 && eMap.get(type[1]) != null) {
                                String dname = eMap.get(type[1]);
                                goods.setDname(dname);
                                goods.setType(type[1]);
                            }
                        }

                    }

                    list.add(goods);
                    Log.e("qqq", scanResult.SSID);
//                    }

                }
            }
        }


        if (listb.size() > 0) {
            handler.sendEmptyMessageDelayed(100, 0);
        }


    }

    public boolean isWifiConnect() {//wifi 是否可用
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifiInfo.isConnected();
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
                view = inflater.inflate(R.layout.items_search, parent, false);
                too = new ListViewTool();
//
                too.name = (TextView) view.findViewById(R.id.items_s_tv);
                too.lx = (TextView) view.findViewById(R.id.items_s_lx);

                view.setTag(too);
            } else {
                too = (ListViewTool) view.getTag();
            }
            if (camera == null) return null;
            // too.del.setOnClickListener(click);

            //  too.up.setTag(position);
            too.name.setText(camera.getName());
            too.lx.setText(camera.getDname());

            return view;
        }

        class ListViewTool {

            public TextView name, lx;
        }
    }


    class Goods {

        private String type;
        private String Dname;

        public String getDname() {
            return Dname;
        }

        public void setDname(String dname) {
            Dname = dname;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        private ScanResult scanResult;

        public ScanResult getScanResult() {
            return scanResult;
        }

        public void setScanResult(ScanResult scanResult) {
            this.scanResult = scanResult;
        }

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


//    class MyThread extends Thread {
//
//        public String content;
//        public String ip;
//
//        public MyThread(String str, String ip) {
//            Log.e("MyThread", str);
//            content = str;
//            this.ip = ip;
//        }
//
//        @Override
//        public void run() {
//            //定义消息
//            Message msg = new Message();
//            msg.what = 1;
//            try {
//                Thread.sleep(5000);
//                if(showPro_zt1){
//                    return;
//                }
//
//                Log.e("MyThread", "ip=" + getIp(context));
//                //连接服务器 并设置连接超时为60秒
//                Socket socket = new Socket();
//                socket.connect(new InetSocketAddress(ip, 8888), 60000);
//
//                InputStream mInStream = null;
//                if (socket != null) {
//                    //获取输出流、输入流
//                    mInStream = socket.getInputStream();
//                }
//                //                //获取输入输出流
//                OutputStream ou = socket.getOutputStream();
////                //向服务器发送信息
//                ou.write(content.getBytes("utf-8"));
//                ou.flush();
//
//                byte b[] = new byte[1024];        // 所有的内容都读到此数组之中
//
//
//                int len = mInStream.read(b);        // 读取内容
//                // 关闭输出流\
//                if (len < 0) {
//                    msg.what = 3;
//                    msg.obj = "len=-1";
//                    //发送消息 修改UI线程中的组件
//                    myHandler.sendMessage(msg);
//                    mInStream.close();
//                    ou.close();
//                    socket.close();
//                    return;
//                }
//                //读取发来服务器信息
//                String result = "";
//                result = new String(b, 0, len);
//                msg.obj = result.toString();
////                //发送消息 修改UI线程中的组件
//                myHandler.sendMessage(msg);
//                //关闭各种输入输出流
////                bff.close();
//                mInStream.close();
//                ou.close();
//                socket.close();
//                Log.e("MyThread", "good");
//            } catch (SocketTimeoutException aa) {
//                //连接超时 在UI界面显示消息
//                msg.what = 2;
//                msg.obj = "服务器连接失败！请检查网络是否打开";
//                //发送消息 修改UI线程中的组件
//                myHandler.sendMessage(msg);
//                Log.e("MyThread", "err1=" + aa.getMessage());
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("MyThread", "err2=" + e.getMessage());
//                msg.what = 2;
//                msg.obj = e.getMessage();
//                //发送消息 修改UI线程中的组件
//                myHandler.sendMessage(msg);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                msg.what = 2;
//                msg.obj = e.getMessage();
//                //发送消息 修改UI线程中的组件
//                myHandler.sendMessage(msg);
//            }
//        }


    private int post = 7777;

    private void setPost() {
        if (socket == null) {
            Random random = new Random();
            post = random.nextInt(9000) + 1000;
        }


    }

    //（接收线程）
    class UdpReceiveThread extends Thread {
        private final String TAG = "UdpReceiveThread";

        @Override
        public void run() {
            setPost();
            result_zt = true;
            Log.e("UdpReceiveThread", "new UdpReceiveThread");
            while (!showPro_zt1 && result_zt && isAlive()) { //循环接收，isAlive() 判断防止无法预知的错误
                try {
                    Log.e("UdpReceiveThread", "+++UdpReceiveThread");
//                    sleep(20); //让它好好休息一会儿
                    if (socket == null) {
                        socket = new DatagramSocket(post); //建立 socket，其中 8888 为端口号
                    }
                    byte data[] = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    socket.receive(packet); //阻塞式，接收发送方的 packet
                    String result = new String(packet.getData(), packet.getOffset(), packet.getLength()); //packet 转换
                    Log.e(TAG, "UDP result: " + result);
//                    MyMessageArrived(result);
                    Message msg = new Message();
                    msg.obj = result.toString();
                    msg.what = 1;
////                //发送消息 修改UI线程中的组件
                    myHandler.sendMessage(msg);
                    Log.e("UdpReceiveThread", " UdpReceiveThread close");
//                    socket2.close(); //必须及时关闭 socket，否则会出现 error
                } catch (Exception e) {
                    e.printStackTrace();
//                    new UdpReceiveThread().start();
//                    break; //当 catch 到错误时，跳出循环
                    Log.e(TAG, "UDP result: err=" + e.getMessage() + "  result_zt=" + result_zt);
                    if (result_zt) {
                        Log.e(TAG, "UDP result: 错误");
//                        Message msg = new Message();
//                        msg.obj = e.getMessage();
//                        msg.what = 2;
//////                //发送消息 修改UI线程中的组件
//                        myHandler.sendMessage(msg);
                    } else {

                    }

                    result_zt = false;
                    break;
                }
            }
        }
    }

    private boolean result_zt = true;

    private DatagramSocket socket;

    private void setDel() {
        result_zt = false;
        if (socket != null) {
            socket.close();
        }
    }

    public class UdpSendThread extends Thread {

        public static final String TAG = "UdpSendThread";
        private int i = 0; //静态变量，记录发送消息的次数
        private String data;
        private String ip;

        public UdpSendThread(String data, String ip) {
            this.data = data;
            this.ip = ip;
        }

        @Override
        public void run() {
            try {
                if (showPro_zt1) {
                    return;
                }

                if (socket == null) {
                    socket = new DatagramSocket(post); //自定端口号
//                    socket.setReuseAddress(true);
//                    socket.bind(new InetSocketAddress(8888));
                }


                InetAddress address = InetAddress.getByName(ip); //通过当前 IP 建立相应的 InetAddress
//                String data = "I love you" + "( " + i++ + " )";
                byte dataByte[] = data.getBytes(); //建立数据
                DatagramPacket packet = new DatagramPacket(dataByte, dataByte.length, address, 8888); //通过该数据建包
                socket.send(packet); //开始发送该包
//                socket.close(); //其实对于发送方来说没必要关闭 socket，但为了防止无法预知的意外，建议关闭
                Log.e(TAG, "send done，data: " + data);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "fa err= " + e.getMessage());
            }

        }

    }

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
//                result.append("server:" + msg.obj + "\n");
                try {
                    handler.removeMessages(204);
                    showPro_zt1 = true;
                    Log.e("MyThread", "good=" + msg.obj);
                    JSONObject jsonObject = new JSONObject(msg.obj.toString());
                    String cmd = jsonObject.optString("cmd", "");
                    if (cmd.equals("wifi_config_ok")) {
                        sid = jsonObject.optString("sid", "");
                        int connect_sta = jsonObject.optInt("connect_sta", 3);
                        if (connect_sta == 1) {//连接成功
                            Toast.makeText(context, "连接成功", Toast.LENGTH_LONG).show();
                            wifiadmin.EnableNetwork(oldNetworkId); //回来
                            if (dialog != null) {
                                dialog.cancel();
                            }

                            Log.e("MyThread", "sid=" + sid);
//                                SharedPreferencesUtils.setParam(context, MainActivity.socket,sid);
                            String ssid_list=(String) SharedPreferencesUtils.getParam(context,"wifi_search","");
                            if(ssid_list.equals("")){
                                ssid_list=l_ssid;
                            }else{
                                String ss[]=ssid_list.split("☯");
                                boolean add_zt=false;
                                for (int i=0;i<ss.length;i++){
                                    if(ss[i].equals(l_ssid)){
                                        add_zt=true;
                                        break;
                                    }
                                }
                                if(!add_zt){
                                    ssid_list=ssid_list+"☯"+l_ssid;
                                }
                            }
                            SharedPreferencesUtils.setParam(context,"wifi_search",ssid_list);//保存wifi列表
                            SharedPreferencesUtils.setParam(context,l_ssid+"zjj",l_pw);//保存wifi密码
                            handler.sendEmptyMessageDelayed(600, 0);
//                                di_zt=true;
                        } else if (connect_sta == 2) {//密码错误
                            if (dialog != null) {
                                dialog.cancel();
                            }
                            showPopWinHasReser2();
                            Toast.makeText(context, "密码错误", Toast.LENGTH_LONG).show();
                        } else if (connect_sta == 3) {//连接错误
                            if (dialog != null) {
                                dialog.cancel();
                            }
                            showPopWinHasReser2();
                            Toast.makeText(context, "连接错误", Toast.LENGTH_LONG).show();
                        }


                    } else {
                        Toast.makeText(context, "发送失败", Toast.LENGTH_LONG).show();
                        if (dialog != null) {
                            dialog.cancel();
                        }
                        showPopWinHasReser2();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }


            } else { //失败
//                    Toast.makeText(context, "发送失败", Toast.LENGTH_LONG).show();
//                    if (dialog != null) {
//                        dialog.cancel();
//                    }
                if (showPro_zt1) {
                    handler.removeMessages(200);
                    return;
                }
                showPro_zt1=false;
                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getScanResult().SSID.equals(ssid)) {
                                handler.sendEmptyMessageDelayed(200, 0);
//                                boolean zt = wifiadmin.connectSpecificAP(myScanResult);  //连接摄像头   账号 密码 类型
                                handler.sendEmptyMessageDelayed(203, 0);
//                                Log.e("qqq", ssid + " -- " + getAuthType(myScanResult) + " " + zt);
                                return;
                            }
                        }
                        handler.sendEmptyMessageDelayed(201, 0);

                    }
                }).start();
                Log.e("MyThread", "err=");
            }
        }

    };
//    }

    @Override
    public String getSid() {
        return "";
    }

    private String sid = ""; //设备id
    private String type = "";//设备类型
    private ProgressDialog dialog;


    private boolean showPro_zt1 = false;

    public void showPro() {
        showPro_zt1 = false;
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在连接中...");
        dialog.setCancelable(false);
        dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showPro_zt1 = true;
            }
        });
        dialog.show();
    }

    public void showPro2() {

        dialog = new ProgressDialog(this);
        dialog.setMessage("获取数据中...");
        dialog.setCancelable(true);
        dialog.show();
    }

    public void showPro3() {

        dialog = new ProgressDialog(this);
        dialog.setMessage("添加设备中...");
        dialog.setCancelable(true);
        dialog.show();
    }

    public String getIp(final Context context) {
        String ip = null;
        ConnectivityManager conMan = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        // mobile 3G Data Network
//        android.net.NetworkInfo.State mobile = conMan.getNetworkInfo(
//                ConnectivityManager.TYPE_MOBILE).getState();
        // wifi
        android.net.NetworkInfo.State wifi = conMan.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState();

        // 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
//        if (mobile == android.net.NetworkInfo.State.CONNECTED
//                || mobile == android.net.NetworkInfo.State.CONNECTING) {
//            ip =  getLocalIpAddress();
//        }
        if (wifi == android.net.NetworkInfo.State.CONNECTED
                || wifi == android.net.NetworkInfo.State.CONNECTING) {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                Log.e("MyThread", "wifi2=");
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = (ipAddress & 0xFF) + "." +
                    ((ipAddress >> 8) & 0xFF) + "." +
                    ((ipAddress >> 16) & 0xFF) + "." +
                    (ipAddress >> 24 & 0xFF);
        }
        return ip;

    }


    public void fh(View view) {
        finish();
    }


    public void push_read() {  //获取插座状态
        if (sid.equals("")) {
//            Toast.makeText(context,"先添加设备",Toast.LENGTH_LONG).show();
            return;
        }

        try {
            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_socket_read");
            jsonObject.put("sid", sid);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
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


    public void dong() {
//        RotateAnimation rotateAnimation=new  RotateAnimation(0,360,Animation.RELATIVE_TO_PARENT,0.5f,Animation.RELATIVE_TO_PARENT,0.5f);
//        rotateAnimation.setRepeatCount(Animation.INFINITE);
////        rotateAnimation.setRepeatMode(Animation.REVERSE);
//        rotateAnimation.setDuration(1500);//设置动画持续时间
////imageView是要旋转的控件的引用.
//        dong_rel.startAnimation(rotateAnimation);

        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        dong_rel.startAnimation(operatingAnim);


    }

    private void setGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setTitle("关闭Gps可能获取不到wifi列表，是否去开启");
        builder.setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                LocationUtils.openGpsSettings(context);
            }
        });

        builder.setNegativeButton(
                getString(R.string.alert_dialog_cancel),  new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub

                    }
                });
        builder.show();
    }

}
