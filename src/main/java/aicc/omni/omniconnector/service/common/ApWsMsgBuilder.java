package aicc.omni.omniconnector.handler;

import aicc.omni.omniconnector.model.ApWsDto;
import aicc.omni.omniconnector.service.kakao.KakaoHttpMsgBuilder;
import aicc.omni.omniconnector.service.meta.MetaHttpMsgBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

import static aicc.omni.omniconnector.handler.WebsocketClientHandler.whUserMap;

@Log4j2
@Service
public class ApWsMsgBuilder {

    public static String apMsgParser(ApWsDto apWsDto) throws Exception {
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

        // ▶▶▶ 최종 데이터
        JsonObject params = new JsonObject();
        JsonObject secondParams = new JsonObject();

        log.info("platformID >>> " + platformID);
        log.info("ChannelSeq >>> " + apWsDto.getChannelSeq());
        log.info("contentType >>> " + apWsDto.getMsgContentType());

        // AP에서 상담 시작, 종료 시 안내 text 메시지를 송출하므로, 더 이상 구분하여 메시지를 빌드하지 않는다.
        switch (apWsDto.getChannelSeq()) {
            case "1": // kakao 분기
                return KakaoHttpMsgBuilder.kakaoHttpMsg(apWsDto);
            case "2": // naver 분기
                // 데이터 구조상 msgContentType이 없는 케이스가 존재하여 null체크
                if(apWsDto.getMsgContentType() != null){
                    switch (apWsDto.getMsgContentType()) {
                        case "text":
                            params.addProperty("user", platformID);
                            params.addProperty("event", "send");
                            secondParams.addProperty("text", apWsDto.getMsg());
                            params.add("textContent", secondParams);
                            log.info("param >>> " + params);
                            break;
                        case "image":
                            params.addProperty("user", platformID.substring(2));
                            params.addProperty("event", "send");
                            String fileUrl = apWsDto.getMsg();
                            String url = "https://hiqri.ai";
                            secondParams.addProperty("imageUrl", fileUrl);
                            params.add("imageContent", secondParams);
                            params.add("options", secondParams);
                            break;
                        default:
                            break;
                    }
                }
                return gson.toJson(params);
            case "3": // facebook 분기
            case "4": // ig 분기
                String httpMsg = null;
                apWsDto.setPlatformID(platformID);
                if(apWsDto.getMsgContentType() != null){
                    if(apWsDto.getMsgContentType().equals("text")){
                        httpMsg = MetaHttpMsgBuilder.metaHttpTextMsg(apWsDto);
                    } else if(apWsDto.getMsgContentType().equals("image")){
                        httpMsg = MetaHttpMsgBuilder.metaHttpMediaMsg(apWsDto);
                    } else if(apWsDto.getMsgContentType().equals("file")){
                        httpMsg = MetaHttpMsgBuilder.metaHttpMediaMsg(apWsDto);
                    } else {
                        httpMsg = MetaHttpMsgBuilder.metaHttpTextMsg(apWsDto);
                    }
                } else {
                    httpMsg = MetaHttpMsgBuilder.metaHttpTextMsg(apWsDto);
                }
                return httpMsg;
        }
        return null;
    }
}