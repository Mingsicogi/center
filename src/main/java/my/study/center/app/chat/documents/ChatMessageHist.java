package my.study.center.app.chat.documents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my.study.center.app.chat.redisEntity.User;
import my.study.center.common.websocket.cd.ChatMessageType;
import my.study.center.common.websocket.dto.ChatMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 메세지 이력관리를 위한 document
 *
 * @author 전민석
 */
@Getter
@Setter
@Document("chatMessageHist")
@NoArgsConstructor
public class ChatMessageHist {
    @Id
    private String histId;
    private ChatMessageType type;
    private String text;
    private long createdAt;
    private User user;

    public ChatMessageHist(ChatMessage message) {
        BeanUtils.copyProperties(message, this);
    }
}
