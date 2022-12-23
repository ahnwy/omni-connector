package aicc.omni.omniconnector.service.kakao;

import aicc.omni.omniconnector.model.kakao.KakaoWhMsgDto;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static aicc.omni.omniconnector.handler.WebsocketClientHandler.whUserMap;


@Log4j2
public class KakaoWhReceiveMsgClassifier {

    public static void getMessageType(KakaoWhMsgDto whMsgDto, String channelId) throws Exception {

        log.info("▶▶▶ getMessageTypeVerifier");

        whMsgDto.setMsgWrtId("1"); // (1- 고객, 2- 상담사, 3- 봇) -> webhook 수신시 자동으로 고객으로 셋팅

        switch (whMsgDto.getPath()) {
            case "message":
                switch (whMsgDto.getType()) {
                    case "photo":
                        fileNameExtractor(whMsgDto);
                        timeRecordGenerator(whMsgDto);

                        if (whMsgDto.getFileSize() > 10485760) {
                            whMsgDto.setOriginalType("photo");
                            whMsgDto.setProcessing("W"); //경고 메시지(AP 전달하지 않고 MW 자체처리)
                            whMsgDto.setContent("(고객이 최대 허용된 크기(10MB) 이상의 이미지를 전송 시도하였습니다.)");
                            initMsgChecker(whMsgDto, channelId);
                        } else {
                            whMsgDto.setProcessing("Y");
                            initMsgChecker(whMsgDto, channelId); // 최초 메시지 판단
                        }
                        break;
                    case "text" :
                        timeRecordGenerator(whMsgDto);

                        //카카오는 1000자를 넘으면 초과분이 파일 url로 전달되므로, 문자수를 카운트 하지 않고
                        //url이 있는지만 판단한다.
//                        if (letterCounter(whMsgDto) > 1000) {
//                            whMsgDto.setProcessing("E");
                        if (whMsgDto.getAttachment() != null) {
                            whMsgDto.setProcessing("W");

                            // 상담사(AP)에 전송할  메시지 셋팅
                            whMsgDto.setOriginalType(whMsgDto.getType());
                            whMsgDto.setContent("(고객이 잘못된 양식(파일, 오디오, 동영상, 용량초과)을 전송 시도하였습니다.)");
                            initMsgChecker(whMsgDto, channelId); // 최초 메시지 판단

                        } else {
                            whMsgDto.setProcessing("Y");
                            initMsgChecker(whMsgDto, channelId); // 최초 메시지 판단
                        }
                        break;
                    case "audio" :
                    case "file" :
                    case "video" :
                        whMsgDto.setProcessing("W");
                        log.info("전달 불가능한 파일 양식");

                        // 상담사(AP)에 고객이 잘못된 파일을 전송시도하였음을 전달하기 위한 셋팅
                        whMsgDto.setOriginalType(whMsgDto.getType());
                        whMsgDto.setType("text");
                        whMsgDto.setContent("(고객이 잘못된 양식(파일, 오디오, 동영상, 용량초과)을 전송 시도하였습니다.)");
                        log.info("~~~~~~~~~~~~~~~~~~~~~수정된 DTO {}", whMsgDto.toString());
                        timeRecordGenerator(whMsgDto);
                        initMsgChecker(whMsgDto, channelId); // 최초 메시지 판단
                        break;
                } break;
            case "result" :
                log.info("▶▶▶ resultresultresultresultresultresultresultresultresultresultresultresultresult");

                if (Objects.equals(whMsgDto.getRequest_type(), "upload")) {
                    log.info("업로드된 이미지/파일의 CDN 어드레스 리턴 확인: {},", whMsgDto.getRequest_type());
                    timeRecordGenerator(whMsgDto);
                    whMsgDto.setProcessing("U");

                    Optional<String> image = Optional.ofNullable(whMsgDto.getImage());
                    Optional<String> file = Optional.ofNullable(whMsgDto.getFile());

                    if (image.isPresent()) {
                        whMsgDto.setMessageType("IM");
                    } else if (file.isPresent()) {
                        whMsgDto.setMessageType("FI");
                    } else {
                        whMsgDto.setProcessing("N");
                    }
                } else {
                    whMsgDto.setProcessing("N");
                } break;
            case "expired_session":
                log.info("상담종료 요청");
                timeRecordGenerator(whMsgDto);
                whMsgDto.setPlatformId(whMsgDto.getUser_key());
                if (whUserMap.containsKey(whMsgDto.getPlatformId())) { // 첫 메시지가 아니라면
                    whMsgDto.setProcessing("E");
                    whMsgDto.setKnockYn("N");
                    whMsgDto.setMsgSeq(whUserMap.get(whMsgDto.getPlatformId()));
                    whUserMap.values().removeAll(Collections.singleton(whMsgDto.getMsgSeq()));
                } else {
                    whMsgDto.setProcessing("N");
                }
                break;

            default:
                whMsgDto.setProcessing("N");
                log.info("▶▶▶ Kakao webhook message processing TERMINATED. MSG TYPE : {}.", whMsgDto.getPath());
                break;
        }
    }

