package aicc.omni.omniconnector.model;

import lombok.Data;

@Data
public class User {
    private String key; //ap에서 받은 방 세션정보(없으면 발급요청 필요)
    private String plateformId; //플랫폼 별 사용자 고유 아이디 정보
    private int channelId; //채널 아이디
}
