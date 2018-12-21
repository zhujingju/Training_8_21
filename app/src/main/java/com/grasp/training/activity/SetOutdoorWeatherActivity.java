package com.grasp.training.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liangmutian.mypicker.TimePickerDialog3;
import com.example.liangmutian.mypicker.TimePickerDialog4;
import com.grasp.training.R;
import com.grasp.training.tool.BaseActivity;
import com.grasp.training.tool.DataStatus;
import com.grasp.training.tool.myActivityManage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetOutdoorWeatherActivity extends BaseActivity {
    @BindView(R.id.set_outdoor_weather_fh)
    ImageView setOutdoorWeatherFh;
    @BindView(R.id.set_outdoor_weather_tv1)
    TextView setOutdoorWeatherTv1;
    @BindView(R.id.set_outdoor_weather_tv2)
    TextView setOutdoorWeatherTv2;
    @BindView(R.id.set_outdoor_weather_tv)
    TextView setOutdoorWeatherTv;
    private int lab=0;
    private String var="";
    private int lab_num;
    public static void stateActivity(Context context,int la,int num){
        Intent in=new Intent(context,SetOutdoorWeatherActivity.class);
        in.putExtra("SetOutdoorWeatherActivity",la);
        in.putExtra("SetOutdoorWeatherActivity1",num);
        context.startActivity(in);
    }

    @Override
    public int setLayoutId() {
        return R.layout.set_outdoor_weather;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        lab=getIntent().getIntExtra("SetOutdoorWeatherActivity",0);
        lab_num=getIntent().getIntExtra("SetOutdoorWeatherActivity1",-1);
        if(lab==0){
            Toast.makeText(getContext(),"参数不对",Toast.LENGTH_LONG).show();

            finish();
            return;
        }else if(lab==1){
            setOutdoorWeatherTv.setText(getStr(R.string.outdoor_weather1));
            setOutdoorWeatherTv1.setText(getStr(R.string.outdoor_weather7));
            setOutdoorWeatherTv2.setText(getStr(R.string.outdoor_weather8));
        }else if(lab==2){
            setOutdoorWeatherTv.setText(getStr(R.string.outdoor_weather2));
            setOutdoorWeatherTv1.setText(getStr(R.string.outdoor_weather9));
            setOutdoorWeatherTv2.setText(getStr(R.string.outdoor_weather10));
            dw="℃";
            max=150;
            min=-150;
            num=10;
            num2=10;
        }else if(lab==3){
            setOutdoorWeatherTv.setText(getStr(R.string.outdoor_weather3));
            setOutdoorWeatherTv1.setText(getStr(R.string.outdoor_weather11));
            setOutdoorWeatherTv2.setText(getStr(R.string.outdoor_weather12));
            dw="%";
            max=100;
            min=0;
            num=30;
            num2=30;
        }else if(lab==4){
            setOutdoorWeatherTv.setText(getStr(R.string.outdoor_weather4));
            setOutdoorWeatherTv1.setText(getStr(R.string.outdoor_weather13));
            setOutdoorWeatherTv2.setText(getStr(R.string.outdoor_weather14));
            dw="μg/m3";
            max=500;
            min=0;
            num=200;
            num2=200;
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


    @OnClick({R.id.set_outdoor_weather_fh, R.id.set_outdoor_weather_tv1, R.id.set_outdoor_weather_tv2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.set_outdoor_weather_fh:
                finish();
                break;
            case R.id.set_outdoor_weather_tv1:
                if(lab==1){
                    if(lab_num!=-1){
                        myActivityManage.removeAll();
                        DataStatus dataStatus=new DataStatus();
                        dataStatus.setIf_left("室外天气");
                        dataStatus.setIf_right("日出时");
                        dataStatus.setIf_num(3);
                        dataStatus.setIf_add(true);
                        dataStatus.setNum3_num(lab);
                        dataStatus.setNum3_num_var(0);
                        dataStatus.setNum3_type(1);
                        NewIntelligentActivity.startActivity(getContext(),dataStatus,false,lab_num);
                        return;
                    }

                    //第一个参数由逗号隔开，1：类型 2：上下 3：具体数值
                    myActivityManage.removeAll();
                    DataStatus dataStatus=new DataStatus();
                    dataStatus.setIf_left("室外天气");
                    dataStatus.setIf_right("日出时");
                    dataStatus.setIf_num(3);
                    dataStatus.setIf_add(true);
                    dataStatus.setNum3_num(lab);
                    dataStatus.setNum3_num_var(0);
                    dataStatus.setNum3_type(1);
                    CarriedOutActivity.startCarriedOutActivity(getContext(),dataStatus,0,0,0);
                }else{
                    showTimePick(num,dw,max,min);
                }

                break;
            case R.id.set_outdoor_weather_tv2:
                Log.e("qqq","dw="+dw);
                if(lab==1){
                    if(lab_num!=-1){
                        myActivityManage.removeAll();
                        DataStatus dataStatus=new DataStatus();
                        dataStatus.setIf_left("室外天气");
                        dataStatus.setIf_right("日落时");
                        dataStatus.setIf_num(3);
                        dataStatus.setIf_add(true);
                        dataStatus.setNum3_num(lab);
                        dataStatus.setNum3_num_var(0);
                        dataStatus.setNum3_type(2);
                        NewIntelligentActivity.startActivity(getContext(),dataStatus,false,lab_num);
                        return;
                    }
                    myActivityManage.removeAll();
                    DataStatus dataStatus=new DataStatus();
                    dataStatus.setIf_left("室外天气");
                    dataStatus.setIf_right("日落时");
                    dataStatus.setIf_num(3);
                    dataStatus.setIf_add(true);
                    dataStatus.setNum3_num(lab);
                    dataStatus.setNum3_num_var(0);
                    dataStatus.setNum3_type(2);
                    CarriedOutActivity.startCarriedOutActivity(getContext(),dataStatus,0,0,0);

                }else{
                    showTimePick2(num2,dw,max,min);
                }

                break;
        }
    }

    private String dw="";
    private int max,min;
    int num=0,num2=0;
    private TimePickerDialog4.Builder builder = null, builder2 = null;
    private Dialog timeDialog, timeDialog2;

    private void showTimePick(int num,String dw,int max,int min) {
        Log.e("qqq","dw="+dw);
        if (timeDialog == null) {

            builder = new TimePickerDialog4.Builder(this);

            timeDialog = builder.setOnTimeSelectedListener(new TimePickerDialog4.OnTimeSelectedListener() {
                @Override
                public void onTimeSelected(String[] times) {

//                    conditionTimingTv2.setText(s_h + ":" + s_m);
                    myActivityManage.removeAll();
                    String var="";
                    if(lab==2){
                        var="温度高于"+times[0]+"℃";
                    }else if(lab==3){
                        var="湿度高于"+times[0]+"%";
                    }else if(lab==4){
                        var="pm2.5高于"+times[0]+"μg/m3";
                    }

                    if(lab_num!=-1){
                        DataStatus dataStatus=new DataStatus();
                        dataStatus.setIf_left("室外天气");
                        dataStatus.setIf_right(var);
                        dataStatus.setIf_num(3);
                        dataStatus.setIf_add(true);
                        dataStatus.setNum3_num(lab);
                        dataStatus.setNum3_num_var(Integer.valueOf(times[0]));
                        dataStatus.setNum3_type(1);
                        NewIntelligentActivity.startActivity(getContext(),dataStatus,false,lab_num);
                        return;
                    }
                    DataStatus dataStatus=new DataStatus();
                    dataStatus.setIf_left("室外天气");
                    dataStatus.setIf_right(var);
                    dataStatus.setIf_num(3);
                    dataStatus.setIf_add(true);
                    dataStatus.setNum3_num(lab);
                    dataStatus.setNum3_num_var(Integer.valueOf(times[0]));
                    dataStatus.setNum3_type(1);
                    CarriedOutActivity.startCarriedOutActivity(getContext(),dataStatus,0,0,0);

                }
            }).create();

        }
        builder.setText_sj(num, dw,max,min);
        timeDialog.show();

    }



    private void showTimePick2(int num,String dw,int max,int min) {

        if (timeDialog2 == null) {

            builder2 = new TimePickerDialog4.Builder(this);

            timeDialog2 = builder2.setOnTimeSelectedListener(new TimePickerDialog4.OnTimeSelectedListener() {
                @Override
                public void onTimeSelected(String[] times) {

//                    conditionTimingTv2.setText(s_h + ":" + s_m);
                    myActivityManage.removeAll();

                    String var="";
                    if(lab==2){
                        var="温度低于"+times[0]+"℃";
                    }else if(lab==3){
                        var="湿度低于"+times[0]+"%";
                    }else if(lab==4){
                        var="pm2.5低于"+times[0]+"μg/m3";
                    }


                    if(lab_num!=-1){
                        DataStatus dataStatus=new DataStatus();
                        dataStatus.setIf_left("室外天气");
                        dataStatus.setIf_right(var);
                        dataStatus.setIf_num(3);
                        dataStatus.setIf_add(true);
                        dataStatus.setNum3_num(lab);
                        dataStatus.setNum3_num_var(Integer.valueOf(times[0]));
                        dataStatus.setNum3_type(2);
                        NewIntelligentActivity.startActivity(getContext(),dataStatus,false,lab_num);
                        return;
                    }
                    DataStatus dataStatus=new DataStatus();
                    dataStatus.setIf_left("室外天气");
                    dataStatus.setIf_right(var);
                    dataStatus.setIf_num(3);
                    dataStatus.setIf_add(true);
                    dataStatus.setNum3_num(lab);
                    dataStatus.setNum3_num_var(Integer.valueOf(times[0]));
                    dataStatus.setNum3_type(2);
                    CarriedOutActivity.startCarriedOutActivity(getContext(),dataStatus,0,0,0);
                }
            }).create();

        }
        builder2.setText_sj(num, dw,max,min);
        timeDialog2.show();

    }
}
