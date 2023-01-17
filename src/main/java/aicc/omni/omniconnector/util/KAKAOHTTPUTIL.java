package aicc.omni.omniconnector.util;

import aicc.omni.omniconnector.model.ap.ApWsDto;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


@Log4j2
public class KakaoHttpUtil {

    @Value("${kakao.apiKey}")
    static String apiKey;
    @Value("${kakao.plusFriendId}")
    static String plusFriendId;
    static String testServerUrl = "https://italk-vir.ibizplus.co.kr:8443/";
    static String serviceServerUrl = "https://italk-api.ibizplus.co.kr:8443/";

    public static void sendMsg(String json, String path) {

        HttpURLConnection conn;

        log.info("▶▶▶ HTTPS_CONNECTION Created");
        try {
            URL url = new URL(testServerUrl+"rs/v1/"+plusFriendId+path);
            log.info(url);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("API-KEY", apiKey);
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            log.info("▶▶▶ HTTPS_CONNECTION wrote : {}", json.toString());
            bw.write(json.toString());
            bw.flush();
            bw.close();
            log.info("▶▶▶ HTTPS_CONNECTION SENT");

            try {
                int responseCode =conn.getResponseCode();
                InputStream responseBody = conn.getInputStream();
                // 전송 후 서버측 리턴 결과 출력
                httpSendAndResponse(responseCode, responseBody);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
        }
    }

    public static void uploadImg(ApWsDto apWsDto) throws IOException {


//        String fileServerPath = apWsDto.getFilePath() + apWsDto.getFileName();
//
//        log.info("▶▶▶ fileServerPath : {}", fileServerPath);
//        File uploadFile = new File(fileServerPath);

        try {
            String fileServerUrl = "https://hiqri.ai";
            String fileServerPath = apWsDto.getFilePath().substring(5);
            String fileName = apWsDto.getFileName();
//            String nameOnly = fileName.substring(0, fileName.lastIndexOf("."));
            String nameSuffix = fileName.substring(fileName.lastIndexOf("."));

            String fileNameEncoded = URLEncoder.encode(fileName, "UTF-8");
            String fileUrl =  fileServerUrl + fileServerPath + fileNameEncoded;

            log.info("▶▶▶ fileUrl : {}", fileUrl);
//            log.info("▶▶▶ nameOnly : {}", nameOnly);
            log.info("▶▶▶ nameSuffix : {}", nameSuffix);

            InputStream is = new URL(fileUrl).openStream();

            File tempFile = File.createTempFile(String.valueOf(is.hashCode()), nameSuffix);
            tempFile.deleteOnExit();

            log.info("▶▶▶ tempFile : {}", tempFile.getName());
//            log.info("▶▶▶ tempFile : {}", tempFile.getTotalSpace());
            copyInputStreamToFile(is, tempFile);
//            Files.copy(is, tempFile.toPath());

            log.info("▶▶▶ tempFile : {}", tempFile.getName());
            log.info("▶▶▶ tempFile : {}", tempFile.getPath());

//            String serial = "fHKhfzdvF4qP_" + getSimpleDate();
            String serial = apWsDto.getUploadSerial();

            // 업로드 형태에 따른 분기
            String postPath = null;
            String imgOrFile = null;
            switch (apWsDto.getMsgContentType()) {
                case "image":
                    postPath = "/image_upload";
                    imgOrFile = "image";
                    break;
                case "file":
                    postPath = "/file_upload";
                    imgOrFile = "file";
                    break;
                default:
                    break;
            }
            CloseableHttpClient client = HttpClients.createDefault();

            String url = testServerUrl + "rs/v1/" + plusFriendId + postPath;
            HttpPost post = new HttpPost(url);
            log.info("▶▶▶ url : " + url);

//            FileBody file = new FileBody(uploadFile);
            FileBody file = new FileBody(tempFile);
            StringBody characterEncoding = new StringBody("UTF-8", ContentType.TEXT_PLAIN);
            StringBody serial_number = new StringBody(serial, ContentType.TEXT_PLAIN);
            StringBody ref_key = new StringBody("ktcs", ContentType.TEXT_PLAIN);
            StringBody file_type = new StringBody("file", ContentType.TEXT_PLAIN);
//
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart(imgOrFile, file)
                    .addPart("serial_number", serial_number)
                    .addPart("ref_key", ref_key)
                    .addPart("file_type", file_type)
//                    .addPart("characterEncoding", characterEncoding)
                    .build();
            log.info(reqEntity.toString());

            post.setHeader("API-KEY", apiKey);
//            post.setHeader("Content-type", "multipart/form-data; boundary=" + reqEntity.getContentType().getValue()+"; charset=utf-8");
            post.setEntity(reqEntity);

            log.info("▶▶▶ reqEntity_getRequestLine: " + post.getRequestLine());
            log.info("▶▶▶ reqEntity_getAllHeaders : " + Arrays.toString(post.getAllHeaders()));

            CloseableHttpResponse response = client.execute(post);

            try {
                int responseCode = response.getStatusLine().getStatusCode();
                InputStream responseBody = response.getEntity().getContent();
                // 전송 후 서버측 리턴 결과 출력
                httpSendAndResponse(responseCode, responseBody);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            tempFile.delete();
            is.close();

        } catch (IOException e) {
        }
    }

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
            log.info("▶▶▶ HTTPS_CONNECTION_STREAM_SENT_FAILED_ERROR ");
        }
    }

    public static String getSimpleDate() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(date);

        return formattedDate;
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