package aicc.omni.omniconnector.model.origin;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OriginOutputMsgDto {
    private String question;
    private boolean fallback;
    private int score;
    private queryItem query;
    private answerItem answer;
    private List<messagesItem> messages;

    @Data
    public static class queryItem {
        private String annotation;
        private String keywords;
        private String entities;
        private String semantics;
        private String params;
    }
    @Data
    public static class answerItem {
        private String answer;
        private outputItem output;
        private String entities;
        private String scenario;
        private String auth;
        private String domain;
        private String id;
        private String semantics;
        private String category;
        private String detector;
        private String intent;

        @Data
        public static class outputItem{
            private String VOICE_NEXT;
        }
    }

    @Data
    public static class messagesItem {
        private String template;
        private String elements;
        private String type;
        private String orgfilename;
        private String align;
        private List<buttonsItem> buttons;
        private String cards;

        @Data
        public static class buttonsItem{
            private String text;
            private String value;
            private boolean loop;
        }
    }
}
