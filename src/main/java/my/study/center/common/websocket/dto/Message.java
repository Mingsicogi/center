package my.study.center.common.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my.study.center.common.websocket.cd.MessageType;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private MessageType type;
    private String data;
}
