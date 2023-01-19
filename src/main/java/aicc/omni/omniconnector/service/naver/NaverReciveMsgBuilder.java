package aicc.omni.omniconnector.service.naver;

import aicc.omni.omniconnector.model.ap.ApWsDto;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class NaverReciveMsgBuilder {
    /*
        상담AP포맷을 네이버로 보내기 위한 데이터 빌더
     */

    public static JsonObject sendTextMsg(ApWsDto apWsDto) {
        JsonObject params = new JsonObject();
        JsonObject secondParams = new JsonObject();
        params.addProperty("user", apWsDto.getPlatformID());
        params.addProperty("event", "send");
        secondParams.addProperty("text", apWsDto.getMsg());
        params.add("textContent", secondParams);
        log.info("naverMsg >>> "+ params);
        return params;
    }

    public static JsonObject sendImageMsg(ApWsDto apWsDto) {
        JsonObject params = new JsonObject();
        JsonObject secondParams = new JsonObject();
        params.addProperty("user", apWsDto.getPlatformID());
        params.addProperty("event", "send");
        String fileUrl = apWsDto.getMsg();
        String url = "https://hiqri.ai";
        secondParams.addProperty("imageUrl", fileUrl);
        params.add("imageContent", secondParams);
        params.add("options", secondParams);

        return params;
    }
}

