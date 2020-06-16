package my.study.center.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

@Slf4j
public class ReactiveMessageHandler implements MessageHandler {

    private WebSocketSession webSocketSession;
    private FluxSink<WebSocketMessage> sink;

    ReactiveMessageHandler(WebSocketSession webSocketSession, FluxSink<WebSocketMessage> sink) {
        this.webSocketSession = webSocketSession;
        this.sink = sink;
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String payload = String.valueOf(message.getPayload());
        log.info("### payload : {} ", payload);
        payload += "echo : " + payload;
        WebSocketMessage webSocketMessage = webSocketSession.textMessage(payload);
        sink.next(webSocketMessage);
    }
}
