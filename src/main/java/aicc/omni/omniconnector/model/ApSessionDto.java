package aicc.omni.omniconnector.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
@NoArgsConstructor
public class ApSessionDto {
    // Socket Server Session 관련 변수
    private String schema;
    private String openFlag;
    private String platformID;
    private String userName;
    private String userPhone;
    private String userEmail;
    private String channelSeq;
    private String sessionId;
    private String msgSeq;
    private String corpCode;
//    private int statusCode;
    private String msgId;
    private String msgReturnTime;
}
