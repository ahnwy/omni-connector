package aicc.omni.omniconnector.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.text.SimpleDateFormat;

@Log4j2
@Data
@NoArgsConstructor
public class ApWsDto {
    private String schema;
    private String openFlag;
    private String channelSeq;
    private String userPhone;
    private String msgId;
    private String userEmail;
    private String platformID;
    private String userName;
    private String corpCode;
    private String msg;
    private String startMsg;
    private String customerCode;
    private String msgSeq;
    private String statusCode;
    private String mode;
    private String status;
    private String idleDate;
    private String requestDate;
    private String startDate;
    private String endDate;
    private String counselId;
    private String msgContentType;
    private String customer;
    private String mobilePhone;
    private String email;
    private String msgWrtTime;

    private String filePath;       // 파일(이미지) 경로
    private String fileName;       // 파일(이미지) 이름
    private String fileSize;       // 파일(이미지) 사이즈

    private String isOpen;         // 상담창 오픈여부

    private String uploadSerial; // 카카오 CND(인포뱅크) 전송시 저장값; kakaoMsgBuilder에서 저장)
    private String uploadUrl; // 카카오 미디어 전송시 서버에 올리고 리턴받은 값 사용

    // currentTime
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private String currentTime = sdf.format(System.currentTimeMillis());

    private String msgReturnTime;
}



