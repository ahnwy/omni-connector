package aicc.omni.omniconnector.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;

@Log4j2
@Component
public class FILENAMEUTIL {

    public static String getFileNm(String browser, String fileNm) {
        String reFileNm = null;
        try {
            if (browser.equals("MSIE") || browser.equals("Trident") || browser.equals("Edge")) {
                reFileNm = URLEncoder.encode(fileNm, "UTF-8").replaceAll("\\+", "%20");
            } else {
                if (browser.equals("Chrome")) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < fileNm.length(); i++) {
                        char c = fileNm.charAt(i);
                        if (c > '~') {
                            sb.append(URLEncoder.encode(Character.toString(c), "UTF-8"));
                        } else {
                            sb.append(c);
                        }
                    }
                    reFileNm = sb.toString();
                } else {
                    reFileNm = new String(fileNm.getBytes("UTF-8"), "ISO-8859-1");
                    log.info("fileNm: " + fileNm);
                    log.info("reFileNm: " + reFileNm);
                }
                if (browser.equals("Safari") || browser.equals("Firefox")) reFileNm = URLDecoder.decode(reFileNm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//
//        try {
//            if (request.getHeader("User-Agent").contains("Trident")) {
//                reFileNm = URLEncoder.encode(fileName, "utf-8").replaceAll("\\+", "%20");
//            } else {
//                reFileNm = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return reFileNm;
    }
}