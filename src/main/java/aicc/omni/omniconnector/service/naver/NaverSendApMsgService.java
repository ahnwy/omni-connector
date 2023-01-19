package aicc.omni.omniconnector.service.naver;

import aicc.omni.omniconnector.handler.WebsocketClientHandler;
import aicc.omni.omniconnector.model.kakao.KakaoWhMsgDto;
import aicc.omni.omniconnector.model.naver.NaverWhMsgDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static aicc.omni.omniconnector.handler.WebsocketClientHandler.*;

@Log4j2
@Service
public class NaverSendApMsgService {
    @Autowired
    private NaverApMsgBuilder naverApMsgBuilder;

    // 최초 인입인지 체크
    private String checkInit(NaverWhMsgDto naverWhMsgDto) throws Exception {
        log.info("whUserMap >>> "+ WebsocketClientHandler.whUserMap);
        log.info("channelMap >>> "+WebsocketClientHandler.channelMap);
        if (!WebsocketClientHandler.whUserMap.containsKey(naverWhMsgDto.getUser())) {
            log.info("최초네....");
            WebsocketClientHandler.channelMap.put(naverWhMsgDto.getUser(), naverWhMsgDto.getChannel());
            log.info("channelMap >>> "+WebsocketClientHandler.channelMap);
            return "Y";
        } else {
            naverWhMsgDto.setMsgSeq(WebsocketClientHandler.whUserMap.get(naverWhMsgDto.getUser()));
            return "N";
        }
    }

    public String sendToAp(NaverWhMsgDto msgDto) throws Exception {
        String socketMsgAP = "";
        log.info("event >>> " + msgDto.getEvent());
        switch (msgDto.getEvent()) {
            case "open":
                socketMsgAP = naverApMsgBuilder.apWebSocketInitMsg(msgDto);
                log.info("socketMsgAP >>> " + socketMsgAP);
                break;
            case "send":
                String initYn = checkInit(msgDto);
                if (msgDto.getTextContent() != null) {
                    if(initYn.equals("Y")){
                        reservedMsgMap.put(msgDto.getUser(), naverApMsgBuilder.apWebSocketTextMsg(msgDto));
                        socketMsgAP =  naverApMsgBuilder.apWebSocketInitMsg(msgDto);
                    } else {
                        socketMsgAP = naverApMsgBuilder.apWebSocketTextMsg(msgDto);
                    }
                } else if (msgDto.getImageContent() != null){
                    if(initYn.equals("Y")){
                        reservedMsgMap.put(msgDto.getUser(), naverApMsgBuilder.apWebSocketImageMsg(msgDto));
                        socketMsgAP =  naverApMsgBuilder.apWebSocketInitMsg(msgDto);
                    } else {
                        socketMsgAP = naverApMsgBuilder.apWebSocketImageMsg(msgDto);
                    }
                }
                break;
            case "leave":
                // 고객이 대화방을 나간 경우 AP로 leave상태 전송 및 MAP에서 고객 삭제
                socketMsgAP = naverApMsgBuilder.apWebSocketEndMsg(msgDto);
                whUserMap.remove(msgDto.getUser());
                channelMap.remove(msgDto.getMsgSeq());
                break;
            default:
                break;
        }
        return socketMsgAP;
    }
}
