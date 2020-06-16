package my.study.center.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import java.util.Optional;

@Slf4j
@Component
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
        WebSocketMessageSubscriber subscriber = new WebSocketMessageSubscriber(eventPublisher);
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

    @Slf4j
    private static class WebSocketMessageSubscriber {
        private UnicastProcessor<Message> eventPublisher;
        private Optional<Message> lastReceivedEvent = Optional.empty();

        public WebSocketMessageSubscriber(UnicastProcessor<Message> eventPublisher) {
            this.eventPublisher = eventPublisher;
        }

        public void onNext(Message event) {
            lastReceivedEvent = Optional.of(event);
            eventPublisher.onNext(event);
        }

        public void onError(Throwable error) {
            //TODO log error
            log.error("ERROR : {}", error.getMessage(), error);
        }

        public void onComplete() {
            lastReceivedEvent.ifPresent(event -> eventPublisher.onNext(new Message(MessageType.USER_LEFT, "User Left... BYE~!")));
        }

    }
}