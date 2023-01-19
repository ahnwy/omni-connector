package aicc.omni.omniconnector.service.meta;

import aicc.omni.omniconnector.handler.WebsocketClientHandler;
import aicc.omni.omniconnector.model.meta.MetaWhMsgDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class MetaApMsgBuilder {
    public static String apWebSocketInitMsg(MetaWhMsgDto msgDto) throws Exception {

        Map<String, String> msgMap = new HashMap<>(9);

        msgMap.put("msgSeq", "unknown");
        msgMap.put("platformID", msgDto.getPlatformId());
        msgMap.put("msg", "VISIT"); // 고정
        msgMap.put("openFlag", "newEvent");
        msgMap.put("userName", msgDto.getEntry().get(0).getMessaging().get(0).getSender().getSenderId());
        msgMap.put("userPhone", "1"); //고정
        msgMap.put("userEmail", "1"); //고정
        msgMap.put("schema", "ap"); //고정
        msgMap.put("corpCode", "CS"); //고정
        msgMap.put("msgId", msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getMid());
        msgMap.put("channelSeq", msgDto.getChannel());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }

    public static String apWebSocketDbMsg(MetaWhMsgDto msgDto) throws Exception {

        Map<String, Map<String, String>> msgMap = new HashMap<>(2);
        Map<String, String> msg = new HashMap<>(10);
        Map<String, String> sendInfo = new HashMap<>(8);

        msg.put("msgSeq", "insert"); //고정
        msg.put("customerName", ""); //없음
        msg.put("mobilePhone", ""); //없음
        msg.put("email", ""); //없음
        msg.put("gender", "N"); // 고정
        msg.put("counselTendency", "L");  // 고정
        msg.put("customerInfoAgree", "N");  // 고정
        msg.put("customerInfoUse", "R");   // 고정
        msg.put("schema", "ap"); //고정
        msg.put("corpCode", "CS"); //고정
        msg.put("msgId", msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getMid());

        sendInfo.put("channelSeq", msgDto.getMsgWrtId());
        sendInfo.put("status", "");
        sendInfo.put("idleDate", "");
        sendInfo.put("msgContentType", "");
        sendInfo.put("customer", "");
        sendInfo.put("customerCode", "");
        sendInfo.put("mobilePhone", "");
        sendInfo.put("email", "");

        msgMap.put("msg", msg);
        msgMap.put("sendInfo", sendInfo);



        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }

    public static String apWebSocketTextMsg(MetaWhMsgDto msgDto) throws Exception {

        Map<String, String> msg = new HashMap<>(9);

        msg.put("mode", "total"); // 고정
        msg.put("msgSeq", WebsocketClientHandler.whUserMap.get(msgDto.getPlatformId()));
        msg.put("msgContentType", "text"); // 고정
        msg.put("msgWrtTime", msgDto.getFormattedDate());
        msg.put("msgWrtId", "1");
        msg.put("schema", "ap"); //고정
        msg.put("corpCode", "CS"); //고정
        msg.put("msgId", msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getMid());
        msg.put("msg", msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getText());
        msg.put("platformID", msgDto.getPlatformId());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg);
    }

    public static String apWebSocketImageMsg(MetaWhMsgDto msgDto, int i) throws Exception {

        Map<String, Object> msg = new HashMap<>(13);

        msg.put("mode", "total"); // 고정
        msg.put("msgSeq", WebsocketClientHandler.whUserMap.get(msgDto.getPlatformId()));
        msg.put("msg", msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getAttachments().
                get(i).getPayload().getUrl()); // filePath와 동일 내용임(AP 요청사항)
        msg.put("msgContentType", "image"); // 고정
        msg.put("filePath", msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getAttachments().
                get(i).getPayload().getUrl());
        msg.put("fileName", msgDto.getFileName());

        msg.put("fileSize", Integer.parseInt(msgDto.getFileSize()));
        msg.put("msgWrtTime", msgDto.getFormattedDate());
        msg.put("channelSeq", "2");
        msg.put("msgWrtId", msgDto.getMsgWrtId());
        msg.put("schema", "ap"); //고정
        msg.put("corpCode", "CS"); //고정
        msg.put("msgId", msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getMid());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg);
    }

    public static String apWebSocketCloseMsg(MetaWhMsgDto msgDto) throws Exception {

        Map<String, String> msg = new HashMap<>(14);

        msg.put("mode", "total");
        msg.put("msgSeq", "");
        msg.put("msgContentType", "text"); // 고정
        msg.put("channelSeq", msgDto.getChannel());
        msg.put("customer", ""); // 없음
        msg.put("status", "종료");
        msg.put("msg", ""); //없음
        msg.put("endDate", ""); //없음
        msg.put("msgWrtTime", ""); //없음
        msg.put("msgWrtId", msgDto.getMsgWrtId());
        msg.put("flag", "E"); //고정
        msg.put("schema", "ap"); //고정
        msg.put("corpCode", "CS"); //고정
        msg.put("msgId", msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getMid());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg);
    }


    public static String apWebSocketRestartMsg(MetaWhMsgDto msgDto, int i) throws Exception {

        Map<String, String> msg = new HashMap<>(12);

        msg.put("mode", "total");
        msg.put("msgSeq", "");
        msg.put("msg", "");
        msg.put("msgContentType", "text");
        msg.put("msgWrtTime", msgDto.getFormattedDate());
        msg.put("channelSeq", msgDto.getChannel());
        msg.put("msgWrtId", msgDto.getMsgWrtId());
        msg.put("endDate", "");
        msg.put("status", "재시작");
        msg.put("flag", "R");
        msg.put("schema", "ap"); //고정
        msg.put("corpCode", "CS"); //고정
        msg.put("msgId", msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getMid());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg);
    }

    public static String buildDmsMsg (MetaWhMsgDto msgDto) throws Exception {

        Map<String, String>msgMap = new HashMap<>(9);

        msgMap.put("userId", msgDto.getEntry().get(0).getMessaging().get(0).getSender().getSenderId());
        if (msgDto.getEntry().get(0).getMessaging().get(0).getMessage() != null) {
            msgMap.put("text", msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getText());
        }
        if (msgDto.getFileName() != null){
            msgMap.put("text", msgDto.getFileName());
        }
        msgMap.put("type", msgDto.getMessageType());
        msgMap.put("channel", msgDto.getChannel());
        msgMap.put("event", "send");
        msgMap.put("schema", "ap"); //고정
        msgMap.put("corpCode", "CS"); //고정
        msgMap.put("msgId", msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getMid());

        ObjectMapper mapper = new ObjectMapper();

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);

    }
}
