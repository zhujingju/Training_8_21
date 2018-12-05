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

public class TimePickerDialog4 extends Dialog {

    public interface OnTimeSelectedListener {
        void onTimeSelected(String[] times);
    }


    private Params params;

    public TimePickerDialog4(Context context, int themeResId) {
        super(context, themeResId);
    }

    private void setParams(Params params) {
        this.params = params;
    }


    private static final class Params {
        private boolean shadow = true;
        private boolean canCancel = true;
        private LoopView loopHour, loopMin;
        private OnTimeSelectedListener callback;
    }

    public static class Builder {
        private final Context context;
        private final Params params;

        public Builder(Context context) {
            this.context = context;
            params = new Params();
        }

        /**
         * 获取当前选择的时间
         *
         * @return int[]数组形式返回。例[12,30]
         */
        private final String[] getCurrDateValues() {
            String currHour = params.loopHour.getCurrentItemValue();
            return new String[]{currHour};
        }

        LoopView loopHour;
        String dw="";
        int max,min;
        TextView tv;

        public void setText_sj(int h,String dw,int max,int min){

            this.dw=dw;
            this.max=max;
            this.min=min;
            tv.setText(dw);
            ArrayList<String> l1=new ArrayList();
            int num=0;
            for (int i=min;i<=max;i++){
//                if(i<10){
//                    l1.add("0"+i);
//                }else{
                    l1.add(i+"");
                    if(h==i){
                        num=i;
                    }
//                }
            }

//            loopHour.setArrayList(d(0, 24));

            loopHour.setArrayList(l1);
            loopHour.setCurrentItem(num+Math.abs(min));

            params.loopHour = loopHour;
            dialog.setParams(params);

        }

         TimePickerDialog4 dialog;
        public TimePickerDialog4 create() {
            dialog = new TimePickerDialog4(context, params.shadow ? R.style.Theme_Light_NoTitle_Dialog : R.style.Theme_Light_NoTitle_NoShadow_Dialog);
            View view = LayoutInflater.from(context).inflate(R.layout.layout_picker_time4, null);

             loopHour = (LoopView) view.findViewById(R.id.loop_hour);
            tv=(TextView) view.findViewById(R.id.loop_tv);

//            tv.setText(dw);
            //修改优化边界值 by lmt 16/ 9 /12.禁用循环滑动,循环滑动有bug
            loopHour.setCyclic(false);
//            ArrayList<String> l1=new ArrayList();
//            for (int i=min;i<=max;i++){
//                if(i<10){
//                    l1.add("0"+i);
//                }else{
//                    l1.add(i+"");
//                }
//            }
//
////            loopHour.setArrayList(d(0, 24));
//            loopHour.setArrayList(l1);
//            loopHour.setCurrentItem(12);



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
