package io.consumer.com;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;
import java.util.Arrays;

public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final KafkaConsumer<String, String> consumer;

    /**
     * Constructor to initialize the KafkaConsumer.
     *
     * @param bootstrapServers Kafka bootstrap servers.
     * @param groupId         Consumer group ID.
     * @param topic           Kafka topic to subscribe.
     */
    public KafkaConsumerService(String bootstrapServers, String groupId, String topic) {
        this.consumer = createConsumer(bootstrapServers, groupId, topic);
    }

    /**
     * Creates and configures the Kafka consumer.
     *
     * @param bootstrapServers Kafka bootstrap servers.
     * @param groupId         Consumer group ID.
     * @param topic           Kafka topic to subscribe.
     * @return KafkaConsumer instance.
     */
    private KafkaConsumer<String, String> createConsumer(String bootstrapServers, String groupId, String topic) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", bootstrapServers);
        properties.setProperty("key.deserializer", StringDeserializer.class.getName());
        properties.setProperty("value.deserializer", StringDeserializer.class.getName());
        properties.setProperty("group.id", groupId);
        properties.setProperty("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(topic));
        log.info("Kafka Consumer initialized for topic: " + topic);
        return consumer;
    }

    /**
     * Polls records from Kafka.
     *
     * @return ConsumerRecords containing messages.
     */
    public ConsumerRecords<String, String> poll() {
        try {
            return consumer.poll(Duration.ofMillis(1000));
        } catch (WakeupException e) {
            // This is an expected exception when consumer.wakeup() is called for shutdown.
            log.info("Kafka consumer wakeup triggered.");
            throw e;  // Re-throw to handle outside if needed
        } catch (Exception e) {
            log.error("Error while polling records from Kafka", e);
            throw new RuntimeException("Error while polling records from Kafka", e);  // Re-throw for higher-level handling
        }
    }

    /**
     * Wakes up the consumer to interrupt any blocking poll.
     */
    public void wakeUp() {
        consumer.wakeup();
        log.info("Kafka consumer wakeup initiated.");
    }

    /**
     * Closes the Kafka consumer.
     */
    public void close() {
        try {
            consumer.close();
            log.info("Kafka consumer closed.");
        } catch (Exception e) {
            log.error("Error while closing Kafka consumer", e);
        }
    }
}
