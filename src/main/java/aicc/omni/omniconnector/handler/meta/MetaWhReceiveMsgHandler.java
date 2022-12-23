package aicc.omni.omniconnector.handler.meta;

import aicc.omni.omniconnector.handler.WebsocketClientHandler;
import aicc.omni.omniconnector.model.meta.MetaWhMsgDto;
import aicc.omni.omniconnector.service.meta.MetaApMsgBuilder;
import aicc.omni.omniconnector.service.meta.MetaHttpInfoMsgBuilder;
import aicc.omni.omniconnector.service.meta.MetaWhReceiveMsgClassifier;
import aicc.omni.omniconnector.util.MetaHttpUtil;
import lombok.extern.log4j.Log4j2;

import java.net.URISyntaxException;

@Log4j2
public class MetaWhReceiveMsgHandler {

    public static void sendToAp(MetaWhMsgDto msgDto) throws Exception {
        String channelId = msgDto.getChannel();
        MetaWhReceiveMsgClassifier.getMessageType(msgDto, channelId);

        switch (msgDto.getProcessing()) {
            case "Y":
                switch (msgDto.getMessageType()) {
                    case "text":
                    case "image":
                        //페이스북에서 이미지를 한번에 보낸 경우 분할 전송
                        if (msgDto.getImageCount() != null) {
                            int i = 0;
                            int imageCount = Integer.parseInt(msgDto.getImageCount());
                            while (i < imageCount) {
                                String apMsg = MetaApMsgBuilder.apWebSocketImageMsg(msgDto, i);
                                ApMsgSend(msgDto, apMsg); // 메시지 전송
                                i++;
                            }
                            // 단순 1회성 전송
                        } else {
                            String apMsg = MetaApMsgBuilder.apWebSocketTextMsg(msgDto);
                            ApMsgSend(msgDto, apMsg);
                            break;
                        }
                        break;
                }
                break;

            case "E":
                String channelSeq = msgDto.getChannel();
                String info;

                switch (msgDto.getMessageType()) {
                    case "text":
                        info = "\uD83D\uDE02 한번에 1,000자 이상의 텍스트는 정책상 전송되지 않습니다.";
                        break;
                    case "image":
                        info = "\uD83D\uDE02 10MB 이상의 파일은 정책상 전송되지 않습니다.";
                        break;
                    case "file":
                    case "audio":
                        info = "\uD83D\uDE02 요청하신 형식의 파일은 정책상 지원하지 않습니다. 텍스트 또는 이미지를 전송해주세요.";
                    break;
                    case "location":
                        info = "\uD83D\uDE02 facebook(instagram) messenger의 위치정보 전송 기능은 정책상 지원하지 않고 있습니다.";
                        break;
                    default:
                        info = "";
                        break;
                }
                String json = MetaHttpInfoMsgBuilder.plainInfoMsg(msgDto, info);
                String apMsg = MetaApMsgBuilder.apWebSocketTextMsg(msgDto);
                MetaHttpUtil.sendMsg(json, channelSeq);
                ApMsgSend(msgDto, apMsg);

            default:
                log.info("");
                break;
        }
    }
    private static void ApMsgSend(MetaWhMsgDto msgDto, String apMsg) throws URISyntaxException {
        if (msgDto.getKnockYn().equals("Y")){ //첫 오픈 메시지인 경우 가공된 msg를 보내지 않고 저장
            WebsocketClientHandler.reservedMsgMap.put(msgDto.getPlatformId(), apMsg);
        } else {
            WebsocketClientHandler.sendMessage(apMsg);
        }
    }
}
