package com.zs.easy.mqtt;

import android.content.Context;

import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SslUtil {
    public static SSLSocketFactory createSocketFactory(Context context) {
        SSLContext sslContext;
        try {
            KeyStore ks = KeyStore.getInstance("BKS");
//            ks.load(context.getResources().openRawResource(R.raw.peer),
//                    "123456".toCharArray());                           //该字符串应随机生成，保证每次session唯一；
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            //    kmf.init(ks, "passw0rd".toCharArray());
            kmf.init(ks, "123456".toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance("X509");
            tmf.init(ks);
            TrustManager[] tm = tmf.getTrustManagers();
            sslContext = SSLContext.getInstance("TLS");

            sslContext.init(kmf.getKeyManagers(), tm, null);
            // SocketFactory factory= SSLSocketFactory.getDefault();

            // Socket socket =factory.createSocket("localhost", 10000);
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            return  ssf;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }
}
