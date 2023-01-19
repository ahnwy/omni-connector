package aicc.omni.omniconnector.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RestController
public class WebhookHandler {
    @Autowired
    private ChatHandler chatHandler;

    @PostMapping("/naver")
    public ResponseEntity<Object> nvWebhook(@RequestBody String message) throws Exception {
        System.out.println(">>>>>>>>> "+message);
        ResponseEntity<Object> response = null;
        response = new ResponseEntity<>("EVENT_RECEIVED", HttpStatus.OK);

        String channelId = "2";
        chatHandler.sendToAp(message, channelId);
        return response;
    }

    @GetMapping("/fb")
    public ResponseEntity<Object> fbWebhook(@RequestParam(name = "hub.verify_token") String token,
                                            @RequestParam(name = "hub.challenge") String challenge,
                                            @RequestParam(name = "hub.mode") String mode) {

        String VERIFY_TOKEN = "ktcs1234";
        ResponseEntity<Object> response = null;
        if (mode != null && token != null) {
            if (mode.equals("subscribe") && token.equals(VERIFY_TOKEN)) {
                response = new ResponseEntity<>(challenge, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        return response;
    }

    //1.1. META VERIFIER
    @GetMapping("/ig")
    public ResponseEntity<Object> igWebhook(@RequestParam(name = "hub.verify_token") String token,
                                            @RequestParam(name = "hub.challenge") String challenge,
                                            @RequestParam(name = "hub.mode") String mode) {

        String VERIFY_TOKEN = "ktcs1234";
        ResponseEntity<Object> response = null;

        if (mode != null && token != null) {
            if (mode.equals("subscribe") && token.equals(VERIFY_TOKEN)) {
                response = new ResponseEntity<>(challenge, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        return response;
    }

    @PostMapping("/fb")
    public ResponseEntity<Object> fbWebhook(@RequestBody String message) throws Exception {
        System.out.println(message);

        ResponseEntity<Object> response = null;
        response = new ResponseEntity<>("EVENT_RECEIVED", HttpStatus.OK);

        try{
            String channelId = "3";
            chatHandler.sendToAp(message, channelId);
            return response;
        }catch (Exception e){
            log.info(e.getMessage());
            return response;
        }
    }

    //1.2.2. META Receiver - IG
    @PostMapping("/ig")
    public ResponseEntity<Object> igWebhook(@RequestBody String message) throws Exception {

//        log.info("▶▶▶▶ IG_WEBHOOK_RECEIVED ◀◀◀◀: " + message.toString());

        ResponseEntity<Object> response = null;
        response = new ResponseEntity<>("EVENT_RECEIVED", HttpStatus.OK);

        int channelId = 4;
//        WebhookParsingService.parse(message, channelId);

        return response;
    }

    //3. Kakao Receiver.
    @PostMapping(value = {"/message","/reference","/expired_session","/result"})
//            , produces = "application/json; charset=utf8")
    private ResponseEntity<Object> getObjectResponseEntity(@RequestBody Optional<String> optMessage,
                                                           @RequestHeader Map<String, Object> requestHeader,
                                                           HttpServletRequest request) throws Exception {
        // Path 값을 기준으로 파싱
        String kakaoPath = request.getServletPath();
        log.info("▶▶▶▶ KAKAO_WEBHOOK_RECEIVED ◀◀◀◀: [{}], {}", kakaoPath, optMessage.toString());

        log.info("SENDER_IP : {}", requestHeader.get("cf-connecting-ip"));
//        log.info("HEADERS: {}", requestHeader.toString());

        ResponseEntity<Object> response;
        if (!optMessage.isPresent()) {
            response = new ResponseEntity<>("BODY_NULL_OR_WRONG_PATH(USE_/message_/reference_/expired_session_/result)",
                    HttpStatus.BAD_REQUEST);
        } else {
            if (request.getServletPath().equals("/message")) {
                // 다른 채널들과 처리 로직 일관성 유지를 위해 헤더에서 추출한 정보를 message string에 합성
                String rawMessage = optMessage.get();
                String message = rawMessage.substring(0, rawMessage.length() - 1) +
                        ",\"path\":\"" + kakaoPath.substring(1) +
                        "\",\"uuid\":\"" + requestHeader.get("x-ib-message-id").toString() +
                        "\",\"retry\":\"" + requestHeader.get("x-ib-retry").toString() + "\"}";
                log.info(message);
                chatHandler.sendToAp(message, "1");
            }
            response = new ResponseEntity<>("EVENT_RECEIVED", HttpStatus.OK);
        }
        log.info(response.toString());
        return response;
    }
}
