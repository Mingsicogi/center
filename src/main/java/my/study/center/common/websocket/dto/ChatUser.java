package my.study.center.common.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatUser {
    private String uid;
    private String firstName;
    private String lastName;
    private String avatar;
    private AtomicLong messageCount = new AtomicLong(0);
    private LocalDateTime joinedYmdt;
    private String nickname;

    public ChatUser(String uid) {
        this.uid = uid;
        this.joinedYmdt = LocalDateTime.now();
    }
}
