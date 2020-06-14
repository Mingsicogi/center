package my.study.center.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * 웹소켓 서버 설정
 *
 * @author 전민석
 */
@Configuration
@RequiredArgsConstructor
public class WebsocketConfiguration {

    private final WebSocketHandler webSocketHandler;

    /**
     * websocket handler router 설정
     *
     * @return HandlerMapping
     */
    @Bean
    public HandlerMapping webSocketHandlerMapping() {

        // TODO 해당 부분을 dynamic하게 셋팅할 수 있어야함.
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/event-emitter", webSocketHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    /**
     * handler mapping 에 request를 핸들링 할 수 있도록 하는 adaptor 설정
     *
     * @return WebSocketHandlerAdapter
     */
    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
