package aicc.omni.omniconnector.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.Objects;

@Log4j2
public class MetaHttpUtil {

    @Value("${meta.fbAccessToken}")
    static String fbAccessToken;
    @Value("${meta.IgAccessToken}")
    static String IgAccessToken;

    public static void sendMsg(String json, String channelSeq) {
        HttpsURLConnection conn;


        log.info("▶▶▶ HTTPS_CONNECTION Created");

        try {
            // Json header
            log.info("▶▶▶ channelSeq : {}", channelSeq);
            String accessToken = AccessTokenSelector(channelSeq);
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
//                HttpFailureResolver.FailedMessageController(json);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void sendMedia(String json, String channelSeq) {

        HttpsURLConnection conn;

        log.info("▶▶▶ HTTPS_CONNECTION Created");

        try {
            // Json header
            String accessToken = AccessTokenSelector(channelSeq);
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
//                HttpFailureResolver.FailedMessageController(json);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public static void sendMediaOld(ApWsDto apWsDto) throws UnsupportedEncodingException {
//
//        String accessToken = AccessTokenSelector(apWsDto.getChannelSeq());
//
//        String recipientId = apWsDto.getCustomer();
//        String contentType = apWsDto.getMsgContentType();
//
//        File uploadFile = new File(apWsDto.getFilePath()+apWsDto.getFileName());
//
//        CloseableHttpClient client = HttpClients.createDefault();
//
//        try {
//            String url = "https://graph.facebook.com/v13.0/me/messages?access_token=" + accessToken;
//
//            HttpPost post = new HttpPost(url);
//            log.info("▶▶▶ url : " + url);
//            String message = "{\"attachment\": {\"type\": \""
//                    + contentType + "\",\"payload\": {\"is_reusable\": true}}}";
//            String recipient = "{\"id\":\"" + recipientId + "\"}";
//
//            log.info(message);
//            log.info(recipient);
//
//            String mimeType = Files.probeContentType(uploadFile.toPath());
//
//            log.info("▶▶▶ mimeType : " + mimeType);
//
//            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//            builder.addTextBody("message", message);
//            builder.addTextBody("recipient",recipient);
//            builder.addBinaryBody("filedata", uploadFile, ContentType.parse(mimeType), uploadFile.getName());
//
//            HttpEntity reqEntity = builder.build();
//            post.setEntity(reqEntity);
//
//
//            CloseableHttpResponse response = client.execute(post);
//
//            try {
//                int responseCode = response.getStatusLine().getStatusCode();
//                InputStream responseBody = response.getEntity().getContent();
//                // 전송 후 서버측 리턴 결과 출력
//                httpSendAndResponse(responseCode, responseBody);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } catch (IOException e) {
//        }
//    }

    private static void httpSendAndResponse(int responseCode, InputStream responseBody) throws IOException {
        log.info("▶▶▶ response CODE : " + responseCode);
        log.info("▶▶▶ response BODY : " + responseBody);

        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(responseBody));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                log.info("▶▶▶ HTTPS_CONNECTION_STREAM_SENT SUCCESS : " + sb);
            }
            br.close();
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(responseBody));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                log.info("▶▶▶ HTTPS_CONNECTION_STREAM_SENT_FAILED_ERROR : " + sb);

            }

        }
    }


    public static String AccessTokenSelector(String channelSeq) {
        String accessToken;
        if (Objects.equals(channelSeq, "3")) {
            accessToken = fbAccessToken;
        } else {
            accessToken = IgAccessToken;
        }
        return accessToken;
    }

    private static void copyInputStreamToFile(InputStream inputStream, File file) throws FileNotFoundException {

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
