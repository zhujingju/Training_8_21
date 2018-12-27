package com.grasp.training.tool;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class WifiAdmin {

    public static final int AUTH_NOPASS = 3;
    public static final int AUTH_WEP = 1;
    public static final int AUTH_WPA = 2;
    private static final String TAG = "[WifiAdmin]";
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<ScanResult> mWifiList = null;
    private List<WifiConfiguration> mWifiConfiguration;
    private WifiLock mWifiLock;
    private DhcpInfo dhcpInfo;
    private Context context;

    public WifiAdmin(Context context) {

//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O||Build.VERSION.SDK_INT==Build.VERSION_CODES.P) {
//            mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            mWifiInfo = mWifiManager.getConnectionInfo();
//        } else if (Build.VERSION.SDK_INT==Build.VERSION_CODES.O_MR1) {
//
//        }
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        this.context=context;
    }

    public boolean openWifi() {//打开wifi
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return mWifiManager.isWifiEnabled();
    }

    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    public int checkState() {
        return mWifiManager.getWifiState();
    }

    public void acquireWifiLock() {//锁定wifiLock
        mWifiLock.acquire();
    }

    public void releaseWifiLock() {//解锁wifiLock
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    public void connectConfiguration(int index) {//指定配置好的网络进行连接
        if (index > mWifiConfiguration.size()) {
            return;
        }
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
    }

    public void startScan() {//wifi扫描
        boolean scan = mWifiManager.startScan();
        Log.i(TAG, "startScan result:" + scan);
        mWifiList = mWifiManager.getScanResults();
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();

        if (mWifiList != null) {
            for (int i = 0; i < mWifiList.size(); i++) {
                ScanResult result = mWifiList.get(i);
            }
        } else {
        }

//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O||Build.VERSION.SDK_INT==Build.VERSION_CODES.P) {
//
//
//
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//                return mWifiInfo.getSSID();
//            } else {
//                return mWifiInfo.getSSID().replace("\"", "");
//            }
//        } else if (Build.VERSION.SDK_INT==Build.VERSION_CODES.O_MR1){
//
//            ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//            assert connManager != null;
//            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
//            if (networkInfo.isConnected()) {
//                if (networkInfo.getExtraInfo()!=null){
//                    return networkInfo.getExtraInfo().replace("\"","");
//                }
//            }
//        }

    }

    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    public StringBuilder lookUpScan() {// 查看扫描结果
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    public String getSSid() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    public DhcpInfo getDhcpInfo() {
        return dhcpInfo = mWifiManager.getDhcpInfo();
    }

    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    public WifiInfo getWifiInfo() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo;
    }

    public boolean addNetwork(WifiConfiguration wcg) { // 添加一个网络配置并连接
//        int wcgID = mWifiManager.addNetwork(wcg);
////        mWifiManager.disableNetwork(wcgID);
//        boolean b = mWifiManager.enableNetwork(wcgID, true);
//        Log.e("addNetwork","wcgID="+wcgID);
//	        System.out.println("addNetwork addNetwork--" + wcgID);
//	        System.out.println("addNetwork enableNetwork--" + b);
//	        System.out.println("addNetwork addNetwork2 " + wcg);
//        Log.e("wcg","wcg="+wcg);
        boolean b=false;
        try {
            b=setStaticIpConfiguration(mWifiManager, wcg,
                    InetAddress.getByName("192.168.5.5"), 24,
                    null,
                    InetAddress.getAllByName("8.8.8.8"));
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }






    /**
     * 连接wifi 参数：wifi的ssid及wifi的密码
     */
    public boolean connectWifiTest(final String ssid, final String pwd) {
        boolean isSuccess = false;
        boolean flag = false;
        mWifiManager.disconnect();
        boolean addSucess = addNetwork(CreateWifiInfo(ssid, pwd, 3));
        if (addSucess) {
            while (!flag && !isSuccess) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                String currSSID = getCurrentWifiInfo().getSSID();
                if (currSSID != null)
                    currSSID = currSSID.replace("\"", "");
                int currIp = getCurrentWifiInfo().getIpAddress();
                if (currSSID != null && currSSID.equals(ssid) && currIp != 0) {
                    isSuccess = true;
                } else {
                    flag = true;
                }
            }
        }
        return isSuccess;
    }

    /**
     * 获取当前手机所连接的wifi信息
     */
    public WifiInfo getCurrentWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }

    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }
    //然后是一个实际应用方法，只验证过没有密码的情况：

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        Log.e("qqq","SSID="+SSID+" Password="+Password+" Type"+Type);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
//        config.priority = 40;
        config.SSID = "\"" + SSID + "\"";
        Log.e("netId","config.BSSID" +" "+ config.BSSID);
//        Log.e("networkId","config.networkId" +" "+ config.networkId);
        WifiConfiguration tempConfig = this.IsExsits(config.SSID);
        if (tempConfig != null) {
//	              mWifiManager.removeNetwork(tempConfig.networkId);
            Log.e("netId","tempConfig!=" +null);
//            mWifiManager.removeNetwork(tempConfig.networkId);
            return tempConfig;
        }
        Log.e("netId","Type=" +" "+ Type);
        if (Type == AUTH_NOPASS) //WIFICIPHER_NOPASS
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + ""+ "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;


