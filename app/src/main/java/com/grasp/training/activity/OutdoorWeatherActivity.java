package com.grasp.training.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.grasp.training.R;
import com.grasp.training.tool.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OutdoorWeatherActivity extends BaseActivity {
    @BindView(R.id.outdoor_weather_fh)
    ImageView outdoorWeatherFh;
    @BindView(R.id.outdoor_weather_layout1)
    RelativeLayout outdoorWeatherLayout1;
    @BindView(R.id.outdoor_weather_layout2)
    RelativeLayout outdoorWeatherLayout2;
    @BindView(R.id.outdoor_weather_layout3)
    RelativeLayout outdoorWeatherLayout3;
    @BindView(R.id.outdoor_weather_layout4)
    RelativeLayout outdoorWeatherLayout4;

    private int num;

    public static void stateActivity(Context context, int num){
        Intent in=new Intent(context,OutdoorWeatherActivity.class);
        in.putExtra("OutdoorWeatherActivity",num);
        context.startActivity(in);
    }

    @Override
    public int setLayoutId() {
        return R.layout.outdoor_weather_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        num=getIntent().getIntExtra("OutdoorWeatherActivity",-1);
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


    @OnClick({R.id.outdoor_weather_fh, R.id.outdoor_weather_layout1, R.id.outdoor_weather_layout2, R.id.outdoor_weather_layout3, R.id.outdoor_weather_layout4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.outdoor_weather_fh:
                finish();
                break;
            case R.id.outdoor_weather_layout1:
                if(num==-1){  //第一次
                    SetOutdoorWeatherActivity.stateActivity(getContext(),1,num);
                }else{//
                    SetOutdoorWeatherActivity.stateActivity(getContext(),1,num);
                }

                break;
            case R.id.outdoor_weather_layout2:
                if(num==-1){
                    SetOutdoorWeatherActivity.stateActivity(getContext(),2,num);
                }else{
                    SetOutdoorWeatherActivity.stateActivity(getContext(),2,num);
                }
                break;
            case R.id.outdoor_weather_layout3:
                if(num==-1){
                    SetOutdoorWeatherActivity.stateActivity(getContext(),3,num);
                }else{
                    SetOutdoorWeatherActivity.stateActivity(getContext(),3,num);
                }
                break;
            case R.id.outdoor_weather_layout4:
                if(num==-1){
                    SetOutdoorWeatherActivity.stateActivity(getContext(),4,num);
                }else{
                    SetOutdoorWeatherActivity.stateActivity(getContext(),4,num);
                }
                break;
        }
    }
}
