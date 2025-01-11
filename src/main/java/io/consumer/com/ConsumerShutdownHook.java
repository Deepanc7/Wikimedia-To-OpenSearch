package io.consumer.com;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerShutdownHook {

    private static final Logger log = LoggerFactory.getLogger(ConsumerShutdownHook.class);

    public static void registerShutdownHook(KafkaConsumerService kafkaConsumerService) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown signal received. Cleaning up resources...");
            kafkaConsumerService.wakeUp();
            try {
                kafkaConsumerService.close();
            } catch (Exception e) {
                log.error("Error while closing Kafka consumer", e);
            }
        }));
    }
}
