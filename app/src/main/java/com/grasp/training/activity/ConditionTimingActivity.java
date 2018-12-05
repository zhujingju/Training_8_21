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

import com.example.liangmutian.mypicker.TimePickerDialog3;
import com.grasp.training.R;
import com.grasp.training.tool.BaseActivity;
import com.grasp.training.tool.DataStatus;
import com.grasp.training.tool.myActivityManage;
import com.grasp.training.view.WeekPopwinDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConditionTimingActivity extends BaseActivity {
    @BindView(R.id.condition_timing_fh)
    ImageView conditionTimingFh;
    @BindView(R.id.condition_timing_save)
    TextView conditionTimingSave;
    @BindView(R.id.condition_timing_cf)
    TextView conditionTimingCf;
    @BindView(R.id.condition_timing_tv1)
    TextView conditionTimingTv1;
    @BindView(R.id.condition_timing_layout1)
    RelativeLayout conditionTimingLayout1;
    @BindView(R.id.condition_timing_tv2)
    TextView conditionTimingTv2;
    @BindView(R.id.condition_timing_layout2)
    RelativeLayout conditionTimingLayout2;

    @BindView(R.id.condition_timing_lin)
    LinearLayout lin;

    private Context context;
    private String week="";
    private String time="";
    private int num;

    public static  void stastActivity(Context context,String week_l,String time_l,int num){
        Intent in=new Intent(context,ConditionTimingActivity.class);
        in.putExtra("ConditionTimingActivity",week_l);
        in.putExtra("ConditionTimingActivity1",time_l);
        in.putExtra("ConditionTimingActivity2",num);
        context.startActivity(in);
    }

    @Override
    public int setLayoutId() {
        return R.layout.condition_timing_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        week=getIntent().getStringExtra("ConditionTimingActivity");
        time=getIntent().getStringExtra("ConditionTimingActivity1");
        num=getIntent().getIntExtra("ConditionTimingActivity2",-1);
        context=this;
        takePhotoPopWin = new WeekPopwinDialog(context);
        if(week!=null){
            setWeekView(week);
        }

        if(time!=null&&!time.equals("")){
            String on[]=time.split(":");
            if(on.length==2){
                on_h= Integer.valueOf(on[0]);
                on_m= Integer.valueOf(on[1]);
                setAddTimingView();
            }
        }else{

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

    }

    @Override
    public void init() {

    }


    @OnClick({R.id.condition_timing_fh, R.id.condition_timing_save, R.id.condition_timing_layout1, R.id.condition_timing_layout2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.condition_timing_fh:
                finish();
                break;
            case R.id.condition_timing_save:

                if(num!=-1){
                    DataStatus dataStatus=new DataStatus();
                    dataStatus.setIf_left("定时");
                    dataStatus.setIf_right(getWeekView(week)+","+time);
                    dataStatus.setIf_num(2);
                    dataStatus.setIf_add(true);
                    dataStatus.setNum2_time(time);
                    dataStatus.setNum2_week(week);
                    myActivityManage.removeAll();
                    NewIntelligentActivity.startActivity(getContext(),dataStatus,false,num);
                }else{
                    DataStatus dataStatus=new DataStatus();
                    dataStatus.setIf_left("定时");
                    dataStatus.setIf_right(getWeekView(week)+","+time);
                    dataStatus.setIf_num(2);
                    dataStatus.setIf_add(true);
                    dataStatus.setNum2_time(time);
                    dataStatus.setNum2_week(week);
                    CarriedOutActivity.startCarriedOutActivity(getContext(),dataStatus,0,0,0);
                    finish();
                }

                break;
            case R.id.condition_timing_layout1:
                showPopWinHasReser(z1,z2,z3,z4,z5,z6,z7);
                break;
            case R.id.condition_timing_layout2:
                showTimePick(on_h,on_m);
                break;
        }
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
            conditionTimingTv1.setText(we);
        }
    }


    private String getWeekView(String s) {

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
            return we;
        }
        return  "永不";
    }

    private int on_h = 10, on_m = 0;
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
                    time=s_h + ":" + s_m;
                    conditionTimingTv2.setText(s_h + ":" + s_m);
                }
            }).create();

        }
        builder.setText_sj(h, m);
        timeDialog.show();

    }

    private void setAddTimingView() {  //更改ui


        int h = on_h;
        int m = on_m;
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
        conditionTimingTv2.setText(s_h + ":" + s_m);

    }
}
