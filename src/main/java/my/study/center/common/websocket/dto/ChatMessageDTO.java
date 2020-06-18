package my.study.center.common.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my.study.center.common.websocket.cd.ChatMessageType;

import static my.study.center.common.utils.CommonUtils.stringToObject;

/**
 * chatting message dto
 *
 * @author minssogi
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private ChatMessageType type;
    private String data;

    public static ChatMessageDTO toMessage(String strMessage) {
        return stringToObject(strMessage, ChatMessageDTO.class).orElseThrow(RuntimeException::new);
    }
}
