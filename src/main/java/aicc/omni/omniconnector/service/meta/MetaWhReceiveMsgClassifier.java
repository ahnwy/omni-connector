package aicc.omni.omniconnector.service.meta;

import aicc.omni.omniconnector.handler.WebsocketClientHandler;
import aicc.omni.omniconnector.model.ApWsDto;
import aicc.omni.omniconnector.model.meta.MetaWhMsgDto;
import aicc.omni.omniconnector.util.MetaHttpUtil;
import aicc.omni.omniconnector.util.MetaTimeConvertUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class MetaWhReceiveMsgClassifier {
    public static void getMessageType(MetaWhMsgDto msgDto, String channelId) throws Exception {

        log.info("▶▶▶ getMessageTypeVerifier");
        msgDto.setMsgWrtId("1"); // (1- 고객, 2- 상담사, 3- 봇) -> webhook 수신시 자동으로 고객으로 셋팅
        msgDto.setProcessing("N"); // AP에게 포워딩할 대상 마킹, 기본적으로 N 셋팅하고, 필요한 경우만  Y, E로 마킹

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

        // delivery
        if (Delivery.isPresent()) {
            log.info("▶▶▶ WEBHOOK_CLASSIFIER verified : {}", "DELIVERY");
            msgDto.setMessageType("delivery");
        }
        // read
        else if (Read.isPresent()) {
            log.info("▶▶▶ WEBHOOK_CLASSIFIER verified : {}", "READ");
            msgDto.setMessageType("read");

            // message
        } else if (Message.isPresent()) {

            Optional<Object> Echo
                    = ofNullable(msgDto.getEntry())
                    .map(entry -> entry.get(0).getMessaging().get(0).getMessage().getIs_echo());

            if (Echo.isPresent()) {
                log.info("▶▶▶ META_WEBHOOK_MSG_TYPE_VERIFIED : {}", "ECHO");
                msgDto.setMessageType("echo");
            } else {
                log.info("▶▶▶ META_WEBHOOK_MSG_TYPE_VERIFIED : {}", "NOT ECHO");

                Optional<Object> Text
                        = ofNullable(msgDto.getEntry())
                        .map(entry -> entry.get(0).getMessaging().get(0).getMessage().getText());

                Optional<Object> Attachments
                        = ofNullable(msgDto.getEntry())
                        .map(entry -> entry.get(0).getMessaging().get(0).getMessage().getAttachments());

                MetaTimeConvertUtil.getTimestampToDate(msgDto); // 시간 변환

                // usermap check 관련
                // PlatformId 생성 및 셋팅
                msgDto.setPlatformId(msgDto.getEntry().get(0).getMessaging().get(0)
                        .getSender().getSenderId());
                log.info("▶▶▶ META_WEBHOOK_SET_PLATFORMID : {}", msgDto.getPlatformId());

                // 최초 메시지(visit) 여부 확인 및 셋팅
                if (WebsocketClientHandler.whUserMap.containsKey(msgDto.getPlatformId())) {
                    msgDto.setKnockYn("N");
                    log.info("▶▶▶ META_WEBHOOK_LOOKING_FOR_msgSeq_KEY : {}",
                            WebsocketClientHandler.whUserMap.get(msgDto.getPlatformId()));
                    msgDto.setMsgSeq(WebsocketClientHandler.whUserMap.get(msgDto.getPlatformId()));
                } else {
                    msgDto.setKnockYn("Y");
                    ApWsDto apWsDto = new ApWsDto();
                    String channelSeq = msgDto.getChannel();

                    String info = "담당자가 상담을 준비중입니다. 잠시만 기다려 주세요.";
                    String json = MetaHttpInfoMsgBuilder.plainInfoMsg(msgDto, info);
                    MetaHttpUtil.sendMsg(json, channelSeq);

                    String initMsg = MetaApMsgBuilder.apWebSocketInitMsg(msgDto);
                    log.info("▶▶▶ META_WEBHOOK_INIT_MSG : {}", initMsg);
                    WebsocketClientHandler.sendMessage(initMsg);
                    WebsocketClientHandler.sendMessage(initMsg);
                }

                if (Text.isPresent()) {
                    log.info("▶▶▶ META_WEBHOOK_MSG_TYPE_VERIFIED : {}", "TEXT");

                    msgDto.setMessageType("text");

                    if (letterCounter(msgDto) > 1000) {
                        msgDto.setProcessing("E"); // 1000자가 넘으면 오류 메시지 리턴 대상으로 설정
                    } else {
                        msgDto.setProcessing("Y"); // 아니면 AP 포워딩 전송 대상으로 설정
                    }

                } else if (Attachments.isPresent()) {
                    log.info("▶▶▶ META_WEBHOOK_MSG_TYPE_VERIFIED : {}", "NOT_TEXT");

                    switch (msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getAttachments().get(0).getType()){
                        case "image" :
                            msgDto.setMessageType("image");

                            int imageCount = msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getAttachments().size();
                            msgDto.setImageCount(String.valueOf(imageCount));
                            log.info("▶▶▶ META_WEBHOOK_MSG_WITH_IMG_ARRAY : {} counts", imageCount);

                            int subParam = 5;

                            fileNameExtractor(msgDto, subParam, imageCount);
                            fileSizeMeasure(msgDto, imageCount);

                            if (Integer.parseInt(msgDto.getFileSize())> 2097152) {
                                msgDto.setProcessing("E"); // 20MB가 넘으면 오류 메시지 리턴 대상으로 설정
                            } else {
                                msgDto.setProcessing("Y"); // 아니면 AP 포워딩 전송 대상으로 설정
                            }
                            break;

                        case "file" :
                            log.info("▶▶▶ META_WEBHOOK_MSG_TYPE_VERIFIED : {}", "file");
                            msgDto.setMessageType("file");

                            subParam = 6;
                            imageCount = 1;

                            fileNameExtractor(msgDto, subParam, imageCount);

                            if (Integer.parseInt(msgDto.getFileSize())> 10485760) {
                                msgDto.setProcessing("E"); // 10MB가 넘으면 오류 메시지 리턴 대상으로 설정
                            } else {
                                msgDto.setProcessing("Y"); // 아니면 AP 포워딩 전송 대상으로 설정
                            }
                            break;

                        case "audio" :
                            log.info("▶▶▶ META_WEBHOOK_MSG_TYPE_VERIFIED : {}", "audio");
                            msgDto.setMessageType("audio");

//                            subParam = 6;
//                            imageCount = 1;
//                            fileNameExtractor(msgDto, subParam, imageCount);

                            msgDto.setProcessing("E"); // 오류 메시지 리턴 대상으로 설정
                            break;

                            case "location" :
                                log.info("▶▶▶ META_WEBHOOK_MSG_TYPE_VERIFIED : {}", "location");
//                                mercatorConverter(msgDto);
                                msgDto.setMessageType("location");
                                msgDto.setProcessing("E");
                                break;
                    }
                }
            }
            // 예외 처리
        } else {
            log.info("▶▶▶ WEBHOOK_CLASSIFIER not verified : {}", msgDto.getEntry());
        }
    }
    public static void fileNameExtractor(MetaWhMsgDto msgDto, int subParam, int imageCount) {
        if (msgDto.getChannel().equals("3")) { //페이스북, 한번에 보낸 이미지 갯수만큼 반복
            int i = 0;
            while (i < imageCount) {
                String fileUrl = msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getAttachments().get(i)
                        .getPayload().getUrl();
                String[] fileNameArray = fileUrl.split("/");
                String fileName = fileNameArray[subParam].substring(0, fileNameArray[subParam].indexOf("?"));
//                msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getAttachments().get(i).setTitle(fileName);
                msgDto.setFileName(fileName);
                log.info("▶▶▶ META_WEBHOOK_FILE_NAME : {}", fileName);
                i++;
            }
        } else { //인스타그램 -> 파일 사이즈까지 한번에 추출, 이미지 여러개 동시에 보내도 순차적으로 발송됨

            String fileUrl = msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getAttachments().get(0)
                    .getPayload().getUrl();
            try {
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) new URL(fileUrl).openConnection();
                conn.setInstanceFollowRedirects(false);
                String headerFields = conn.getHeaderFields().toString();
                log.info("▶▶▶ META_WEBHOOK_FILE_HEADERS : {}", headerFields);

                // 인스타그램 case 1 - image/jpeg
                if (Objects.equals(conn.getHeaderField("Content-Type"), "image/jpeg")) {
                    String rawFileName = conn.getHeaderField("Content-Disposition");
                    String fileName = rawFileName.substring(16);
                    msgDto.setFileName(fileName);
                    log.info("▶▶▶ META_WEBHOOK_IG_IMG_NAME : {}", fileName);

                    //인스타그램 case 2 - text/html(스티커, 파일)
                } else if (Objects.equals(conn.getHeaderField("Content-Type"), "text/html")) {
                    String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") - 18, fileUrl.lastIndexOf("/"))
                            + fileUrl.substring(fileUrl.lastIndexOf(".") - 1);
                    msgDto.setFileName(fileName);
                    log.info("▶▶▶ META_WEBHOOK_IG_STICKER_NAME : {}", fileName);

                }
                String fileSize = conn.getHeaderField("Content-Length");
                msgDto.setFileSize(fileSize);
                log.info("▶▶▶ META_WEBHOOK_FILE_SIZE : {}", fileSize);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void fileSizeMeasure(MetaWhMsgDto msgDto, int imageCount)  {
        // 인스타그램 제외(fileNameExtractor에서 파일 사이즈가 구해지지 않은 경우에만 수행)
        if (msgDto.getFileSize() == null) {

            try {
                HttpURLConnection conn = null;
                int i = 0;
                while (i < imageCount) {
                    URL url = new URL(msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getAttachments()
                            .get(i).getPayload().getUrl());
                    conn = (HttpURLConnection) new URL(String.valueOf(url)).openConnection();
                    conn.setInstanceFollowRedirects(false);
                    String fileSize = conn.getHeaderField("Content-Length");
                    msgDto.setFileSize(fileSize);
                    log.info("▶▶▶ META_WEBHOOK_FILE_HEADERS : {}", conn.getHeaderFields().toString());
                    log.info("▶▶▶ META_WEBHOOK_FILE_SIZE : {}", fileSize);
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static int letterCounter (MetaWhMsgDto msgDto) {
        String textBody = msgDto.getEntry().get(0).getMessaging().get(0).getMessage().getText();
        int letterCount = textBody.length();
        log.info("▶▶▶ META_WEBHOOK_LETTER_COUNT : {}", letterCount);
        return letterCount;
    }

//    public static <Hashmap> void mercatorConverter(MetaWhMsgDto msgDto) throws TransformException, FactoryException {
//
//
//        String latitude = msgDto.getEntry().get(0).getMessaging().get(0).getMessage()
//                .getAttachments().get(0).getPayload().getCoordinates().getLat();
//        String longitude = msgDto.getEntry().get(0).getMessaging().get(0).getMessage()
//                .getAttachments().get(0).getPayload().getCoordinates().getLng();
//        log.info("▶▶▶ META_WEBHOOK_LATITUDE X longitude (String) : {}, {}", latitude, longitude);
//
//        double x = Double.parseDouble(latitude);
//        double y = Double.parseDouble(longitude);
//        log.info("▶▶▶ META_WEBHOOK_LATITUDE X longitude (Double) : {}, {}", x, y);
//
////        Coordinate coordinate = new Coordinate(x, y);
////        MathTransform transform = CRS.findMathTransform(
////                CRS.decode("EPSG:4326"), CRS.decode("EPSG:3857"), false
////        );
////        JTS.transform(coordinate, coordinate, transform);
////
////        String location = coordinate.x + "," + coordinate.y;
////        log.info("▶▶▶ META_WEBHOOK_LOCATION : {}", location);//
////        HashMap<String, Double> mercatorConverter = new HashMap<>(2);
////        mercatorConverter.put("newLat", coordinate.x);
////        mercatorConverter.put("newLong", coordinate.y);
//
//        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4236");
//        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");
//        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, false);
//        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
//        Point point = geometryFactory.createPoint(new Coordinate(x, y));
//        Point targetPoint = (Point) JTS.transform(point, transform);
//        String location = targetPoint.getX() + "," + targetPoint.getY();
//        log.info("▶▶▶ META_WEBHOOK_LOCATION : {}", location);
//
//
//    }
}