package com.grasp.training.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liangmutian.mypicker.TimePickerDialog;
import com.grasp.training.R;
import com.grasp.training.tool.BaseActivity;
import com.grasp.training.tool.DataStatus;
import com.grasp.training.tool.myActivityManage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarriedOutActivity extends BaseActivity {  //结果条件页

    @BindView(R.id.carride_out_fh)
    ImageView carrideOutFh;
    @BindView(R.id.carried_out_layout1)
    RelativeLayout carriedOutLayout1;
    @BindView(R.id.carried_out_layout2)
    RelativeLayout carriedOutLayout2;
    @BindView(R.id.carried_out_layout3)
    RelativeLayout carriedOutLayout3;
    @BindView(R.id.carride_out_xian1)
    TextView carrideOutXian1;
    @BindView(R.id.carride_out_xian2)
    TextView carrideOutXian2;
    private int  my_ca1, my_ca2, my_ca3;
    private DataStatus dataStatus;

    public static String CarriedOutActivity1 = "CarriedOutActivity1";
    public static String CarriedOutActivity2 = "CarriedOutActivity2";
    public static String CarriedOutActivity3 = "CarriedOutActivity3";
    public static String CarriedOutActivity4 = "CarriedOutActivity4";
    public static String CarriedOutActivity5 = "CarriedOutActivity5";

    private int car_num=0;
    public static void startCarriedOutActivity(Context context, DataStatus dataStatus, int i_ca1, int i_ca2, int i_ca3) {
        Intent in = new Intent(context, CarriedOutActivity.class);
        in.putExtra(CarriedOutActivity1, dataStatus);
        in.putExtra(CarriedOutActivity3, i_ca1);
        in.putExtra(CarriedOutActivity4, i_ca2);
        in.putExtra(CarriedOutActivity5, i_ca3);
        context.startActivity(in);
    }

    public static void startCarriedOutActivity(Context context, DataStatus dataStatus, int i_ca1, int i_ca2, int i_ca3,int car_num) {
        Intent in = new Intent(context, CarriedOutActivity.class);
        in.putExtra(CarriedOutActivity1, dataStatus);
        in.putExtra(CarriedOutActivity3, i_ca1);
        in.putExtra(CarriedOutActivity4, i_ca2);
        in.putExtra(CarriedOutActivity5, i_ca3);
        in.putExtra(CarriedOutActivity2, car_num);
        context.startActivity(in);
    }
    @Override
    public int setLayoutId() {
        return R.layout.carried_out_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        Intent in = getIntent();
        dataStatus=(DataStatus)in.getSerializableExtra(CarriedOutActivity1);
        my_ca1 = in.getIntExtra(CarriedOutActivity3, 0);
        my_ca2 = in.getIntExtra(CarriedOutActivity4, 0);
        my_ca3 = in.getIntExtra(CarriedOutActivity5, 0);
        car_num= in.getIntExtra(CarriedOutActivity2, -1);
        if (my_ca1 == 0) {
            carriedOutLayout1.setVisibility(View.VISIBLE);
        } else {
            carriedOutLayout1.setVisibility(View.GONE);
        }

        if (my_ca2 == 0) {
            carriedOutLayout2.setVisibility(View.VISIBLE);
            if(my_ca1 == 0){
                carrideOutXian1.setVisibility(View.VISIBLE);
            }else{
                carrideOutXian1.setVisibility(View.GONE);
            }

        } else {
            carriedOutLayout2.setVisibility(View.GONE);
            carrideOutXian1.setVisibility(View.GONE);
        }
        if (my_ca3 == 0) {
            carriedOutLayout3.setVisibility(View.VISIBLE);


            if(my_ca1 != 0&&my_ca2 != 0){
                carrideOutXian2.setVisibility(View.GONE);
            }else{
                carrideOutXian2.setVisibility(View.VISIBLE);
            }
        } else {
            carriedOutLayout3.setVisibility(View.GONE);
            carrideOutXian2.setVisibility(View.GONE);
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


    @OnClick({R.id.carride_out_fh, R.id.carried_out_layout1, R.id.carried_out_layout2, R.id.carried_out_layout3, R.id.carried_out_sx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.carride_out_fh:
                finish();
                break;
            case R.id.carried_out_layout1:
//                myActivityManage.removeAll();
//                startActivity(new Intent(getContext(),NewIntelligentActivity.class));
//                DeviceStatusActivity.startActivity(getContext(),true,dataStatus);

                SmartDeviceActivity.startActivity(getContext(),dataStatus,car_num);
                break;
            case R.id.carried_out_layout2:
                dataStatus.setElse_right(" ");
                dataStatus.setElse_left(getStr(R.string.carride_out5));
                dataStatus.setElse_num(2);
                myActivityManage.removeAll();
                if(car_num==-1){
                    NewIntelligentActivity.startActivity(getContext(),dataStatus,"");
                }else{
                    NewIntelligentActivity.startActivity(getContext(),dataStatus,true,car_num);
                }

                break;
            case R.id.carried_out_layout3:
                showTimePick(h,m,h_s);
                break;
            case R.id.carried_out_sx:

                break;
        }
    }

    private int h = 0, m = 0, h_s = 1;
    private String on_time_cy="00,00,01";
    int num = 0; //开启时长
    private TimePickerDialog.Builder builder = null;
    private Dialog timeDialog;

    private void showTimePick(int h, int m, int s) {

        if (timeDialog == null) {

            builder = new TimePickerDialog.Builder(this);

            timeDialog = builder.setOnTimeSelectedListener(new TimePickerDialog.OnTimeSelectedListener() {
                @Override
                public void onTimeSelected(String[] times) {

//                    mTextView.setText(times[0] + ":" + times[1]);
                    int h = Integer.valueOf(times[0]);
                    int m = Integer.valueOf(times[1]);
                    int s = Integer.valueOf(times[2]);
                    int long_l = h * 60 * 60 + m * 60 + s;
                    dataStatus.setElse_right(long_l+"s");
                    dataStatus.setElse_left("延时");
                    dataStatus.setElse_num(3);
                    dataStatus.setElse_num3_time(long_l);
                    myActivityManage.removeAll();
                    if(num==-1){
                        NewIntelligentActivity.startActivity(getContext(),dataStatus,"");
                    }else{
                        NewIntelligentActivity.startActivity(getContext(),dataStatus,true,car_num);
                    }


                }
            }).create();

        }


        builder.setText_sj(h, m, s, "延时");
        timeDialog.show();

    }

}
