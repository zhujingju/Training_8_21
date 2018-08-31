package com.grasp.training;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grasp.training.fragmet.Personal;
import com.grasp.training.fragmet.Robot;
import com.grasp.training.fragmet.SmartHome;
import com.grasp.training.tool.BaseFragmentActivity;
import com.grasp.training.tool.BaseMqttFragmentActivity;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.grasp.training.view.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseMqttFragmentActivity {

    public static String MainSB = "MainActivity_UID";
    public static String SID = "12345678";
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

    public static String NameUser = "";
    public static String Mode_kx = "MODEKX";
    public static String Mode_fm = "MODEFM";
    public static String Mode_zdy = "MODEZDY";
    @BindView(R.id.main_gl)
    RelativeLayout mainGl;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_xia_im1)
    ImageView mainXiaIm1;
    @BindView(R.id.main_xia_tv1)
    TextView mainXiaTv1;
    @BindView(R.id.main_xia_im2)
    ImageView mainXiaIm2;
    @BindView(R.id.main_xia_tv2)
    TextView mainXiaTv2;
    @BindView(R.id.main_xia_im3)
    ImageView mainXiaIm3;
    @BindView(R.id.main_xia_tv3)
    TextView mainXiaTv3;
    @BindView(R.id.main_xia_im4)
    ImageView mainXiaIm4;
    @BindView(R.id.main_xia_tv4)
    TextView mainXiaTv4;
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
    }

    @Override
    public void initView() {
        NameUser=SharedPreferencesUtils.getParam(getContext(),MyApplication.NAME_USER,"").toString();


    }

    @Override
    public void initObject() {

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
        NameUser= SharedPreferencesUtils.getParam(getContext(), MyApplication.NAME_USER,"").toString();
        fragments = new ArrayList<Fragment>();
        SmartHome smartHome = new SmartHome();
        SmartHome smartHome2 = new SmartHome();
        Robot robot = new Robot();
        Personal personal=new Personal();

        fragments.add(smartHome);
        fragments.add(smartHome2);
        fragments.add(robot);
        fragments.add(personal);


        viewPager.setAdapter(new myPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.setCurrentItem(0);
        setLin(0);


    }

    private void setLin(int i) {
        int[] in1 = {R.drawable.bg_nor, R.drawable.bg_pre};
        int[] in2 = {R.drawable.bg_nor, R.drawable.bg_pre};
        int[] in3 = {R.drawable.bg_nor, R.drawable.bg_pre};
        int[] in4 = {R.drawable.bg_nor, R.drawable.bg_pre};
        int[] c_1 = {R.color.hui, R.color.hu2};
        mainXiaIm1.setBackgroundResource(in1[0]);
        mainXiaIm2.setBackgroundResource(in2[0]);
        mainXiaIm3.setBackgroundResource(in3[0]);
        mainXiaIm4.setBackgroundResource(in4[0]);
        mainXiaTv1.setTextColor(getResources().getColor(c_1[0]));
        mainXiaTv2.setTextColor(getResources().getColor(c_1[0]));
        mainXiaTv3.setTextColor(getResources().getColor(c_1[0]));
        mainXiaTv4.setTextColor(getResources().getColor(c_1[0]));
        if (i == 0) {
            mainXiaIm1.setBackgroundResource(in1[1]);
            mainXiaTv1.setTextColor(getResources().getColor(c_1[1]));

        } else if (i == 1) {
            mainXiaIm2.setBackgroundResource(in2[1]);
            mainXiaTv2.setTextColor(getResources().getColor(c_1[1]));

        } else if (i == 2) {
            mainXiaIm3.setBackgroundResource(in3[1]);
            mainXiaTv3.setTextColor(getResources().getColor(c_1[1]));

        } else if (i == 3) {
            mainXiaIm4.setBackgroundResource(in4[1]);
            mainXiaTv4.setTextColor(getResources().getColor(c_1[1]));
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
    private String myTopic = "iotbroad/iot";
    @Override
    public String  getMyTopic() {
        return myTopic;
    }

    @Override
    public String getMyTopicDing() {
        return myTopic;
    }

    @Override
    public void MyMessageArrived(String message) {

    }

    @Override
    protected void onDestroy() {
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

}
