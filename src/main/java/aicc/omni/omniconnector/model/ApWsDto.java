package aicc.omni.omniconnector.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
@NoArgsConstructor
public class ApWsDto {

//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS") ;

    private int statusCode;
    private String msgId;
    private String msgReturnTime;

    // mode
    private String mode;

    // client schema information
    private String schema;

    // clinet corpCord information
    private String corpCode;

    // 인입채널 아이콘
    private String channelSeq; // 1.카카오 2.네이버 3.페이스북 4.인스타그램 5.이메일 6.웹채팅

    // user
    private String customer;

//    private String idleDate = sdf.format(System.currentTimeMillis()); // 상담방 생성시각
//    private String endDate = sdf.format(System.currentTimeMillis());  // 상담 종료 시각

    // 현재 상태
    private String status; // visit,대기,상담,종료


    private String msg;            // 상담 메세지
    private String msgWrtId;       // 메세지 작성자 코드 1. 고객, 2. 상담사, 3.봇
    private String msgSeq;         // 메세지 ID
//    private String msgWrtTime = sdf.format(System.currentTimeMillis()); // 메세지 작성 시간
    private String msgContentType; // 메세지 종류

    private String filePath;       // 파일(이미지) 경로
    private String fileName;       // 파일(이미지) 이름
    private String fileSize;       // 파일(이미지) 사이즈

    private String isOpen;         // 상담창 오픈여부

    private String requestDate;
    private String counselId;
    private String uploadSerial; // 카카오 CND(인포뱅크) 전송시 저장값; kakaoMsgBuilder에서 저장)
    private String uploadUrl; // 카카오 미디어 전송시 서버에 올리고 리턴받은 값 사용



    }



