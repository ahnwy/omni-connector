package aicc.omni.omniconnector.handler;

import aicc.omni.omniconnector.model.ExceptionVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class WebsocketChatHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JSONParser parser = new JSONParser();
        try{
            String payload = message.getPayload();
            JSONObject jsonObject = (JSONObject) parser.parse(payload);

//            session.sendMessage(new TextMessage(gson.toJson(messageVO)));
        } catch (Exception e){
            e.printStackTrace();
            session.sendMessage(new TextMessage(gson.toJson(ExceptionVO
                    .of("EC000",e.toString()))));
        }
    }
}