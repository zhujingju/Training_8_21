package com.grasp.training.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.grasp.training.R;

import java.util.List;


/**
 * Created by zhujingju on 2018/6/14.
 */

public class PopwinDialog extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private View view;
    private Button qx, ok;
    private EditText editText;
    private Spinner spinner;
    private ProgressBar progressBar;
    private LinearLayout lin;
    private RelativeLayout rel;
    private TextView tv;

    public PopwinDialog(Context context) {
        mContext = context;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.popwindialog, null);

        tv = (TextView) view.findViewById(R.id.popwindialog_tv_1);
        qx = (Button) view.findViewById(R.id.popwindialog_b_qx);
        ok = (Button) view.findViewById(R.id.popwindialog_b_ok);
        editText = (EditText) view.findViewById(R.id.popwindialog_ed);
        spinner = (Spinner) view.findViewById(R.id.popwindialog_spinner);
        lin = (LinearLayout) view.findViewById(R.id.popwindialog_lin);
        progressBar = (ProgressBar) view.findViewById(R.id.popwindialog_pro);
        rel = (RelativeLayout) view.findViewById(R.id.popwindialog_layout);

        rel.setOnClickListener(this);
        qx.setOnClickListener(this);
        ok.setOnClickListener(this);
        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        //防止虚拟软键盘被弹出菜单遮住
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
//        this.setBackgroundDrawable(dw);
        // 设置弹出窗体显示时的动画
        this.setAnimationStyle(R.style.take_photo_anim);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.popwindialog_b_ok:
//                progressBar.setVisibility(View.VISIBLE);
//                lin.setVisibility(View.GONE);

                String s = spinner_list2.get(spinner.getSelectedItemPosition()).SSID;
                if (s.length() < 2) {
                    getPopwinInterface.getPopwinInterface(spinner_list2.get(spinner.getSelectedItemPosition()), editText.getText().toString());
                    dismiss();
                    return;
                }
                //开启提示
                if (s.substring(s.length() - 2, s.length()).equals("5G")) {
//                        Toast.makeText(mContext, "您选择的网络可能是5G网络，请换一个", Toast.LENGTH_LONG).show();
//                        ts_zt = true;
                    setWl();
                } else {
                    getPopwinInterface.getPopwinInterface(spinner_list2.get(spinner.getSelectedItemPosition()), editText.getText().toString());
                    dismiss();
                }


                break;
            case R.id.popwindialog_b_qx:
                dismiss();
                break;
            case R.id.popwindialog_layout:
                dismiss();
                break;

        }
    }


    public void setWl() {
        //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //    设置Title的图标
        //    设置Title的内容
        //    设置Content来显示一个信息
        builder.setMessage("您选择的网络可能是5G网络，确定要连接吗");
        //    设置一个PositiveButton
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getPopwinInterface.getPopwinInterface(spinner_list2.get(spinner.getSelectedItemPosition()), editText.getText().toString());
                dismiss();
            }
        });
        //    设置一个NegativeButton
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        //    设置一个NeutralButton

        //    显示出该对话框
        builder.show();
    }


    @Override

    public void dismiss() {
        super.dismiss();
    }

    List<ScanResult> spinner_list2;

    public void setList(List list, boolean zt) {
        spinner_list2 = list;
        if (zt) {
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }
        initSpinner2();

    }

    MyArrayAdapter arrayAdapter;

    private void initSpinner2() {
        //拿到被选择项的值
        Log.e("qqq", spinner_list2.size() + "");
        arrayAdapter = new MyArrayAdapter(mContext, spinner_list2);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
//                R.layout.spinner_layout, citys);
//        adapter.setDropDownViewResource(R.layout.spinner_layout);
        arrayAdapter.setList(spinner_list2);
        spinner.setAdapter(arrayAdapter);
    }


    class MyArrayAdapter extends BaseAdapter {

        private List<ScanResult> mList;
        private Context mContext;

        public MyArrayAdapter(Context pContext, List<ScanResult> pList) {
            this.mContext = pContext;
            this.mList = pList;
        }

        public void setList(List<ScanResult> pList) {
            this.mList = pList;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 下面是重要代码
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            tool too;
            View view = convertView;
            if (convertView == null) {
                too = new tool();
                LayoutInflater _LayoutInflater = LayoutInflater.from(mContext);
                view = _LayoutInflater.inflate(R.layout.spinner_layout2, null);
//                convertView=inflater.inflate(R.layout.spinner_layout2,  parent, false);
                too.tv = (TextView) view.findViewById(R.id.sp_tv);
                view.setTag(too);
            } else {
                too = (tool) view.getTag();
            }
            too.tv.setText(mList.get(position).SSID);

            return view;

        }

        class tool {
            TextView tv;
        }
    }

    private popwinInterface getPopwinInterface;

    public void setGetPopwinInterface(popwinInterface getPopwinInterface) {
        this.getPopwinInterface = getPopwinInterface;
    }

    public interface popwinInterface {
        public void getPopwinInterface(ScanResult scanResult, String pw);
    }
}
