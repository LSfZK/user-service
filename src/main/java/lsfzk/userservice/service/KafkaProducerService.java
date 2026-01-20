package lsfzk.userservice.service;

import lsfzk.events.BusinessRegistrationEvent;
import lsfzk.events.PromoteRequestEvent;
import lsfzk.events.PromoteResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    // Spring's auto-configuration creates this bean for us based on our YAML file.
    // It is correctly typed to send our event object.
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Generic sender for ANY event.
     * This reduces code duplication significantly.
     */
    public void sendEvent(String topic, String key, Object event) {
        log.info("Producing event to topic '{}': {}", topic, event);
        this.kafkaTemplate.send(topic, key, event);
    }

    /**
     * Sends a BusinessRegistrationEvent to the "business-registrations" topic.
     * @param event The event object to send.
     */
    public void sendBusinessRegistrationEvent(BusinessRegistrationEvent event) {
        log.info("Producing BusinessRegistrationEvent: {}", event);
        // Spring's JsonSerializer handles the conversion from the object to JSON.
        this.kafkaTemplate.send("business-registrations", String.valueOf(event.registrationId()), event);
    }

    public void sendPromoteRequestEvent(PromoteRequestEvent event) {
        log.info("Producing PromoteRequestEvent: {}", event);
        this.kafkaTemplate.send("promote-request", String.valueOf(event.userId()), event);
    }

    public void sendPromoteResponseEvent(PromoteResponseEvent event) {
        log.info("Producing PromoteRequestEvent: {}", event);
        this.kafkaTemplate.send("promote-response", String.valueOf(event.userId()), event);
    }

    // You can add more methods here to send different event types.
    // For example, you would need a different KafkaTemplate<String, PasswordChangedEvent>
    // if you wanted to send a different type of object.
}
