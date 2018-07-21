package com.example.demo.Utils;

import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;



public class HttpHelper {

    //添加代码块说明用于跳过网站安全证书认证
    //添加代码块1
    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }


    public static String ByPost(String requestURL, JSONObject jsonobj, String token) {
        String msg = "";
        try {

            //添加代码块2  该部分必须在获取connection前调用
            trustAllHttpsCertificates();
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(hv);


            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (token != null) {
                connection.setRequestProperty("Grpc-Metadata-Authorization", token); //向header里添加信息
            }
            connection.setRequestMethod("POST");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            OutputStream out = connection.getOutputStream();
            out.write(jsonobj.toString().getBytes());
            out.flush();
            out.close();
            connection.connect();

            if (connection.getResponseCode() != 200) {
                return null;
            }

            InputStream in = connection.getInputStream();
            ByteArrayOutputStream message = new ByteArrayOutputStream();
            int len = 0;
            byte buffer[] = new byte[1024];
            while((len = in.read(buffer)) != -1) {
                message.write(buffer, 0, len);
            }
            in.close();
            message.close();
            msg = new String(message.toByteArray());
//            System.out.println(msg);
            return msg;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //添加代码块3
    static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }


    public static String ByGet(String requestURL, String token) {
        String msg = "";
        try {

            //添加代码块2  该部分必须在获取connection前调用
            trustAllHttpsCertificates();
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(hv);


            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (token != null) {
                connection.setRequestProperty("Grpc-Metadata-Authorization", token); //向header里添加信息
            }

            if (connection.getResponseCode() != 200) {
                return null;
            }
            InputStream in = connection.getInputStream();
            ByteArrayOutputStream message = new ByteArrayOutputStream();
            int len = 0;
            byte buffer[] = new byte[1024];
            while((len = in.read(buffer)) != -1) {
                message.write(buffer, 0, len);
            }
            in.close();
            message.close();
            msg = new String(message.toByteArray());
            return msg;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static boolean ByDelete(String requestURL, String token) {
        try {

            //添加代码块2  该部分必须在获取connection前调用
            trustAllHttpsCertificates();
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(hv);


            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (token != null) {
                connection.setRequestProperty("Grpc-Metadata-Authorization", token); //向header里添加信息
            }
            connection.setRequestMethod("DELETE");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.connect();

            if (connection.getResponseCode() != 200) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

