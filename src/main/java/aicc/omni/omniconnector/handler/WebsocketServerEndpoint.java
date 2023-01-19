package aicc.omni.omniconnector.handler;

import aicc.omni.omniconnector.config.ServerEndpointConfigurator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
@ServerEndpoint(value="/mw/chat", configurator = ServerEndpointConfigurator.class)
public class WebsocketServerEndpoint {
    @Autowired
    private ChatHandler chatHandler;

    public static Map<String, Session> chatPidSessMap = new HashMap<String, Session>();

    @OnMessage
    public void onMessage (Session session, String message) throws Exception {
        System.out.println(message);
        chatHandler.sendToAp(message, "6");
    }

    public static void send(String message) throws IOException {
        if (chatPidSessMap.get("a") != null){
            chatPidSessMap.get("a").getBasicRemote().sendText(message);
        }
    }
    @OnClose
    public void onClose(){
        chatPidSessMap.clear();
    }

    @OnOpen
    public void onOpen(Session session){
        chatPidSessMap.put("a", session);
    }
}
