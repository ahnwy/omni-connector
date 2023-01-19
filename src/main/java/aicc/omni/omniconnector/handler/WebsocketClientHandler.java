package aicc.omni.omniconnector.handler;

import aicc.omni.omniconnector.config.ServerEndpointConfigurator;
import aicc.omni.omniconnector.model.ap.ApWsDto;
import aicc.omni.omniconnector.service.common.ApWsMsgBuilder;
import aicc.omni.omniconnector.service.naver.NaverApMsgBuilder;
import aicc.omni.omniconnector.service.common.CommonService;
import aicc.omni.omniconnector.service.common.ChannelType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static aicc.omni.omniconnector.config.WebSocketConfig.getBean;

@Log4j2
@Service
@ClientEndpoint
public class WebsocketClientHandler {

    // user 및 메세지키 저장용 MAP
    public static Map<String, String> whUserMap = new HashMap<String, String>();
    // user 및 채널시퀀스 저장용 MAP
    public static Map<String, String> channelMap = new HashMap<String, String>();
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
                        log.info("send heartbeat" + userSession.getOpenSessions() + userSession.isOpen());
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
        System.out.println("client message recive");
        ApWsMsgBuilder apWsMsgBuilder = (ApWsMsgBuilder) getBean("apWsMsgBuilder");
        String msg = "";
        // json data
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, false);

        // 여러 AP메세지 포맷을 하나의 DTO로 관리
        ApWsDto apWsDto = mapper.readValue(message, ApWsDto.class);

        log.info(">>>> "+message);
        log.info(">>>> "+apWsDto);

        // visit일때 아무 이벤트를 발생시키지 않음
        if("visit".equals(apWsDto.getMsg())){

        } else if("AUTH_VISIT".equals(apWsDto.getMsg())){
            // AUTH_VISIT이면 신규 접속이라 판단하여 맵으로 저장
            whUserMap.put(apWsDto.getPlatformID(), apWsDto.getMsgSeq());
            channelMap.put(apWsDto.getMsgSeq(), apWsDto.getChannelSeq());
            // 웹챗일 경우 소캣전송해야하므로 소캣 send 호출
            if(apWsDto.getChannelSeq().equals(ChannelType.WEBCHAT)){
                WebsocketServerEndpoint.send(message);
            } else {
                // 웹챗이 아닌경우 데이터를 저장 해야하기 때문에 고정데이터를 만들어서 AP로 재전송
                sendMessage(NaverApMsgBuilder.socketMsgParserApAddAuth(apWsDto));
                if (reservedMsgMap.containsKey(apWsDto.getPlatformID())) {
                    String rPlatformID = apWsDto.getPlatformID();
                    String reservedMsg = reservedMsgMap.get(rPlatformID);
                    String newReservedMsg = reservedMsg.replace("\"msgSeq\" : null,",
                            "\"msgSeq\" : \"" + whUserMap.get(rPlatformID) + "\", ");
                    log.info("☎☎☎☎☎WCE reservedMsg: " + newReservedMsg);
                    sendMessage(newReservedMsg);
                    reservedMsgMap.remove(rPlatformID);
                }
                apWsDto.setMsg(apWsDto.getStartMsg());
                apWsDto.setMsgContentType("text");
                msg = apWsMsgBuilder.apMsgParser(apWsDto);
                if(msg != null || !msg.equals("")){
                    CommonService.sendApi(msg, apWsDto.getChannelSeq());
                }
            }
        } else {
            if(channelMap.get(apWsDto.getMsgSeq()).equals(ChannelType.WEBCHAT)) {
                WebsocketServerEndpoint.send(message);
            } else {
                if(apWsDto.getMsg() != null && apWsDto.getMsgContentType() != null && !apWsDto.getMsgWrtId().equals("1")){
                    msg = apWsMsgBuilder.apMsgParser(apWsDto);
                    if(msg != null || !msg.equals("")){
                        CommonService.sendApi(msg, apWsDto.getChannelSeq());
                    }
                }
            }
            // 메세지가 종료 메세지이면 whUserMap의 해당 값 삭제
            if ("종료".equals(apWsDto.getStatus())) {
                whUserMap.values().removeAll(Collections.singleton(apWsDto.getMsgSeq()));
            }
        }
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

    public void sendMessage(String message) throws URISyntaxException {
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

    public void reConnect() throws URISyntaxException {
//        new WebSocketClientEndpoint(new URI("wss://test.hiqri.ai/omni/counsel/broadsocket"));
        new WebsocketClientHandler().WebSocketClientEndpoint();

    }
}
