package my.study.center.config;


import lombok.RequiredArgsConstructor;
import my.study.center.app.chat.repository.ChatMessageHistRepository;
import my.study.center.app.chat.repository.ChatUserRepository;
import my.study.center.common.websocket.ReactiveWebsocketConnectionHandler;
import my.study.center.common.websocket.dto.ChatMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

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

    private final ChatMessageHistRepository chatMessageHistRepository;
    private final ChatUserRepository chatUserRepository;


    /**
     * 특정 uri에 connection이 될 때 사용하는 핸들러 설정
     *
     * @param eventPublisher 여러 이벤트를 처리할 수 있는 producer
     * @param events
     * @return
     */
    @Bean
    public HandlerMapping webSocketHandlerMapping(UnicastProcessor<ChatMessage> eventPublisher, Flux<ChatMessage> events) {

        // TODO 해당 부분을 dynamic하게 셋팅할 수 있어야함.
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/chat/room", new ReactiveWebsocketConnectionHandler(eventPublisher, events, chatUserRepository, chatMessageHistRepository));

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);

        return handlerMapping;
    }

    /**
     * publisher
     *
     * @return
     */
    @Bean
    public UnicastProcessor<ChatMessage> eventPublisher() {
        return UnicastProcessor.create();
    }

    /**
     * @param eventPublisher
     * @return
     */
    @Bean
    public Flux<ChatMessage> events(UnicastProcessor<ChatMessage> eventPublisher) {
        return eventPublisher.replay(0).autoConnect(); // 새로 접속한 유저에게 이전 메세지를 얼만큼 보장할 것인지에 대한 설정.
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
