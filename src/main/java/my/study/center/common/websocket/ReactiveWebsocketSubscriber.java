package my.study.center.common.websocket;

import lombok.extern.slf4j.Slf4j;
import my.study.center.common.websocket.cd.ChatMessageType;
import my.study.center.common.websocket.dto.ChatMessage;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.UnicastProcessor;

import java.util.Optional;

/**
 * 메세지를 전달 받아 처리하는 subscriber
 *
 * @author minssogi
 */
@Slf4j
public class ReactiveWebsocketSubscriber implements Subscriber<ChatMessage> {
    private UnicastProcessor<ChatMessage> eventPublisher;
    private Optional<ChatMessage> lastReceivedEvent;

    ReactiveWebsocketSubscriber(UnicastProcessor<ChatMessage> eventPublisher) {
        this.eventPublisher = eventPublisher;
        lastReceivedEvent = Optional.empty();
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(10_000);
    }

    @Override
    public void onNext(ChatMessage chatMessage) {
        lastReceivedEvent = Optional.of(chatMessage);

        eventPublisher.onNext(chatMessage);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("ERROR : {}", throwable.getMessage(), throwable);
    }

    @Override
    public void onComplete() {
        lastReceivedEvent.ifPresent(event -> eventPublisher.onNext(new ChatMessage(ChatMessageType.USER_LEFT, "User Left... BYE~!")));
    }
}
