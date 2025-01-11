package io.consumer.com;

import io.openSearch.com.OpenSearchService;
import io.util.com.JsonUtility;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConsumerToOpenSearch {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerToOpenSearch.class.getSimpleName());

    public static void main(String[] args) {
        // Configuration for Kafka and OpenSearch
        String bootstrapServers = "172.23.131.70:9092";
        String groupId = "wikimedia-messages";
        String topic = "wikimedia-topic";
        String indexName = "wikimedia";

        // Instantiate services
        KafkaConsumerService kafkaConsumerService = new KafkaConsumerService(bootstrapServers, groupId, topic);
        OpenSearchService openSearchService = new OpenSearchService("localhost", 9200);

        try {
            // Ensure OpenSearch index exists
            openSearchService.createIndexIfNotExists(indexName);

            // Register shutdown hook to cleanly shut down
            ConsumerShutdownHook.registerShutdownHook(kafkaConsumerService);

            // Variables for processing Kafka records
            String event = null;
            String data = null;
            String id = null;

            // Poll for records from Kafka and index them into OpenSearch
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumerService.poll();
                BulkRequest bulkRequest = new BulkRequest();

                for (var record : records) {
                    // Parse record value to extract the necessary fields
                    if (record.value().contains("event: message")) {
                        event = record.value();
                    } else if (record.value().contains("id:")) {
                        id = record.value();
                    } else if (record.value().contains("data:")) {
                        data = record.value();
                    }

                    // When all necessary fields are collected, prepare the document and add to bulk request
                    if (id != null && event != null && data != null) {
                        String jsonDocument = JsonUtility.extractJson(event, id, data);
                        bulkRequest.add(new IndexRequest(indexName).source(jsonDocument, XContentType.JSON));

                        // Reset values for the next record
                        id = null;
                        event = null;
                        data = null;
                    }
                }

                // Perform the bulk indexing operation in OpenSearch
                openSearchService.bulkIndexDocuments(bulkRequest);
            }
        } catch (Exception e) {
            log.error("Unexpected error in Kafka to OpenSearch process", e);
        } finally {
            // Ensure resources are closed after execution
            try {
                kafkaConsumerService.close();
                openSearchService.close();
            } catch (Exception e) {
                log.error("Error while closing resources", e);
            }
            log.info("Kafka consumer and OpenSearch client shut down gracefully");
        }
    }
}
