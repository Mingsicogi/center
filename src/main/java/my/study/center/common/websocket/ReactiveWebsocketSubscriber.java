package my.study.center.common.websocket;

import lombok.extern.slf4j.Slf4j;
import my.study.center.common.websocket.cd.MessageType;
import my.study.center.common.websocket.dto.Message;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.UnicastProcessor;

import java.util.Optional;

@Slf4j
class ReactiveWebsocketSubscriber implements Subscriber<Message> {
    private UnicastProcessor<Message> eventPublisher;
    private Optional<Message> lastReceivedEvent = Optional.empty();

    ReactiveWebsocketSubscriber(UnicastProcessor<Message> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onSubscribe(Subscription subscription) {

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
