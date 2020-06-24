package my.study.center.common.app.repository;

import my.study.center.common.app.documents.ChatMessageHist;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


public interface ChatMessageHistRepository extends ReactiveMongoRepository<ChatMessageHist, String> {
}
