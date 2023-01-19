package aicc.omni.omniconnector.service.naver;

import aicc.omni.omniconnector.model.naver.NaverWhMsgDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static aicc.omni.omniconnector.handler.WebsocketClientHandler.channelMap;
import static aicc.omni.omniconnector.handler.WebsocketClientHandler.whUserMap;

@Log4j2
@Service
public class NaverWhReceiveMsgHandler {
    @Autowired
    private NaverApMsgBuilder naverApMsgBuilder;

    public String sendToAp(NaverWhMsgDto naverWhMsgDto) throws Exception {
        String socketMsgAP = "";
        log.info("event >>> " + naverWhMsgDto.getEvent());
        switch (naverWhMsgDto.getEvent()) {
            case "open":
                socketMsgAP = naverApMsgBuilder.apWebSocketInitMsg(naverWhMsgDto);
                log.info("socketMsgAP >>> " + socketMsgAP);
                break;
            case "send":
                if (naverWhMsgDto.getTextContent() != null) {
                    socketMsgAP = naverApMsgBuilder.apWebSocketTextMsg(naverWhMsgDto);
                } else {
                    socketMsgAP = naverApMsgBuilder.apWebSocketImageMsg(naverWhMsgDto);
                }
                break;
            case "leave":
                // 고객이 대화방을 나간 경우 AP로 leave상태 전송 및 MAP에서 고객 삭제
                socketMsgAP = naverApMsgBuilder.apWebSocketEndMsg(naverWhMsgDto);
                whUserMap.remove(naverWhMsgDto.getUser());
                channelMap.remove(naverWhMsgDto.getMsgSeq());
                break;
            default:
                break;
        }
        return socketMsgAP;
    }
}
