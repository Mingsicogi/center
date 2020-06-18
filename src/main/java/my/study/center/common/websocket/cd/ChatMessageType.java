package my.study.center.common.websocket.cd;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * chatting message type
 *
 * @author minssogi
 */
@Getter
@AllArgsConstructor
public enum ChatMessageType {
    USER_LEFT,
    CHAT_MESSAGE,
    USER_JOINED,
    USER_STATUS,

    ;
}
