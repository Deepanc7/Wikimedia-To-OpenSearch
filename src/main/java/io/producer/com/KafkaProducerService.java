package io.producer.com;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaProducerService {

    private final KafkaProducer<String, String> producer;
    private final String topic;

    public KafkaProducerService(String bootstrapServers, String topic) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", bootstrapServers);
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());
        this.producer = new KafkaProducer<>(properties);
        this.topic = topic;
    }

    public void sendToKafka(String message) {
        producer.send(new ProducerRecord<>(topic, null, message), (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Error sending message to Kafka: " + exception.getMessage());
            } else {
                System.out.println("Message sent successfully: " + message);
            }
        });
    }

    public void close() {
        producer.close();
    }
}