    public static void fileNameExtractor(KakaoWhMsgDto whMsgDto) { //파일 이름과 사이즈 한번에 파싱
        String rawFileUrl = whMsgDto.getContent();
        String fileUrl = rawFileUrl.substring(8, rawFileUrl.length()-2); //file Url 파싱
        log.info("▶▶▶ KAKAO_WEBHOOK_FILE_URL : {}", fileUrl);

        String[] fileNameArray = fileUrl.split("/");
        String fileName = fileNameArray[6];
        log.info("▶▶▶ KAKAO_WEBHOOK_FILE_NAME : {}", fileName);

        //http 헤더에서 파일 용량을 가져온다.
        try {
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) new URL(fileUrl).openConnection();
            conn.setInstanceFollowRedirects(false);
            String headerFields = conn.getHeaderFields().toString();
            log.info("▶▶▶ KAKAO_WEBHOOK_FILE_HEADERS : {}", headerFields);

            int fileSize = Integer.parseInt(conn.getHeaderField("Content-Length"));
            log.info("▶▶▶ KAKAO_WEBHOOK_FILE_SIZE : {}", fileSize);

            whMsgDto.setFileName(fileName);
            whMsgDto.setFileSize(fileSize);
            whMsgDto.setFileUrl(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void timeRecordGenerator (KakaoWhMsgDto whMsgDto) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(date);
        log.info("▶▶▶ KAKAO_WEBHOOK_TIMESTAMP_CREATED: {}", formattedDate);
        whMsgDto.setFormattedDate(formattedDate);
    }

    public static int letterCounter (KakaoWhMsgDto whMsgDto) {
        String textBody = whMsgDto.getContent();
        int letterCount = textBody.length();
        log.info("▶▶▶ KAKAO_WEBHOOK_LETTER_COUNT : {}", letterCount);
        whMsgDto.setLetterCount(letterCount);
        return letterCount;
    }

    public static void initMsgChecker (KakaoWhMsgDto whMsgDto, String channelId) throws Exception {

        whMsgDto.setPlatformId(whMsgDto.getUser_key());
        log.info("▶▶▶ KAKAO_WEBHOOK_SET_PLATFORM_ID : {}", whMsgDto.getPlatformId());

        if (whUserMap.containsKey(whMsgDto.getPlatformId())) { // 첫 메시지가 아니라면
            whMsgDto.setKnockYn("N");
            log.info("▶▶▶ KAKAO_WEBHOOK_LOOKING_FOR_msgSeq_KEY : {}",
                    whUserMap.get(whMsgDto.getPlatformId()));
            whMsgDto.setMsgSeq(whUserMap.get(whMsgDto.getPlatformId()));
        } else { // 첫 메시지라면
            whMsgDto.setKnockYn("Y");
        }
    }
}