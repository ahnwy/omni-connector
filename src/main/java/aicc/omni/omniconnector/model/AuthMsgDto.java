package aicc.omni.omniconnector.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthMsgDto {
    private String msg; //인증여부(AUTH_VISIT, VISIT)
    private String startMsg; //오픈 후 인사말
    private String customerCode; // 고객코드
    private String channelSeq; // 채널별 시퀀스(1-카카오톡,2-네이버톡톡,3-페이스북,4-인스타그램,5-이메일,6-웹챗)
    private String msgId; // 메세지 아이디(uuid)
    private String msgSeq; // 메세지 아이디(uuid)
    private String platformID; //채널별 플랫폼 아이디
    private String statusCode; // 수신상태값(0:정상수신, 102:파라미터 오류, 104: DB저장오류, 106:타업무중, 999:시스템오류)
    private String userName; // 유저명
    private String userPhone; // 유저번호
    private String userEmail; // 유저 이메일
}
