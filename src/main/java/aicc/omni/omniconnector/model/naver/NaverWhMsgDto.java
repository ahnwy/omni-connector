package aicc.omni.omniconnector.model.naver;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.List;

@Data
@NoArgsConstructor
public class NaverWhMsgDto {

    // currentTime
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private String currentTime = sdf.format(System.currentTimeMillis());

    // ChatBot ==> AP API 전송
    // base API
    private String event;
    private String user;
    private String messageId;
    private TextContent textContent;
    private OptionsItem options;
    private ImageContent imageContent;
    private String channel;
    // client information
    private String schema;
    private String corpCode;
    private int statusCode;
    private String msgId;
    private String msgReturnTime;

    @Data
    @NoArgsConstructor
    public static class OptionsItem {
        private String noReflectBot;
        private String mobile;
        private String sourceId;
        private String threadOwnerId;
        private String nickname;
        private String result;
        private String field;
        private String inflow;
        private String referer;
        private List<Agreements> agreements;

        @Data
        @NoArgsConstructor
        public static class Agreements {
            // agreements
            private String cellphone;
            private String address;
        }
    }

    @Data
    @NoArgsConstructor
    public static class ImageContent {
        // imageContent API
        private String imageUrl;
        private String width;
        private String height;
    }

    @Data
    @NoArgsConstructor
    public static class TextContent {
        // textContent API
        private String text;
        private String inputType;
        private String code;
    }

    // AP용 msgSeq
    private String msgSeq;
}
