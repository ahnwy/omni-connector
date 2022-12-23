package aicc.omni.omniconnector.model.naver;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.text.SimpleDateFormat;

@Log4j2
@Data
public class NaverApiMsgDto {

    // 상담AP ==> ChatBot API 전송

    // 필수 정보
    private String user = "y9Jkl7CvLiiRxtRQJCdsqg";

    // view 변수
    private String msg;
    private String alarm;
    private String img;

    // json 정보
    // profile
    private String event;
    private JsonObject options;
    // profile options
    private String field;
    private JsonArray agreements;

    // agreements
    private String cellphone;
    private String address;

    private String messageId;

    // base API
    private JsonObject textContent;
    private JsonObject imageContent;

    // textContent API
    private String text;
    private String inputType;
    private String code;

    // options API
    private String noReflectBot;
    private String mobile;
    private String sourceId;
    private String threadOwnerId;
    private String nickname;
    private String result;
    private String action;

    // imageContent API
    private String imageUrl;
    private String width;
    private String height;

    // 시간 세팅
    // currentTime
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
    private String currentTime = sdf.format(System.currentTimeMillis());

    // AP용 msgSeq
    private String msgSeq;
}
