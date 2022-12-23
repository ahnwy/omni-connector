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
        // whUserMap에서 platformID 찾기
        String platformID = null;
        Set<Map.Entry<String, String>> entrySet = whUserMap.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            if (entry.getValue().equals(apWsDto.getMsgSeq())) {
                platformID = entry.getKey();
            } else {
                log.info("★★★ platformID NULL ★★★");
            }
        }
        if (platformID != null) {
            apWsDto.setChannelSeq(platformID.substring(0, 1));
            apWsDto.setCustomer(platformID.substring(2));
        }

        //Gson 관련 셋팅
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        // ▶▶▶ 최종 데이터
        JsonObject params = new JsonObject();
        JsonObject secondParams = new JsonObject();

        // AP에서 상담 시작, 종료 시 안내 text 메시지를 송출하므로, 더 이상 구분하여 메시지를 빌드하지 않는다.
        if (apWsDto.getMsgContentType() != null) {
            switch (apWsDto.getChannelSeq()) {
                case "1": // kakao 분기
                    String kakaoMsg = null;

                    switch (apWsDto.getMsgContentType()) {
                        case "text": //
                        case "image":
                        case "file":
                            kakaoMsg = KakaoHttpMsgBuilder.kakaoHttpMsg(apWsDto);
                            break;
                        default:
                            break;
                    }
                    return kakaoMsg;

                case "2": // naver 분기
                    switch (apWsDto.getMsgContentType()) {
                        case "text":
                            params.addProperty("user", platformID.substring(2));
                            params.addProperty("event", "send");
                            secondParams.addProperty("text", apWsDto.getMsg());
                            params.add("textContent", secondParams);
                            break;
                        case "image":
                            params.addProperty("user", platformID.substring(2));
                            params.addProperty("event", "send");
                            String fileUrl = apWsDto.getMsg();
                            String url = "https://hiqri.ai";
                            secondParams.addProperty("imageUrl", url + fileUrl);
                            params.add("imageContent", secondParams);
                            params.add("options", secondParams);
                            break;
                        default:
                            break;
                    }
                    return gson.toJson(params);
                case "3": // facebook 분기
                case "4": // ig 분기
                    String httpMsg = null;

                    switch (apWsDto.getMsgContentType()) {
                        case "text":
                            httpMsg = MetaHttpMsgBuilder.metaHttpTextMsg(apWsDto);
                            log.info(httpMsg);
                            break;
                        case "image":
                        case "file":
                            httpMsg = MetaHttpMsgBuilder.metaHttpMediaMsg(apWsDto);
                            log.info(httpMsg);
                            break;
                        default:
                            break;
                    }
                    return httpMsg;
            }
        }
        return null;
    }
}