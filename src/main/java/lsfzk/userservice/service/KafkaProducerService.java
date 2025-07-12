package lsfzk.userservice.service;

import lsfzk.userservice.event.BusinessRegistrationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final String TOPIC = "business-registrations";
    private final KafkaTemplate<String, BusinessRegistrationEvent> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, BusinessRegistrationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRegistrationEvent(BusinessRegistrationEvent event) {
        System.out.println("Producing message: " + event);
        // The key is the registrationId, which helps Kafka partition the data.
        this.kafkaTemplate.send(TOPIC, String.valueOf(event.registrationId()), event);
    }
}
