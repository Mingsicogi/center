package my.study.center.app.chat.controller;

import lombok.RequiredArgsConstructor;
import my.study.center.app.chat.documents.ChatMessageHist;
import my.study.center.app.chat.dto.ChatUserDTO;
import my.study.center.app.chat.repository.ChatMessageHistRepository;
import my.study.center.common.websocket.cd.ChatMessageType;
import my.study.center.common.websocket.dto.ChatUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.UUID;

import static my.study.center.common.websocket.ReactiveWebsocketConnectionHandler.userSessionManager;

@RestController
@RequestMapping(value = "/chat/user")
@RequiredArgsConstructor
public class ChatUserManage {

    private final ChatMessageHistRepository repository;

    @PostMapping(value = "/message/count", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Flux<Long>> getMessageCount(@RequestBody ChatUserDTO.InfoReq param) {

        ChatUser chatUser = userSessionManager.get(param.getSessionId());

        return ResponseEntity.ok(Flux.just(chatUser.getMessageCount().get()));
    }

    @GetMapping(value = "/current/count")
    public ResponseEntity<Flux<ChatMessageHist>> getCurrentJoinedUser() {
        ChatMessageHist sampleData = this.getSampleData();

        repository.save(sampleData);

        return ResponseEntity.ok(repository.save(sampleData).map(savedData -> {
            System.out.println(savedData.getId());
            System.out.println(savedData.getText());
            return savedData;
        }).flux().log());
    }

    private ChatMessageHist getSampleData() {
        ChatMessageHist mongoParam = new ChatMessageHist();
        mongoParam.setCreatedAt(Instant.now().toEpochMilli());
        mongoParam.setId(UUID.randomUUID().toString());
        mongoParam.setText("test");
        mongoParam.setType(ChatMessageType.CHAT_MESSAGE);
        ChatUser user = new ChatUser();
        user.setNickname("minssogi");
        mongoParam.setUser(user);

        return mongoParam;
    }
}
