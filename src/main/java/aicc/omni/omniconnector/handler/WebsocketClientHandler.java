package aicc.omni.omniconnector.handler;

import aicc.omni.omniconnector.model.ApMsgSeqDto;
import aicc.omni.omniconnector.model.ApWsDto;
import aicc.omni.omniconnector.service.kakao.KakaoHttpMsgBuilder;
import aicc.omni.omniconnector.service.naver.NaverSocketMsgBuilder;
import aicc.omni.omniconnector.util.KakaoHttpUtil;
import aicc.omni.omniconnector.util.MetaHttpUtil;
import aicc.omni.omniconnector.util.NAVERHTTPUTIL;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
@ClientEndpoint
public class WebsocketClientHandler {

    // user 및 메세지키 저장용 MAP
    public static Map<String, String> whUserMap = new HashMap<String, String>();
    // user 및 메세지 저장용 MAP
    public static Map<String, String> reservedMsgMap = new HashMap<String, String>();

    //카카오 파일 이름 저장용. See KakaoHttpMsgBuilder
    public static Map<String, String> kakaoFileNameMap = new HashMap<>();

    static Session userSession = null;

    public WebsocketClientHandler WebSocketClientEndpoint() throws URISyntaxException {
        try{
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI("ws://localhost/omni/counsel/broadSocket"));
        } catch (Exception e){
            reConnect();
        }
        return null;
    }

    @OnOpen
    public void onOpen(Session session){
        System.out.println(session);
        WebsocketClientHandler.userSession = session;

        // websocket session 유지를 위한 heartbeat check
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    String data = "Ping";
                    ByteBuffer payload = ByteBuffer.wrap(data.getBytes());
                    if (userSession != null) {
                        userSession.getBasicRemote().sendPing(payload);
                        log.info("send heartbeat");
                    }
                } catch (IOException e) {
                    log.error("send heartbeat error", e);
                }
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(r, 5, 30, TimeUnit.SECONDS);
    }

    @OnMessage
    public void onMessage(String message) throws Exception {
        // json data
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, false);

        // AP 인증을 위한 DTO & AP 인증용 맵핑
        ApMsgSeqDto apMsgSeqDto = mapper.readValue(message, ApMsgSeqDto.class);

        // AP 사용 DTO & ap 전송용 맵핑
        ApWsDto apWsDto = mapper.readValue(message, ApWsDto.class);

        WebsocketServerEndpoint.send(message);

