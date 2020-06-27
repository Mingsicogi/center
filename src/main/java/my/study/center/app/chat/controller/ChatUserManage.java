package my.study.center.app.chat.controller;

import lombok.RequiredArgsConstructor;
import my.study.center.app.chat.dto.ChatUserManageDTO;
import my.study.center.common.websocket.dto.ChatUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static my.study.center.common.websocket.ReactiveWebsocketConnectionHandler.userSessionManager;

/**
 * 현재 접속 중인 유저 관리를 위한 컨트롤러
 *
 * @author minssogi
 */
@RestController
@RequestMapping(value = "/chat/user")
@RequiredArgsConstructor
public class ChatUserManage {

    /**
     * 파라미터로 전달한 세션이 보낸 메세지 갯수
     *
     * @param param sessionId
     * @return Long
     */
    @PostMapping(value = "/message/count", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Flux<Long>> getMessageCount(@RequestBody ChatUserManageDTO.TotalMessageCountReq param) {

        ChatUser chatUser = userSessionManager.get(param.getSessionId());

        return ResponseEntity.ok(Flux.just(chatUser.getMessageCount().get()));
    }

    /**
     * 현재 메신저를 사용중인 유저
     *
     * @return Integer
     */
    @GetMapping(value = "/current/count")
    public ResponseEntity<Flux<Integer>> getCurrentJoinedUser() {
        return ResponseEntity.ok(Flux.just(userSessionManager.size()));
    }
}
