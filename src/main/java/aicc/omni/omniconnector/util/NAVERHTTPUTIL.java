package aicc.omni.omniconnector.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Log4j2
public class NAVERHTTPUTIL {

    @Value("${naver.authorization}")
    public static String authorization;

    public static void sendApi(String jsonOutput) {

        HttpURLConnection conn;

        try {
            URL url = new URL("https://gw.talk.naver.com/chatbot/v1/event");
            conn = (HttpURLConnection) url.openConnection();

            // type의 경우 POST, GET, PUT, DELETE 가능
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Authorization", authorization);

            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

            bw.write(jsonOutput);
            bw.flush();
            bw.close();

            // 보내고 결과값 받기
            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    log.info("▶▶▶ NAVER_HTTPS_CONNECTION_STREAM_SENT SUCCESS : " + sb);
                }
                br.close();
            }else {
                log.info("▶▶▶ HTTPS_CONNECTION_STREAM_SENT FAILED " );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
