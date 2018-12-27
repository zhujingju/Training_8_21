package com.grasp.training.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liangmutian.mypicker.TimePickerDialog3;
import com.grasp.training.R;
import com.grasp.training.tool.BaseActivity;
import com.grasp.training.view.WeekPopwinDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewIntelligentTimeActivity extends BaseActivity {
    @BindView(R.id.nit_fh)
    ImageView nitFh;
    @BindView(R.id.nit_bc)
    TextView nitBc;
    @BindView(R.id.nit_layout1_tv1)
    TextView nitLayout1Tv1;
    @BindView(R.id.nit_layout1)
    RelativeLayout nitLayout1;
    @BindView(R.id.nit_layout1_tv2)
    TextView nitLayout1Tv2;
    @BindView(R.id.nit_layout2)
    RelativeLayout nitLayout2;
    @BindView(R.id.nit_layout1_tv3)
    TextView nitLayout1Tv3;
    @BindView(R.id.nit_layout3)
    RelativeLayout nitLayout3;
    @BindView(R.id.nit_lin)
    LinearLayout lin;

    private String week="",time1="",time2="";

    public static void startActivity(Context context, String week, String time1,String time2) {
        Intent in = new Intent(context, NewIntelligentTimeActivity.class);
        in.putExtra("NewIntelligentTimeActivity", week);
        in.putExtra("NewIntelligentTimeActivity2", time1);
        in.putExtra("NewIntelligentTimeActivity3", time2);
        ((Activity)context).startActivityForResult(in,1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //这里将我们临时输入的一些数据存储起来
        outState.putString("get_week", week);
        outState.putString("get_time1", time1);
        outState.putString("get_time2", time2);
    }
    @Override
    public int setLayoutId() {
        return R.layout.new_intelligent_time_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        takePhotoPopWin = new WeekPopwinDialog(getContext());
        Intent in=getIntent();
        week=in.getStringExtra("NewIntelligentTimeActivity");
        time1=in.getStringExtra("NewIntelligentTimeActivity2");
        time2=in.getStringExtra("NewIntelligentTimeActivity3");

        if (getSavedInstanceState() != null) {
//
            week = getSavedInstanceState().getString("get_week", ":");
            time1 = getSavedInstanceState().getString("get_time1", "");
            time2 = getSavedInstanceState().getString("get_time2", "");
            if (week.equals("")) {
                Toast.makeText(getContext(), "页面被系统回收", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
//            Log.e("qqq", "json=" + js);
        }

        String s1[]=time1.split(":");
        if(s1.length==2){
            on_h=Integer.valueOf(s1[0]);
            on_m=Integer.valueOf(s1[1]);
        }
        String s2[]=time2.split(":");
        if(s2.length==2){
            off_h=Integer.valueOf(s2[0]);
            off_m=Integer.valueOf(s2[1]);
        }
        setWeekView(week);
        nitLayout1Tv2.setText(time1);
        nitLayout1Tv3.setText(time2);

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


    @OnClick({R.id.nit_fh, R.id.nit_bc, R.id.nit_layout1, R.id.nit_layout2, R.id.nit_layout3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.nit_fh:
                finish();
                break;
            case R.id.nit_bc:
                Intent in=new Intent();
                in.putExtra("week",week);
                in.putExtra("time1",time1);
                in.putExtra("time2",time2);
                setResult(RESULT_OK,in);
                finish();
                break;
            case R.id.nit_layout1:
                showPopWinHasReser(z1, z2, z3, z4, z5, z6, z7);
                break;
            case R.id.nit_layout2:
                showTimePick(on_h,on_m);
                break;
            case R.id.nit_layout3:
                showTimePick2(off_h,off_m);
                break;
        }
    }

    private int on_h = 10, on_m = 0;
    private int off_h = 16, off_m = 0;
    private TimePickerDialog3.Builder builder = null, builder2 = null;
    private Dialog timeDialog, timeDialog2;

    private void showTimePick(int h, int m) {

        if (timeDialog == null) {

            builder = new TimePickerDialog3.Builder(this);

            timeDialog = builder.setOnTimeSelectedListener(new TimePickerDialog3.OnTimeSelectedListener() {
                @Override
                public void onTimeSelected(String[] times) {

//                    mTextView.setText(times[0] + ":" + times[1]);
                    int h = Integer.valueOf(times[0]);
                    int m = Integer.valueOf(times[1]);
                    on_h = h;
                    on_m = m;
                    String s_h = "";
                    String s_m = "";
                    if (h < 10) {
                        s_h = "0" + h;
                    } else {
                        s_h = "" + h;
                    }

                    if (m < 10) {
                        s_m = "0" + m;
                    } else {
                        s_m = "" + m;
                    }
                    time1=s_h + ":" + s_m;
                    nitLayout1Tv2.setText(s_h + ":" + s_m);
                }
            }).create();

        }
        builder.setText_sj(h, m);
        timeDialog.show();

    }

    private void showTimePick2(int h, int m) {

        if (timeDialog2 == null) {

            builder2 = new TimePickerDialog3.Builder(this);

            timeDialog2 = builder2.setOnTimeSelectedListener(new TimePickerDialog3.OnTimeSelectedListener() {
                @Override
                public void onTimeSelected(String[] times) {

//                    mTextView.setText(times[0] + ":" + times[1]);
                    int h = Integer.valueOf(times[0]);
                    int m = Integer.valueOf(times[1]);
                    off_h = h;
                    off_m = m;
                    String s_h = "";
                    String s_m = "";
                    if (h < 10) {
                        s_h = "0" + h;
                    } else {
                        s_h = "" + h;
                    }

                    if (m < 10) {
                        s_m = "0" + m;
                    } else {
                        s_m = "" + m;
                    }
                    time2=s_h + ":" + s_m;
                    nitLayout1Tv3.setText(s_h + ":" + s_m);
                }
            }).create();

        }
        builder2.setText_sj(h, m);
        timeDialog2.show();

    }



    private WindowManager.LayoutParams params;
    private WeekPopwinDialog takePhotoPopWin;


    public void showPopWinHasReser(boolean z1, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7) { //日期设定
        Log.e("qqq", "showPopWinHasReser=" + week);
        takePhotoPopWin.setWeek(z1, z2, z3, z4, z5, z6, z7);
        takePhotoPopWin.setWeekInterface(new WeekPopwinDialog.weekInterface() {
            @Override
            public void onWeekInterface(boolean z1, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7) {
                week = "";
                if (z1) {
                    week += "1";
                } else {
                    week += "0";
                }

                if (z2) {
                    week += "2";
                } else {
                    week += "0";
                }

                if (z3) {
                    week += "3";
                } else {
                    week += "0";
                }

                if (z4) {
                    week += "4";
                } else {
                    week += "0";
                }
                if (z5) {
                    week += "5";
                } else {
                    week += "0";
                }
                if (z6) {
                    week += "6";
                } else {
                    week += "0";
                }
                if (z7) {
                    week += "7";
                } else {
                    week += "0";
                }
                setWeekView(week);
                Log.e("qqq", "week=" + week);
            }
        });
        takePhotoPopWin.showAtLocation(lin, Gravity.BOTTOM, 0, 0);
        params = ((Activity) getContext()).getWindow().getAttributes();
        //当弹出Popupwindow时，背景变半透明
        params.alpha = 0.8f;
        ((Activity) getContext()).getWindow().setAttributes(params);
        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        takePhotoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params = ((Activity) getContext()).getWindow().getAttributes();
                params.alpha = 1f;
                ((Activity) getContext()).getWindow().setAttributes(params);

            }
        });

    }

    private boolean z1, z2, z3, z4, z5, z6, z7;

    private void setWeekView(String s) {

        if (s.length() == 7) {
            String we = "";
            if (s.substring(0, 1).equals("1")) {
                z1 = true;
                we += "周一";
            } else {
                z1 = false;
            }

            if (s.substring(1, 2).equals("2")) {
                z2 = true;
                if (we.equals("")) {
                    we += "周二";
                } else {
                    we += "，周二";
                }
            } else {
                z2 = false;
            }

            if (s.substring(2, 3).equals("3")) {
                z3 = true;
                if (we.equals("")) {
                    we += "周三";
                } else {
                    we += "，周三";
                }
            } else {
                z3 = false;
            }

            if (s.substring(3, 4).equals("4")) {
                z4 = true;
                if (we.equals("")) {
                    we += "周四";
                } else {
                    we += "，周四";
                }
            } else {
                z4 = false;
            }
            if (s.substring(4, 5).equals("5")) {
                z5 = true;
                if (we.equals("")) {
                    we += "周五";
                } else {
                    we += "，周五";
                }
            } else {
                z5 = false;
            }
            if (s.substring(5, 6).equals("6")) {
                z6 = true;
                if (we.equals("")) {
                    we += "周六";
                } else {
                    we += "，周六";
                }
            } else {
                z6 = false;
            }
            if (s.substring(6, 7).equals("7")) {
                z7 = true;
                if (we.equals("")) {
                    we += "周日";
                } else {
                    we += "，周日";
                }
            } else {
                z7 = false;
            }
            if (z1 && z2 && z3 && z4 & z5 && z6 && z7) {
                we = "每天";
            } else if (!z1 && !z2 && !z3 && !z4 & !z5 && !z6 && !z7) {
                we = "永不";
            } else if (z1 && z2 && z3 && z4 & z5 && !z6 && !z7) {
                we = "工作日";
            } else if (!z1 && !z2 && !z3 && !z4 & !z5 && z6 && z7) {
                we = "周末";
            }
            nitLayout1Tv1.setText(we);
        }
    }
}
