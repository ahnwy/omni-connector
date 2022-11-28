package aicc.omni.omniconnector.handler;

import aicc.omni.omniconnector.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class parseAp {

    //고객정보
    static List<User> users = new ArrayList<>();

    private interface SearchExpression {
        boolean expression(User user);
    }

    private static User searchUser(SearchExpression func) {
        Optional<User> op = users.stream().filter(x -> func.expression(x)).findFirst();
        if (op.isPresent()) {
            return op.get();
        }
        return null;
    }

    public static void parseAp(String message, int channelId){
        //TODO 1.메세지 파싱

        //TODO 2.최초인입 체크

        //TODO 3-1. 최초일 경우 AP 인증키 발급(플래그를 통한 웹소켓 호출 요청)

        //TODO 3-2. 최초가 아닐경우 메세지 전송
    }


}
