package aicc.omni.omniconnector.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionVO {
    private String errorCode;
    private String errorMessage;

    public static ExceptionVO of(String errorCode, String errorMessage) {
        return ExceptionVO.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}