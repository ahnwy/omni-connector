package aicc.omni.omniconnector.service.kakao;

import aicc.omni.omniconnector.model.ap.ApWsDto;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Log4j2
@Component
public class KakaoReciveMsgBuilder {

    public static JsonObject sendTextMsg(ApWsDto apWsDto) {
        JsonObject params = new JsonObject();
        params.addProperty("user_key", apWsDto.getPlatformID());
        params.addProperty("serial_number", serialNoMaker(apWsDto.getPlatformID()));
        params.addProperty("message_type", "TX");
        params.addProperty("message", apWsDto.getMsg());
        log.info("kakaoMsg >>> "+ params);
        return params;
    }

    public static JsonObject sendImageMsg(ApWsDto apWsDto) {
        JsonObject params = new JsonObject();
        params.addProperty("user_key", apWsDto.getPlatformID());
        params.addProperty("serial_number", serialNoMaker(apWsDto.getPlatformID()));
        params.addProperty("message_type", "IM");
        params.addProperty("image_url", apWsDto.getFilePath() + apWsDto.getFileName());
        log.info("kakaoMsg >>> "+ params);
        return params;
    }

    public static String serialNoMaker(String customerId){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(date);

        String serialNo = customerId + "_" + formattedDate;

        return serialNo;
    }
}

