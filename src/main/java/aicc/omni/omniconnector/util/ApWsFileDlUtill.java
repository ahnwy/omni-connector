package aicc.omni.omniconnector.util;//package com.chatmw.common.util;
//
//import lombok.extern.log4j.Log4j2;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.net.ssl.*;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.net.URL;
//import java.security.KeyManagementException;
//import java.security.NoSuchAlgorithmException;
//import java.security.cert.X509Certificate;
//
//
//@Log4j2
//@Controller
//public class ApWsFileDlUtill {
//
//    @RequestMapping ("/ttest")
//    public static void apWsFileDl() {
//        FileInputStream fis = null;
//        FileOutputStream fos = null;
////        InputStream is = null;
//
////        disableSslVerification();
//
//        try {
//
////            URL url = new URL("https://test.hiqri.ai/upload/TS/2022/06/crycry.jpg");
////            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
////
////            log.info(httpCon.toString());
//
//
//            InputStream is = new URL("https://test.hiqri.ai/upload/TS/2022/06/crycry.jpg").openStream();
////            fis = new FileInputStream("https://test.hiqri.ai/upload/TS/2022/06/crycry.jpg");
//            log.info(is.toString());
//            fos = new FileOutputStream("C:\\Users\\user\\Desktop\\test.jpg");
//
////            fis = new FileInputStream("https://222.108.82.96:443/data/upload/TS/2022/06/crycry.jpg");
////            fos = new FileOutputStream("C:\\crycry.jpg");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }public static void disableSslVerification(){
//        try
//        {
//            // Create a trust manager that does not validate certificate chains
//            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//                public void checkClientTrusted(X509Certificate[] certs, String authType){
//                }
//                public void checkServerTrusted(X509Certificate[] certs, String authType){
//                }
//            }
//            };
//
//            // Install the all-trusting trust manager
//            SSLContext sc = SSLContext.getInstance("SSL");
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//
//            // Create all-trusting host name verifier
//            HostnameVerifier allHostsValid = new HostnameVerifier() {
//                public boolean verify(String hostname, SSLSession session){
//                    return true;
//                }
//            };
//
//            // Install the all-trusting host verifier
//            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
