package aicc.omni.omniconnector.model.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MetaWhMsgDto {
    private String object;
    private String msgSource; // not original, added for convenience. customer/ap/dms
    private String messageType; // not original, added for convenience
    private String formattedDate; // not original, added for convenience
    private String channel; // not original, added for convenience
    private String fileName; // not original, added for convenience
    private String fileSize; // not original, added for convenience
    private String imageCount; // not original, added for convenience
    private String msgWrtId; // not original, added for convenience
    private String platformId; // not original, added for convenience -- 플랫폼 아이디
    private String knockYn; // not original, added for convenience -- 첫메시지(visit 여부)
    private String msgSeq; // not original, added for convenience
    private String processing; // Y - 전송, N - 무시 , E - 에러 텍스트 메시지 전송
    private List<EntryList> entry;

    @Data
    public static class EntryList {
        @JsonProperty("id")
        private String pageId;
        private String time;
        private List<MessagingList> messaging;

        @Data
        public static class MessagingList {
            private SenderItem sender;
            private RecipientItem recipient;
            private String timestamp;
            private MessageItem message;
            private ReadItem read;
            private OptinItem optin;
            private Account_linkingItem account_linking;
            private PostbackItem postback;
            private DeliveryItem delivery;
//            private Pass_thread_controlItem pass_thread_control; //thread control 관련 일단 제외
//            private Take_thread_controlItem take_thread_control;
//            private Request_thread_controlItem request_thread_control;
//            private App_rolesItem app_roles;

            @Data
            public static class SenderItem {
                @JsonProperty("id")
                private String senderId;
            }
            @Data
            public static class RecipientItem {
                @JsonProperty("id")
                private String recipientId;
            }
            @Data
            public static class MessageItem {
                private String mid;
                private String seq;
                private String text;
                private String is_echo;
                private List<AttachmentsList> attachments;
                private List<QuickReplyList> quick_replies;
                private RefferalItem referral;
                private ReactionItem reaction;

                @Data
                public static class AttachmentsList {
                    private String title;
                    private String url;
                    private String type;
                    private String fileSize; // not original, added for convenience

                    private PayloadItem payload;

                    @Data
                    public static class PayloadItem {
                        private String url;
                        private String title;
                        private String sticker_id;
                        private String template_type;
                        private CoordinatesItem coordinates;
                        private ProductItem product;
                        private List<ButtonsList> buttons;

                        @Data
                        public static class ButtonsList {
                            private String type;
                            private String title;
                            private String payload;
                            private String url;
                        }

                        @Data
                        public static class CoordinatesItem {
                            private String lat;
                            @JsonProperty("long")
                            private String lng;
                        }

                        @Data
                        public static class ProductItem {
                            private List<ElementsList> elements;

                            @Data
                            public static class ElementsList {
                                @JsonProperty("id")
                                private String elements_id;
                                private String retailer_id;
                                private String image_url;
                                private String title;
                                private String subtitle;
                            }
                        }
                    }
                }

                @Data
                public static class RefferalItem {
                    private String ref;
                }
            }
            @Data
            public static class ReactionItem {
                private String mid;
                private String action;
                private String emoji;
                private String reaction;
            }
            @Data
            public static class QuickReplyList {
                private String content_type;
                private String title;
                private String payload;
            }
        }
        @Data
        public static class ReadItem {
            private String watermark;
            private String seq;
        }
        @Data
        public static class OptinItem {
            private String ref;
            private String user_ref;
        }
        @Data
        public static class Account_linkingItem {
            private String status;
            private String authorization_code;
        }
        @Data
        public static class PostbackItem {
            private String payload;
        }
        @Data
        public static class DeliveryItem {
            private List<MidsList> midsList;
            private String watermark;

            @Data
            public static class MidsList {
                private String mids;
            }
        }
//        @Data
//        public static class Pass_thread_controlItem {
//            private String previous_owner_app_id;
//            private String new_owner_app_id;
//            private String metadata;
//        }
//        @Data
//        public static class Take_thread_controlItem {
//            private String previous_owner_app_id;
//            private String new_owner_app_id;
//            private String metadata;
//        }
//        @Data
//        public static class Request_thread_controlItem {
//            private String request_owner_app_id;
//            private String metadata;
//        }
//        @Data
//        public static class App_rolesItem {
//            private String app_id;
//            private String metadata;
//        }
    }
}