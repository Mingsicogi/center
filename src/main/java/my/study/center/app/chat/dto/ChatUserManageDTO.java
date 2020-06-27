package my.study.center.app.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅 유저 관리 기능에 사용되는 DTO 정의
 *
 * @author 전민석
 */
public class ChatUserManageDTO {

    /**
     * 총 보낸 메세지 조회를 위한 dto
     *
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class TotalMessageCountReq {
        private String sessionId;
    }
}
