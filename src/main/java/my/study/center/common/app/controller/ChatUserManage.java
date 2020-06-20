package my.study.center.common.app.controller;

import my.study.center.common.app.dto.ChatUserDTO;
import my.study.center.common.websocket.dto.ChatUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static my.study.center.common.websocket.ReactiveWebsocketConnectionHandler.userSessionManager;

@RestController
@RequestMapping(value = "/chat/user")
public class ChatUserManage {

    @PostMapping(value = "/message/count", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Flux<Long>> getMessageCount(@RequestBody ChatUserDTO.InfoReq param) {

        ChatUser chatUser = userSessionManager.get(param.getSessionId());

        return ResponseEntity.ok(Flux.just(chatUser.getMessageCount().get()));
    }

    @GetMapping(value = "/current/count")
    public ResponseEntity<Flux<Integer>> getCurrentJoinedUser() {
        return ResponseEntity.ok(Flux.just(userSessionManager.size()));
    }
}
