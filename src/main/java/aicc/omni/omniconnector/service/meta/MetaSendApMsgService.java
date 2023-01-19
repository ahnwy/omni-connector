package aicc.omni.omniconnector.service.meta;

import aicc.omni.omniconnector.handler.WebsocketClientHandler;
import aicc.omni.omniconnector.model.meta.MetaWhMsgDto;
import aicc.omni.omniconnector.util.MetaTimeConvertUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static aicc.omni.omniconnector.handler.WebsocketClientHandler.reservedMsgMap;
import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class MetaSendApMsgService {
    @Autowired
    private WebsocketClientHandler websocketClientHandler;

    public String getMessageType(MetaWhMsgDto msgDto){
        //Message 여부 판단.
        Optional<Object> Message
                = ofNullable(msgDto.getEntry())
                .map(entry -> entry.get(0).getMessaging().get(0).getMessage());

        Optional<Object> Delivery
                = ofNullable(msgDto.getEntry())
                .map(entry -> entry.get(0).getMessaging().get(0).getDelivery());

        Optional<Object> Read
                = ofNullable(msgDto.getEntry())
                .map(entry -> entry.get(0).getMessaging().get(0).getRead());


        if (Message.isPresent()) {
            Optional<Object> Echo
                    = ofNullable(msgDto.getEntry())
                    .map(entry -> entry.get(0).getMessaging().get(0).getMessage().getIs_echo());
            if (Echo.isPresent() || Read.isPresent() || Delivery.isPresent()) {
                log.info("▶▶▶ META_WEBHOOK_MSG_TYPE_VERIFIED : {}", "ECHO");
                return "echo";
            } else {
                Optional<Object> Text
                        = ofNullable(msgDto.getEntry())
                        .map(entry -> entry.get(0).getMessaging().get(0).getMessage().getText());

                Optional<Object> Attachments
                        = ofNullable(msgDto.getEntry())
                        .map(entry -> entry.get(0).getMessaging().get(0).getMessage().getAttachments());

                MetaTimeConvertUtil.getTimestampToDate(msgDto); // 시간 변환

                msgDto.setPlatformId(msgDto.getEntry().get(0).getMessaging().get(0)
                        .getSender().getSenderId());
                log.info("▶▶▶ META_WEBHOOK_SET_PLATFORMID : {}", msgDto.getPlatformId());

                if (Text.isPresent()) {
                    return "text";
                } else if(Attachments.isPresent()){
                    String type = msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getAttachments().get(0).getType();
                    if(type.equals("image")){
                        return "image";
                    } else if(type.equals("file")){
                        return "file";
                    } else if(type.equals("audio")){
                        return "audio";
                    } else {
                        return "";
                    }
                } else {
                    return "";
                }
            }
        } else {
            return "";
        }
    }

    public String sendToAp(MetaWhMsgDto msgDto) throws Exception {
        msgDto.setMessageType(getMessageType(msgDto));
        if (!WebsocketClientHandler.whUserMap.containsKey(msgDto.getPlatformId())) {
            log.info("최초네....");
            WebsocketClientHandler.channelMap.put(msgDto.getPlatformId(), msgDto.getChannel());
        }

        if (msgDto.getMessageType().equals("text")) {
            reservedMsgMap.put(msgDto.getPlatformId(),MetaApMsgBuilder.apWebSocketTextMsg(msgDto));
            return MetaApMsgBuilder.apWebSocketInitMsg(msgDto);
        } else if (msgDto.getMessageType().equals("image")) {

        } else if (msgDto.getMessageType().equals("file")) {

        } else if (msgDto.getMessageType().equals("audio")) {

        } else {
            log.info("없넹...");
        }
        return "";
    }
}
