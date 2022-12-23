package aicc.omni.omniconnector.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.text.SimpleDateFormat;

@Log4j2
@Data
@NoArgsConstructor
public class ApMsgSeqDto {
    // currentTime
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
    private String currentTime = sdf.format(System.currentTimeMillis());

    // AP용 msgSeq
    private String msg;
    private String msgSeq;
    private String platformID;
    private String userName;
    private String userPhone;
    private String userEmail;
    private String channelSeq;
    private String customerCode;
    private int statusCode;
    private String msgId;
    private String msgReturnTime;


}

