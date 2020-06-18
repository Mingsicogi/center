package my.study.center.common.websocket;

import lombok.extern.slf4j.Slf4j;
import my.study.center.common.websocket.cd.ChatMessageType;
import my.study.center.common.websocket.dto.ChatMessageDTO;
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
public class ReactiveWebsocketSubscriber implements Subscriber<ChatMessageDTO> {
    private UnicastProcessor<ChatMessageDTO> eventPublisher;
    private Optional<ChatMessageDTO> lastReceivedEvent;

    ReactiveWebsocketSubscriber(UnicastProcessor<ChatMessageDTO> eventPublisher) {
        this.eventPublisher = eventPublisher;
        lastReceivedEvent = Optional.empty();
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(10_000);
    }

    @Override
    public void onNext(ChatMessageDTO chatMessageDTO) {
        lastReceivedEvent = Optional.of(chatMessageDTO);
        eventPublisher.onNext(chatMessageDTO);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("ERROR : {}", throwable.getMessage(), throwable);
    }

    @Override
    public void onComplete() {
        lastReceivedEvent.ifPresent(event -> eventPublisher.onNext(new ChatMessageDTO(ChatMessageType.USER_LEFT, "User Left... BYE~!")));
    }
}
