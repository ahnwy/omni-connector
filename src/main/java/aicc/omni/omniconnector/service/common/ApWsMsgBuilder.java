package aicc.omni.omniconnector.service.common;

import aicc.omni.omniconnector.model.ap.ApWsDto;
import aicc.omni.omniconnector.model.origin.OriginOutputMsgDto;
import aicc.omni.omniconnector.service.kakao.KakaoReciveMsgBuilder;
import aicc.omni.omniconnector.service.meta.MetaReciveMsgBuilder;
import aicc.omni.omniconnector.service.naver.NaverReciveMsgBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static aicc.omni.omniconnector.handler.WebsocketClientHandler.channelMap;
import static aicc.omni.omniconnector.handler.WebsocketClientHandler.whUserMap;

@Log4j2
@Service
public class ApWsMsgBuilder {

    @EventListener
    public String apMsgParser(ApWsDto apWsDto) throws Exception {
        log.info("짜증나!!!" + apWsDto);
        // whUserMap에서 platformID 찾기
        String platformID = null;
        Set<Map.Entry<String, String>> entrySet = whUserMap.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            if (entry.getValue().equals(apWsDto.getMsgSeq())) {
                platformID = entry.getKey();
                break;
            }
        }

        //Gson 관련 셋팅
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        log.info("platformID >>> " + platformID);
        log.info("ChannelSeq >>> " + channelMap.get(apWsDto.getMsgSeq()));
        log.info("contentType >>> " + apWsDto.getMsgContentType());
        apWsDto.setPlatformID(platformID);
        apWsDto.setChannelSeq(channelMap.get(apWsDto.getMsgSeq()));

        // AP에서 상담 시작, 종료 시 안내 text 메시지를 송출하므로, 더 이상 구분하여 메시지를 빌드하지 않는다.
        switch (apWsDto.getChannelSeq()) {
            case "1": // kakao 분기
                if(apWsDto.getMsgContentType() != null && apWsDto.getMsg() != null) {
                    if (apWsDto.getMsgContentType().equals("text")) {
                        return gson.toJson(KakaoReciveMsgBuilder.sendTextMsg(apWsDto));
                    } else if (apWsDto.getMsgContentType().equals("image")) {
                        return gson.toJson(KakaoReciveMsgBuilder.sendImageMsg(apWsDto));
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            case "2": // naver 분기
                // 데이터 구조상 msgContentType이 없는 케이스가 존재하여 null체크
                if(apWsDto.getMsgContentType() != null) {
                    if (apWsDto.getMsgContentType().equals("text")) {
                        return gson.toJson(NaverReciveMsgBuilder.sendTextMsg(apWsDto));
                    } else if (apWsDto.getMsgContentType().equals("image")) {
                        return gson.toJson(NaverReciveMsgBuilder.sendImageMsg(apWsDto));
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            case "3": // facebook 분기
            case "4": // ig 분기
                String httpMsg = null;
                apWsDto.setPlatformID(platformID);
                if(apWsDto.getMsgContentType() != null){
                    if(apWsDto.getMsgContentType().equals("text")){
                        httpMsg = MetaReciveMsgBuilder.metaHttpTextMsg(apWsDto);
                    } else if(apWsDto.getMsgContentType().equals("image")){
                        httpMsg = MetaReciveMsgBuilder.metaHttpMediaMsg(apWsDto);
                    } else if(apWsDto.getMsgContentType().equals("file")){
                        httpMsg = MetaReciveMsgBuilder.metaHttpMediaMsg(apWsDto);
                    } else {
                        httpMsg = MetaReciveMsgBuilder.metaHttpTextMsg(apWsDto);
                    }
                } else {
                    httpMsg = MetaReciveMsgBuilder.metaHttpTextMsg(apWsDto);
                }
                return httpMsg;
        }
        return null;
    }
//            if (apWsDto.getMsgContentType().equals("text")) {
//                return sendTextMsg(apWsDto);
//            } else if (apWsDto.getMsgContentType().equals("image")) {
//                return gson.toJson(KakaoReciveMsgBuilder.sendImageMsg(apWsDto));
//            } else {
//                return null;
//            }
//        } else {
//            return null;
//        }
    public static String sendTextMsg(ApWsDto apWsDto) throws JsonProcessingException {
        OriginOutputMsgDto originOutputMsgDto = new OriginOutputMsgDto();
        originOutputMsgDto.setQuestion(""); // not null
        originOutputMsgDto.setFallback(true); // not null
        originOutputMsgDto.setScore(0); // not null

        OriginOutputMsgDto.queryItem queryItem =new OriginOutputMsgDto.queryItem();
//        queryItem.setAnnotation(null);
//        queryItem.setKeywords();
//        queryItem.setEntities();
//        queryItem.setSemantics();
//        queryItem.setParams();
        originOutputMsgDto.setQuery(queryItem); // not null

        OriginOutputMsgDto.answerItem answerItem =new OriginOutputMsgDto.answerItem();
        answerItem.getAnswer(); // not null

        answerItem.getOutput(); // not null
        answerItem.getEntities();
        answerItem.getScenario();
        answerItem.getAuth();
        answerItem.getDomain();
        answerItem.getId();
        answerItem.getSemantics();
        answerItem.getCategory();
        answerItem.getDetector();
        answerItem.getIntent();
        originOutputMsgDto.setAnswer(answerItem); // not null

        ArrayList messageList = new ArrayList<>();
        OriginOutputMsgDto.messagesItem messagesItem = new OriginOutputMsgDto.messagesItem();
//        messagesItem.setTemplate(); // not null
//        messagesItem.setElements(); // not null
//        messagesItem.setType(); // not null
//        messagesItem.setOrgfilename();
//        messagesItem.setAlign();
//        OriginOutputMsgDto.messagesItem.buttonsItem buttonsItem = new OriginOutputMsgDto.messagesItem.buttonsItem();
//        buttonsItem.setText();
//        buttonsItem.setValue();
//        buttonsItem.setLoop();
//        messagesItem.setButtons(buttonsItem);
//        messagesItem.setCards();
        messageList.add(messagesItem);
        originOutputMsgDto.setMessages(messageList); // not null

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(originOutputMsgDto);
    }
}