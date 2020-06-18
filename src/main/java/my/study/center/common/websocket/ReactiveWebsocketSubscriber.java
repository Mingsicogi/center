package my.study.center.common.websocket;

import lombok.extern.slf4j.Slf4j;
import my.study.center.common.websocket.cd.MessageType;
import my.study.center.common.websocket.dto.Message;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.UnicastProcessor;

import java.util.Optional;

/**
 *
 *
 * @author minssogi
 */
@Slf4j
public class ReactiveWebsocketSubscriber implements Subscriber<Message> {
    private UnicastProcessor<Message> eventPublisher;
    private Optional<Message> lastReceivedEvent;

    ReactiveWebsocketSubscriber(UnicastProcessor<Message> eventPublisher) {
        this.eventPublisher = eventPublisher;
        lastReceivedEvent = Optional.empty();
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(10_000);
    }

    @Override
    public void onNext(Message message) {
        lastReceivedEvent = Optional.of(message);
        eventPublisher.onNext(message);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("ERROR : {}", throwable.getMessage(), throwable);
    }

    @Override
    public void onComplete() {
        lastReceivedEvent.ifPresent(event -> eventPublisher.onNext(new Message(MessageType.USER_LEFT, "User Left... BYE~!")));
    }
}
