package aicc.omni.omniconnector.service.kakao;

import aicc.omni.omniconnector.handler.WebsocketClientHandler;
import aicc.omni.omniconnector.model.kakao.KakaoWhMsgDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static aicc.omni.omniconnector.handler.WebsocketClientHandler.reservedMsgMap;

@Log4j2
@Service
public class KakaoSendApMsgService {
    @Autowired
    private KakaoApMsgBuilder kakaoApMsgBuilder;

    // 최초 인입인지 체크
    private String checkInit(KakaoWhMsgDto msgDto) throws Exception {
        log.info("whUserMap >>> "+WebsocketClientHandler.whUserMap);
        log.info("channelMap >>> "+WebsocketClientHandler.channelMap);
        if (!WebsocketClientHandler.whUserMap.containsKey(msgDto.getUser_key())) {
            log.info("최초네....");
            WebsocketClientHandler.channelMap.put(msgDto.getUser_key(), msgDto.getChannel());
            log.info("channelMap >>> "+WebsocketClientHandler.channelMap);
            return "Y";
        } else {
            msgDto.setMsgSeq(WebsocketClientHandler.whUserMap.get(msgDto.getUser_key()));
            return "N";
        }
    }

    //AP로 보내기전 초기 인입체크 및 메세지 빌드 로직
    public String sendToAp(KakaoWhMsgDto msgDto) throws Exception {
        log.info("KakaoWhMsgDto >>> " + msgDto);
        if(msgDto.getPath().equals("message")){
            String initYn = checkInit(msgDto);
            msgDto.setFormattedDate(timeRecordGenerator());
            msgDto.setMsgWrtId("1");
            if(msgDto.getType().equals("text")){
                if(initYn.equals("Y")){
                    reservedMsgMap.put(msgDto.getUser_key(), kakaoApMsgBuilder.apWebSocketTextMsg(msgDto));
                    return kakaoApMsgBuilder.apWebSocketInitMsg(msgDto);
                } else {
                    return kakaoApMsgBuilder.apWebSocketTextMsg(msgDto);
                }
            } else if(msgDto.getType().equals("photo")){
                fileNameExtractor(msgDto);
                if(initYn.equals("Y")){
                    reservedMsgMap.put(msgDto.getUser_key(), kakaoApMsgBuilder.apWebSocketImageMsg(msgDto));
                    return kakaoApMsgBuilder.apWebSocketInitMsg(msgDto);
                } else {
                    return kakaoApMsgBuilder.apWebSocketImageMsg(msgDto);
                }
            } else if(msgDto.getType().equals("video")){

            } else if(msgDto.getType().equals("audio")){

            } else {

            }
        }
        return "";
    }

    public String timeRecordGenerator() {
        Date date = new java.util.Date();
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        return sdf.format(date);
    }

    public void fileNameExtractor(KakaoWhMsgDto whMsgDto) { //파일 이름과 사이즈 한번에 파싱
        String rawFileUrl = whMsgDto.getContent();
        String fileUrl = rawFileUrl.substring(8, rawFileUrl.length()-2); //file Url 파싱

        String[] fileNameArray = fileUrl.split("/");
        String fileName = fileNameArray[6];

        //http 헤더에서 파일 용량을 가져온다.
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(fileUrl).openConnection();
            conn.setInstanceFollowRedirects(false);
            int fileSize = Integer.parseInt(conn.getHeaderField("Content-Length"));
            whMsgDto.setFileName(fileName);
            whMsgDto.setFileSize(fileSize);
            whMsgDto.setFileUrl(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
