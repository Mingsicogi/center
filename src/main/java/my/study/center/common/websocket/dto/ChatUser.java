package my.study.center.common.websocket.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
public class ChatUser {
    private String sessionId;
    private AtomicLong messageCount = new AtomicLong(0);
    private LocalDateTime joinedYmdt;
    private String nickname;

    public ChatUser(String sessionId) {
        this.sessionId = sessionId;
        this.joinedYmdt = LocalDateTime.now();
    }
}
