package aicc.omni.omniconnector.service.naver;

import aicc.omni.omniconnector.model.ap.ApWsDto;
import aicc.omni.omniconnector.model.naver.NaverWhMsgDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static aicc.omni.omniconnector.handler.WebsocketClientHandler.channelMap;
import static aicc.omni.omniconnector.handler.WebsocketClientHandler.whUserMap;

@Log4j2
@Service
public class NaverApMsgBuilder {
    /*
        네이버 포맷을 상담AP로 보내기 위한 데이터 빌더
     */

    public String apWebSocketInitMsg(NaverWhMsgDto naverWhMsgDto) throws Exception {

        Map<String, String> msgMap = new HashMap<>(10);

        msgMap.put("msgSeq", "unknown");
        msgMap.put("platformID", naverWhMsgDto.getUser());
        msgMap.put("msg", "VISIT"); // 고정
        msgMap.put("openFlag", "newEvent");
        msgMap.put("userName", naverWhMsgDto.getUser());
        msgMap.put("userPhone", "1"); //고정
        msgMap.put("userEmail", "1"); //고정
        msgMap.put("schema", "ap"); //고정
        msgMap.put("corpCode", "CS"); //고정
        msgMap.put("msgId", UUID.randomUUID().toString().replace("-", ""));
        msgMap.put("channelSeq", naverWhMsgDto.getChannel());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }

    public String apWebSocketTextMsg(NaverWhMsgDto naverWhMsgDto) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String currentTime = sdf.format(System.currentTimeMillis());

        Map<String, String> msgMap = new HashMap<>(9);
        msgMap.put("mode", "total"); // 고정
        msgMap.put("msgSeq", whUserMap.get(naverWhMsgDto.getUser()));
        msgMap.put("msg", naverWhMsgDto.getTextContent().getText());
        msgMap.put("msgContentType", "text"); // 고정
        msgMap.put("msgWrtTime", currentTime);
        msgMap.put("msgWrtId", "1");
        msgMap.put("schema", "ap"); //고정
        msgMap.put("corpCode", "CS"); //고정
        msgMap.put("msgId", naverWhMsgDto.getMessageId());
        msgMap.put("channelSeq", naverWhMsgDto.getChannel());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }

    public String apWebSocketImageMsg(NaverWhMsgDto naverWhMsgDto) throws Exception {

        String imageUrl = naverWhMsgDto.getImageContent().getImageUrl();

        // fileVolume(byte)
        HttpURLConnection conn = null;
        conn = (HttpURLConnection) new URL(imageUrl).openConnection();
        conn.setInstanceFollowRedirects(false);
        String fileBytes = conn.getHeaderField("Content-Length");

        if (Long.parseLong(fileBytes) < 10485760) {
            Map<String, Object> msgMap = new HashMap<>(13);
            msgMap.put("mode", "total"); // 고정
            msgMap.put("msgSeq", whUserMap.get(naverWhMsgDto.getUser()));
            msgMap.put("msg", naverWhMsgDto.getImageContent().getImageUrl());
            msgMap.put("msgContentType", "image"); // 고정
            msgMap.put("filePath", naverWhMsgDto.getImageContent().getImageUrl());
            msgMap.put("fileName", URLDecoder.decode(imageUrl.substring(imageUrl.lastIndexOf('/') + 1, imageUrl.length()), "EUC-KR"));
            msgMap.put("fileSize", Long.parseLong(fileBytes));
            msgMap.put("msgWrtTime", naverWhMsgDto.getCurrentTime());
            msgMap.put("channelSeq", naverWhMsgDto.getChannel());
            msgMap.put("msgWrtId", "1");
            msgMap.put("schema", "ap"); //고정
            msgMap.put("corpCode", "CS"); //고정
            msgMap.put("msgId", naverWhMsgDto.getMessageId());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
        } else {
            return "";
        }
    }

    public String apWebSocketEndMsg(NaverWhMsgDto naverWhMsgDto) throws Exception {

        Map<String, String> msgMap = new HashMap<>(14);

        msgMap.put("mode", "total");
        msgMap.put("msgSeq", whUserMap.get(naverWhMsgDto.getUser()));
        msgMap.put("msg", "");
        msgMap.put("msgContentType", "text"); // 고정
        msgMap.put("channelSeq", channelMap.get(naverWhMsgDto.getUser()));
        msgMap.put("customer", naverWhMsgDto.getUser());
        msgMap.put("status", "종료");
        msgMap.put("endDate", naverWhMsgDto.getCurrentTime()); //고정
        msgMap.put("msgWrtTime", naverWhMsgDto.getCurrentTime()); //고정
        msgMap.put("flag", "E"); //고정
        msgMap.put("msgWrtId", "1"); // 메시지작성자 (1(고객), 2(상담사), 3(봇))
        msgMap.put("schema", "ap"); //고정
        msgMap.put("corpCode", "CS"); //고정
        msgMap.put("msgId", "9999");

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }

    // 최초 인입 시 추가 인증 메소드
    public static String socketMsgParserApAddAuth(ApWsDto apWsDto) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        JsonObject obj = new JsonObject();

        // client information setting
        obj.addProperty("schema", "ap");
        obj.addProperty("corpCode", "CS");
        obj.addProperty("msgId", apWsDto.getMsgId());
        // 고객화면 인증 연결
        obj.addProperty("mode", "insert");
        // msgSeq : 채팅방번호 (서버에서 할당)
        obj.addProperty("msgSeq", apWsDto.getMsgSeq());
        // 인입채널 | 1.kakao, 2.naver, 3.facebook, 4.instagram, 5.webchat, 6.email
        obj.addProperty("channelSeq", apWsDto.getChannelSeq());
        // 채팅방모드
        obj.addProperty("status", "대기");
        // 최초 접속 시각
        obj.addProperty("idleDate", apWsDto.getCurrentTime());
        // 상담요청시간 (상담사가 배정된 시간-ap배정)
        obj.addProperty("requestDate", "-");
        // 상담시작시간 (상담사가 시작클릭시간-ap배정)
        obj.addProperty("startDate", "-");
        // 상담종료시간 (상담사혹은 고객이 상담종료를 누른시간)
        obj.addProperty("endDate", "-");
        // 메세지 write 시각
        obj.addProperty("msgWrtTime", apWsDto.getCurrentTime());
        // 상담사id (최초는 "미할당")
        obj.addProperty("counselId", "미할당");
        // 이메시지 유형 (text/email/file/image로구분)
        obj.addProperty("msgContentType", "text");
        // 고객이름(고객입력)
        obj.addProperty("customer", apWsDto.getUserName());
        // 고객코드 (ap에서 할당받은 고객통합코드)
        obj.addProperty("customerCode", apWsDto.getCustomerCode());
        // 고객휴대폰번호 (고객입력)
        obj.addProperty("mobilePhone", "");
        // 고객이메일주소 (고객입력)
        obj.addProperty("email", "");
        log.info("inset Data >>> "+obj);
        return gson.toJson(obj);
    }
}
