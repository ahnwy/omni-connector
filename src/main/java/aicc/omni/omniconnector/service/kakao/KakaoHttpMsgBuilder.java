package aicc.omni.omniconnector.service.kakao;

import aicc.omni.omniconnector.model.ApWsDto;
import aicc.omni.omniconnector.model.kakao.KakaoWhMsgDto;
import aicc.omni.omniconnector.util.KakaoHttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static aicc.omni.omniconnector.handler.WebsocketClientHandler.kakaoFileNameMap;

@Log4j2
public class KakaoHttpMsgBuilder {

    //AP에서 전송받은 정보로 메시지 가공
    public static String kakaoHttpMsg(ApWsDto apWsDto) throws IOException {

        Map<String, Object> msgMap = new HashMap<>(4);

        String customerId = apWsDto.getCustomer();
        String fileName = apWsDto.getFileName();
        String serialNo = serialNoMaker(customerId);

        apWsDto.setUploadSerial(serialNo);

        kakaoFileNameMap.put(serialNo, fileName); //전송된 파일 이름을 저장

        log.info("▶▶▶ KAKAO_FILE_NAME_SAVED : CId_{}, SN_{}, FN_{} " , customerId, serialNo, fileName);

        msgMap.put("user_key", customerId);
        msgMap.put("serial_number", serialNo);

        switch ((apWsDto.getMode())) {
            case "total":
                switch (apWsDto.getMsgContentType()) {
                    case "text":
                        msgMap.put("message_type", "TX");
                        msgMap.put("message", apWsDto.getMsg());
                        break;
                    case "image":
                        KakaoHttpUtil.uploadImg(apWsDto);
                        msgMap.put("message_type", "IM");
                        msgMap.put("image_url", apWsDto.getFilePath() + fileName);
                        break;
                    case "file":
                        KakaoHttpUtil.uploadImg(apWsDto);
                        msgMap.put("message_type", "FI");
                        msgMap.put("file_url", apWsDto.getFilePath() + apWsDto.getFileName());
                        break;

                    default:
                        break;
                }
            default:
                break;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }

    public static String kakaoHttpEndMsg(ApWsDto apWsDto) throws JsonProcessingException {

        String customerId = apWsDto.getCustomer();

        Map<String, Object> msgMap = new HashMap<>(3);

        msgMap.put("user_key", customerId);
        msgMap.put("serial_number", serialNoMaker(customerId));
        msgMap.put("end_type", "E"); // bot 이벤트 종료는 B
//                msgMap.put("bot_event", "");  // bot 이벤트 종료

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }


    // AP에서 받은 정보없이 Webhook 요청에 담긴 정보로 메시지 가공
    public static String kakaoHttpInfoMsg(KakaoWhMsgDto whMsgDto, String info) throws Exception {

        Map<String, Object> msgMap = new HashMap<>(4);

        String customerId = whMsgDto.getUser_key();

        msgMap.put("user_key", customerId);
        msgMap.put("serial_number", serialNoMaker(customerId));

        msgMap.put("message_type", "TX");
        msgMap.put("message", info);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }

    public static String kakaoHttpSendUploadedImg(KakaoWhMsgDto whMsgDto) throws Exception {

        // 저장된 파일 네임 로딩 후 map에서 삭제

        log.info("▶▶▶ KAKAO_FILE_NAME_LIST : {} " , kakaoFileNameMap.toString());


        for(String key0 : kakaoFileNameMap.keySet()) {
            String value0 = (String) kakaoFileNameMap.get(key0);
            log.info("▶▶▶ KAKAO_FILE_NAME_LIST : {}  +  {}", key0 , value0);
            }

        String originalFileName = kakaoFileNameMap.get(whMsgDto.getSerial_number());

        log.info("▶▶▶ KAKAO_FILE_NAME_LOADED : {}", originalFileName);
        kakaoFileNameMap.remove(whMsgDto.getSerial_number());

        Map<String, Object> msgMap = new HashMap<>(6);

        String customerId = whMsgDto.getSerial_number().split("_")[0];

        msgMap.put("user_key", customerId);
        msgMap.put("serial_number", serialNoMaker(customerId));
//        msgMap.put("message_type", whMsgDto.getMessageType());

        if (whMsgDto.getMessageType().equals("IM")) {
            msgMap.put("message_type", "IM");
            msgMap.put("image_url", whMsgDto.getImage());
        } else if (whMsgDto.getMessageType().equals("FI")) {
            msgMap.put("message_type", "FI");
            msgMap.put("file_url", whMsgDto.getFile());
            msgMap.put("file_name", originalFileName);
            msgMap.put("file_size", whMsgDto.getFile_size());
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgMap);
    }

    public static String serialNoMaker(String customerId){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(date);

        String serialNo = customerId + "_" + formattedDate;

        return serialNo;
    }
}

