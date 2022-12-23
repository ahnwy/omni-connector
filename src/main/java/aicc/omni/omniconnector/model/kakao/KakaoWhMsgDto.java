package aicc.omni.omniconnector.model.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoWhMsgDto {

    private String pf_id; //플러스 친구 ID
    private String user_key; // 고객 key
    private String type; // 고객이 전송한 메시지 종류(text, photo, video, audio)
    private String content; // type이 text가 아닐 시 url 전송

    private AttachmentItem attachment;

    @Data
    public static class AttachmentItem {
        @JsonProperty("url")
        private String attUrl;// 1000자 초과 메시지의 경우 4000자까지 content 필드로 전달하고 헤당 필드로 파일  url 전송
    }
    private String extra; // 사용자 상호작용 fallback

    private String app_user_key; // 고객의 고유ID - 없으면 null

    private ReferenceItem Reference;
    @Data
    public static class ReferenceItem {
        @JsonProperty("extra")
        private String refExtra; // (reference) 버튼을 통해 전달된 메타정보
        @JsonProperty("text")
        private String refText; // [인포뱅크에서 삭제예정] (reference) 버튼을 통해 전달된 메타정보
        @JsonProperty("lastText")
        private String refLastText; // [인포뱅크에서 삭제예정] (reference) 버튼을 통하지 않고 재상담시 가장 최근에 버튼을 통해 전달된 메타정보
        @JsonProperty("lastTextDate")
        private String refLastTextDate; // [인포뱅크에서 삭제예정] (reference) lastText 생성 시각
    }

    private Last_referenceItem last_reference;
    @Data
    public static class Last_referenceItem {
        @JsonProperty("extra")
        private String lastExtra; // (last_reference) 버튼을 통해 전달된 메타 정보
        @JsonProperty("bot")
        private String lastBot; // (last_reference) 상담을 어떻게 시작했는지
        @JsonProperty("bot_event")
        private String lastBot_event; // (last_reference)  봇으로 상담 시작 시, 봇 클릭 이벤트 값
        @JsonProperty("created_at")
        private String lastCreated_at; // (last_reference) 마지막 메타정보 생성일
    }
    private String support_button; // chat_write_API의 links 사용가능 여부

    private String session_id; // 종료된 세션 ID

    private String serial_number; //요청 일련번호
    private String request_type; // 요청 타입 “activate“, “deactivate“, “write“, “upload“(image/file), “block“, “unblock“, “end“, “endwithbot”
    private String code; // 처리결과, 0 - 정상
    private String message; // 오류 발생시 오류 내용
    private String image; // 업로드된 이미지 경로(image_upload 요청의 결과일 경우만 존재)
    private String file;  // 업로드된 파일 경로(file_upload 요청의 결과일 경우만 존재)
    private String file_name; // 업로드된 파일 경로(file_upload & file_type=file 경우만 존재)
    private String file_size; // 업로드된 파일 사이즈 (file_upload & file_type=file 경우만 존재)

    private String uuid; //(header) x-ib-message-id에서 가져옴
    private String retry; //(header) x-ib-retry에서 가져옴
    private String path; // HttpServletRequest에서 가져옴

    //메시지 가공용 임의값

    private String msgSource; // not original, added for convenience. customer/ap/dms
    private String messageType; // not original, added for convenience
    private String formattedDate; // not original, added for convenience
    private String channel; // not original, added for convenience
    private String fileName; // not original, added for convenience
    private String fileUrl; // not original, added for convenience
    private int fileSize; // not original, added for convenience
    private String imageCount; // not original, added for convenience
    private String msgWrtId; // not original, added for convenience
    private String platformId; // not original, added for convenience -- 플랫폼 아이디
    private String knockYn; // not original, added for convenience -- 첫메시지(visit 여부)
    private int letterCount; // not original, added for convenience
    private String processing; // Y, N ,E
    private String msgSeq; // not original, added for convenience
    private String originalType; // 고객에게 오류 리턴시 사용하기 위해 원래 메시지 타입 저장

}