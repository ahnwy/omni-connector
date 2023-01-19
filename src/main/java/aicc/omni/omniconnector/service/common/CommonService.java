package aicc.omni.omniconnector.service.common;


import aicc.omni.omniconnector.util.KAKAOHTTPUTIL;
import aicc.omni.omniconnector.util.METAHTTPUTIL;
import aicc.omni.omniconnector.util.NAVERHTTPUTIL;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    public static void sendApi(String msg, String channel) throws ParseException {
        if(channel.equals(ChannelType.KAKAO)){
            KAKAOHTTPUTIL.sendMsg(msg);
        } else if(channel.equals(ChannelType.NAVER)){
            NAVERHTTPUTIL.sendApi(msg);
        } else if(channel.equals(ChannelType.FACEBOOK)){
            METAHTTPUTIL.sendApi(msg,"3");
        } else if(channel.equals(ChannelType.IG)){
            METAHTTPUTIL.sendApi(msg,"4");
        } else if(channel.equals(ChannelType.EMAIL)){

        } else if(channel.equals(ChannelType.WEBCHAT)){

        } else {

        }

    }
}
