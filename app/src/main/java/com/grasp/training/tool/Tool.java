package com.grasp.training.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Tool {

    private  final static String string="Tool_bs";
    public static String getIMEI(Context context) {
        if(context==null){
            return  "12345678";
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

    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }
}