//        try {
//            // platformId, msgSeq 있고 최초인입 아닐 시 웹소켓으로 바로 전송(웹챗) // 아니면 아래 최초인입 분기 탐
//            if (chatPidSessMap.containsKey(chatSeqPidMap.get(apMsgSeqDto.getMsgSeq())) &&
//                    !"AUTH_VISIT".equals(apMsgSeqDto.getMsg())) {
//                WebsocketServerEndpoint.sendMessage(message);
//            } else {
//                // 최초 인입
//                if ("AUTH_VISIT".equals(apMsgSeqDto.getMsg())) {
//                    // 최초 인입 시 platformID, msgSeq 정보 저장 || webchat용 chatSeqPidMap 등록. 웹훅용 whUserMap 등록
//                    if (apMsgSeqDto.getPlatformID().contains("_º_")) {
//                        chatSeqPidMap.put(apMsgSeqDto.getMsgSeq(), apMsgSeqDto.getPlatformID());
//                    } else if (apMsgSeqDto.getPlatformID().contains("§")) {
//                        whUserMap.put(apMsgSeqDto.getPlatformID(), apMsgSeqDto.getMsgSeq());
//                    }
//
//                    // 웹챗(6)일 경우 오픈 메세지 전송
//                    if ("6".equals(apMsgSeqDto.getChannelSeq())) {
//                        WebsocketServerEndpoint.sendOpenMessage(message);
//                    } else {
//                        // 추가 인증 정보 AP 송신
//                        sendMessage(NaverSocketMsgBuilder.socketMsgParserApAddAuth(apMsgSeqDto));
//
//                        // 최초 인입 시 메세지가 존재할 때 : kakao, meta 저장된 메시지 전송. else if는 네이버 저장된 메시지 전송
//                        if (reservedMsgMap.containsKey(apMsgSeqDto.getPlatformID())) {
//                            String rPlatformID = apMsgSeqDto.getPlatformID();
//                            String reservedMsg = reservedMsgMap.get(rPlatformID);
//                            String newReservedMsg = reservedMsg.replace("\"msgSeq\" : null,",
//                                    "\"msgSeq\" : \"" + whUserMap.get(rPlatformID) + "\", ");
//                            log.info("☎☎☎☎☎WCE reservedMsg: " + newReservedMsg);
//                            sendMessage(newReservedMsg);
//                            reservedMsgMap.remove(rPlatformID);
//                        } else if (naverUserMsgMap.containsKey(apMsgSeqDto.getPlatformID())) {
//                            String msg = null;
//                            if (naverUserMsgMap.get(apMsgSeqDto.getPlatformID()) != null) {
//                                msg = naverUserMsgMap.get(apMsgSeqDto.getPlatformID()).replace("§", whUserMap.get(apMsgSeqDto.getPlatformID()));
//                            } else {
//                                log.info("☎☎☎☎☎WCE naverUserMsgMap's MSG IS NULL ");
//                            }
//
//                            // msg null check
//                            if (msg != null && !"{}".equals(msg)) {
//                                sendMessage(msg);
//                            } else {
//                                log.info("☎☎☎☎☎WCE MSG IS NULL ");
//                            }
//
//                            // 전송 후 naverUserMsgMap map 데이터 제거(일회성 정보이므로)
//                            naverUserMsgMap.remove(apMsgSeqDto.getPlatformID());
//                        }
//                    }
//                    // 최초 인입 외 이벤트
//                } else if ("상담".equals(apWsDto.getStatus()) || ("종료".equals(apWsDto.getStatus()))
//                        || ("요청".equals(apWsDto.getStatus()))) {
//                    // 웹채팅(6)이면 웹소켓 메세지 전송,
//                    if ("6".equals(apWsDto.getChannelSeq())) {
//                        WebsocketServerEndpoint.sendMessage(message);
//                    } else if (apWsDto.getChannelSeq() == null) {
//                        // 웹훅에 맵핑되는 메세지키가 있으면
//                        if (whUserMap.containsValue(apWsDto.getMsgSeq())) {
//                            // 웹훅으로 메세지 전송
//                            String msg = ApWsMsgBuilder.apMsgParser(apWsDto);
//                            String channelSeq = apWsDto.getChannelSeq();
//
//                            if (msg != null) {
//                                switch (apWsDto.getChannelSeq()) {
//                                    case "1":
//                                        switch (apWsDto.getMsgContentType()) {
//                                            case "text":
//                                                String path = "/chat_write2";
//                                                KakaoHttpUtil.sendMsg(msg, path);
//
//                                                if (apWsDto.getStatus().equals("종료")) {
//                                                    path = "/chat_end";
//                                                    String kakaoEndMsg = KakaoHttpMsgBuilder.kakaoHttpEndMsg(apWsDto);
//                                                    assert kakaoEndMsg != null;
//                                                    Thread.sleep(50); // 종료 메시지 전송과 시간차가 없으면 마지막 메시지 전송시 전달 누락
//                                                    KakaoHttpUtil.sendMsg(kakaoEndMsg, path);
//                                                }
//                                                break;
//                                            case "image":
//                                            case "file":
//                                                break;
//                                        }
//                                        break;
//                                    case "2":
//                                        NAVERHTTPUTIL.sendApi(msg);
//                                        break;
//                                    case "3":
//                                    case "4":
//                                        switch (apWsDto.getMsgContentType()) {
//                                            case "text":
//                                            case "image":
//                                            case "file":
//                                                MetaHttpUtil.sendMsg(msg, channelSeq);
//                                                break;
//                                            default:
//                                                break;
//                                        }
//                                        break;
//                                }
//                                log.info("☎☎☎☎☎WCE HTTP SEND SUCCESS");
//                            }
//                            // 메세지가 종료 메세지이면 whUserMap의 해당 값 삭제
//                            if ("종료".equals(apWsDto.getStatus())) {
//                                whUserMap.values().removeAll(Collections.singleton(apWsDto.getMsgSeq()));
//                                log.info("☎☎☎☎☎WCE whUserMap에서 세션 삭제!!!!! ");
//                            } else {
//                                log.info("☎☎☎☎☎WCE 종료 메세지가 아닙니다!!!!! " + msg);
//                            }
//                        } else {
//                            // 웹훅에 맵핑되는 메세지키가 없으면 웹챗 전송
//                            WebsocketServerEndpoint.sendMessage(message);
//                        }
//                    } else {
//                        log.info("☎☎☎☎☎ NOT DEFINE ☎☎☎☎☎");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @OnClose
    public void onClose() throws URISyntaxException {
        WebsocketClientHandler.userSession = null;

        // reconnect 호출
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    reConnect();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Timer timer = new Timer("ReConnect");
        timer.schedule(task, 3000L);

    }

    public static void sendMessage(String message) throws URISyntaxException {
        if(userSession == null){
            reConnect();
        }
        log.info(userSession);
        try {
            synchronized (userSession) {
                userSession.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            reConnect();
            e.printStackTrace();
        }
    }

    public static void reConnect() throws URISyntaxException {
//        new WebSocketClientEndpoint(new URI("wss://test.hiqri.ai/omni/counsel/broadsocket"));
        new WebsocketClientHandler().WebSocketClientEndpoint();

    }
}
