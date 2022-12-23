package aicc.omni.omniconnector.service.meta;

import aicc.omni.omniconnector.model.ApWsDto;
import aicc.omni.omniconnector.model.meta.MetaWhMsgDto;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MetaHttpInfoMsgBuilder {
    public static String plainInfoMsg(MetaWhMsgDto msgDto, String info) {
        String partA = "{\"recipient\":{\"id\":\"";
        String partId = msgDto.getEntry().get(0).getMessaging().get(0).getSender().getSenderId();
        String partB = "\"},\"message\":{\"text\":\"";
        String partText = info;
        String partC = "\"}}";

        String json = partA + partId + partB + partText + partC;

        log.info("▶▶▶ auto return message built : " + json);

        return json;


    }
    public static String apInfoMsg(ApWsDto apWsDto, String info) {
        String partA = "{\"recipient\":{\"id\":\"";
        String partId = apWsDto.getCustomer();
        String partB = "\"},\"message\":{\"text\":\"";
        String PartText  = info;
        String partC = "\"}}";

        String json = partA + partId + partB + PartText + partC;

        log.info("▶▶▶ auto return message built : " + json);

        return json;
    }
}
