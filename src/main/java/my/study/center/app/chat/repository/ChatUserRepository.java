package my.study.center.app.chat.repository;

import my.study.center.app.chat.redisEntity.User;
import org.springframework.data.repository.CrudRepository;

public interface ChatUserRepository extends CrudRepository<User, Long> {
}
