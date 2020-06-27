package my.study.center.common.websocket;

import lombok.extern.slf4j.Slf4j;
import my.study.center.app.chat.documents.ChatMessageHist;
import my.study.center.app.chat.repository.ChatMessageHistRepository;
import my.study.center.common.websocket.cd.ChatMessageType;
import my.study.center.common.websocket.dto.ChatMessage;
import my.study.center.common.websocket.dto.ChatUser;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static my.study.center.common.utils.CommonUtils.objectToString;

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
    private ChatMessageHistRepository chatMessageHistRepository;

    public ReactiveWebsocketConnectionHandler(UnicastProcessor<ChatMessage> eventPublisher, Flux<ChatMessage> events, ChatMessageHistRepository chatMessageHistRepository) {
        this.eventPublisher = eventPublisher;
        this.events = events;
        this.chatMessageHistRepository = chatMessageHistRepository;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        ReactiveWebsocketSubscriber subscriber = new ReactiveWebsocketSubscriber(eventPublisher, new ChatUser(webSocketSession.getId()), chatMessageHistRepository);

        // 세션관리를 위해 현재 연결을 요청한 세션이 없다면 추가함.
        ChatUser chatUser = userSessionManager.get(webSocketSession.getId());
        AtomicReference<ChatMessage> receiveMessage = new AtomicReference<>(new ChatMessage());
        if (chatUser == null) {
            ChatMessage newMemberJoinMessage = new ChatMessage(
                    UUID.randomUUID().toString(), ChatMessageType.CHAT_MESSAGE, webSocketSession.getId() + " 님이 접속하셨습니다.",
                    Instant.now().toEpochMilli(), new ChatUser(webSocketSession.getId()));

            receiveMessage.set(newMemberJoinMessage);

            // 현재 접속한 세션을 저장함
            userSessionManager.put(webSocketSession.getId(), subscriber.getMySessionInfo());
        }

        webSocketSession.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(ChatMessage::toMessage)
                .subscribe(subscriber::onNext, subscriber::onError, subscriber::onComplete)
        ;

        chatMessageHistRepository.save(new ChatMessageHist(receiveMessage.get())).subscribe(chatMessageHist -> {
            subscriber.onNext(receiveMessage.get());
        });

        return webSocketSession.send(events.map(value -> webSocketSession.textMessage(objectToString(value))));
    }
}