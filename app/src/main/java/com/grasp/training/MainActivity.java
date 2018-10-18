package com.grasp.training;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Player.Core.PlayerClient;
import com.Player.web.websocket.ClientCore;
import com.grasp.training.Umeye_sdk.Constants;
import com.grasp.training.activity.LoginActivity;
import com.grasp.training.fragmet.Personal;
import com.grasp.training.fragmet.Robot;
import com.grasp.training.fragmet.Scenario;
import com.grasp.training.fragmet.SmartHome;
import com.grasp.training.fragmet.SmartHomeMain;
import com.grasp.training.service.MqttService;
import com.grasp.training.tool.BaseFragmentActivity;
import com.grasp.training.tool.BaseMqttFragmentActivity;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.grasp.training.tool.Tool;
import com.grasp.training.tool.Utility;
import com.grasp.training.view.MyViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseMqttFragmentActivity {

    public static String MainSB = "MainActivity_UID";
    public final static String MainData = "MainActivity_Data";
    public static Activity activity;
    @BindView(R.id.main_sb)
    RelativeLayout mainSb;
    @BindView(R.id.main_yk)
    RelativeLayout mainYk;
    @BindView(R.id.main_dh)
    RelativeLayout mainDh;
    @BindView(R.id.main_ms)
    RelativeLayout mainMs;
    @BindView(R.id.main_rel_dong)
    RelativeLayout mainRelDong;
    @BindView(R.id.main_memu)
    ImageView mainMemu;
    @BindView(R.id.main_memu_tv)
    TextView mainMemuTv;
//    @BindView(R.id.main_gl_frame)
//    FrameLayout mainGlFrame;

    public static String NameUser = "";  //Uid
    public static String Mode_kx = "MODEKX";
    public static String Mode_fm = "MODEFM";
    public static String Mode_zdy = "MODEZDY";
    @BindView(R.id.main_gl)
    RelativeLayout mainGl;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_xia_im1)
    ImageView mainXiaIm1;
    @BindView(R.id.main_xia_im2)
    ImageView mainXiaIm2;
    @BindView(R.id.main_xia_im3)
    ImageView mainXiaIm3;
    @BindView(R.id.main_xia_im4)
    ImageView mainXiaIm4;
    @BindView(R.id.main_xia_ll1)
    LinearLayout mainXiaLl1;
    @BindView(R.id.main_xia_ll2)
    LinearLayout mainXiaLl2;
    @BindView(R.id.main_xia_ll3)
    LinearLayout mainXiaLl3;
    @BindView(R.id.main_xia_ll4)
    LinearLayout mainXiaLl4;
    @BindView(R.id.viewpager)
    MyViewPager viewPager;


    private List<Fragment> fragments;// Tab页面列表

    @Override
    public int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        activity = this;
        MqttService.appZt=true;
    }

    @Override
    public void initView() {
        MqttService.ip_zt = putMap(MqttService.MqttService1);
        MqttService.sid_ip = putMap(MqttService.MqttService2);
        Log.e("qqq", "ip.s=" + MqttService.ip_zt.size() + " " + MqttService.ip_zt);
        Log.e("qqq", "sid_ip.s=" + MqttService.sid_ip.size() + "  " + MqttService.sid_ip);
        NameUser = SharedPreferencesUtils.getParam(getContext(), MyApplication.NAME_USER, "").toString();
        if(NameUser.equals("")){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }
        handler.sendEmptyMessageDelayed(1000, 0); //获取设备类型


        String data = SharedPreferencesUtils.getParam(getContext(), MainData, "").toString();
        if (data.equals("")) {
            inData();
        }

        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();


    }

    @Override
    public void initObject() {
        initPlay();
        startBestServer2();
    }

    @Override
    public void initListener() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void init() {
//        mainSbTv.setLetterSpacing(0.2f);
//        mainYkTv.setLetterSpacing(0.2f);
//        mainDhTv.setLetterSpacing(0.2f);
//        initFragment2(new SmartHome());
        NameUser = SharedPreferencesUtils.getParam(getContext(), MyApplication.NAME_USER, "").toString();
        fragments = new ArrayList<Fragment>();
//        SmartHome smartHome = new SmartHome();
//        SmartHome smartHome2 = new SmartHome();
        Robot robot = new Robot();
        Personal personal = new Personal();
        fragments.add(new SmartHomeMain());
        fragments.add(new Scenario());
        fragments.add(robot);
        fragments.add(personal);


        viewPager.setAdapter(new myPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.setCurrentItem(0);
        setLin(0);


    }

    private void setLin(int i) {
        int[] in1 = {R.drawable.icon_dao_znjj_normal, R.drawable.icon_dao_znjj_selected};
        int[] in2 = {R.drawable.icon_dao_yycj_normal, R.drawable.icon_dao_yycj_selected};
        int[] in3 = {R.drawable.icon_dao_jqr_normal, R.drawable.icon_dao_jqr_selected};
        int[] in4 = {R.drawable.icon_dao_grzx_normal, R.drawable.icon_dao_grzx_selected};
        mainXiaIm1.setBackgroundResource(in1[0]);
        mainXiaIm2.setBackgroundResource(in2[0]);
        mainXiaIm3.setBackgroundResource(in3[0]);
        mainXiaIm4.setBackgroundResource(in4[0]);
        if (i == 0) {
            mainXiaIm1.setBackgroundResource(in1[1]);

        } else if (i == 1) {
            mainXiaIm2.setBackgroundResource(in2[1]);

        } else if (i == 2) {
            mainXiaIm3.setBackgroundResource(in3[1]);

        } else if (i == 3) {
            mainXiaIm4.setBackgroundResource(in4[1]);
        }
    }

    //    //显示fragment
//    private void initFragment2(Fragment f1) {
//        //开启事务，fragment的控制是由事务来实现的
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//        //第一种方式（add），初始化fragment并添加到事务中，如果为null就new一个
////        if(f1 == null){
////            f1 = new MyFragment("消息");
//        transaction.replace(R.id.main_gl_frame, f1);
////        }
//        //隐藏所有fragment
////        hideFragment(transaction);
//        //显示需要显示的fragment
//        transaction.show(f1);
//
//        //第二种方式(replace)，初始化fragment
////        if(f1 == null){
////            f1 = new MyFragment("消息");
////        }
////        transaction.replace(R.id.main_frame_layout, f1);
//
//        //提交事务
//        transaction.commit();
//    }
    private String myTopic = "iotbroad/iot/device";

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
        Log.e("qqq", "main messageArrived  message= " + message);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.optString("cmd", "");
                    String uname = jsonObject.optString("uname", "");
                    if (!uname.equals(NameUser)) {
                        return;
                    }
                    String clientid = jsonObject.optString("clientid", "");
                    if (!clientid.equals(Tool.getIMEI(getContext()))) {
                        return;
                    }
                    switch (cmd) {
                        case "querydevicetype_ok":
                            SharedPreferencesUtils.setParam(getContext(), MainData, message);
//                            JSONArray js=jsonObject.getJSONArray("data");
//                            for(int i=0;i<js.length();i++){
//                                JSONObject jsonObject1=js.getJSONObject(i);
//                                String dname=jsonObject1.optString("dname","");//名称
//                                String type=jsonObject1.optString("type","");//类型
//                                String thumbnail=jsonObject1.optString("dname",""); //预览图
//                                String stateall=jsonObject1.optString("stateall","");
//                            }
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
        MqttService.appZt=false;
        saveMap(MqttService.ip_zt, MqttService.MqttService1);
        saveMap(MqttService.sid_ip, MqttService.MqttService2);
        super.onDestroy();


    }

    private boolean zt_menu;


    @OnClick({R.id.main_xia_ll1, R.id.main_xia_ll2, R.id.main_xia_ll3, R.id.main_xia_ll4, R.id.main_ms, R.id.main_sb, R.id.main_yk, R.id.main_dh, R.id.main_memu, R.id.main_gl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_xia_ll1:
                viewPager.setCurrentItem(0);
                setLin(0);
                break;
            case R.id.main_xia_ll2:
                viewPager.setCurrentItem(1);
                setLin(1);
                break;
            case R.id.main_xia_ll3:
                viewPager.setCurrentItem(2);
                setLin(2);
                break;
            case R.id.main_xia_ll4:
                viewPager.setCurrentItem(3);
                setLin(3);
                break;
//            case R.id.main_ms:
//                initFragment2(mode);
//                mainLin.startAnimation(anim_lai);
//                zt_menu = false;
//                mainMemuTv.setText("欢迎词");
//                mainMore.setVisibility(View.GONE);
//                break;
//            case R.id.main_sb:
//                initFragment2(acSearchDevice);
//                mainLin.startAnimation(anim_lai);
//                zt_menu = false;
//                mainMemuTv.setText("设备");
//                mainMore.setVisibility(View.GONE);
//                break;
//            case R.id.main_yk:
//                initFragment2(control);
//                mainLin.startAnimation(anim_lai);
//                zt_menu = false;
//                mainMemuTv.setText("遥控");
//                mainMore.setVisibility(View.GONE);
//                break;
//            case R.id.main_dh:
//                initFragment2(voice);
//                mainLin.startAnimation(anim_lai);
//                zt_menu = false;
//                mainMemuTv.setText("对话");
//                mainMore.setVisibility(View.VISIBLE);
//                break;
            case R.id.main_memu:
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);//侧滑打开
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);//侧滑打开

                }
                break;

        }
    }


    /**
     * 定义适配器
     */
    class myPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        private List<Fragment> fragmentsList;
        private FragmentManager fm;

        public myPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public myPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragmentsList = fragments;
            this.fm = fm;
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragmentsList.get(arg0);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            //知道当前是第几页，但是每次滑动后可能会调用多次
            //这个方法是重点
            super.setPrimaryItem(container, position, object);
            fragmentsList.get(position);
            setLin(position);
        }

        @Override
        public int getItemPosition(Object object) {
            //加此方法可以使viewpager可以进行刷新
            return PagerAdapter.POSITION_NONE;
        }

        //使用此方法刷新数据 每次都要NEW一个新的List，不然没有刷新效果 转至http://blog.sina.com.cn/s/blog_783ede03010173b4.html
        public void setFragments(ArrayList<Fragment> fragments) {
            if (fragments != null) {
                FragmentTransaction ft = fm.beginTransaction();
                for (Fragment f : fragments) {
                    ft.remove(f);
                }
                ft.commit();
                ft = null;
                fm.executePendingTransactions();
            }
            this.fragmentsList = fragments;
            notifyDataSetChanged();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // 注释自带的销毁方法防止页面被销毁
            //这个方法是重点
            // super.destroyItem(container, position, object);}
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        /**
         * //		 * 为选项卡绑定监听器
         * //
         */
        @Override
        public void onPageSelected(int i) {
            // TODO Auto-generated method stub
//            SetCurSelText(i);

        }
    }


    public void push_data() {  //获取数据类型列表
        if (NameUser.equals("")) {
            return;
        }
        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "querydevicetype");
            jsonObject.put("uname", NameUser);
            jsonObject.put("clientid", Tool.getIMEI(getContext()));
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    if (isConnected()) {
                        push_data();
                    } else {
                        handler.sendEmptyMessageDelayed(1000, 1000);
                    }
                    break;
            }
        }
    };


    public void inData() {
        String ss = "{\"cmd\":\"querydevicetype_ok\",\"uname\":\"zjj\",\"clientid\":\"862534031873693\",\"data\":[{\"dname\":\"摄像头\",\"type\":\"camera\",\"stateall\":\"on|off\",\"thumbnail\":\"server://images/\"},{\"dname\":\"插座\",\"type\":\"socket\",\"stateall\":\"on|off\",\"thumbnail\":\"server://images/\"},{\"dname\":\"灯\",\"type\":\"light\",\"stateall\":\"on|off\",\"thumbnail\":\"server://images/\"},{\"dname\":\"网关\",\"type\":\"gateway\",\"stateall\":\"on|off\",\"thumbnail\":\"server://images/\"},{\"dname\":\"定时器\",\"type\":\"timer\",\"stateall\":\"0~24\",\"thumbnail\":\"server://images/\"}]}";
        SharedPreferencesUtils.setParam(getContext(), MainData, ss);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private ClientCore clientCore;
    private PlayerClient playClient;
    private MyApplication appMain;
    void initPlay() {
        appMain = (MyApplication) this.getApplicationContext();
        playClient = appMain.getPlayerclient();
//        startBestServer();
    }

    void startBestServer2() {
        clientCore = ClientCore.getInstance();
        int language = 1;
        clientCore.setupHost(this, Constants.server, 0, Utility.getImsi(this),
                language, Constants.CustomName, Utility.getVersionName(this),
                "");
        clientCore.getCurrentBestServer(this, clientCorehandler);
    }

    private Handler clientCorehandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            super.handleMessage(msg);
            Log.e("test", "startBestServer");
//            ha.sendEmptyMessageDelayed(222, 500);
//            if (pd != null) {
//                pd.dismiss();
//            }
//            ha.sendEmptyMessage(1002);
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            finish();

        }

    };

    public <Srring, V> HashMap<String, V> putMap(String mz) {
        HashMap hashMap = new HashMap();
        String s = SharedPreferencesUtils.getParam(this, mz, "").toString();
        if (!s.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                Iterator keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    V var = (V) jsonObject.opt(key);
                    hashMap.put(key, var);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return hashMap;

    }

    public <Srring, V> void saveMap(HashMap<String, V> map, String mz) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, V> entry : map.entrySet()) {
            try {
                jsonObject.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SharedPreferencesUtils.setParam(this, mz, jsonObject.toString());
    }
}
