package my.study.center.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class WebsocketConfiguration {
    private final WebSocketHandler webSocketHandler;

    @Bean
    public HandlerMapping websocketHandlerMapping() {
        Map<String, WebSocketHandler> mapper = new HashMap<>();

        mapper.put("/event-emitter", webSocketHandler);

        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setUrlMap(mapper);

        return simpleUrlHandlerMapping;
    }
}
