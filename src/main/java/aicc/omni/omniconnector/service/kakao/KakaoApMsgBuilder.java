package aicc.omni.omniconnector.service.kakao;

import aicc.omni.omniconnector.handler.WebsocketClientHandler;
import aicc.omni.omniconnector.model.kakao.KakaoWhMsgDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class KakaoApMsgBuilder {

    public static String apWebSocketInitMsg(KakaoWhMsgDto whMsgDto) throws Exception {

        Map<String, String> msgMap = new HashMap<>(10);

        msgMap.put("msgSeq", "unknown");
        msgMap.put("platformID", whMsgDto.getPlatformId());
        msgMap.put("msg", "VISIT"); // 고정
        msgMap.put("openFlag", "newEvent");
        msgMap.put("userName", whMsgDto.getUser_key());
        msgMap.put("userPhone", "1"); //고정
        msgMap.put("userEmail", "1"); //고정
        msgMap.put("schema", "ap"); //고정
        msgMap.put("corpCode", "CS"); //고정
        msgMap.put("msgId", whMsgDto.getUuid());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }

    public static String apWebSocketTextMsg(KakaoWhMsgDto whMsgDto) throws Exception {

        Map<String, String> msg = new HashMap<>(9);

        msg.put("mode", "total"); // 고정
        msg.put("msgSeq", WebsocketClientHandler.whUserMap.get(whMsgDto.getPlatformId()));
        msg.put("msg", whMsgDto.getContent());
        msg.put("msgContentType", "text"); // 고정
        msg.put("msgWrtTime", whMsgDto.getFormattedDate());
        msg.put("msgWrtId", whMsgDto.getMsgWrtId());
        msg.put("schema", "ap"); //고정
        msg.put("corpCode", "CS"); //고정
        msg.put("msgId", whMsgDto.getUuid());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg);
    }

    public static String apWebSocketImageMsg(KakaoWhMsgDto whMsgDto) throws Exception {

        Map<String, Object> msg = new HashMap<>(13);

        msg.put("mode", "total"); // 고정
        msg.put("msgSeq", WebsocketClientHandler.whUserMap.get(whMsgDto.getPlatformId()));
        msg.put("msg", whMsgDto.getFileUrl()); // filePath와 동일 내용임(AP 요청사항)
        msg.put("msgContentType", "image"); // 고정
        msg.put("filePath", whMsgDto.getFileUrl());
        msg.put("fileName", whMsgDto.getFileName());
        msg.put("fileSize", whMsgDto.getFileSize());
        msg.put("msgWrtTime", whMsgDto.getFormattedDate());
        msg.put("channelSeq", "1");
        msg.put("msgWrtId", whMsgDto.getMsgWrtId());
        msg.put("schema", "ap"); //고정
        msg.put("corpCode", "CS"); //고정
        msg.put("msgId", whMsgDto.getUuid());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg);
    }

    public static String apWebSocketEndMsg(KakaoWhMsgDto whMsgDto) throws Exception {

        Map<String, String> msgMap = new HashMap<>(14);

        msgMap.put("mode", "total");
        msgMap.put("msgSeq", whMsgDto.getMsgSeq());
        msgMap.put("msgContentType", "text"); // 고정
        msgMap.put("channelSeq", "1");
        msgMap.put("customer", whMsgDto.getUser_key());
        msgMap.put("status", "종료");
        msgMap.put("msg", "고객이 상담을 종료하였습니다.");
        msgMap.put("endDate", whMsgDto.getFormattedDate()); //고정
        msgMap.put("msgWrtTime", whMsgDto.getFormattedDate()); //고정
        msgMap.put("flag", "F"); //고정
        msgMap.put("msgWrtId", whMsgDto.getMsgWrtId()); // 메시지작성자 (1(고객), 2(상담사), 3(봇))
        msgMap.put("schema", "ap"); //고정
        msgMap.put("corpCode", "CS"); //고정
        msgMap.put("msgId", whMsgDto.getUuid());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }
}