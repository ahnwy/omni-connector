package aicc.omni.omniconnector.handler.web;

import aicc.omni.omniconnector.handler.WebsocketClientHandler;
import aicc.omni.omniconnector.model.ap.ApWsDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;

@Log4j2
@Service
public class WebReceiveMsgHandler {
    @Autowired
    private WebsocketClientHandler websocketClientHandler;

    public void sendToAp(String message) throws URISyntaxException {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        ApWsDto apWsDto = gson.fromJson(message, ApWsDto.class);

        // newEvent 시 메세지 재 빌드 후 전송
        if ("newEvent".equals(apWsDto.getOpenFlag())) {
            JsonObject obj = new JsonObject();
            obj.addProperty("schema", apWsDto.getSchema());
            obj.addProperty("openFlag", apWsDto.getOpenFlag());
            obj.addProperty("platformID", apWsDto.getPlatformID());
            obj.addProperty("userName", apWsDto.getUserName());
            obj.addProperty("userPhone", apWsDto.getUserPhone());
            obj.addProperty("userEmail", apWsDto.getUserEmail());
            obj.addProperty("channelSeq", apWsDto.getChannelSeq());
            obj.addProperty("corpCode", apWsDto.getCorpCode());
            obj.addProperty("msgId", apWsDto.getMsgId());
            obj.addProperty("msgReturnTime", apWsDto.getMsgReturnTime());

            // 재빌드 메세지 전송
            websocketClientHandler.sendMessage(gson.toJson(obj));

        } else {
            // newEvent 외 받은 메세지를 AP 서버로 전송
            websocketClientHandler.sendMessage(message);
        }
    }
}
