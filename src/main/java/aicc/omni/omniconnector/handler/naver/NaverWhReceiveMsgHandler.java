package aicc.omni.omniconnector.handler.naver;

import aicc.omni.omniconnector.handler.WebsocketClientHandler;
import aicc.omni.omniconnector.model.naver.NaverWhMsgDto;
import aicc.omni.omniconnector.service.naver.NaverApiMsgBuilder;
import aicc.omni.omniconnector.service.naver.NaverSocketMsgBuilder;
import aicc.omni.omniconnector.util.NAVERHTTPUTIL;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NaverWhReceiveMsgHandler {

    public static void sendToAp(NaverWhMsgDto naverWhMsgDto) throws Exception {
        String socketMsgAP = "";
        switch (naverWhMsgDto.getEvent()) {
            case "open":
                socketMsgAP = NaverSocketMsgBuilder.apWebSocketInitMsg(naverWhMsgDto);
                WebsocketClientHandler.sendMessage(socketMsgAP);
                NAVERHTTPUTIL.sendApi(NaverApiMsgBuilder.sendTextMsg(naverWhMsgDto, "welcome"));
                break;
            case "send":
                if (naverWhMsgDto.getTextContent() != null) {
                    socketMsgAP = NaverSocketMsgBuilder.apWebSocketTextMsg(naverWhMsgDto);
                } else {
                    socketMsgAP = NaverSocketMsgBuilder.apWebSocketImageMsg(naverWhMsgDto);
                }
                WebsocketClientHandler.sendMessage(socketMsgAP);
                break;
            case "leave":
                socketMsgAP = NaverSocketMsgBuilder.apWebSocketEndMsg(naverWhMsgDto);
                WebsocketClientHandler.sendMessage(socketMsgAP);
                break;
            default:
                break;
        }
    }
}
