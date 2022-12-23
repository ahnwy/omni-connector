package aicc.omni.omniconnector.handler.web;

import aicc.omni.omniconnector.handler.WebsocketClientHandler;
import aicc.omni.omniconnector.model.ApSessionDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;

import java.net.URISyntaxException;

@Log4j2
public class WebReceiveMsgHandler {

    public static void sendToAp(String message) throws URISyntaxException {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        ApSessionDto apSessionDto = gson.fromJson(message, ApSessionDto.class);

        // newEvent 시 메세지 재 빌드 후 전송
        if ("newEvent".equals(apSessionDto.getOpenFlag())) {
            JsonObject obj = new JsonObject();
            obj.addProperty("schema", apSessionDto.getSchema());
            obj.addProperty("openFlag", apSessionDto.getOpenFlag());
            obj.addProperty("platformID", apSessionDto.getPlatformID());
            obj.addProperty("userName", apSessionDto.getUserName());
            obj.addProperty("userPhone", apSessionDto.getUserPhone());
            obj.addProperty("userEmail", apSessionDto.getUserEmail());
            obj.addProperty("channelSeq", apSessionDto.getChannelSeq());
            obj.addProperty("corpCode", apSessionDto.getCorpCode());
            obj.addProperty("msgId", apSessionDto.getMsgId());
            obj.addProperty("msgReturnTime", apSessionDto.getMsgReturnTime());

            // 재빌드 메세지 전송
            WebsocketClientHandler.sendMessage(gson.toJson(obj));

        } else {
            // newEvent 외 받은 메세지를 AP 서버로 전송
            WebsocketClientHandler.sendMessage(message);
        }
    }
}
