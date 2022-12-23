package aicc.omni.omniconnector.service.naver;

import aicc.omni.omniconnector.model.naver.NaverWhMsgDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class NaverApiMsgBuilder {

    // file 용량 초과 시 error 메세지
    public static String sendTextMsg(NaverWhMsgDto naverWhMsgDto, String reaseon) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        JsonObject params = new JsonObject();

        // JSON 형식의 데이터 셋팅
        params.addProperty("user", naverWhMsgDto.getUser());
        JsonObject textContent = new JsonObject();
        params.addProperty("event", "send");
        // reaseon 에 따른 메세지 세팅
        switch (reaseon) {
            case "welcome":
                textContent.addProperty("text", "담당자가 상담을 준비중입니다. 잠시만 기다려 주세요!!!");
                break;
            case "text":
                textContent.addProperty("text", "글자수 제한 초과입니다. 상담사가 메세지를 받지 못했습니다. 공백 포함 1000자 이내로 보내주세요!!!");
                break;
            case "image":
                textContent.addProperty("text", "이미지 용량 초과입니다. 상담사가 파일을 받지 못했습니다. 10MB미만 이미지를 보내주세요!!!");
                break;
            case "vphone":
                textContent.addProperty("text", "안심번호 상담요청은 지원하지 않습니다!!!");
                break;
        }
        params.add("textContent", textContent);
        log.info("▶▶▶ NAVER_API_MSG_BUILT : " + params);

        return gson.toJson(params);
    }
}

