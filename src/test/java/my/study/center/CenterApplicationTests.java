package my.study.center;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.study.center.app.chat.redisEntity.User;
import my.study.center.common.websocket.cd.ChatMessageType;
import my.study.center.common.websocket.dto.ChatMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.UUID;

@SpringBootTest
class CenterApplicationTests {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void contextLoads() throws Exception {
        User user = new User("123123");
        user.setFirstName("Minseok");
        user.setLastName("Jeon");
        user.setNickname("minssogi");

        ChatMessage chatMessage =
                new ChatMessage(UUID.randomUUID().toString(), ChatMessageType.CHAT_MESSAGE, "하이루~",
                        Instant.now().toEpochMilli(), user);


        String str = objectMapper.writeValueAsString(chatMessage);

        System.out.println(str);
    }

}
