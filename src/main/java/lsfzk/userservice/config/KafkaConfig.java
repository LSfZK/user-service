package lsfzk.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    /**
     * Define the "promote-request" topic explicitly.
     * partitions(3) -> Allows up to 3 consumers to work in parallel.
     * replicas(1)   -> Since you have 1 broker in Docker, use 1. (In prod, use 3).
     */
    @Bean
    public NewTopic promoteRequestTopic() {
        return TopicBuilder.name("promote-request")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic businessRegistrationTopic() {
        return TopicBuilder.name("business-registrations")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
