package my.study.center.common.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum MessageType {
    USER_LEFT,
    CHAT_MESSAGE,
    USER_JOINED,
    USER_STATUS,

    ;
}