//            config.preSharedKey = null;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedAuthAlgorithms.clear();
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }
        if (Type == AUTH_WEP) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == AUTH_WPA) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }


    private WifiConfiguration IsExsits(String SSID) { // 查看以前是否已经配置过该SSID
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }


    public int GetCurrentNetwordId() {
        WifiInfo info = mWifiManager.getConnectionInfo();
        if (info != null) {
            return info.getNetworkId();
        }
        return 0;
    }

    public void EnableNetwork(int networkId) {
        if (networkId != 0) {
            mWifiManager.enableNetwork(networkId, true);
        }
    }




    @SuppressWarnings("unchecked")
    public static boolean setStaticIpConfiguration(WifiManager manager,
                                                WifiConfiguration config, InetAddress ipAddress, int prefixLength,
                                                InetAddress gateway, InetAddress[] dns)
            throws ClassNotFoundException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, NoSuchFieldException, InstantiationException {
        // First set up IpAssignment to STATIC.
        Object ipAssignment = getEnumValue(
                "android.net.IpConfiguration$IpAssignment", "STATIC");
        callMethod(config, "setIpAssignment",
                new String[] { "android.net.IpConfiguration$IpAssignment" },
                new Object[] { ipAssignment });

        // Then set properties in StaticIpConfiguration.
        Object staticIpConfig = newInstance("android.net.StaticIpConfiguration");

        Object linkAddress = newInstance("android.net.LinkAddress",
                new Class[] { InetAddress.class, int.class }, new Object[] {
                        ipAddress, prefixLength });
        setField(staticIpConfig, "ipAddress", linkAddress);
//        setField(staticIpConfig, "gateway", gateway);
        ArrayList<Object> aa = (ArrayList<Object>) getField(staticIpConfig,
                "dnsServers");
        aa.clear();
        for (int i = 0; i < dns.length; i++)
            aa.add(dns[i]);
        callMethod(config, "setStaticIpConfiguration",
                new String[] { "android.net.StaticIpConfiguration" },
                new Object[] { staticIpConfig });
        System.out.println("conconconm" + config);
        int updateNetwork = manager.updateNetwork(config);
        boolean saveConfiguration = manager.saveConfiguration();
        System.out.println("updateNetwork" + updateNetwork + saveConfiguration);

        System.out.println("ttttttttttt" + "成功");

//        manager.disconnect();
        int netId = manager.addNetwork(config);
//        int netId = config.networkId;
        Log.e("netId","netId="+netId+"");
//
        manager.disableNetwork(netId);


        boolean  flag  = manager.enableNetwork(netId, true);

        Log.e("flag",flag+"");

        return flag;

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Object getEnumValue(String enumClassName, String enumValue)
            throws ClassNotFoundException {
        Class enumClz = (Class) Class.forName(enumClassName);
        return Enum.valueOf(enumClz, enumValue);
    }



    private static void setField(Object object, String fieldName, Object value)
            throws IllegalAccessException, IllegalArgumentException,
            NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.set(object, value);
    }



    private static Object getField(Object object, String fieldName)
            throws IllegalAccessException, IllegalArgumentException,
            NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        Object out = field.get(object);
        return out;
    }



    @SuppressWarnings("rawtypes")
    private static void callMethod(Object object, String methodName,
                                   String[] parameterTypes, Object[] parameterValues)
            throws ClassNotFoundException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException {
        Class[] parameterClasses = new Class[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++)
            parameterClasses[i] = Class.forName(parameterTypes[i]);

        Method method = object.getClass().getDeclaredMethod(methodName,
                parameterClasses);
        method.invoke(object, parameterValues);
    }


    public String intToIp(int ipAddress) {
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));

    }
    // 直接使用set方法调用 可能遇到需要地址转换方法如下：
    public static String int2ip(int ip) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf((int) (ip & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        return sb.toString();
    }

    private static Object newInstance(String className)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException {
        return newInstance(className, new Class[0], new Object[0]);
    }



    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object newInstance(String className,
                                      Class[] parameterClasses, Object[] parameterValues)
            throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, ClassNotFoundException {
        Class clz = Class.forName(className);
        Constructor constructor = clz.getConstructor(parameterClasses);
        return constructor.newInstance(parameterValues);
    }



    //## 获取SSID 的方法如下

    /**
     * 获取SSID
     * @param activity 上下文
     * @return  WIFI 的SSID
     */
    public String getWIFISSID(Activity activity) {
        String ssid="unknown id";

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O||Build.VERSION.SDK_INT==Build.VERSION_CODES.P) {

            WifiManager mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            assert mWifiManager != null;
            WifiInfo info = mWifiManager.getConnectionInfo();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return info.getSSID();
            } else {
                return info.getSSID().replace("\"", "");
            }
        } else if (Build.VERSION.SDK_INT==Build.VERSION_CODES.O_MR1){

            ConnectivityManager connManager = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connManager != null;
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo.isConnected()) {
                if (networkInfo.getExtraInfo()!=null){
                    return networkInfo.getExtraInfo().replace("\"","");
                }
            }
        }
        return ssid;
    }

}

