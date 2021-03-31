package com.jizhi.geektime;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.cert.X509Certificate;

/**
 * 2021/3/26
 * jizhi7
 **/
public class TestA {

    private static class TrustAllManager
            implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkServerTrusted(X509Certificate[] certs,
                                       String authType) {
        }

        public void checkClientTrusted(X509Certificate[] certs,
                                       String authType) {
        }
    }

    public static void main(String[] args) throws Throwable {

        URI uri = new URI("https://www.jenkins.io");

        // 跳过证书认证
//        TrustManager[] trustAllCerts = new TrustManager[1];
//        trustAllCerts[0] = new TrustAllManager();
//        SSLContext sc = SSLContext.getInstance("SSL");
//        sc.init(null, trustAllCerts, null);
//        HttpsURLConnection.setDefaultSSLSocketFactory(
//                sc.getSocketFactory());
//
//        HttpsURLConnection.setDefaultHostnameVerifier
//                (
//                        (urlHostName, session) -> true
//                );
//        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
//
//        connection.connect();
//        int responseCode = connection.getResponseCode();
//        System.out.println(responseCode);
//        connection.disconnect();

        URL serverUrl = new URL("https://updates.jenkins.io");
        HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
       // conn.setRequestMethod("GET");
      //  conn.setRequestProperty("Content-type", "application/json");
        //必须设置false，否则会自动redirect到重定向后的地址
        conn.setInstanceFollowRedirects(false);
        conn.connect();
        System.out.println(conn.getResponseCode());

    }

    @Test
    public void testa() {
        Assert.assertTrue(true);
    }

}
