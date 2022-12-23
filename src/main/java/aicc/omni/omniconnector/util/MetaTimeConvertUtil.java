package aicc.omni.omniconnector.util;

import aicc.omni.omniconnector.model.meta.MetaWhMsgDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Log4j2
@Component
public class MetaTimeConvertUtil {
    public static void getTimestampToDate(MetaWhMsgDto msgDto) {
        String rawTimeStamp = msgDto.getEntry().get(0).getMessaging().get(0).getTimestamp();
        long timeStamp = Long.parseLong(rawTimeStamp);
        Date date = new Date(timeStamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(date);
        log.info("▶▶▶ META_WEBHOOK_TIMESTAMP_CONVERTED: {}", formattedDate);
        msgDto.setFormattedDate(formattedDate);
    }
}
