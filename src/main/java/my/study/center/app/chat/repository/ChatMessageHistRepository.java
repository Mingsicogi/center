package my.study.center.app.chat.repository;

import my.study.center.app.chat.documents.ChatMessageHist;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ChatMessageHistRepository extends ReactiveMongoRepository<ChatMessageHist, String> {
}