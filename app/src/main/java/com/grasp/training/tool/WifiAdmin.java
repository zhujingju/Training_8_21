package com.grasp.training.tool;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class WifiAdmin {

    // 定义一个WifiManager对象
    private WifiManager mWifiManager;
    // 定义一个WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mScanWifiList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfigurations;

    private WifiManager.WifiLock mWifiLock;

    public WifiAdmin(Context mContext) {
        //取得WifiManager对象
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        //取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();

    }
    /**
     * Function:关闭wifi<br>
     * @return<br>
     */
    public boolean closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            return mWifiManager.setWifiEnabled(false);
        }
        return false;
    }

    /**
     * Gets the Wi-Fi enabled state.检查当前wifi状态
     */
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 锁定wifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁wifiLock
    public void releaseWifiLock() {
        // 判断是否锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个wifiLock
    public void createWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("test");
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfigurations;
    }

    // 指定配置好的网络进行连接
    public void connetionConfiguration(int index) {
        if (index > mWifiConfigurations.size()) {
            return;
        }
        // 连接配置好指定ID的网络
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId,
                true);
    }

    public void startScan() {
        // 开启wifi
        openWifi();
        // 开始扫描
        mWifiManager.startScan();
        // 得到扫描结果
        mScanWifiList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();

    }

    // 得到网络列表
    public List<ScanResult> getWifiList() {
        return mScanWifiList;
    }

    // 查看扫描结果
    public StringBuffer lookUpScan() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mScanWifiList.size(); i++) {
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            sb.append((mScanWifiList.get(i)).toString()).append("\n");
        }
        return sb;
    }

    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    /**
     * Return the basic service set identifier (BSSID) of the current access
     * point. The BSSID may be {@code null} if there is no network currently
     * connected.
     *
     * @return the BSSID, in the form of a six-byte MAC address:
     *         {@code XX:XX:XX:XX:XX:XX}
     */
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    public int getIpAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    /**
     * the network ID, or -1 if there is no currently connected network
     */
    public int getNetWordId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /**
     * Function: 得到wifiInfo的所有信息
     */
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    // 添加一个网络并连接
    public void addNetWork(WifiConfiguration configuration) {
        int wcgId = mWifiManager.addNetwork(configuration);
        mWifiManager.enableNetwork(wcgId, true);
    }

    // 断开指定ID的网络
    public void disConnectionWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    /**
     * Function: 打开wifi功能<br>
     * @return true:打开成功；false:打开失败<br>
     */
    public boolean openWifi() {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    /**
     * 给外部提供一个借口，连接无线网络
     *
     * @param SSID
     * @param Password
     * @param Type
     * @return <br>
     * @return true:连接成功；false:连接失败<br>
     *
     */
    public boolean connect(String SSID, String Password, WifiConnectUtils.WifiCipherType Type) {
        Log.e("WifiListActivity", "Wifi：" + SSID+" "+Password+" "+Type);
        if (!this.openWifi()) {
            return false;
        }
        // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                // 为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
        }
        if (SSID == null || Password == null || SSID.equals("")) {
            Log.e(this.getClass().getName(),
                    "addNetwork() ## nullpointer error!");
            return false;
        }
        WifiConfiguration wifiConfig = createWifiInfo(SSID, Password, Type);
        // wifi的配置信息
        if (wifiConfig == null) {
            return false;
        }
        // 查看以前是否也配置过这个网络
        WifiConfiguration tempConfig = this.isExsits(SSID);
        if (tempConfig != null) {

            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        // 添加一个新的网络描述为一组配置的网络。
        int netID = mWifiManager.addNetwork(wifiConfig);
        Log.e("WifiListActivity", "wifi的netID为：" + netID);
        // 断开连接
//        mWifiManager.disconnect();
        // 重新连接
        Log.e("WifiListActivity", "Wifi的重新连接netID为：" + netID);
        // 设置为true,使其他的连接断开
        boolean mConnectConfig = mWifiManager.enableNetwork(netID, true);
        mWifiManager.reconnect();
        return mConnectConfig;
    }

    // 查看以前是否也配置过这个网络
    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password,
                                             WifiConnectUtils.WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == WifiConnectUtils.WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiConnectUtils.WifiCipherType.WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiConnectUtils.WifiCipherType.WIFICIPHER_WPA) {
            // 修改之后配置
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);

        } else {
            return null;
        }
        return config;
    }

    /**
     * Function:判断扫描结果是否连接上<br>
     * @param result
     * @return<br>
     */
    public boolean isConnect(ScanResult result) {
        if (result == null) {
            return false;
        }

        mWifiInfo = mWifiManager.getConnectionInfo();
        String g2 = "\"" + result.SSID + "\"";
        if (mWifiInfo.getSSID() != null && mWifiInfo.getSSID().endsWith(g2)) {
            return true;
        }
        return false;
    }

    /**
     * Function: 将int类型的IP转换成字符串形式的IP<br>
     * @param ip
     * @return<br>
     */
    public String ipIntToString(int ip) {
        try {
            byte[] bytes = new byte[4];
            bytes[0] = (byte) (0xff & ip);
            bytes[1] = (byte) ((0xff00 & ip) >> 8);
            bytes[2] = (byte) ((0xff0000 & ip) >> 16);
            bytes[3] = (byte) ((0xff000000 & ip) >> 24);
            return Inet4Address.getByAddress(bytes).getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }

    public int getConnNetId() {
        // result.SSID;
        mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo.getNetworkId();
    }

    /**
     * Function:信号强度转换为字符串
     *
     * @author Xiho
     * @param level
     */
    public static String singlLevToStr(int level) {

        String resuString = "无信号";

        if (Math.abs(level) > 100) {
        } else if (Math.abs(level) > 80) {
            resuString = "弱";
        } else if (Math.abs(level) > 70) {
            resuString = "强";
        } else if (Math.abs(level) > 60) {
            resuString = "强";
        } else if (Math.abs(level) > 50) {
            resuString = "较强";
        } else {
            resuString = "极强";
        }
        return resuString;
    }

    /**
     * 添加到网络
     *
     * @author Xiho
     * @param wcg
     */

    public boolean addNetwork(WifiConfiguration wcg) { // 添加一个网络配置并连接
        if (wcg == null) {
            return false;
        }
//        int wcgID = mWifiManager.addNetwork(wcg);
//        Log.e("qqq,","wcgID="+wcgID);
//        boolean b = mWifiManager.enableNetwork(wcgID, true);
//        mWifiManager.saveConfiguration();
//        System.out.println(b);
        boolean b=false;
        if(b){
            int wcgID = mWifiManager.addNetwork(wcg);
            Log.e("addNetwork","wcgID="+wcgID);
//        mWifiManager.disableNetwork(wcgID);
            b = mWifiManager.enableNetwork(wcgID, true);
            mWifiManager.saveConfiguration();

//	        System.out.println("addNetwork addNetwork--" + wcgID);
//	        System.out.println("addNetwork enableNetwork--" + b);
//	        System.out.println("addNetwork addNetwork2 " + wcg);
//        Log.e("wcg","wcg="+wcg);
        }else{
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
        }



        return b;
    }

    public boolean connectSpecificAP(ScanResult scan) {
        Log.e("qqq","scan="+scan);
        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
        boolean networkInSupplicant = false;
        boolean connectResult = false;
        // 重新连接指定AP
//        mWifiManager.disconnect();
        for (WifiConfiguration w : list) {
            // 将指定AP 名字转化
            // String str = convertToQuotedString(info.ssid);
            if (w.SSID != null ) {
//
                if(w.SSID.equals("\"" + scan.SSID + "\"")||w.SSID.equals( scan.SSID )){
//                    connectResult = addNetwork(w);
                    Log.e("qqq","w="+w);
                    Log.e("qqq","w.ssid="+w.SSID+"  w.networkId="+w.networkId);
                    connectResult = mWifiManager.enableNetwork(w.networkId, true);
                    networkInSupplicant = true;
                    break;
                }
            }
        }
        if (!networkInSupplicant) {
            WifiConfiguration config = CreateWifiInfo(scan, "");
            connectResult = addNetwork(config);
        }
        Log.e("addNetwork","networkInSupplicant="+networkInSupplicant);
        Log.e("addNetwork","connectResult="+connectResult);
        return connectResult;
    }
    // 然后是一个实际应用方法，只验证过没有密码的情况：
    public WifiConfiguration CreateWifiInfo(ScanResult scan, String Password) {
        WifiConfiguration config = new WifiConfiguration();
        config.hiddenSSID = false;
        config.status = WifiConfiguration.Status.ENABLED;

        if (scan.capabilities.contains("WEP")) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);

            config.SSID = "\"" + scan.SSID + "\"";

            config.wepTxKeyIndex = 0;
            config.wepKeys[0] = Password;
            // config.preSharedKey = "\"" + SHARED_KEY + "\"";
        } else if (scan.capabilities.contains("PSK")) {
            //
            config.SSID = "\"" + scan.SSID + "\"";
            config.preSharedKey = "\"" + Password + "\"";
        } else if (scan.capabilities.contains("EAP")) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.SSID = "\"" + scan.SSID + "\"";
            config.preSharedKey = "\"" + Password + "\"";
        } else {
            Log.e("qqq","其他"+"scan.SSID="+scan.SSID);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.SSID = "\"" + scan.SSID + "\"";
            config.preSharedKey = null;
            //
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P){
                Log.e("qqq","sdk=Build.VERSION_CODES.P");
//                if(isMIUI()){
//                    Log.e("qqq","小米 android9.0+");
//                }else{
//                    config.wepKeys[0] = "\"" + "\"";
//                    config.wepTxKeyIndex = 0;
//                }
            }else{
                config.wepKeys[0] = "\"" + "\"";
                config.wepTxKeyIndex = 0;
            }

        }
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P){
//            Log.e("qqq","sdk=Build.VERSION_CODES.P");
//            config.SSID = scan.SSID ;
//            if(isMIUI()){
//                Log.e("qqq","小米 android9.0+");
//                config.SSID = "\"" + scan.SSID + "\"";
////                config.SSID =  scan.SSID ;
//            }
//        }else{
//            config.SSID = "\"" + scan.SSID + "\"";
//        }
        Log.e("qqq"," config.SSID ="+ config.SSID );
        Log.e("qqq"," config ="+ config );


        return config;
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
        manager.saveConfiguration();
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
    /**
     * 获取当前手机所连接的wifi信息
     */
    public WifiInfo getCurrentWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }


    public void EnableNetwork(int networkId) {
        if (networkId != 0) {
            boolean EnableNetworkzt=mWifiManager.enableNetwork(networkId, true);
            Log.e("qqq","EnableNetwork="+EnableNetworkzt);
        }
    }


    public static boolean isMIUI() {
        String manufacturer = Build.MANUFACTURER;
        //这个字符串可以自己定义,例如判断华为就填写huawei,魅族就填写meizu
        if ("xiaomi".equalsIgnoreCase(manufacturer)) {
            return true;
        }
        return false;
    }




    /**
     * 通过反射出不同版本的connect方法来连接Wifi
     *
     * @author jiangping.li
     * @param netId
     * @return
     * @since MT 1.0
     *
     */
    private Method connectWifiByReflectMethod(int netId) {
        Method connectMethod = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // 反射方法： connect(int, listener) , 4.2 <= phone's android version
            for (Method methodSub : mWifiManager.getClass()
                    .getDeclaredMethods()) {
                if ("connect".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0) {
                        if ("int".equalsIgnoreCase(types[0].getName())) {
                            connectMethod = methodSub;
                        }
                    }
                }
            }
            if (connectMethod != null) {
                try {
                    connectMethod.invoke(mWifiManager, netId, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            // 反射方法: connect(Channel c, int networkId, ActionListener listener)
            // 暂时不处理4.1的情况 , 4.1 == phone's android version
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // 反射方法：connectNetwork(int networkId) ,
            // 4.0 <= phone's android version < 4.1
            for (Method methodSub : mWifiManager.getClass()
                    .getDeclaredMethods()) {
                if ("connectNetwork".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0) {
                        if ("int".equalsIgnoreCase(types[0].getName())) {
                            connectMethod = methodSub;
                        }
                    }
                }
            }
            if (connectMethod != null) {
                try {
                    connectMethod.invoke(mWifiManager, netId);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } else {
            // < android 4.0
            return null;
        }
        return connectMethod;
    }

}
