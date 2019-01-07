package com.grasp.training.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Tool {

    private final static String string = "Tool_bs";

    public static String getIMEI(Context context) {
        String timeS = (String) SharedPreferencesUtils.getParam(context, string, "");
        if (timeS.equals("")) {
            long timeStamp = System.currentTimeMillis();
            timeS = timeStamp + "zjj";
            SharedPreferencesUtils.setParam(context, string, timeS + "");
        }


        if (context == null) {
            return timeS;
        }
        if (lacksPermission(context, "android.permission.READ_PHONE_STATE")) {
            Log.e("qqq","lacksPermission=没有权限"+" timeS="+timeS);
            return timeS;
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

        @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
        SharedPreferencesUtils.setParam(context, string, imei + "");
        if (imei != null && !imei.equals("")) {

        } else {

            imei = timeS;
        }

        return imei;
    }


    /**
     * 判断是否缺少权限
     */
    private static boolean lacksPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_DENIED;
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
