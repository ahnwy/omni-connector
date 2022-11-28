package aicc.omni.omniconnector.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewOpenDto {
    private String schema; //DB스키마
    private String platformID; //채널별 플랫폼 아이디
    private String openFlag; // 최초 오픈 flag("newEvent" or "")
    private String channelSeq; // 채널별 시퀀스(1-카카오톡,2-네이버톡톡,3-페이스북,4-인스타그램,5-이메일,6-웹챗)
    private String msgId; // 메세지 아이디(uuid)
    private String corpCode; // 업체코드
    private String userName; // 유저명
    private String userPhone; // 유저번호
    private String userEmail; // 유저 이메일
}
