package my.study.center.common.websocket;

import lombok.extern.slf4j.Slf4j;
import my.study.center.app.chat.documents.ChatMessageHist;
import my.study.center.app.chat.redisEntity.User;
import my.study.center.app.chat.repository.ChatMessageHistRepository;
import my.study.center.app.chat.repository.ChatUserRepository;
import my.study.center.common.websocket.cd.ChatMessageType;
import my.study.center.common.websocket.dto.ChatMessage;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import java.time.Instant;
import java.util.UUID;
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
    private ChatUserRepository chatUserRepository;
    private ChatMessageHistRepository chatMessageHistRepository;

    public ReactiveWebsocketConnectionHandler(UnicastProcessor<ChatMessage> eventPublisher,
                                              Flux<ChatMessage> events, ChatUserRepository chatUserRepository,
                                              ChatMessageHistRepository chatMessageHistRepository) {
        this.eventPublisher = eventPublisher;
        this.events = events;
        this.chatUserRepository = chatUserRepository;
        this.chatMessageHistRepository = chatMessageHistRepository;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        ReactiveWebsocketSubscriber subscriber = new ReactiveWebsocketSubscriber(eventPublisher, chatMessageHistRepository, chatUserRepository);

        // 세션관리를 위해 현재 연결을 요청한 세션이 없다면 추가함.
        AtomicReference<ChatMessage> receiveMessage = new AtomicReference<>(new ChatMessage());
        chatUserRepository.findById(webSocketSession.getId()).ifPresentOrElse(
                user -> log.info("sessionId : {}, name : {} already created session", user.getUid(), user.getLastName()),
                () -> {
                    ChatMessage newMemberJoinMessage = new ChatMessage(
                            UUID.randomUUID().toString(), ChatMessageType.CHAT_MESSAGE, webSocketSession.getId() + " 님이 접속하셨습니다.",
                            Instant.now().toEpochMilli(), new User(webSocketSession.getId()));

                    receiveMessage.set(newMemberJoinMessage);

                    // 현재 접속한 세션을 저장함
                    chatUserRepository.save(new User(webSocketSession.getId()));
                }
        );

        // 전송하는 메세지를 저장함.
        chatMessageHistRepository.save(new ChatMessageHist(receiveMessage.get()))
                .subscribe(chatMessageHist -> subscriber.onNext(receiveMessage.get()));

        // 연결된 세션에 receive 메세지를 처리하는 subscriber 설정
        webSocketSession.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(ChatMessage::toMessage)
                .subscribe(subscriber::onNext, subscriber::onError, subscriber::onComplete)
        ;

        // 연결된 세션에 send 메세지를 처리하는 publisher 설정
        return webSocketSession.send(events.map(value -> webSocketSession.textMessage(objectToString(value))));
    }
}