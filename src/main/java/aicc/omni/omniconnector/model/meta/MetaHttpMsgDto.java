package aicc.omni.omniconnector.model.meta;

import lombok.Data;


@Data
public class MetaHttpMsgDto {
    public RecipientItem recipient;
    public MessageItem message;
    public String messaging_type;
    public String tag;

    @Data
    public static class RecipientItem {
        public String id;
    }
    @Data
    public static class MessageItem {
        public String text;
        public AttachmentItem attachment;

        @Data
        public static class AttachmentItem {
            public String type;
            public PayloadItem payload;

            @Data
            public static class PayloadItem {
                public String template_type;
                public String text;
                public String is_reusable;
                public ButtonsList buttons;

                @Data
                public static class ButtonsList {
                    String type;
                    String title;
                    String url;
                    String payload;
                    String type2;
                    String title2;
                    String url2;
                    String payload2;
                }
            }
        }
    }
    public MetaHttpMsgDto(String id,
                          String text,
                          String type,
                          String is_reusable,
                          String messaging_type,
                          String tag
    ) {
        recipient = new RecipientItem();
        recipient.id = id;
        message = new MessageItem();
        message.text = text;
        message.attachment = new MessageItem.AttachmentItem();
        message.attachment.type = type;
        message.attachment.payload = new MessageItem.AttachmentItem.PayloadItem();
        message.attachment.payload.is_reusable = is_reusable;
        this.messaging_type = messaging_type;
        this.tag = tag;

    }
}



