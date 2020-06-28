package my.study.center.app.chat.redisEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@RedisHash
@NoArgsConstructor
public class User implements Serializable {

    @Id
    private String uid;
    private String firstName;
    private String lastName;
    private String avatar;
    private Long messageCount;
    private LocalDateTime joinedYmdt;
    private String nickname;

    public User(String uid) {
        this.uid = uid;
        messageCount = 0L;
    }
}
