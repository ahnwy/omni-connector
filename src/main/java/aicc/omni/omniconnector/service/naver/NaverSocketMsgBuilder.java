package aicc.omni.omniconnector.service.naver;

import aicc.omni.omniconnector.model.ApMsgSeqDto;
import aicc.omni.omniconnector.model.naver.NaverWhMsgDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class NaverSocketMsgBuilder {

    public static String apWebSocketInitMsg(NaverWhMsgDto naverWhMsgDto) throws Exception {

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
        msgMap.put("msgId", naverWhMsgDto.getMessageId());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }

    public static String apWebSocketTextMsg(NaverWhMsgDto naverWhMsgDto) throws Exception {

        Map<String, String> msgMap = new HashMap<>(9);

        msgMap.put("mode", "total"); // 고정
        msgMap.put("msgSeq", naverWhMsgDto.getUser());
        msgMap.put("msg", naverWhMsgDto.getTextContent().getText());
        msgMap.put("msgContentType", "text"); // 고정
        msgMap.put("msgWrtTime", naverWhMsgDto.getCurrentTime());
        msgMap.put("msgWrtId", "1");
        msgMap.put("schema", "ap"); //고정
        msgMap.put("corpCode", "CS"); //고정
        msgMap.put("msgId", naverWhMsgDto.getMessageId());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }

    public static String apWebSocketImageMsg(NaverWhMsgDto naverWhMsgDto) throws Exception {

        Map<String, Object> msg = new HashMap<>(13);

//        msg.put("mode", "total"); // 고정
//        msg.put("msgSeq", naverWhMsgDto.getUser());
//        msg.put("msg", naverWhMsgDto.getImageContent().getImageUrl()); // filePath와 동일 내용임(AP 요청사항)
//        msg.put("msgContentType", "image"); // 고정
//        msg.put("filePath", naverWhMsgDto.getImageContent().getImageUrl());
//        msg.put("fileName", url.substring(url.lastIndexOf('/') + 1, url.length());
//        msg.put("fileSize", whMsgDto.getFileSize());
//        msg.put("msgWrtTime", URLDecoder.decode(null, "EUC-KR"));
//        msg.put("channelSeq", "1");
//        msg.put("msgWrtId", "1");
//        msg.put("schema", "ap"); //고정
//        msg.put("corpCode", "CS"); //고정
//        msg.put("msgId", naverWhMsgDto.getMessageId());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg);
    }

    public static String apWebSocketEndMsg(NaverWhMsgDto naverWhMsgDto) throws Exception {

        Map<String, String> msgMap = new HashMap<>(14);

        msgMap.put("mode", "total");
        msgMap.put("msgSeq", naverWhMsgDto.getUser());
        msgMap.put("msg", "");
        msgMap.put("msgContentType", "text"); // 고정
        msgMap.put("channelSeq", "2");
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
    public static String socketMsgParserApAddAuth(ApMsgSeqDto apMsgSeqDto) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        JsonObject obj = new JsonObject();

        // client information setting
        obj.addProperty("schema", "ap");
        obj.addProperty("corpCode", "CS");
        obj.addProperty("msgId", apMsgSeqDto.getMsgId());
        // 고객화면 인증 연결
        obj.addProperty("mode", "insert");
        // msgSeq : 채팅방번호 (서버에서 할당)
        obj.addProperty("msgSeq", apMsgSeqDto.getMsgSeq());
        // 인입채널 | 1.kakao, 2.naver, 3.facebook, 4.instagram, 5.webchat, 6.email
        obj.addProperty("channelSeq", apMsgSeqDto.getPlatformID().substring(0, 1));
        // 채팅방모드
        obj.addProperty("status", "대기");
        // 최초 접속 시각
        obj.addProperty("idleDate", apMsgSeqDto.getCurrentTime());
        // 상담요청시간 (상담사가 배정된 시간-ap배정)
        obj.addProperty("requestDate", "-");
        // 상담시작시간 (상담사가 시작클릭시간-ap배정)
        obj.addProperty("startDate", "-");
        // 상담종료시간 (상담사혹은 고객이 상담종료를 누른시간)
        obj.addProperty("endDate", "-");
        // 메세지 write 시각
        obj.addProperty("msgWrtTime", apMsgSeqDto.getCurrentTime());
        // 상담사id (최초는 "미할당")
        obj.addProperty("counselId", "미할당");
        // 이메시지 유형 (text/email/file/image로구분)
        obj.addProperty("msgContentType", "text");
        // 고객이름(고객입력)
        obj.addProperty("customer", apMsgSeqDto.getUserName());
        // 고객코드 (ap에서 할당받은 고객통합코드)
        obj.addProperty("customerCode", apMsgSeqDto.getCustomerCode());
        // 고객휴대폰번호 (고객입력)
        obj.addProperty("mobilePhone", "");
        // 고객이메일주소 (고객입력)
        obj.addProperty("email", "");

        return gson.toJson(obj);
    }
}
