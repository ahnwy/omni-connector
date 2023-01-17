package aicc.omni.omniconnector.service.common;


import aicc.omni.omniconnector.util.MetaHttpUtil;
import aicc.omni.omniconnector.util.NAVERHTTPUTIL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    public static void sendApi(String msg, String channel) throws ParseException {
        if(channel.equals("1")){

        } else if(channel.equals("2")){
            NAVERHTTPUTIL.sendApi(msg);
        } else if(channel.equals("3")){
            MetaHttpUtil.sendApi(msg,"3");
        } else if(channel.equals("4")){
            MetaHttpUtil.sendApi(msg,"4");
        } else if(channel.equals("5")){

        } else if(channel.equals("6")){

        } else {

        }

    }
}
