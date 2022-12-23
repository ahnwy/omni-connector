package aicc.omni.omniconnector;

import aicc.omni.omniconnector.handler.WebsocketClientHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URISyntaxException;

@SpringBootApplication
public class OmniConnectorApplication {

    public static void main(String[] args) throws URISyntaxException {
        SpringApplication.run(OmniConnectorApplication.class, args);
        WebsocketClientHandler websocketClientHandler = new WebsocketClientHandler().WebSocketClientEndpoint();
    }

}
