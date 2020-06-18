package my.study.center.common.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my.study.center.common.websocket.cd.MessageType;

import static my.study.center.common.utils.CommonUtils.stringToObject;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private MessageType type;
    private String data;

    public static Message toMessage(String strMessage) {
        return stringToObject(strMessage, Message.class).orElseThrow(RuntimeException::new);
    }
}
