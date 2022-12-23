package aicc.omni.omniconnector.handler.meta;

import aicc.omni.omniconnector.model.ApWsDto;
import aicc.omni.omniconnector.service.meta.MetaHttpInfoMsgBuilder;
import aicc.omni.omniconnector.service.meta.MetaHttpMsgBuilder;
import aicc.omni.omniconnector.util.MetaHttpUtil;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MetaHttpSendMsgHandler {

    public static void HttpSerializer(ApWsDto apWsDto) throws Exception {
        String channelSeq = apWsDto.getChannelSeq();
        switch ((apWsDto.getMode())) {
            case "total":
                log.info("▶▶▶ AP_SENT_CONTENTS_{}", apWsDto.getMsgContentType());
                log.info(apWsDto.toString());


//                MetaHttpMsgDto msgDto = new MetaHttpMsgDto();
//                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, false);

                switch ((apWsDto.getMsgContentType())) {
                    case "text":
                        String httpMsg = MetaHttpMsgBuilder.metaHttpTextMsg(apWsDto);

                        log.info(httpMsg);
                        MetaHttpUtil.sendMsg(channelSeq, httpMsg);
                        break;
                    case "file":
                        break;

                    default:
                        break;
                }
                       break;
                case "update":
                    log.info("▶▶▶ AP_SENT_INFORMATION_{}", apWsDto.getStatus());

                    switch ((apWsDto.getStatus())) {
                        case "상담":
                            String info = "상담사가 배정되었습니다. 상담내용은 상담품질관리를 위해 저장됩니다. 산업안전보건법에 따라 상담원 보호를 위한 조치가 시행되고 있으니 욕설, 폭언 등의 언어폭력을 하지 말아주세요.";
                            String httpMsg = MetaHttpInfoMsgBuilder.apInfoMsg(apWsDto, info);
                            log.info(httpMsg);
                            MetaHttpUtil.sendMsg(channelSeq, httpMsg);
                            break;

                        case "종료":
                            info = "상담이 종료되었습니다. 다시 상담을 시작하려면 메시지를 입력해 주세요.";
                            httpMsg = MetaHttpInfoMsgBuilder.apInfoMsg(apWsDto, info);
                            log.info(httpMsg);
                            MetaHttpUtil.sendMsg(channelSeq, httpMsg);
                            break;
                    }
            default:
                    break;
        }
    }
}
