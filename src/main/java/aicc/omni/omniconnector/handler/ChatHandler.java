package aicc.omni.omniconnector.handler;

import aicc.omni.omniconnector.service.kakao.KakaoSendApMsgService;
import aicc.omni.omniconnector.service.meta.MetaSendApMsgService;
import aicc.omni.omniconnector.service.naver.NaverSendApMsgService;
import aicc.omni.omniconnector.service.web.WebApMsgService;
import aicc.omni.omniconnector.model.kakao.KakaoWhMsgDto;
import aicc.omni.omniconnector.model.meta.MetaWhMsgDto;
import aicc.omni.omniconnector.model.naver.NaverWhMsgDto;
import aicc.omni.omniconnector.model.origin.OriginInputMsgDto;
import aicc.omni.omniconnector.service.common.ChannelType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ChatHandler {
    @Autowired
    private KakaoSendApMsgService kakaoSendApMsgService;
    @Autowired
    private NaverSendApMsgService naverSendApMsgService;
    @Autowired
    private MetaSendApMsgService metaSendApMsgService;
    @Autowired
    private WebApMsgService webApMsgService;
    @Autowired
    private WebsocketClientHandler websocketClientHandler;

    // I/F서버에서 통일된 데이터셋으로 전달받아 로직 수행
    public void sendToAp(String message, String channel) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, false);
        OriginInputMsgDto originInputMsgDto = mapper.readValue(message, OriginInputMsgDto.class);
        String apMsg = "";

        // channelId로 분기, 1 = kakao, 2 = naver, 3 = facebook, 4 = instagram
        if(channel.equals(ChannelType.KAKAO)){
            KakaoWhMsgDto whMsgDto = mapper.readValue(message, KakaoWhMsgDto.class);
            whMsgDto.setChannel(channel);
            apMsg = kakaoSendApMsgService.sendToAp(whMsgDto);
        } else if(channel.equals(ChannelType.NAVER)){
            NaverWhMsgDto naverWhMsgDto = mapper.readValue(message, NaverWhMsgDto.class);
            naverWhMsgDto.setChannel(channel);
            apMsg = naverSendApMsgService.sendToAp(naverWhMsgDto);
        } else if(channel.equals(ChannelType.FACEBOOK) || channel.equals(ChannelType.IG)){
            MetaWhMsgDto msgDto = mapper.readValue(message, MetaWhMsgDto.class);
            msgDto.setChannel(channel);
            apMsg = metaSendApMsgService.sendToAp(msgDto);
        } else if(channel.equals(ChannelType.WEBCHAT)){
            apMsg = webApMsgService.sendToAp(message);
        } else {
            //TODO - 채널정보가 없는 경우 예외메세지 리턴 필요
        }
        if(!apMsg.equals("")){
            log.info("AP 보내기 전 메세지 >>> " + apMsg);
            websocketClientHandler.sendMessage(apMsg);
        }
    }
}
