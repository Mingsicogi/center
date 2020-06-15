package my.study.center.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.internal.SuppressJava6Requirement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveWebsocketHandler implements WebSocketHandler {

    private Flux<String> intervalFlux;
    private final ObjectMapper mapper;
    private final MessageGenerator messageGenerator;
    private final Map<String, MessageHandler> connections = new ConcurrentHashMap<>();


    @PostConstruct
    private void setup() {
        intervalFlux = Flux.interval(Duration.ofSeconds(1)).map(it -> getEvent());
    }

    @Bean
    public IntegrationFlow integrationFlow() {
        return IntegrationFlows.from("test").channel(this.publishSubscribeChannel()).get();
    }

    @Bean
    public PublishSubscribeChannel publishSubscribeChannel() {
        return new PublishSubscribeChannel();
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
//        return webSocketSession
//                .send(intervalFlux.map(webSocketSession::textMessage))
//                .and(webSocketSession.receive().map(WebSocketMessage::getPayloadAsText).log());

        log.info("wesocketSessionId : {}", webSocketSession.getId());

        return webSocketSession
                .send(Flux.create((Consumer<FluxSink<WebSocketMessage>>) sink -> {
                    ReactiveMessageHandler reactiveMessageHandler = new ReactiveMessageHandler(webSocketSession, sink);

                    // register the connection to the client
                    connections.put(webSocketSession.getId(), reactiveMessageHandler);

                    // connect to the client
                    this.publishSubscribeChannel().subscribe(reactiveMessageHandler);
                })
                .doFinally((signalType) -> {
                        this.publishSubscribeChannel().unsubscribe(connections.get(webSocketSession.getId()));
                        connections.remove(webSocketSession.getId());
                })).and(webSocketSession.receive().map(WebSocketMessage::getPayloadAsText).log());
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
