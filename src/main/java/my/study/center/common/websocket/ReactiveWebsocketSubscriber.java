package my.study.center.common.websocket;

import lombok.extern.slf4j.Slf4j;
import my.study.center.app.chat.documents.ChatMessageHist;
import my.study.center.app.chat.redisEntity.User;
import my.study.center.app.chat.repository.ChatMessageHistRepository;
import my.study.center.app.chat.repository.ChatUserRepository;
import my.study.center.common.websocket.cd.ChatMessageType;
import my.study.center.common.websocket.dto.ChatMessage;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.UnicastProcessor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


/**
 * 메세지를 전달 받아 처리하는 subscriber
 *
 * @author minssogi
 */
@Slf4j
public class ReactiveWebsocketSubscriber implements Subscriber<ChatMessage> {
    private UnicastProcessor<ChatMessage> eventPublisher;
    private Optional<ChatMessage> lastReceivedEvent;
    private User user; // 현재 세션 관리를 위한 객체
    private ChatMessageHistRepository chatMessageHistRepository;
    private ChatUserRepository chatUserRepository;

    ReactiveWebsocketSubscriber(UnicastProcessor<ChatMessage> eventPublisher,
                                ChatMessageHistRepository chatMessageHistRepository,
                                ChatUserRepository chatUserRepository) {
        this.eventPublisher = eventPublisher;
        this.chatUserRepository = chatUserRepository;
        lastReceivedEvent = Optional.empty();
        this.chatMessageHistRepository = chatMessageHistRepository;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(10_000); // request 요청을 핸들링 할 수 있는 용량. 만개 이하를 처리하고,
    }

    /**
     * 실제로 클라가 메세지를 보내면 수신하는 부분
     *
     * @param chatMessage ChatMessage
     */
    @Override
    public void onNext(ChatMessage chatMessage) {

        // 현재 세션에서 보낸 메세지 총 갯수를 관리함.
        chatUserRepository.findById(chatMessage.getUser().getUid()).ifPresentOrElse(savedSessionUser -> {
            savedSessionUser.setMessageCount(savedSessionUser.getMessageCount() + 1);
            chatUserRepository.save(savedSessionUser);
        }, () -> chatUserRepository.save(chatMessage.getUser()));

        lastReceivedEvent = Optional.of(chatMessage);
        chatMessageHistRepository.save(new ChatMessageHist(chatMessage)).subscribe(chatMessageHist -> eventPublisher.onNext(chatMessage));
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("ERROR : {}", throwable.getMessage(), throwable);
    }

    @Override
    public void onComplete() {
        lastReceivedEvent.ifPresent(lastMessage -> {
            ChatMessage leaveMessage = new ChatMessage(UUID.randomUUID().toString(), ChatMessageType.USER_LEFT,
                    lastMessage.getUser().getUid() + " 님이 퇴장했습니다.", Instant.now().toEpochMilli(),
                    new User(lastMessage.getUser().getUid())
            );

            chatMessageHistRepository.save(new ChatMessageHist(leaveMessage))
                    .subscribe(chatMessageHist -> eventPublisher.onNext(leaveMessage)); // broadcasting other user
            chatUserRepository.deleteById(lastMessage.getUser().getUid()); // session remove
        });
    }
}
