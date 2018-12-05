package com.example.liangmutian.mypicker;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimePickerDialog extends Dialog {

    public interface OnTimeSelectedListener {
        void onTimeSelected(String[] times);
    }


    private Params params;

    public TimePickerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private void setParams(TimePickerDialog.Params params) {
        this.params = params;
    }


    private static final class Params {
        private boolean shadow = true;
        private boolean canCancel = true;
        private LoopView loopHour, loopMin,loopS;
        private OnTimeSelectedListener callback;
    }

    public static class Builder {
        private final Context context;
        private final TimePickerDialog.Params params;

        public Builder(Context context) {
            this.context = context;
            params = new TimePickerDialog.Params();
        }

        /**
         * 获取当前选择的时间
         *
         * @return int[]数组形式返回。例[12,30]
         */
        private final String[] getCurrDateValues() {
            String currHour = params.loopHour.getCurrentItemValue();
            String currMin = params.loopMin.getCurrentItemValue();
            String currS = params.loopS.getCurrentItemValue();
            return new String[]{currHour, currMin,currS};
        }

        LoopView loopMin ,loopHour,loopS;
        TextView tv;
        public void setText_sj(int h,int m,int s,String z_zt){
            loopHour.setCurrentItem(h);
            loopMin.setCurrentItem(m);
            loopS.setCurrentItem(s);
                    tv.setText(z_zt);


        }
        public TimePickerDialog create() {
            final TimePickerDialog dialog = new TimePickerDialog(context, params.shadow ? R.style.Theme_Light_NoTitle_Dialog : R.style.Theme_Light_NoTitle_NoShadow_Dialog);
            View view = LayoutInflater.from(context).inflate(R.layout.layout_picker_time, null);

            tv=(TextView) view.findViewById(R.id.timePicker_tv);
             loopHour = (LoopView) view.findViewById(R.id.loop_hour);

            //修改优化边界值 by lmt 16/ 9 /12.禁用循环滑动,循环滑动有bug
            loopHour.setCyclic(false);
            ArrayList<String> l1=new ArrayList();
//            l1.add("--");
            for (int i=0;i<=23;i++){
                if(i<10){
                    l1.add("0"+i);
                }else{
                    l1.add(i+"");
                }
            }

//            loopHour.setArrayList(d(0, 24));
            loopHour.setArrayList(l1);
            loopHour.setCurrentItem(12);

             loopMin = (LoopView) view.findViewById(R.id.loop_min);
            loopMin.setCyclic(false);
            ArrayList<String> l2=new ArrayList();
//            l2.add("--");
            for (int i=0;i<=59;i++){
                if(i<10){
                    l2.add("0"+i);
                }else{
                    l2.add(i+"");
                }

            }

            loopMin.setArrayList(l2);
            loopMin.setCurrentItem(30);


            loopS = (LoopView) view.findViewById(R.id.loop_s);
            loopS.setCyclic(false);
            ArrayList<String> l3=new ArrayList();
//            l2.add("--");
            for (int i=0;i<=59;i++){
                if(i<10){
                    l3.add("0"+i);
                }else{
                    l3.add(i+"");
                }

            }

            loopS.setArrayList(l3);
            loopS.setCurrentItem(0);

            view.findViewById(R.id.tx_finish).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    params.callback.onTimeSelected(getCurrDateValues());
                }
            });
            view.findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            Window win = dialog.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setAttributes(lp);
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.Animation_Bottom_Rising);

            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(params.canCancel);
            dialog.setCancelable(params.canCancel);

            params.loopHour = loopHour;
            params.loopMin = loopMin;
            params.loopS = loopS;
            dialog.setParams(params);

            return dialog;
        }


        public Builder setOnTimeSelectedListener(OnTimeSelectedListener onTimeSelectedListener) {
            params.callback = onTimeSelectedListener;
            return this;
        }


        /**
         * 将数字传化为集合，并且补充0
         *
         * @param startNum 数字起点
         * @param count    数字个数
         * @return
         */
        private static List<String> d(int startNum, int count) {
            String[] values = new String[count];
            for (int i = startNum; i < startNum + count; i++) {
                String tempValue = (i < 10 ? "0" : "") + i;
                values[i - startNum] = tempValue;
            }
            return Arrays.asList(values);
        }
    }
}
