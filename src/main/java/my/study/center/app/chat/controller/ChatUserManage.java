package my.study.center.app.chat.controller;

import lombok.RequiredArgsConstructor;
import my.study.center.app.chat.dto.ChatUserManageDTO;
import my.study.center.app.chat.redisEntity.User;
import my.study.center.app.chat.repository.ChatUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Optional;

/**
 * 현재 접속 중인 유저 관리를 위한 컨트롤러
 *
 * @author minssogi
 */
@RestController
@RequestMapping(value = "/chat/user")
@RequiredArgsConstructor
public class ChatUserManage {

    private final ChatUserRepository chatUserRepository;

    /**
     * 파라미터로 전달한 세션이 보낸 메세지 갯수
     *
     * @param param sessionId
     * @return Long
     */
    @PostMapping(value = "/message/count", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getMessageCount(@RequestBody ChatUserManageDTO.TotalMessageCountReq param) {
        Optional<User> user = chatUserRepository.findById(param.getSessionId());

        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found " + param.getSessionId() + " information");
        }

        return ResponseEntity.ok(Flux.just(user.get().getMessageCount()));
    }

    /**
     * 현재 메신저를 사용중인 유저
     *
     * @return Integer
     */
    @GetMapping(value = "/current/count")
    public ResponseEntity<Flux<Integer>> getCurrentJoinedUser() {
        return ResponseEntity.ok(Flux.just(chatUserRepository.countAll()));
    }
}
