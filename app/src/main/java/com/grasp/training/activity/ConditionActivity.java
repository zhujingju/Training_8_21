package com.grasp.training.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grasp.training.R;
import com.grasp.training.tool.BaseActivity;
import com.grasp.training.tool.DataStatus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConditionActivity extends BaseActivity {

    @BindView(R.id.condition_fh)
    ImageView conditionFh;
    @BindView(R.id.condition_layout1)
    RelativeLayout conditionLayout1;
    @BindView(R.id.condition_layout2)
    RelativeLayout conditionLayout2;
    @BindView(R.id.condition_layout3)
    RelativeLayout conditionLayout3;
    @BindView(R.id.condition_xian)
    TextView xian;

    private int num;
    private boolean zt;
    public static void startActivity(Context context ,int num,boolean zt){
        Intent in = new Intent(context, ConditionActivity.class);
        in.putExtra("ConditionActivity", num);
        in.putExtra("ConditionActivity2", zt);
        context.startActivity(in);
    }

    @Override
    public int setLayoutId() {
        return R.layout.condition_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        num=getIntent().getIntExtra("ConditionActivity",-1);
        zt=getIntent().getBooleanExtra("ConditionActivity2",false);
        Log.e("onNewIntent","cond num="+num);
        if(num==1){
            xian.setVisibility(View.GONE);
            conditionLayout1.setVisibility(View.GONE);
        }else if(num==0){
            xian.setVisibility(View.GONE);
            conditionLayout1.setVisibility(View.GONE);
        }
        else{
            xian.setVisibility(View.VISIBLE);
            conditionLayout1.setVisibility(View.VISIBLE);
        }

        if(zt){
            xian.setVisibility(View.GONE);
            conditionLayout1.setVisibility(View.GONE);
        }else{
            xian.setVisibility(View.VISIBLE);
            conditionLayout1.setVisibility(View.VISIBLE);
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


    @OnClick({R.id.condition_fh, R.id.condition_layout1, R.id.condition_layout2,R.id.condition_layout3, R.id.condition_sx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.condition_fh:
                finish();
                break;
            case R.id.condition_layout1:
                if(num==-1) {
                    DataStatus dataStatus = new DataStatus();
                    dataStatus.setIf_left("手动执行");
                    dataStatus.setIf_right("");
                    dataStatus.setIf_num(1);
                    dataStatus.setIf_add(false);
                    CarriedOutActivity.startCarriedOutActivity(getContext(), dataStatus, 0, 0, 0);
                }else{
                    DataStatus dataStatus = new DataStatus();
                    dataStatus.setIf_left("手动执行");
                    dataStatus.setIf_right("");
                    dataStatus.setIf_num(1);
                    dataStatus.setIf_add(false);
                    NewIntelligentActivity.startActivity(getContext(),dataStatus,false,num);
                }
                break;
            case R.id.condition_layout2:
                if(num==-1){
                    ConditionTimingActivity.stastActivity(getContext(),"0000000","10:00",num);
                }else{
                    ConditionTimingActivity.stastActivity(getContext(),"0000000","10:00",num);
                }

                break;
            case R.id.condition_layout3:
                if(num==-1){
                    startActivity(new Intent(getContext(),OutdoorWeatherActivity.class));
                }else{
                    OutdoorWeatherActivity.stateActivity(getContext(),num);
                }

                break;
            case R.id.condition_sx:
                if(num==-1){
                    SmartDeviceActivity.startActivity(getContext(),null,num);
                }else{
                    SmartDeviceActivity.startActivity(getContext(),null,num);
                }

                break;
        }
    }

}
