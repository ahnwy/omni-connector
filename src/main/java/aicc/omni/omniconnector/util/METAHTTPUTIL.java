package aicc.omni.omniconnector.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

@Log4j2
public class METAHTTPUTIL {

    @Value("${meta.fbAccessToken}")
    static String fbAccessToken;
    @Value("${meta.IgAccessToken}")
    static String IgAccessToken;

    public static void sendApi(String json, String channelSeq) {
        HttpsURLConnection conn;


        log.info("▶▶▶ HTTPS_CONNECTION Created");

        try {
            // Json header
            log.info("▶▶▶ channelSeq : {}", channelSeq);
//            String accessToken = AccessTokenSelector(channelSeq);
            String accessToken = "EAAGSZCPZAAhNUBAHbLuLcyun9kbIbGD9KfdNFZAlVFWJKnODLQx2oxZBJi04SnobCiQPzYD7t4aCCJ6mJ9MwJ4bZCLoWZB2SXwlWY1255ybapdlUPrMCYCTiPD7KQWSeIUC8SfxTYbIuOCK5lB0cz0vQE5gqgnrZCZB6YbhoSq0Nlq3CkZAebqYjheF0EE25kgYAZD";
            URL url = new URL("https://graph.facebook.com/v14.0/me/messages?access_token=" + accessToken);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            log.info("▶▶▶ HTTPS_CONNECTION wrote : {}", json.toString());
            bw.write(json.toString());
            bw.flush();
            bw.close();

            //send and response
            int responseCode = conn.getResponseCode();
            log.info("▶▶▶ HTTPS_CONNECTION responseCode : {}", responseCode);

            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    log.info("▶▶▶ HTTPS_CONNECTION_STREAM_SENT SUCCESS : " + sb);
                }
                br.close();
            }else {
                log.info("▶▶▶ HTTPS_CONNECTION_STREAM_SENT_FAILED_ERROR " );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
