package my.study.center.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class ReactiveWebsocketHandler implements WebSocketHandler {

    private Flux<String> intervalFlux;
    private final ObjectMapper mapper;
    private final MessageGenerator messageGenerator;

    @PostConstruct
    private void setup() {
        intervalFlux = Flux.interval(Duration.ofSeconds(1)).map(it -> getEvent());
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
//        return webSocketSession
//                .send(intervalFlux.map(webSocketSession::textMessage))
//                .and(webSocketSession.receive().map(WebSocketMessage::getPayloadAsText).log());

        Flux<String> receiveFlux = webSocketSession.receive().map(WebSocketMessage::getPayloadAsText);

        return webSocketSession.send(Flux.generate(() -> webSocketSession.textMessage(webSocketSession.receive().map(WebSocketMessage::getPayloadAsText).toString())))
                .and(receiveFlux.log());
    }

    private String getEvent() {
        JsonNode node = mapper.valueToTree(new Event(messageGenerator.generate(), Instant.now()));
        return node.toString();
    }


}

@Component
class MessageGenerator {
    private List<String> messages =
            Arrays.asList("Bonjour", "Hola", "Zdravstvuyte", "Salve", "Guten Tag", "Hello");

    private final Random random = new Random(messages.size());

    public String generate() {
        return messages.get(random.nextInt(messages.size()));
    }
}

@Getter
class Event {
    private String message;
    private Instant time;

    Event(String message, Instant time) {
        this.message = message;
        this.time = time;
    }
}
