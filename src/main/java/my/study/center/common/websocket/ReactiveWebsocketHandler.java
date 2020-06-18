package my.study.center.common.websocket;

import lombok.extern.slf4j.Slf4j;
import my.study.center.common.websocket.dto.ChatMessageDTO;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

/**
 *
 *
 * @author minssogi
 */
@Slf4j
public class ReactiveWebsocketHandler implements WebSocketHandler {

    private UnicastProcessor<ChatMessageDTO> eventPublisher;
    private Flux<ChatMessageDTO> events;

    public ReactiveWebsocketHandler(UnicastProcessor<ChatMessageDTO> eventPublisher, Flux<ChatMessageDTO> events) {
        this.eventPublisher = eventPublisher;
        this.events = events;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        ReactiveWebsocketSubscriber subscriber = new ReactiveWebsocketSubscriber(eventPublisher);
        webSocketSession.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(ChatMessageDTO::toMessage)
                .subscribe(subscriber::onNext, subscriber::onError, subscriber::onComplete);


        return webSocketSession.send(events.map((value) -> webSocketSession.textMessage(value.getData())));
    }
}