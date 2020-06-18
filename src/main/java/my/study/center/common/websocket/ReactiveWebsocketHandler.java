package my.study.center.common.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import my.study.center.common.websocket.dto.Message;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

@Slf4j
public class ReactiveWebsocketHandler implements WebSocketHandler {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private UnicastProcessor<Message> eventPublisher;
    private Flux<Message> events;

    public ReactiveWebsocketHandler(UnicastProcessor<Message> eventPublisher, Flux<Message> events) {
        this.eventPublisher = eventPublisher;
        this.events = events;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        ReactiveWebsocketSubscriber subscriber = new ReactiveWebsocketSubscriber(eventPublisher);
        webSocketSession.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(this::toMessage)
                .subscribe(subscriber::onNext, subscriber::onError, subscriber::onComplete);

        return webSocketSession.send(events.map((value) -> webSocketSession.textMessage(value.getData())));
    }

    private Message toMessage(String strMessage) {
        try {
            return objectMapper.readValue(strMessage, Message.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}