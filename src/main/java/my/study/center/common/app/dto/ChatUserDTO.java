package my.study.center.common.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChatUserDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class InfoReq {
        private String sessionId;
    }
}
