package aicc.omni.omniconnector.model.origin;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OriginInputMsgDto {
    private String domain;
    private String text;
    private String channel;
    private String user;
    private String api_key;
}
