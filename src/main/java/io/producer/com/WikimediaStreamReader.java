package io.producer.com;

import java.util.concurrent.atomic.AtomicBoolean;

public class WikimediaStreamReader {

    private static final AtomicBoolean running = new AtomicBoolean(true);

    public static void main(String[] args) {
        // Create Kafka producer service
        KafkaProducerService producerService = new KafkaProducerService("172.23.131.70:9092", "wikimedia-topic");

        // Create the stream processor service
        WikimediaStreamProcessor streamProcessor = new WikimediaStreamProcessor();

        // Register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            running.set(false);
            producerService.close();  // Close Kafka producer gracefully
        }));

        // Start the stream processor
        streamProcessor.startStream(producerService);

        // Keep the main thread alive until shutdown
        while (running.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Main thread interrupted.");
            }
        }

        System.out.println("Shutting down...");
    }
}
