package my.study.center.config;

import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "my.study.center")
public class ReactiveMongoConfiguration {

//    @Value("${spring.data.mongodb.database}")
//    private String databaseNm;
//
//    @Bean
//    public SimpleReactiveMongoDatabaseFactory simpleReactiveMongoDatabaseFactory(MongoClient mongoClient) {
//        return new SimpleReactiveMongoDatabaseFactory(mongoClient, databaseNm);
//    }
//
//    @Bean
//    public ReactiveMongoTemplate reactiveMongoTemplate(ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory, MongoConverter converter) {
//        return new ReactiveMongoTemplate(reactiveMongoDatabaseFactory, converter);
//    }
}
