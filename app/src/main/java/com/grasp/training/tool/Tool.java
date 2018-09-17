package com.grasp.training.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

public class Tool {

    private  final static String string="Tool_bs";
    public static String getIMEI(Context context) {
        if(context==null){
            return  SharedPreferencesUtils.getParam(context,string,"").toString();
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

        @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
        SharedPreferencesUtils.setParam(context,string,imei+"");
        if (imei != null && !imei.equals("")) {

        } else {
            String bs=SharedPreferencesUtils.getParam(context,string,"").toString();
            if(bs.equals("")){
                long timeStamp = System.currentTimeMillis();
                SharedPreferencesUtils.setParam(context,string,timeStamp+"");
                imei = timeStamp + "";
            }else{
                imei=bs;
            }
        }

        return imei;
    }
}
