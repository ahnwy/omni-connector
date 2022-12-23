package aicc.omni.omniconnector.handler.kakao;

import aicc.omni.omniconnector.handler.WebsocketClientHandler;
import aicc.omni.omniconnector.model.kakao.KakaoWhMsgDto;
import aicc.omni.omniconnector.service.kakao.KakaoApMsgBuilder;
import aicc.omni.omniconnector.service.kakao.KakaoHttpMsgBuilder;
import aicc.omni.omniconnector.service.kakao.KakaoWhReceiveMsgClassifier;
import aicc.omni.omniconnector.util.KakaoHttpUtil;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;

@Log4j2
public class KakaoWhReceiveMsgHandler {

    public static void sendToAp(KakaoWhMsgDto whMsgDto) throws Exception {
        String channelId = whMsgDto.getChannel();
        KakaoWhReceiveMsgClassifier.getMessageType(whMsgDto, channelId);
        String json;

        switch (whMsgDto.getProcessing()){
            case "Y" :
                if(Objects.equals(whMsgDto.getType(), "text")){
                    String apMsg = KakaoApMsgBuilder.apWebSocketTextMsg(whMsgDto);
                    ApMsgSend(whMsgDto, apMsg);
                }  else if (Objects.equals(whMsgDto.getType(), "photo")) {
                    String apMsg = KakaoApMsgBuilder.apWebSocketImageMsg(whMsgDto);
                    ApMsgSend(whMsgDto, apMsg);
                }
                break;
            case "U" :
                json = KakaoHttpMsgBuilder.kakaoHttpSendUploadedImg(whMsgDto);
                KakaoHttpUtil.sendMsg(json, "/chat_write2");
                break;
            case "W" : //경고 메시지(MW 자체에서 경고메시지 고객에게 return, AP에는 고객이 잘못된 양식을 전달시도하였음을 통지)
                String info = null;
                String path = "/chat_write2";

                switch (whMsgDto.getOriginalType()) {
                    case "text" :
                        info = "1000자가 넘는 메시지는 전송되지 않습니다.";
                        json = KakaoHttpMsgBuilder.kakaoHttpInfoMsg(whMsgDto, info);
                        KakaoHttpUtil.sendMsg(json, path);
                        break;
                    case "photo" :
                        int fileSizeRawMb = whMsgDto.getFileSize() / (1024 * 1024);
                        info = "10MB가 넘는 사진은 전송되지 않습니다. (현재 입력한 파일 사이즈 " + fileSizeRawMb + " MB)";
                        json = KakaoHttpMsgBuilder.kakaoHttpInfoMsg(whMsgDto, info);
                        KakaoHttpUtil.sendMsg(json, path);
                        break;
                    default:
                        info = "해당 파일 형식은 지원하지 않습니다. 텍스트나 이미지를 입력해주세요.";
                        json = KakaoHttpMsgBuilder.kakaoHttpInfoMsg(whMsgDto, info);
                        KakaoHttpUtil.sendMsg(json, path);
                        break;
                }
                String apMsg = KakaoApMsgBuilder.apWebSocketTextMsg(whMsgDto);
                ApMsgSend(whMsgDto, apMsg);
                break;
            case "E" :
                apMsg = KakaoApMsgBuilder.apWebSocketEndMsg(whMsgDto);
                ApMsgSend(whMsgDto, apMsg);
                break;
            case "N" :
                break;
        }
    }
    private static void ApMsgSend(KakaoWhMsgDto whMsgDto, String apMsg) throws Exception {
        if (Objects.equals(whMsgDto.getKnockYn(), "Y")){
            //첫 오픈 메시지인 경우 가공된 msg를 보내지 않고 저장
            WebsocketClientHandler.reservedMsgMap.put(whMsgDto.getPlatformId(), apMsg);
            // AP에 userssion 정보 전달
            String initMsg = KakaoApMsgBuilder.apWebSocketInitMsg(whMsgDto);
            WebsocketClientHandler.sendMessage(initMsg);

            //고객에게 상담사의 배정을 기다리고 있음을 알림
            String info = "담당자가 상담을 준비중입니다. 잠시만 기다려 주세요.";
            String json = KakaoHttpMsgBuilder.kakaoHttpInfoMsg(whMsgDto, info);

            String path = "/chat_write2";
            KakaoHttpUtil.sendMsg(json, path);
        } else {
            WebsocketClientHandler.sendMessage(apMsg);
        }
    }
}
