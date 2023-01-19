package aicc.omni.omniconnector.service.kakao;

import aicc.omni.omniconnector.handler.WebsocketClientHandler;
import aicc.omni.omniconnector.model.kakao.KakaoWhMsgDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static aicc.omni.omniconnector.handler.WebsocketClientHandler.reservedMsgMap;

@Log4j2
@Service
public class KakaoMsgHandler {

    // 최초 인입인지 체크
    private String checkInit(KakaoWhMsgDto msgDto) throws Exception {
        if (!WebsocketClientHandler.whUserMap.containsKey(msgDto.getPlatformId())) {
            log.info("최초네....");
            WebsocketClientHandler.channelMap.put(msgDto.getUser_key(), msgDto.getChannel());
            log.info(WebsocketClientHandler.channelMap);
            return "Y";
        } else {
            return "N";
        }
    }

    //AP로 보내기전 초기 인입체크 및 메세지 빌드 로직
    public String sendToAp(KakaoWhMsgDto msgDto) throws Exception {
        log.info("KakaoWhMsgDto >>> " + msgDto);
        String initYn = checkInit(msgDto);
        if(msgDto.getPath().equals("message")){
            if(msgDto.getType().equals("text")){
                if(initYn.equals("Y")){
                    reservedMsgMap.put(msgDto.getPlatformId(), KakaoApMsgBuilder.apWebSocketTextMsg(msgDto));
                    return KakaoApMsgBuilder.apWebSocketInitMsg(msgDto);
                } else {
                    return KakaoApMsgBuilder.apWebSocketTextMsg(msgDto);
                }
            } else if(msgDto.getMessageType().equals("photo")){

            } else if(msgDto.getMessageType().equals("video")){

            } else if(msgDto.getMessageType().equals("audio")){

            } else {

            }
        }
        return "";
    }
}
