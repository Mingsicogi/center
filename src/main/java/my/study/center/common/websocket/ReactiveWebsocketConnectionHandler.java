package my.study.center.common.websocket;

import lombok.extern.slf4j.Slf4j;
import my.study.center.common.websocket.cd.ChatMessageType;
import my.study.center.common.websocket.dto.ChatMessage;
import my.study.center.common.websocket.dto.ChatUser;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 최초 커넥션 요청을 처리하는 핸들러
 *
 * @author minssogi
 */
@Slf4j
public class ReactiveWebsocketConnectionHandler implements WebSocketHandler {

    private UnicastProcessor<ChatMessage> eventPublisher;
    private Flux<ChatMessage> events;
    public final static Map<String, ChatUser> userSessionManager = new ConcurrentHashMap<>();

    public ReactiveWebsocketConnectionHandler(UnicastProcessor<ChatMessage> eventPublisher, Flux<ChatMessage> events) {
        this.eventPublisher = eventPublisher;
        this.events = events;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        ReactiveWebsocketSubscriber subscriber = new ReactiveWebsocketSubscriber(eventPublisher, new ChatUser(webSocketSession.getId()));

        // 세션관리를 위해 현재 연결을 요청한 세션이 없다면 추가함.
        ChatUser chatUser = userSessionManager.get(webSocketSession.getId());
        if(chatUser == null) {
            ChatMessage newMemberJoinMessage = new ChatMessage(ChatMessageType.CHAT_MESSAGE, webSocketSession.getId() + " 님이 접속하셨습니다.");
            subscriber.onNext(newMemberJoinMessage);

            // 현재 접속한 세션을 저장함
            userSessionManager.put(webSocketSession.getId(), subscriber.getMySessionInfo());
        }

        webSocketSession.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(ChatMessage::toMessage)
                .subscribe(subscriber::onNext, subscriber::onError, subscriber::onComplete)
        ;

        return webSocketSession.send(events.map((value) -> webSocketSession.textMessage(value.getData())));
    }
}