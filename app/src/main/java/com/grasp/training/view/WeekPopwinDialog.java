package com.grasp.training.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.grasp.training.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class WeekPopwinDialog extends PopupWindow {

    @BindView(R.id.week_pop_tv_ok)
    TextView weekPopTvOk;
    @BindView(R.id.week_pop_tv_qx)
    TextView weekPopTvQx;
    @BindView(R.id.week_pop_tv1)
    TextView weekPopTv1;
    @BindView(R.id.week_pop_im1)
    ImageView weekPopIm1;
    @BindView(R.id.week_pop_layout1)
    RelativeLayout weekPopLayout1;
    @BindView(R.id.week_pop_tv2)
    TextView weekPopTv2;
    @BindView(R.id.week_pop_im2)
    ImageView weekPopIm2;
    @BindView(R.id.week_pop_layout2)
    RelativeLayout weekPopLayout2;
    @BindView(R.id.week_pop_tv3)
    TextView weekPopTv3;
    @BindView(R.id.week_pop_im3)
    ImageView weekPopIm3;
    @BindView(R.id.week_pop_layout3)
    RelativeLayout weekPopLayout3;
    @BindView(R.id.week_pop_tv4)
    TextView weekPopTv4;
    @BindView(R.id.week_pop_im4)
    ImageView weekPopIm4;
    @BindView(R.id.week_pop_layout4)
    RelativeLayout weekPopLayout4;
    @BindView(R.id.week_pop_tv5)
    TextView weekPopTv5;
    @BindView(R.id.week_pop_im5)
    ImageView weekPopIm5;
    @BindView(R.id.week_pop_layout5)
    RelativeLayout weekPopLayout5;
    @BindView(R.id.week_pop_tv6)
    TextView weekPopTv6;
    @BindView(R.id.week_pop_im6)
    ImageView weekPopIm6;
    @BindView(R.id.week_pop_layout6)
    RelativeLayout weekPopLayout6;
    @BindView(R.id.week_pop_tv7)
    TextView weekPopTv7;
    @BindView(R.id.week_pop_im7)
    ImageView weekPopIm7;
    @BindView(R.id.week_pop_layout7)
    RelativeLayout weekPopLayout7;
    Unbinder unbinder;
    private Context mContext;
    private View view;

    public WeekPopwinDialog(Context context) {
        mContext = context;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.week_popwin_dialog, null);
        unbinder = ButterKnife.bind(this, view);
        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        //防止虚拟软键盘被弹出菜单遮住
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x50000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // 设置弹出窗体显示时的动画
        this.setAnimationStyle(R.style.take_photo_anim);

    }


    @Override
    public void dismiss() {
        super.dismiss();
//        unbinder.unbind();
    }


    public void setWeek(boolean z1,boolean z2,boolean z3,boolean z4,boolean z5,boolean z6,boolean z7){
        zt1=z1;
        zt2=z2;
        zt3=z3;
        zt4=z4;
        zt5=z5;
        zt6=z6;
        zt7=z7;
        if(zt1){
            weekPopIm1.setBackgroundResource(R.drawable.control_selected_selected);
        }else{
            weekPopIm1.setBackgroundResource(R.drawable.control_selected_normal);
        }
        if(zt2){
            weekPopIm2.setBackgroundResource(R.drawable.control_selected_selected);
        }else{
            weekPopIm2.setBackgroundResource(R.drawable.control_selected_normal);
        }
        if(zt3){
            weekPopIm3.setBackgroundResource(R.drawable.control_selected_selected);
        }else{
            weekPopIm3.setBackgroundResource(R.drawable.control_selected_normal);
        }
        if(zt4){
            weekPopIm4.setBackgroundResource(R.drawable.control_selected_selected);
        }else{
            weekPopIm4.setBackgroundResource(R.drawable.control_selected_normal);
        }
        if(zt5){
            weekPopIm5.setBackgroundResource(R.drawable.control_selected_selected);
        }else{
            weekPopIm5.setBackgroundResource(R.drawable.control_selected_normal);
        }
        if(zt6){
            weekPopIm6.setBackgroundResource(R.drawable.control_selected_selected);
        }else{
            weekPopIm6.setBackgroundResource(R.drawable.control_selected_normal);
        }
        if(zt7){
            weekPopIm7.setBackgroundResource(R.drawable.control_selected_selected);
        }else{
            weekPopIm7.setBackgroundResource(R.drawable.control_selected_normal);
        }

    }

    private boolean zt1,zt2,zt3,zt4,zt5,zt6,zt7;
    @OnClick({R.id.week_pop_lin,R.id.week_pop_tv_ok, R.id.week_pop_tv_qx, R.id.week_pop_layout1, R.id.week_pop_layout2, R.id.week_pop_layout3, R.id.week_pop_layout4, R.id.week_pop_layout5, R.id.week_pop_layout6, R.id.week_pop_layout7})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.week_pop_lin:
                this.dismiss();
                break;
            case R.id.week_pop_tv_ok:
                weekInterface.onWeekInterface(zt1,zt2,zt3,zt4,zt5,zt6,zt7);
                this.dismiss();
                break;
            case R.id.week_pop_tv_qx:

                this.dismiss();

                break;
            case R.id.week_pop_layout1:
                if(!zt1){
                    weekPopIm1.setBackgroundResource(R.drawable.control_selected_selected);
                }else{
                    weekPopIm1.setBackgroundResource(R.drawable.control_selected_normal);
                }
                zt1=!zt1;
                break;
            case R.id.week_pop_layout2:
                if(!zt2){
                    weekPopIm2.setBackgroundResource(R.drawable.control_selected_selected);
                }else{
                    weekPopIm2.setBackgroundResource(R.drawable.control_selected_normal);
                }
                zt2=!zt2;
                break;
            case R.id.week_pop_layout3:
                if(!zt3){
                    weekPopIm3.setBackgroundResource(R.drawable.control_selected_selected);
                }else{
                    weekPopIm3.setBackgroundResource(R.drawable.control_selected_normal);
                }
                zt3=!zt3;
                break;
            case R.id.week_pop_layout4:
                if(!zt4){
                    weekPopIm4.setBackgroundResource(R.drawable.control_selected_selected);
                }else{
                    weekPopIm4.setBackgroundResource(R.drawable.control_selected_normal);
                }
                zt4=!zt4;
                break;
            case R.id.week_pop_layout5:
                if(!zt5){
                    weekPopIm5.setBackgroundResource(R.drawable.control_selected_selected);
                }else{
                    weekPopIm5.setBackgroundResource(R.drawable.control_selected_normal);
                }
                zt5=!zt5;
                break;
            case R.id.week_pop_layout6:
                if(!zt6){
                    weekPopIm6.setBackgroundResource(R.drawable.control_selected_selected);
                }else{
                    weekPopIm6.setBackgroundResource(R.drawable.control_selected_normal);
                }
                zt6=!zt6;
                break;
            case R.id.week_pop_layout7:

                if(!zt7){
                    weekPopIm7.setBackgroundResource(R.drawable.control_selected_selected);
                }else{
                    weekPopIm7.setBackgroundResource(R.drawable.control_selected_normal);
                }
                zt7=!zt7;
                break;
        }
    }

    private  weekInterface weekInterface;

    public void setWeekInterface(WeekPopwinDialog.weekInterface weekInterface) {
        this.weekInterface = weekInterface;
    }

    public interface  weekInterface{
        public void onWeekInterface(boolean z1, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7);
    }
}
