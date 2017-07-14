package com.xiaomi.zhibo.crawler.util;

import com.xiaomi.zhibo.crawler.constant.Constant;
import com.xiaomi.zhibo.crawler.cookie.SinaLogonDog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author : zhongxiankui
 * @time : 2016-08,18 16:26:下午4:26
 * @mail : zhongxiankui@xiaomi.com
 * @project MiTest
 */
public class HttpClient {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    
    private static final String USER_AGENT =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36";
    private static final String USER_AGENT1 =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/43.0.2357.81 Chrome/43.0.2357.81 Safari/537.36)";
    /*private static final String USER_AGENT2 =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/43.0.2357.81 Chrome/43.0.2357.81 Safari/537.36";*/
    private static final int HTTP_CONNECT_TIMEOUT = 30 * 1000;
    private static final int HTTP_READ_DATA_TIMEOUT = 60 * 1000;
    
    public static String get(String url) {

        HttpURLConnection con = null;
        String result = null;
        try{
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            // optional default is GET
            
            //add request header
            con.setRequestMethod("GET");
            con.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
            con.setReadTimeout(HTTP_READ_DATA_TIMEOUT);

            con.setRequestProperty("User-Agent", USER_AGENT);

            con.connect();
            //con.setDoInput(true);
            logger.debug("timeout connect:{}, read:{}", con.getConnectTimeout(), con.getReadTimeout());


            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            if(con.getResponseCode() != HttpURLConnection.HTTP_OK){
                logger.error("网络错误异常");
                return null;
            }
            StringBuffer response = new StringBuffer();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            result = response.toString();
            return result;
        }catch (Exception e){
            logger.error("connect timeout");
            result = Constant.POSTBAR_TIMEOUT;
        }finally{
            if(null != con){
                con.disconnect();
            }
        }
        logger.debug("result:{}", result);
        return result;
    }


    public static String getWeibo(String url) {

        String result = "";
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT1);
            con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            con.setRequestProperty("Cache-Control", "max-age=0");
            SinaLogonDog sinaLogonDog = new SinaLogonDog();
            sinaLogonDog.init();
            String  cookie = sinaLogonDog.logonAndValidate("15210872526", "hhy123456");
            logger.debug(cookie);
            if(null == cookie || cookie.isEmpty()){
                return null;
            }
            con.setRequestProperty("Cookie", cookie);
            con.setRequestProperty("Connection", "keep-alive");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String result1 = response.toString();
            result = new String(result1.toString().getBytes("UTF-8"), "UTF-8");

        } catch (Exception e) {
            logger.error("e:{}",e);
        }

        return result;
    }

    public String post(String url){
        
        try{
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
    
            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
            con.setReadTimeout(HTTP_READ_DATA_TIMEOUT);
            logger.debug("timeout connect:{}, read:{}", con.getConnectTimeout(), con.getReadTimeout());
    
//            String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
    
            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
    
            int responseCode = con.getResponseCode();
            logger.info("Sending 'POST' request to URL:{}" + url);
//            logger.info("Post parameters:{}" + urlParameters);
            logger.info("Response Code:{}" + responseCode);
    
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
    
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return response.toString();
        }catch (Exception e){
            logger.error("e:{}", e);
        }
        
        return null;
    }
}
