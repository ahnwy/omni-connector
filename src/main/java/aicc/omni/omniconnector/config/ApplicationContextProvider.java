package aicc.omni.omniconnector.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author : 박재형
 * @date : 2022-02-23
 * 수정일       수정자  수정내용
 * ----------  -----  ------------------------------------------------------
 * 2022-02-23  박재형  최초생성
 * @see :
 **/
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException{
        applicationContext = ctx;
    }

    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }
}
