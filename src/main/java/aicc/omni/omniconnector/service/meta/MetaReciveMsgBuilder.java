package aicc.omni.omniconnector.service.meta;


import aicc.omni.omniconnector.model.ap.ApWsDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class MetaReciveMsgBuilder {

    public static String metaHttpTextMsg(ApWsDto apWsDto) throws JsonProcessingException {
        log.info("apWsDto >>> "+apWsDto);
        Map<String, Object> msgMap = new HashMap<>();
        Map<String, String> recipient = new HashMap<>();
        recipient.put("id", apWsDto.getPlatformID());
        Map<String, String> message = new HashMap<>();
        message.put("text", apWsDto.getMsg());
        msgMap.put("recipient", recipient);
        msgMap.put("message", message);

        msgMap.put("messaging_type", "MESSAGE_TAG");
        msgMap.put("tag", "HUMAN_AGENT");

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }

    public static String metaHttpMediaMsg(ApWsDto apWsDto) throws JsonProcessingException {

        Map<String, Object> msgMap = new HashMap<>();

        Map<String, String> recipient = new HashMap<>();
        recipient.put("id", apWsDto.getCustomer());

        Map<String, Object> message = new HashMap<>();

        Map<String, Object> attachment = new HashMap<>();
        attachment.put("type", apWsDto.getMsgContentType());

        Map<String, String> payload = new HashMap<>();

        String fileServerUrl = "https://hiqri.ai";
        //라이브 코드
        String fileServerPath = apWsDto.getFilePath().substring(5);
        String fileName = apWsDto.getFileName();
        //테스트용 코드
//        String fileServerPath = "/upload/TS/2022/06/";
//        String fileName = "crycry.jpg";

        String fileUrl = fileServerUrl + fileServerPath + fileName;

        payload.put("url", fileUrl);
        payload.put("is_reusable", "true");

        attachment.put("payload", payload);
        message.put("attachment", attachment);
        msgMap.put("message", message);

        msgMap.put("recipient", recipient);


        msgMap.put("messaging_type", "MESSAGE_TAG");
        msgMap.put("tag", "HUMAN_AGENT");

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }

}
