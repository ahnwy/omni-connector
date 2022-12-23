package aicc.omni.omniconnector.handler;

import aicc.omni.omniconnector.handler.kakao.KakaoWhReceiveMsgHandler;
import aicc.omni.omniconnector.handler.meta.MetaWhReceiveMsgHandler;
import aicc.omni.omniconnector.handler.naver.NaverWhReceiveMsgHandler;
import aicc.omni.omniconnector.handler.web.WebReceiveMsgHandler;
import aicc.omni.omniconnector.model.kakao.KakaoWhMsgDto;
import aicc.omni.omniconnector.model.meta.MetaWhMsgDto;
import aicc.omni.omniconnector.model.naver.NaverWhMsgDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ChatHandler {

    // I/F서버에서 통일된 데이터셋으로 전달받아 로직 수행
    public static void onMessage(String message) throws Exception {
        String channelId = "6"; // TODO-채널정보 셋팅필요
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, false);

        // channelId로 분기, 1 = kakao, 2 = naver, 3 = facebook, 4 = instagram

        switch (channelId) {
            //kakao  메시지 파싱 후 핸들러 처리
            case "1":
                KakaoWhMsgDto whMsgDto = mapper.readValue(message, KakaoWhMsgDto.class);
                whMsgDto.setChannel(channelId);
                KakaoWhReceiveMsgHandler.sendToAp(whMsgDto);
                break;
            // naver 메시지 파싱 후 핸들러 처리
            case "2":
                NaverWhMsgDto naverWhMsgDto = mapper.readValue(message, NaverWhMsgDto.class);
                naverWhMsgDto.setChannel(channelId);
                NaverWhReceiveMsgHandler.sendToAp(naverWhMsgDto);
                break;
            //meta  메시지 파싱 후 핸들러 처리
            case "3":
            case "4":
                MetaWhMsgDto msgDto = mapper.readValue(message, MetaWhMsgDto.class);
                msgDto.setChannel(channelId);
                MetaWhReceiveMsgHandler.sendToAp(msgDto);
                break;
            // 웹채팅 호출시
            case "6":
                WebReceiveMsgHandler.sendToAp(message);
                break;
            default:
        }
    }
}
